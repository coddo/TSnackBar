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
    public void dismiss(int event) {
        mTSnackBar.Handler.sendMessage(mTSnackBar.Handler.obtainMessage(TSnackbar.MSG_DISMISS, event, 0, mTSnackBar));
    }
}
