package com.androidadvance.topsnackbar.listeners;

import android.view.View;

import com.androidadvance.topsnackbar.TSnackbar;
import com.androidadvance.topsnackbar.interfaces.IOnLayoutChangeListener;

/**
 * Created by Claudiu Codoban on 7/25/2016.
 */
public class TSnackbarOnLayoutChangeTSnackbarListener extends BaseTSnackbarListener implements IOnLayoutChangeListener {
    public TSnackbarOnLayoutChangeTSnackbarListener(TSnackbar tSnackbar) {
        super(tSnackbar);
    }

    @Override
    public void onLayoutChange(View view, int left, int top, int right, int bottom) {
        mTSnackbar.animateViewIn();
        mTSnackbar.Layout.setOnLayoutChangeListener(null);
    }
}
