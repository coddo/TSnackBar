package com.androidadvance.topsnackbar.listeners;

import android.view.animation.Animation;

import com.androidadvance.topsnackbar.TSnackbar;

/**
 * Created by Claudiu Codoban on 7/25/2016.
 */
public class TSnackbarCloseAnimationListener extends BaseTSnackbarListener implements Animation.AnimationListener {
    private int mEvent;

    public TSnackbarCloseAnimationListener(TSnackbar tSnackbar, int eventValue) {
        super(tSnackbar);
        mEvent = eventValue;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mTSnackbar.onViewHidden(mEvent);
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}
