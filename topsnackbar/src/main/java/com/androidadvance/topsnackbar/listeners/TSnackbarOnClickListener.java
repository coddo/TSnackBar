package com.androidadvance.topsnackbar.listeners;

import android.view.View;

import com.androidadvance.topsnackbar.TSnackbar;
import com.androidadvance.topsnackbar.TSnackbarCallback;

/**
 * Created by Claudiu Codoban on 7/26/2016.
 */
public class TSnackbarOnClickListener implements View.OnClickListener {
    private TSnackbar mTSnackbar;
    private View.OnClickListener mParentListener;

    public TSnackbarOnClickListener(TSnackbar tSnackbar, View.OnClickListener parentListener) {
        mTSnackbar = tSnackbar;
        mParentListener = parentListener;
    }

    @Override
    public void onClick(View view) {
        mParentListener.onClick(view);

        mTSnackbar.dispatchDismiss(TSnackbarCallback.DISMISS_EVENT_ACTION);
    }
}
