package com.androidadvance.topsnackbar.listeners;

import android.view.animation.Animation;

import com.androidadvance.topsnackbar.TSnackbar;
import com.androidadvance.topsnackbar.TSnackbarManager;

/**
 * Created by Claudiu Codoban on 7/25/2016.
 */
public class TSnackbarShowAnimationListener extends BaseTSnackbarListener implements Animation.AnimationListener {
    public TSnackbarShowAnimationListener(TSnackbar tSnackbar) {
        super(tSnackbar);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (mTSnackbar.TSnackbarCallback != null) {
            mTSnackbar.TSnackbarCallback.onShown(mTSnackbar);
        }
        TSnackbarManager.getInstance().onShown(mTSnackbar.ManagerCallback);
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}
