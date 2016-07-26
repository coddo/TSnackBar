package com.androidadvance.topsnackbar;

import com.androidadvance.topsnackbar.interfaces.ITSnackbarManagerCallback;

import java.lang.ref.WeakReference;

/**
 * Created by Claudiu Codoban on 7/26/2016.
 */
public class TSnackbarRecord {
    public final WeakReference<ITSnackbarManagerCallback> Callback;
    public int Duration;

    TSnackbarRecord(int duration, ITSnackbarManagerCallback tSnackbarManagerCallback) {
        this.Callback = new WeakReference<>(tSnackbarManagerCallback);
        this.Duration = duration;
    }

    boolean isSnackbar(ITSnackbarManagerCallback ITSnackbarManagerCallback) {
        return ITSnackbarManagerCallback != null && this.Callback.get() == ITSnackbarManagerCallback;
    }
}
