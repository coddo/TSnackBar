package com.androidadvance.topsnackbar.listeners;

import android.support.design.widget.SwipeDismissBehavior;
import android.view.View;

import com.androidadvance.topsnackbar.TSnackbar;
import com.androidadvance.topsnackbar.TSnackbarCallback;
import com.androidadvance.topsnackbar.TSnackbarManager;

/**
 * Created by Claudiu Codoban on 7/25/2016.
 */
public class TSnackbarDismissTSnackbarListener extends BaseTSnackbarListener implements SwipeDismissBehavior.OnDismissListener {

    public TSnackbarDismissTSnackbarListener(TSnackbar tSnackbar) {
        super(tSnackbar);
    }

    @Override
    public void onDismiss(View view) {
        mTSnackbar.dispatchDismiss(TSnackbarCallback.DISMISS_EVENT_SWIPE);
    }

    @Override
    public void onDragStateChanged(int state) {
        switch (state) {
            case SwipeDismissBehavior.STATE_DRAGGING:
            case SwipeDismissBehavior.STATE_SETTLING:

                TSnackbarManager.getInstance().cancelTimeout(mTSnackbar.ManagerCallback);
                break;
            case SwipeDismissBehavior.STATE_IDLE:

                TSnackbarManager.getInstance().restoreTimeout(mTSnackbar.ManagerCallback);
                break;
        }
    }
}
