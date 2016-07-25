package com.androidadvance.topsnackbar.listeners;

import android.view.View;

import com.androidadvance.topsnackbar.TSnackbar;
import com.androidadvance.topsnackbar.TSnackbarCallback;
import com.androidadvance.topsnackbar.interfaces.IOnAttachStateChangeListener;

/**
 * Created by Claudiu Codoban on 7/25/2016.
 */
public class TSnackbarAttachStateChangeListener extends BaseTSnackbarListener implements IOnAttachStateChangeListener {

    public TSnackbarAttachStateChangeListener(TSnackbar tSnackbar) {
        super(tSnackbar);
    }

    @Override
    public void onViewAttachedToWindow(View v) {
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        if (mTSnackbar.isShownOrQueued()) {
            mTSnackbar.Handler.post(new Runnable() {
                @Override
                public void run() {
                    mTSnackbar.onViewHidden(TSnackbarCallback.DISMISS_EVENT_MANUAL);
                }
            });
        }
    }
}
