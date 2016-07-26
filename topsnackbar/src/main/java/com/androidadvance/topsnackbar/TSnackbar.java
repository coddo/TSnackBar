package com.androidadvance.topsnackbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.androidadvance.topsnackbar.adapters.TSnackbarViewOutPropertyAnimatorListenerAdapter;
import com.androidadvance.topsnackbar.adapters.TSnackbarViewInPropertyAnimatorListenerAdapter;
import com.androidadvance.topsnackbar.interfaces.ITSnackbarManagerCallback;
import com.androidadvance.topsnackbar.interfaces.IOnTSnackbarAttachStateChangeListener;
import com.androidadvance.topsnackbar.interfaces.IOnTSnackbarLayoutChangeListener;
import com.androidadvance.topsnackbar.listeners.TSnackbarCloseAnimationListener;
import com.androidadvance.topsnackbar.listeners.TSnackbarShowAnimationListener;
import com.androidadvance.topsnackbar.listeners.TSnackbarAttachStateChangeListener;
import com.androidadvance.topsnackbar.listeners.TSnackbarDismissListener;
import com.androidadvance.topsnackbar.listeners.TSnackbarLayoutChangeListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public final class TSnackbar {
    @IntDef({LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    public static final int LENGTH_INDEFINITE = -2;

    public static final int LENGTH_SHORT = -1;

    public static final int LENGTH_LONG = 0;

    public static final int ANIMATION_DURATION = 250;
    public static final int ANIMATION_FADE_DURATION = 180;

    public static final int MSG_SHOW = 0;
    public static final int MSG_DISMISS = 1;

    private static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();

    private final ViewGroup mParent;
    private final Context mContext;
    private int mDuration;

    public final TSnackbarLayout Layout;
    public final Handler Handler;
    public TSnackbarCallback TSnackbarCallback;
    public final ITSnackbarManagerCallback ManagerCallback = new TSnackbarManagerCallback(this);

    private final SwipeDismissBehavior.OnDismissListener mDismissListener = new TSnackbarDismissListener(this);
    private final IOnTSnackbarAttachStateChangeListener mAttachStateChangeListener = new TSnackbarAttachStateChangeListener(this);
    private final IOnTSnackbarLayoutChangeListener mLayoutChangeListener = new TSnackbarLayoutChangeListener(this);
    private final Animation.AnimationListener mShowAnimationListener = new TSnackbarShowAnimationListener(this);
    private final ViewPropertyAnimatorListenerAdapter mFadeInViewPropertyAnimatorListenerAdapter = new TSnackbarViewInPropertyAnimatorListenerAdapter(this);

    private TSnackbar(ViewGroup parent) {
        mParent = parent;
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        Layout = (TSnackbarLayout) inflater.inflate(R.layout.tsnackbar_layout, mParent, false);
        Handler = new Handler(Looper.getMainLooper(), new TSnackbarHandler(this));
    }

    @NonNull
    public static TSnackbar make(@NonNull View view, @NonNull CharSequence text,
                                 @Duration int duration) {
        TSnackbar snackbar = new TSnackbar(findSuitableParent(view));
        snackbar.setText(text);
        snackbar.setDuration(duration);
        return snackbar;
    }

    @NonNull
    public static TSnackbar make(@NonNull View view, @StringRes int resId, @Duration int duration) {
        return make(view, view.getResources().getText(resId), duration);
    }

    private static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;
        do {
            if (view instanceof CoordinatorLayout) {

                return (ViewGroup) view;
            } else if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {


                    return (ViewGroup) view;
                } else {

                    fallback = (ViewGroup) view;
                }
            }

            if (view != null) {

                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);


        return fallback;
    }

    @Deprecated
    public TSnackbar addIcon(int resource_id, int size) {
        final TextView tv = Layout.getMessageView();

        tv.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(Bitmap.createScaledBitmap(((BitmapDrawable) (mContext.getResources()
                .getDrawable(resource_id))).getBitmap(), size, size, true)), null, null, null);

        return this;
    }

    public TSnackbar setIconPadding(int padding) {
        final TextView tv = Layout.getMessageView();
        tv.setCompoundDrawablePadding(padding);
        return this;
    }

    public TSnackbar setIconLeft(@DrawableRes int drawableRes, float sizeDp) {
        final TextView tv = Layout.getMessageView();
        Drawable drawable = ContextCompat.getDrawable(mContext, drawableRes);
        if (drawable != null) {
            drawable = fitDrawable(drawable, (int) convertDpToPixel(sizeDp, mContext));
        } else {
            throw new IllegalArgumentException("resource_id is not a valid drawable!");
        }
        final Drawable[] compoundDrawables = tv.getCompoundDrawables();
        tv.setCompoundDrawables(drawable, compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
        return this;
    }

    public TSnackbar setIconRight(@DrawableRes int drawableRes, float sizeDp) {
        final TextView tv = Layout.getMessageView();
        Drawable drawable = ContextCompat.getDrawable(mContext, drawableRes);
        if (drawable != null) {
            drawable = fitDrawable(drawable, (int) convertDpToPixel(sizeDp, mContext));
        } else {
            throw new IllegalArgumentException("resource_id is not a valid drawable!");
        }
        final Drawable[] compoundDrawables = tv.getCompoundDrawables();
        tv.setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], drawable, compoundDrawables[3]);
        return this;
    }

    private Drawable fitDrawable(Drawable drawable, int sizePx) {
        if (drawable.getIntrinsicWidth() != sizePx || drawable.getIntrinsicHeight() != sizePx) {

            if (drawable instanceof BitmapDrawable) {

                drawable = new BitmapDrawable(mContext.getResources(), Bitmap.createScaledBitmap(getBitmap(drawable), sizePx, sizePx, true));
            }
        }
        drawable.setBounds(0, 0, sizePx, sizePx);

        return drawable;
    }

    private static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private static Bitmap getBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    @NonNull
    public TSnackbar setAction(@StringRes int resId, View.OnClickListener listener) {
        return setAction(mContext.getText(resId), listener);
    }

    @NonNull
    public TSnackbar setAction(CharSequence text, final View.OnClickListener listener) {
        final TextView tv = Layout.getActionView();

        if (TextUtils.isEmpty(text) || listener == null) {
            tv.setVisibility(View.GONE);
            tv.setOnClickListener(null);
        } else {
            tv.setVisibility(View.VISIBLE);
            tv.setText(text);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(view);

                    dispatchDismiss(TSnackbarCallback.DISMISS_EVENT_ACTION);
                }
            });
        }
        return this;
    }

    @NonNull
    public TSnackbar setActionTextColor(ColorStateList colors) {
        final TextView tv = Layout.getActionView();
        tv.setTextColor(colors);
        return this;
    }

    @NonNull
    public TSnackbar setActionTextColor(@ColorInt int color) {
        final TextView tv = Layout.getActionView();
        tv.setTextColor(color);
        return this;
    }


    @NonNull
    public TSnackbar setText(@NonNull CharSequence message) {
        final TextView tv = Layout.getMessageView();
        tv.setText(message);
        return this;
    }

    @NonNull
    public TSnackbar setText(@StringRes int resId) {
        return setText(mContext.getText(resId));
    }

    @NonNull
    public TSnackbar setDuration(@Duration int duration) {
        mDuration = duration;
        return this;
    }

    @Duration
    public int getDuration() {
        return mDuration;
    }

    @NonNull
    public View getView() {
        return Layout;
    }

    public void show() {
        TSnackbarManager.getInstance().show(mDuration, ManagerCallback);
    }

    public void dismiss() {
        dispatchDismiss(TSnackbarCallback.DISMISS_EVENT_MANUAL);
    }

    public void dispatchDismiss(@TSnackbarCallback.DismissEvent int event) {
        TSnackbarManager.getInstance().dismiss(ManagerCallback, event);
    }

    @NonNull
    public TSnackbar setCallback(TSnackbarCallback tSnackbarCallback) {
        TSnackbarCallback = tSnackbarCallback;
        return this;
    }

    public boolean isShown() {
        return TSnackbarManager.getInstance().isCurrent(ManagerCallback);
    }

    public boolean isShownOrQueued() {
        return TSnackbarManager.getInstance().isCurrentOrNext(ManagerCallback);
    }

    final void showView() {
        if (Layout.getParent() == null) {
            final ViewGroup.LayoutParams lp = Layout.getLayoutParams();

            if (lp instanceof CoordinatorLayout.LayoutParams) {
                final TSnackbarBehavior TSnackbarBehavior = new TSnackbarBehavior(ManagerCallback);
                TSnackbarBehavior.setStartAlphaSwipeDistance(0.1f);
                TSnackbarBehavior.setEndAlphaSwipeDistance(0.6f);
                TSnackbarBehavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END);
                TSnackbarBehavior.setListener(mDismissListener);

                CoordinatorLayout.LayoutParams layoutParameters = (CoordinatorLayout.LayoutParams) lp;
                layoutParameters.setBehavior(TSnackbarBehavior);
            }

            mParent.addView(Layout);
        }

        Layout.setOnAttachStateChangeListener(mAttachStateChangeListener);

        if (ViewCompat.isLaidOut(Layout)) {

            animateViewIn();
        } else {

            Layout.setOnLayoutChangeListener(mLayoutChangeListener);
        }
    }

    public void animateViewIn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.setTranslationY(Layout, -Layout.getHeight());
            ViewCompat.animate(Layout)
                    .translationY(0f)
                    .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(mFadeInViewPropertyAnimatorListenerAdapter)
                    .start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(Layout.getContext(),
                    R.anim.top_in);
            anim.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
            anim.setDuration(ANIMATION_DURATION);
            anim.setAnimationListener(mShowAnimationListener);
            Layout.startAnimation(anim);
        }
    }

    private void animateViewOut(final int event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.animate(Layout)
                    .translationY(-Layout.getHeight())
                    .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(new TSnackbarViewOutPropertyAnimatorListenerAdapter(this, event))
                    .start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(Layout.getContext(), R.anim.top_out);
            anim.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
            anim.setDuration(ANIMATION_DURATION);
            anim.setAnimationListener(new TSnackbarCloseAnimationListener(this, event));
            Layout.startAnimation(anim);
        }
    }

    final void hideView(int event) {
        if (Layout.getVisibility() != View.VISIBLE || isBeingDragged()) {
            onViewHidden(event);
        } else {
            animateViewOut(event);
        }
    }

    public void onViewHidden(int event) {
        TSnackbarManager.getInstance().onDismissed(ManagerCallback);

        if (TSnackbarCallback != null) {
            TSnackbarCallback.onDismissed(this, event);
        }

        final ViewParent parent = Layout.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(Layout);
        }
    }

    private boolean isBeingDragged() {
        final ViewGroup.LayoutParams lp = Layout.getLayoutParams();

        if (lp instanceof CoordinatorLayout.LayoutParams) {
            final CoordinatorLayout.LayoutParams cllp = (CoordinatorLayout.LayoutParams) lp;
            final CoordinatorLayout.Behavior behavior = cllp.getBehavior();

            if (behavior instanceof SwipeDismissBehavior) {
                return ((SwipeDismissBehavior) behavior).getDragState()
                        != SwipeDismissBehavior.STATE_IDLE;
            }
        }
        return false;
    }
}