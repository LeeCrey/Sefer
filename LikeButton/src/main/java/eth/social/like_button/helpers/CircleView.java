package eth.social.like_button.helpers;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * @author Miroslaw Stanek on 21.12.2015.
 * Modified by Lee Crey(Solomon Boloshe) Fri Jun 30, 2023
 */
public class CircleView extends View {
    public static final Property<CircleView, Float> INNER_CIRCLE_RADIUS_PROGRESS =
            new Property<CircleView, Float>(Float.class, "innerCircleRadiusProgress") {
                @Override
                public Float get(CircleView object) {
                    return object.getInnerCircleRadiusProgress();
                }

                @Override
                public void set(CircleView object, Float value) {
                    object.setInnerCircleRadiusProgress(value);
                }
            };
    public static final Property<CircleView, Float> OUTER_CIRCLE_RADIUS_PROGRESS =
            new Property<CircleView, Float>(Float.class, "outerCircleRadiusProgress") {
                @Override
                public Float get(CircleView object) {
                    return object.getOuterCircleRadiusProgress();
                }

                @Override
                public void set(CircleView object, Float value) {
                    object.setOuterCircleRadiusProgress(value);
                }
            };
    private final ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    private final Paint circlePaint = new Paint();
    private final Paint maskPaint = new Paint();
    private int startColor = 0xFFFF5722;
    private int endColor = 0xFFFFC107;
    private Bitmap tempBitmap;
    private Canvas tempCanvas;
    private float outerCircleRadiusProgress = 0f;
    private float innerCircleRadiusProgress = 0f;
    private int maxCircleSize;

    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint.setStyle(Paint.Style.FILL);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        maxCircleSize = w / 2;
        tempBitmap = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(tempBitmap);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        tempCanvas.drawColor(0xffffff, PorterDuff.Mode.CLEAR);
        float cx = (float) getWidth() / 2;
        float cy = (float) getHeight() / 2;
        tempCanvas.drawCircle(cx, cy, outerCircleRadiusProgress * maxCircleSize, circlePaint);
        tempCanvas.drawCircle(cx, cy, innerCircleRadiusProgress * maxCircleSize, maskPaint);
        canvas.drawBitmap(tempBitmap, 0, 0, null);
    }

    public void setColors(int startColor, int endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
    }

    public float getInnerCircleRadiusProgress() {
        return innerCircleRadiusProgress;
    }

    public void setInnerCircleRadiusProgress(float innerCircleRadiusProgress) {
        this.innerCircleRadiusProgress = innerCircleRadiusProgress;
        postInvalidate();
    }

    private void updateCircleColor() {
        float colorProgress = (float) Utils.clamp(outerCircleRadiusProgress, 0.5, 1);
        colorProgress = (float) Utils.mapValueFromRangeToRange(colorProgress, 0.5f, 1f, 0f, 1f);
        this.circlePaint.setColor((Integer) argbEvaluator.evaluate(colorProgress, startColor, endColor));
    }

    public float getOuterCircleRadiusProgress() {
        return outerCircleRadiusProgress;
    }

    public void setOuterCircleRadiusProgress(float outerCircleRadiusProgress) {
        this.outerCircleRadiusProgress = outerCircleRadiusProgress;
        updateCircleColor();
        postInvalidate();
    }
}