package com.androidadvance.topsnackbar;

import com.androidadvance.topsnackbar.interfaces.ITSnackbarManagerCallback;

/**
 * Created by Claudiu Codoban on 7/25/2016.
 */
public class TSnackbarManagerCallback implements ITSnackbarManagerCallback {

    private TSnackbar mTSnackBar;

    public TSnackbarManagerCallback(TSnackbar tSnackbar) {
        mTSnackBar = tSnackbar;
    }

    @Override
    public void show() {
        mTSnackBar.Handler.sendMessage(mTSnackBar.Handler.obtainMessage(TSnackbar.MSG_SHOW, mTSnackBar));
    }

    @Override
    public void dismiss(int eventValue) {
        mTSnackBar.Handler.sendMessage(mTSnackBar.Handler.obtainMessage(TSnackbar.MSG_DISMISS, eventValue, 0, mTSnackBar));
    }
}
