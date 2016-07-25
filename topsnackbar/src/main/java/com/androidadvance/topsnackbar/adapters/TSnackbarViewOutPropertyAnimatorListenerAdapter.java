package com.androidadvance.topsnackbar.adapters;

import android.view.View;

import com.androidadvance.topsnackbar.TSnackbar;

/**
 * Created by Claudiu Codoban on 7/25/2016.
 */
public class TSnackbarViewOutPropertyAnimatorListenerAdapter extends BaseViewPropertyAnimatorListenerAdapter {
    private int mEvent;

    public TSnackbarViewOutPropertyAnimatorListenerAdapter(TSnackbar tSnackbar, int event) {
        super(tSnackbar);
        mEvent = event;
    }

    @Override
    public void onAnimationStart(View view) {
        mTSnackbar.Layout.animateChildrenOut(0, TSnackbar.ANIMATION_FADE_DURATION);
    }

    @Override
    public void onAnimationEnd(View view) {
        mTSnackbar.onViewHidden(mEvent);
    }
}
