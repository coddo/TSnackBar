package com.androidadvance.topsnackbar;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.SwipeDismissBehavior;
import android.view.MotionEvent;
import android.view.View;

import com.androidadvance.topsnackbar.interfaces.ITSnackbarManagerCallback;

/**
 * Created by Claudiu Codoban on 7/25/2016.
 */
public final class TSnackbarBehavior extends SwipeDismissBehavior<TSnackbarLayout> {
    private ITSnackbarManagerCallback mTSnackbarManagerCallback;

    public TSnackbarBehavior(ITSnackbarManagerCallback tSnackbarManagerCallback) {
        mTSnackbarManagerCallback = tSnackbarManagerCallback;
    }

    @Override
    public boolean canSwipeDismissView(View child) {
        return child instanceof TSnackbarLayout;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, TSnackbarLayout child,
                                         MotionEvent event) {


        if (parent.isPointInChildBounds(child, (int) event.getX(), (int) event.getY())) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    TSnackbarManager.getInstance()
                            .cancelTimeout(mTSnackbarManagerCallback);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    TSnackbarManager.getInstance()
                            .restoreTimeout(mTSnackbarManagerCallback);
                    break;
            }
        }

        return super.onInterceptTouchEvent(parent, child, event);
    }
}
