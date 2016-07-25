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

import com.androidadvance.topsnackbar.interfaces.ICallback;

import java.lang.ref.WeakReference;

/**
 * Manages {@link TSnackbar}s.
 */
public class TSnackbarManager {

    private static final int MSG_TIMEOUT = 0;

    private static final int SHORT_DURATION_MS = 1500;
    private static final int LONG_DURATION_MS = 2750;

    private static TSnackbarManager sTSnackbarManager;

    static TSnackbarManager getInstance() {
        if (sTSnackbarManager == null) {
            sTSnackbarManager = new TSnackbarManager();
        }
        return sTSnackbarManager;
    }

    private final Object mLock;
    private final Handler mHandler;

    private SnackbarRecord mCurrentSnackbar;
    private SnackbarRecord mNextSnackbar;

    private TSnackbarManager() {
        mLock = new Object();
        mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case MSG_TIMEOUT:
                        handleTimeout((SnackbarRecord) message.obj);
                        return true;
                }
                return false;
            }
        });
    }

    public void show(int duration, ICallback ICallback) {
        synchronized (mLock) {
            if (isCurrentSnackbar(ICallback)) {
                // Means that the ICallback is already in the queue. We'll just update the duration
                mCurrentSnackbar.duration = duration;
                // If this is the TSnackbar currently being shown, call re-schedule it's
                // timeout
                mHandler.removeCallbacksAndMessages(mCurrentSnackbar);
                scheduleTimeoutLocked(mCurrentSnackbar);
                return;
            } else if (isNextSnackbar(ICallback)) {
                // We'll just update the duration
                mNextSnackbar.duration = duration;
            } else {
                // Else, we need to create a new record and queue it
                mNextSnackbar = new SnackbarRecord(duration, ICallback);
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

    public void dismiss(ICallback ICallback, int event) {
        synchronized (mLock) {
            if (isCurrentSnackbar(ICallback)) {
                cancelSnackbarLocked(mCurrentSnackbar, event);
            } else if (isNextSnackbar(ICallback)) {
                cancelSnackbarLocked(mNextSnackbar, event);
            }
        }
    }

    /**
     * Should be called when a TSnackbar is no longer displayed. This is after any exit
     * animation has finished.
     */
    public void onDismissed(ICallback ICallback) {
        synchronized (mLock) {
            if (isCurrentSnackbar(ICallback)) {
                // If the ICallback is from a TSnackbar currently show, remove it and show a new one
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
    public void onShown(ICallback ICallback) {
        synchronized (mLock) {
            if (isCurrentSnackbar(ICallback)) {
                scheduleTimeoutLocked(mCurrentSnackbar);
            }
        }
    }

    public void cancelTimeout(ICallback ICallback) {
        synchronized (mLock) {
            if (isCurrentSnackbar(ICallback)) {
                mHandler.removeCallbacksAndMessages(mCurrentSnackbar);
            }
        }
    }

    public void restoreTimeout(ICallback ICallback) {
        synchronized (mLock) {
            if (isCurrentSnackbar(ICallback)) {
                scheduleTimeoutLocked(mCurrentSnackbar);
            }
        }
    }

    public boolean isCurrent(ICallback ICallback) {
        synchronized (mLock) {
            return isCurrentSnackbar(ICallback);
        }
    }

    public boolean isCurrentOrNext(ICallback ICallback) {
        synchronized (mLock) {
            return isCurrentSnackbar(ICallback) || isNextSnackbar(ICallback);
        }
    }

    private void showNextSnackbarLocked() {
        if (mNextSnackbar != null) {
            mCurrentSnackbar = mNextSnackbar;
            mNextSnackbar = null;

            final ICallback ICallback = mCurrentSnackbar.ICallback.get();
            if (ICallback != null) {
                ICallback.show();
            } else {
                // The ICallback doesn't exist any more, clear out the TSnackbar
                mCurrentSnackbar = null;
            }
        }
    }

    private boolean cancelSnackbarLocked(SnackbarRecord record, int event) {
        final ICallback ICallback = record.ICallback.get();
        if (ICallback != null) {
            ICallback.dismiss(event);
            return true;
        }
        return false;
    }

    private boolean isCurrentSnackbar(ICallback ICallback) {
        return mCurrentSnackbar != null && mCurrentSnackbar.isSnackbar(ICallback);
    }

    private boolean isNextSnackbar(ICallback ICallback) {
        return mNextSnackbar != null && mNextSnackbar.isSnackbar(ICallback);
    }

    private void scheduleTimeoutLocked(SnackbarRecord r) {
        if (r.duration == TSnackbar.LENGTH_INDEFINITE) {
            // If we're set to indefinite, we don't want to set a timeout
            return;
        }

        int durationMs = LONG_DURATION_MS;
        if (r.duration > 0) {
            durationMs = r.duration;
        } else if (r.duration == TSnackbar.LENGTH_SHORT) {
            durationMs = SHORT_DURATION_MS;
        }
        mHandler.removeCallbacksAndMessages(r);
        mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_TIMEOUT, r), durationMs);
    }

    private void handleTimeout(SnackbarRecord record) {
        synchronized (mLock) {
            if (mCurrentSnackbar == record || mNextSnackbar == record) {
                cancelSnackbarLocked(record, TSnackbarCallback.DISMISS_EVENT_TIMEOUT);
            }
        }
    }

    private static class SnackbarRecord {
        private final WeakReference<ICallback> ICallback;
        private int duration;

        SnackbarRecord(int duration, ICallback ICallback) {
            this.ICallback = new WeakReference<>(ICallback);
            this.duration = duration;
        }

        boolean isSnackbar(ICallback ICallback) {
            return ICallback != null && this.ICallback.get() == ICallback;
        }
    }
}
