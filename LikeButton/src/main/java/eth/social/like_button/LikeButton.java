package eth.social.like_button;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import eth.social.like_button.helpers.CircleView;
import eth.social.like_button.helpers.DotsView;
import eth.social.like_button.helpers.Utils;

/**
 * @author varun 7th July 2016
 * Modified by Lee Crey(Solomon Boloshe) Fri Jun 30, 2023
 */
public class LikeButton extends FrameLayout implements View.OnClickListener {
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private static final int INVALID_RESOURCE_ID = -1;
    private static final float DOT_VIEW_SIZE_FACTOR = 3;
    private static final float DOTS_SIZE_FACTOR = .08f;
    private static final float CIRCLE_VIEW_SIZE_FACTOR = 1.4f;

    int imageResourceIdActive = INVALID_RESOURCE_ID;
    int imageResourceIdInactive = INVALID_RESOURCE_ID;

    int imageSize;
    int dotsSize;
    int circleSize;
    int secondaryColor;
    int primaryColor;
    int activeImageTint;
    int inActiveImageTint;
    DotsView dotsView;
    CircleView circleView;
    ImageView imageView;

    boolean pressOnTouch = true;
    float animationSpeed = 1;
    boolean isChecked = false;

    private AnimatorSet animatorSet;
    private HeartEventListener listener;

