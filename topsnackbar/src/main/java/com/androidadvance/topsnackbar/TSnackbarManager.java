package com.androidadvance.topsnackbar;

/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.androidadvance.topsnackbar.interfaces.ITSnackbarManagerCallback;

/**
 * Manages {@link TSnackbar}s.
 */
public class TSnackbarManager {

    private static final int MSG_TIMEOUT = 0;

    private static final int SHORT_DURATION_MS = 1500;
    private static final int LONG_DURATION_MS = 2750;

    private static TSnackbarManager sTSnackbarManager;

    public static TSnackbarManager getInstance() {
        if (sTSnackbarManager == null) {
            sTSnackbarManager = new TSnackbarManager();
        }
        return sTSnackbarManager;
    }

    private final Object mLock;
    private final Handler mHandler;

    private TSnackbarRecord mCurrentSnackbar;
    private TSnackbarRecord mNextSnackbar;

    private TSnackbarManager() {
        mLock = new Object();
        mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case MSG_TIMEOUT:
                        handleTimeout((TSnackbarRecord) message.obj);
                        return true;
                }
                return false;
            }
        });
    }

    public void show(int duration, ITSnackbarManagerCallback ITSnackbarManagerCallback) {
        synchronized (mLock) {
            if (isCurrentSnackbar(ITSnackbarManagerCallback)) {
                // Means that the ITSnackbarManagerCallback is already in the queue. We'll just update the Duration
                mCurrentSnackbar.Duration = duration;
                // If this is the TSnackbar currently being shown, call re-schedule it's
                // timeout
                mHandler.removeCallbacksAndMessages(mCurrentSnackbar);
                scheduleTimeoutLocked(mCurrentSnackbar);
                return;
            } else if (isNextSnackbar(ITSnackbarManagerCallback)) {
                // We'll just update the Duration
                mNextSnackbar.Duration = duration;
            } else {
                // Else, we need to create a new record and queue it
                mNextSnackbar = new TSnackbarRecord(duration, ITSnackbarManagerCallback);
            }

            if (mCurrentSnackbar != null && cancelSnackbarLocked(mCurrentSnackbar,
                    TSnackbarCallback.DISMISS_EVENT_CONSECUTIVE)) {
                // If we currently have a TSnackbar, try and cancel it and wait in line
                return;
            } else {
                // Clear out the current snackbar
                mCurrentSnackbar = null;
                // Otherwise, just show it now
                showNextSnackbarLocked();
            }
        }
    }

    public void dismiss(ITSnackbarManagerCallback ITSnackbarManagerCallback, int event) {
        synchronized (mLock) {
            if (isCurrentSnackbar(ITSnackbarManagerCallback)) {
                cancelSnackbarLocked(mCurrentSnackbar, event);
            } else if (isNextSnackbar(ITSnackbarManagerCallback)) {
                cancelSnackbarLocked(mNextSnackbar, event);
            }
        }
    }

    /**
     * Should be called when a TSnackbar is no longer displayed. This is after any exit
     * animation has finished.
     */
    public void onDismissed(ITSnackbarManagerCallback ITSnackbarManagerCallback) {
        synchronized (mLock) {
            if (isCurrentSnackbar(ITSnackbarManagerCallback)) {
                // If the ITSnackbarManagerCallback is from a TSnackbar currently show, remove it and show a new one
                mCurrentSnackbar = null;
                if (mNextSnackbar != null) {
                    showNextSnackbarLocked();
                }
            }
        }
    }

    /**
     * Should be called when a TSnackbar is being shown. This is after any entrance animation has
     * finished.
     */
    public void onShown(ITSnackbarManagerCallback ITSnackbarManagerCallback) {
        synchronized (mLock) {
            if (isCurrentSnackbar(ITSnackbarManagerCallback)) {
                scheduleTimeoutLocked(mCurrentSnackbar);
            }
        }
    }

    public void cancelTimeout(ITSnackbarManagerCallback ITSnackbarManagerCallback) {
        synchronized (mLock) {
            if (isCurrentSnackbar(ITSnackbarManagerCallback)) {
                mHandler.removeCallbacksAndMessages(mCurrentSnackbar);
            }
        }
    }

    public void restoreTimeout(ITSnackbarManagerCallback ITSnackbarManagerCallback) {
        synchronized (mLock) {
            if (isCurrentSnackbar(ITSnackbarManagerCallback)) {
                scheduleTimeoutLocked(mCurrentSnackbar);
            }
        }
    }

    public boolean isCurrent(ITSnackbarManagerCallback ITSnackbarManagerCallback) {
        synchronized (mLock) {
            return isCurrentSnackbar(ITSnackbarManagerCallback);
        }
    }

    public boolean isCurrentOrNext(ITSnackbarManagerCallback ITSnackbarManagerCallback) {
        synchronized (mLock) {
            return isCurrentSnackbar(ITSnackbarManagerCallback) || isNextSnackbar(ITSnackbarManagerCallback);
        }
    }

    private void showNextSnackbarLocked() {
        if (mNextSnackbar != null) {
            mCurrentSnackbar = mNextSnackbar;
            mNextSnackbar = null;

            final ITSnackbarManagerCallback ITSnackbarManagerCallback = mCurrentSnackbar.Callback.get();
            if (ITSnackbarManagerCallback != null) {
                ITSnackbarManagerCallback.show();
            } else {
                // The ITSnackbarManagerCallback doesn't exist any more, clear out the TSnackbar
                mCurrentSnackbar = null;
            }
        }
    }

    private boolean cancelSnackbarLocked(TSnackbarRecord record, int event) {
        final ITSnackbarManagerCallback ITSnackbarManagerCallback = record.Callback.get();
        if (ITSnackbarManagerCallback != null) {
            ITSnackbarManagerCallback.dismiss(event);
            return true;
        }
        return false;
    }

    private boolean isCurrentSnackbar(ITSnackbarManagerCallback ITSnackbarManagerCallback) {
        return mCurrentSnackbar != null && mCurrentSnackbar.isSnackbar(ITSnackbarManagerCallback);
    }

    private boolean isNextSnackbar(ITSnackbarManagerCallback ITSnackbarManagerCallback) {
        return mNextSnackbar != null && mNextSnackbar.isSnackbar(ITSnackbarManagerCallback);
    }

    private void scheduleTimeoutLocked(TSnackbarRecord r) {
        if (r.Duration == TSnackbar.LENGTH_INDEFINITE) {
            // If we're set to indefinite, we don't want to set a timeout
            return;
        }

        int durationMs = LONG_DURATION_MS;
        if (r.Duration > 0) {
            durationMs = r.Duration;
        } else if (r.Duration == TSnackbar.LENGTH_SHORT) {
            durationMs = SHORT_DURATION_MS;
        }

        mHandler.removeCallbacksAndMessages(r);
        mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_TIMEOUT, r), durationMs);
    }

    private void handleTimeout(TSnackbarRecord record) {
        synchronized (mLock) {
            if (mCurrentSnackbar == record || mNextSnackbar == record) {
                cancelSnackbarLocked(record, TSnackbarCallback.DISMISS_EVENT_TIMEOUT);
            }
        }
    }
}
