package com.androidadvance.topsnackbar.adapters;

import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;

import com.androidadvance.topsnackbar.TSnackbar;

/**
 * Created by Claudiu Codoban on 7/25/2016.
 */
public abstract class BaseViewPropertyAnimatorListenerAdapter extends ViewPropertyAnimatorListenerAdapter {
    protected TSnackbar mTSnackbar;

    public BaseViewPropertyAnimatorListenerAdapter(TSnackbar tSnackbar) {
        mTSnackbar = tSnackbar;
    }
}
