package com.androidadvance.topsnackbar;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Claudiu Codoban on 7/26/2016.
 */
public class TSnackbarHandler implements Handler.Callback {
    private TSnackbar mTSnackbar;

    public TSnackbarHandler(TSnackbar tSnackbar) {
        mTSnackbar = tSnackbar;
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message.what == mTSnackbar.MSG_SHOW) {
            mTSnackbar.showView();
            return true;
        }

        if (message.what == mTSnackbar.MSG_DISMISS) {
            mTSnackbar.hideView(message.arg1);
            return true;
        }

        return false;
    }
}
