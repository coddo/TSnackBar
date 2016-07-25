package com.androidadvance.topsnackbar.helpers;

import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.View;

import com.androidadvance.topsnackbar.TSnackbar;
import com.androidadvance.topsnackbar.TSnackbarManager;

/**
 * Created by Claudiu Codoban on 7/25/2016.
 */
public class TSnackbarViewPropertyAnimatorListenerAdapter extends ViewPropertyAnimatorListenerAdapter {

    private TSnackbar mTSnackbar;

    public TSnackbarViewPropertyAnimatorListenerAdapter(TSnackbar tSnackbar) {
        mTSnackbar = tSnackbar;
    }

    @Override
    public void onAnimationStart(View view) {
        mTSnackbar.Layout.animateChildrenIn(mTSnackbar.ANIMATION_DURATION - mTSnackbar.ANIMATION_FADE_DURATION,
                mTSnackbar.ANIMATION_FADE_DURATION);
    }

    @Override
    public void onAnimationEnd(View view) {
        if (mTSnackbar.TSnackbarCallback != null) {
            mTSnackbar.TSnackbarCallback.onShown(mTSnackbar);
        }

        TSnackbarManager.getInstance().onShown(mTSnackbar.ManagerCallback);
    }
}
