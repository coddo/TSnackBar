package com.androidadvance.topsnackbar;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Claudiu Codoban on 7/26/2016.
 */
public class TSnackbarManagerHandler implements Handler.Callback {
    private TSnackbarManager mTSnackbarManager;

    public TSnackbarManagerHandler(TSnackbarManager tSnackbarManager) {
        mTSnackbarManager = tSnackbarManager;
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case TSnackbarManager.MSG_TIMEOUT:
                mTSnackbarManager.handleTimeout((TSnackbarRecord) message.obj);
                return true;
        }
        return false;
    }
}
