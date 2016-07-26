package com.androidadvance.topsnackbar.listeners;

import android.view.View;

import com.androidadvance.topsnackbar.TSnackbar;
import com.androidadvance.topsnackbar.interfaces.IOnTSnackbarLayoutChangeListener;

/**
 * Created by Claudiu Codoban on 7/25/2016.
 */
public class TSnackbarLayoutChangeListener extends BaseTSnackbarListener implements IOnTSnackbarLayoutChangeListener {
    public TSnackbarLayoutChangeListener(TSnackbar tSnackbar) {
        super(tSnackbar);
    }

    @Override
    public void onLayoutChange(View view, int left, int top, int right, int bottom) {
        mTSnackbar.animateViewIn();
        mTSnackbar.Layout.setOnLayoutChangeListener(null);
    }
}
