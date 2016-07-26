package com.androidadvance.topsnackbar;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by coddo on 7/25/16.
 */
public class TSnackbarCallback {

    public static final int DISMISS_EVENT_SWIPE = 0;

    public static final int DISMISS_EVENT_ACTION = 1;

    public static final int DISMISS_EVENT_TIMEOUT = 2;

    public static final int DISMISS_EVENT_MANUAL = 3;

    public static final int DISMISS_EVENT_CONSECUTIVE = 4;


    @IntDef({
            DISMISS_EVENT_SWIPE, DISMISS_EVENT_ACTION, DISMISS_EVENT_TIMEOUT,
            DISMISS_EVENT_MANUAL, DISMISS_EVENT_CONSECUTIVE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DismissEvent {
    }

    public void onDismissed(TSnackbar snackbar, @DismissEvent int event) {

    }

    public void onShown(TSnackbar snackbar) {

    }
}
