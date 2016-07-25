package com.androidadvance.topsnackbar.interfaces;

import android.view.View;

/**
 * Created by coddo on 7/25/16.
 */
public interface IOnAttachStateChangeListener {
    void onViewAttachedToWindow(View v);

    void onViewDetachedFromWindow(View v);
}