    public LikeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        getStuffFromXML(attrs);
        init();
    }

    public LikeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getStuffFromXML(attrs);
        init();
    }

    public LikeButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getStuffFromXML(attrs);
        init();
    }

    void init() {
        circleSize = (int) (imageSize * CIRCLE_VIEW_SIZE_FACTOR);
        dotsSize = (int) (imageSize * DOT_VIEW_SIZE_FACTOR);

        LayoutInflater.from(getContext()).inflate(R.layout.like_view, this, true);
        circleView = findViewById(R.id.circle_view);
        circleView.setColors(secondaryColor, primaryColor);
        circleView.getLayoutParams().height = circleSize;
        circleView.getLayoutParams().width = circleSize;

        dotsView = findViewById(R.id.dots_view);
        dotsView.getLayoutParams().width = dotsSize;
        dotsView.getLayoutParams().height = dotsSize;
        dotsView.setColors(secondaryColor, primaryColor);
        dotsView.setMaxDotSize((int) (imageSize * DOTS_SIZE_FACTOR));

        imageView = findViewById(R.id.icon_view);

        imageView.getLayoutParams().height = imageSize;
        imageView.getLayoutParams().width = imageSize;
        if (imageResourceIdInactive != INVALID_RESOURCE_ID) {
            // should load inactive img first
            imageView.setImageResource(imageResourceIdInactive);
            imageView.setColorFilter(inActiveImageTint, PorterDuff.Mode.SRC_ATOP);
        } else if (imageResourceIdActive != INVALID_RESOURCE_ID) {
            imageView.setImageResource(imageResourceIdActive);
            imageView.setColorFilter(activeImageTint, PorterDuff.Mode.SRC_ATOP);
        } else {
            throw new IllegalArgumentException("One of Inactive/Active Image Resources are required!!");
        }
        setOnTouchListener();
        setOnClickListener(this);
    }

    /**
     * Call this function to start spark animation
     */
    public void playAnimation() {
        if (animatorSet != null) {
            animatorSet.cancel();
        }

        imageView.animate().cancel();
        imageView.setScaleX(0);
        imageView.setScaleY(0);
        circleView.setInnerCircleRadiusProgress(0);
        circleView.setOuterCircleRadiusProgress(0);
        dotsView.setCurrentProgress(0);

        animatorSet = new AnimatorSet();

        ObjectAnimator outerCircleAnimator = ObjectAnimator.ofFloat(circleView, CircleView.OUTER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
        outerCircleAnimator.setDuration((long) (250 / animationSpeed));
        outerCircleAnimator.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator innerCircleAnimator = ObjectAnimator.ofFloat(circleView, CircleView.INNER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
        innerCircleAnimator.setDuration((long) (200 / animationSpeed));
        innerCircleAnimator.setStartDelay((long) (200 / animationSpeed));
        innerCircleAnimator.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator starScaleYAnimator = ObjectAnimator.ofFloat(imageView, ImageView.SCALE_Y, 0.2f, 1f);
        starScaleYAnimator.setDuration((long) (350 / animationSpeed));
        starScaleYAnimator.setStartDelay((long) (250 / animationSpeed));
        starScaleYAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator starScaleXAnimator = ObjectAnimator.ofFloat(imageView, ImageView.SCALE_X, 0.2f, 1f);
        starScaleXAnimator.setDuration((long) (350 / animationSpeed));
        starScaleXAnimator.setStartDelay((long) (250 / animationSpeed));
        starScaleXAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator dotsAnimator = ObjectAnimator.ofFloat(dotsView, DotsView.DOTS_PROGRESS, 0, 1f);
        dotsAnimator.setDuration((long) (900 / animationSpeed));
        dotsAnimator.setStartDelay((long) (50 / animationSpeed));
        dotsAnimator.setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR);

        animatorSet.playTogether(
                outerCircleAnimator,
                innerCircleAnimator,
                starScaleYAnimator,
                starScaleXAnimator,
                dotsAnimator
        );

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                circleView.setInnerCircleRadiusProgress(0);
                circleView.setOuterCircleRadiusProgress(0);
                dotsView.setCurrentProgress(0);
                imageView.setScaleX(1);
                imageView.setScaleY(1);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (listener != null) {
                    listener.onEventAnimationEnd(imageView, isChecked);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationEnd(animation);
                if (listener != null) {
                    listener.onEventAnimationStart(imageView, isChecked);
                }
            }
        });

        animatorSet.start();
    }

    public void setChecked(boolean flag) {
        isChecked = flag;
        imageView.setImageResource(isChecked ? imageResourceIdActive : imageResourceIdInactive);
        imageView.setColorFilter(isChecked ? activeImageTint : inActiveImageTint, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onClick(View v) {
        if (imageResourceIdInactive != INVALID_RESOURCE_ID) {
            isChecked = !isChecked;

            imageView.setImageResource(isChecked ? imageResourceIdActive : imageResourceIdInactive);
            imageView.setColorFilter(isChecked ? activeImageTint : inActiveImageTint, PorterDuff.Mode.SRC_ATOP);

            if (animatorSet != null) {
                animatorSet.cancel();
            }
            if (isChecked) {
                circleView.setVisibility(View.VISIBLE);
                dotsView.setVisibility(VISIBLE);
                playAnimation();
            } else {
                dotsView.setVisibility(INVISIBLE);
                circleView.setVisibility(View.GONE);
            }
        } else {
            playAnimation();
        }
        if (listener != null) {
            listener.onEvent(imageView, isChecked);
        }
    }

    private void setOnTouchListener() {
        if (pressOnTouch) {
            setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imageView.animate().scaleX(0.8f).scaleY(0.8f).setDuration(150).setInterpolator(DECELERATE_INTERPOLATOR);
                        setPressed(true);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        imageView.animate().scaleX(1).scaleY(1).setInterpolator(DECELERATE_INTERPOLATOR);
                        if (isPressed()) {
                            performClick();
                            setPressed(false);
                        }
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        imageView.animate().scaleX(1).scaleY(1).setInterpolator(DECELERATE_INTERPOLATOR);
                        break;
                }
                return true;
            });
        } else {
            setOnTouchListener(null);
        }
    }

    private void getStuffFromXML(AttributeSet attr) {
        TypedArray a = getContext().obtainStyledAttributes(attr, R.styleable.LikeButton);
        imageSize = a.getDimensionPixelOffset(R.styleable.LikeButton_like_button_iconSize, Utils.dpToPx(getContext(), 50));
        imageResourceIdActive = a.getResourceId(R.styleable.LikeButton_like_button_activeImage, INVALID_RESOURCE_ID);
        imageResourceIdInactive = a.getResourceId(R.styleable.LikeButton_like_button_inActiveImage, INVALID_RESOURCE_ID);
        primaryColor = ContextCompat.getColor(getContext(), a.getResourceId(R.styleable.LikeButton_like_button_primaryColor, R.color.spark_primary_color));
        secondaryColor = ContextCompat.getColor(getContext(), a.getResourceId(R.styleable.LikeButton_like_button_secondaryColor, R.color.spark_secondary_color));
        activeImageTint = ContextCompat.getColor(getContext(), a.getResourceId(R.styleable.LikeButton_like_button_activeImageTint, R.color.spark_image_tint));
        inActiveImageTint = ContextCompat.getColor(getContext(), a.getResourceId(R.styleable.LikeButton_like_button_inActiveImageTint, R.color.spark_image_tint));
        pressOnTouch = a.getBoolean(R.styleable.LikeButton_like_button_pressOnTouch, true);
        animationSpeed = a.getFloat(R.styleable.LikeButton_like_button_animationSpeed, 1);
        // recycle typedArray
        a.recycle();
    }
}
