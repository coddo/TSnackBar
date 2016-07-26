package com.androidadvance.topsnackbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidadvance.topsnackbar.interfaces.IOnTSnackbarAttachStateChangeListener;
import com.androidadvance.topsnackbar.interfaces.IOnTSnackbarLayoutChangeListener;

/**
 * Created by coddo on 7/25/16.
 */
public class TSnackbarLayout extends LinearLayout {
    private TextView mMessageView;
    private Button mActionView;

    private int mMaxWidth;
    private int mMaxInlineActionWidth;

    private IOnTSnackbarLayoutChangeListener mIOnTSnackbarLayoutChangeListener;
    private IOnTSnackbarAttachStateChangeListener mIOnTSnackbarAttachStateChangeListener;

    public TSnackbarLayout(Context context) {
        this(context, null);
    }

    public TSnackbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SnackbarLayout);
        mMaxWidth = a.getDimensionPixelSize(R.styleable.SnackbarLayout_android_maxWidth, -1);
        mMaxInlineActionWidth = a.getDimensionPixelSize(
                R.styleable.SnackbarLayout_maxActionInlineWidth, -1);
        if (a.hasValue(R.styleable.SnackbarLayout_elevation)) {
            ViewCompat.setElevation(this, a.getDimensionPixelSize(
                    R.styleable.SnackbarLayout_elevation, 0));
        }
        a.recycle();

        setClickable(true);


        LayoutInflater.from(context)
                .inflate(R.layout.tsnackbar_layout_include, this);

        ViewCompat.setAccessibilityLiveRegion(this,
                ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMessageView = (TextView) findViewById(R.id.snackbar_text);
        mActionView = (Button) findViewById(R.id.snackbar_action);
    }

    TextView getMessageView() {
        return mMessageView;
    }

    Button getActionView() {
        return mActionView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mMaxWidth > 0 && getMeasuredWidth() > mMaxWidth) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        final int multiLineVPadding = getResources().getDimensionPixelSize(
                R.dimen.design_snackbar_padding_vertical_2lines);
        final int singleLineVPadding = getResources().getDimensionPixelSize(
                R.dimen.design_snackbar_padding_vertical);
        final boolean isMultiLine = mMessageView.getLayout()
                .getLineCount() > 1;

        boolean remeasure = false;
        if (isMultiLine && mMaxInlineActionWidth > 0
                && mActionView.getMeasuredWidth() > mMaxInlineActionWidth) {
            if (updateViewsWithinLayout(VERTICAL, multiLineVPadding,
                    multiLineVPadding - singleLineVPadding)) {
                remeasure = true;
            }
        } else {
            final int messagePadding = isMultiLine ? multiLineVPadding : singleLineVPadding;
            if (updateViewsWithinLayout(HORIZONTAL, messagePadding, messagePadding)) {
                remeasure = true;
            }
        }

        if (remeasure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void animateChildrenIn(int delay, int duration) {
        ViewCompat.setAlpha(mMessageView, 0f);
        ViewCompat.animate(mMessageView)
                .alpha(1f)
                .setDuration(duration)
                .setStartDelay(delay)
                .start();

        if (mActionView.getVisibility() == VISIBLE) {
            ViewCompat.setAlpha(mActionView, 0f);
            ViewCompat.animate(mActionView)
                    .alpha(1f)
                    .setDuration(duration)
                    .setStartDelay(delay)
                    .start();
        }
    }

    public void animateChildrenOut(int delay, int duration) {
        ViewCompat.setAlpha(mMessageView, 1f);
        ViewCompat.animate(mMessageView)
                .alpha(0f)
                .setDuration(duration)
                .setStartDelay(delay)
                .start();

        if (mActionView.getVisibility() == VISIBLE) {
            ViewCompat.setAlpha(mActionView, 1f);
            ViewCompat.animate(mActionView)
                    .alpha(0f)
                    .setDuration(duration)
                    .setStartDelay(delay)
                    .start();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && mIOnTSnackbarLayoutChangeListener != null) {
            mIOnTSnackbarLayoutChangeListener.onLayoutChange(this, l, t, r, b);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mIOnTSnackbarAttachStateChangeListener != null) {
            mIOnTSnackbarAttachStateChangeListener.onViewAttachedToWindow(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mIOnTSnackbarAttachStateChangeListener != null) {
            mIOnTSnackbarAttachStateChangeListener.onViewDetachedFromWindow(this);
        }
    }

    public void setOnLayoutChangeListener(IOnTSnackbarLayoutChangeListener IOnTSnackbarLayoutChangeListener) {
        mIOnTSnackbarLayoutChangeListener = IOnTSnackbarLayoutChangeListener;
    }

    public void setOnAttachStateChangeListener(IOnTSnackbarAttachStateChangeListener listener) {
        mIOnTSnackbarAttachStateChangeListener = listener;
    }

    private boolean updateViewsWithinLayout(final int orientation,
                                            final int messagePadTop, final int messagePadBottom) {
        boolean changed = false;
        if (orientation != getOrientation()) {
            setOrientation(orientation);
            changed = true;
        }
        if (mMessageView.getPaddingTop() != messagePadTop
                || mMessageView.getPaddingBottom() != messagePadBottom) {
            updateTopBottomPadding(mMessageView, messagePadTop, messagePadBottom);
            changed = true;
        }
        return changed;
    }

    private static void updateTopBottomPadding(View view, int topPadding, int bottomPadding) {
        if (ViewCompat.isPaddingRelative(view)) {
            ViewCompat.setPaddingRelative(view,
                    ViewCompat.getPaddingStart(view), topPadding,
                    ViewCompat.getPaddingEnd(view), bottomPadding);
        } else {
            view.setPadding(view.getPaddingLeft(), topPadding,
                    view.getPaddingRight(), bottomPadding);
        }
    }
}
