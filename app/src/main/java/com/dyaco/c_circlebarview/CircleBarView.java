package com.dyaco.c_circlebarview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class CircleBarView extends View {
    private static final String TAG = CircleBarView.class.getSimpleName();

    private int mViewWidth;
    private int mViewHeight;

    private Path mFingerPath;
    private Paint mFingerPaint;
    private boolean mPaintFingerPath = false;
    private int mFingerPaintColor = Color.BLUE;

    private Bitmap mBitmap;
    private Paint mBitmapPaint;

    private float mCurrentX, mCurrentY;
    private final float TOUCH_TOLERANCE = 4;
    private boolean mTouchable = true;

    // Background part
    private Paint mBackRectanglePaint;
    private int mBackColor = Color.parseColor("#c3c3c3");


    private Paint mBackgroundColorPaint;
    private int mBarSelectSolidColor = Color.parseColor("#9AFF35");

    // Bar part
    private Canvas mCanvas;
    private Paint mBarPaint;


    // line part
    private Paint mLinePaint;
    private int mLineColor = Color.parseColor("#9D2227");
    private float mStrokeWidth = 0.4f;


    private int mBarCount = 20;
    private int mBarMaxLevel = 10;
    private int mBarMinLevel = 1;
    //    private int mBarColor = Color.parseColor("#E4002B"); // red
    private int mBarColor = Color.parseColor("#597084");

    private Bar[] mBars;

    private float mBarWidth = 30f;
    private float mSideWidth = 0f;

    private float mLevelUnitX;
    private float mLevelUnitY;

    private float mBarSpace;

    private Paint mCirclePaint;
    private float mBarCircleRadius = 15f;
    private float mBarCircleWidth = 5f;
    private int mBarSelectColor = Color.parseColor("#9D2227");

    private LevelChangedListener mLevelChangedListener;

    public CircleBarView(Context context) {
        this(context, null);
    }

    public CircleBarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        initAttr(attributeSet);

        initBars();

        initPaints();
    }


    public int getBarCount() {
        return this.mBarCount;
    }

    public int getBarMaxLevel() {
        return this.mBarMaxLevel;
    }

    public int getBarMinLevel() {
        return this.mBarMinLevel;
    }

    public boolean setBarLevel(int bar, int level) {
        if (bar > (mBarCount - 1) || bar < 0) {
            return false;
        }
        if (level > mBarMaxLevel || level < mBarMinLevel) {
            return false;
        }

        mBars[bar].setLevel(level);

        invalidate();
        return true;
    }

    public Bar[] getBars() {
        return mBars;
    }

    public void setLevelChangedListener(LevelChangedListener listener) {
        this.mLevelChangedListener = listener;
    }

    private void initAttr(AttributeSet attributeSet) {
        TypedArray typedArray = attributeSet == null ? null : getContext()
                .obtainStyledAttributes(attributeSet, R.styleable.CircleBarView);
        if (typedArray != null) {
            mPaintFingerPath = typedArray.getBoolean(R.styleable.CircleBarView_paintFingerPath, mPaintFingerPath);
            mFingerPaintColor = typedArray.getInteger(R.styleable.CircleBarView_fingerPaintColor, mFingerPaintColor);
            mTouchable = typedArray.getBoolean(R.styleable.CircleBarView_touchable, mTouchable);
            mBarCount = typedArray.getInt(R.styleable.CircleBarView_barCount, mBarCount);
            mBarMaxLevel = typedArray.getInteger(R.styleable.CircleBarView_barMaxLevel, mBarMaxLevel);
            mBarMinLevel = typedArray.getInteger(R.styleable.CircleBarView_barMinLevel, mBarMinLevel);
            mBarColor = typedArray.getColor(R.styleable.CircleBarView_barColor, mBarColor);
            mBackColor = typedArray.getColor(R.styleable.CircleBarView_backColor, mBackColor);
            mBarWidth = dp2px(typedArray.getInt(R.styleable.CircleBarView_barWidth, (int) mBarWidth));
            mSideWidth = dp2px(typedArray.getInteger(R.styleable.CircleBarView_sideWidth, (int) mSideWidth));

            mBarSelectColor = typedArray.getColor(R.styleable.CircleBarView_barSelectColor, mBarSelectColor);
            mBarSelectSolidColor = typedArray.getColor(R.styleable.CircleBarView_barSelectSolidColor, mBarSelectSolidColor);

            mLineColor = typedArray.getColor(R.styleable.CircleBarView_lineColor, mLineColor);
        }
    }

    private void initBars() {
        mBars = new Bar[mBarCount];
        for (int i = 0; i < mBars.length; i++) {
            mBars[i] = new Bar();
        }
    }

    private void initPaints() {
        mFingerPaint = new Paint();
        mFingerPaint.setAntiAlias(true);
        mFingerPaint.setDither(true);
        mFingerPaint.setColor(mFingerPaintColor);
        mFingerPaint.setStyle(Paint.Style.STROKE);
        mFingerPaint.setStrokeJoin(Paint.Join.ROUND);
        mFingerPaint.setStrokeCap(Paint.Cap.ROUND);
        mFingerPaint.setStrokeWidth(12);
//        mFingerPaint.setAlpha(200);

        mFingerPath = new Path();

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        // background paint setting
        mBackRectanglePaint = new Paint();
        mBackRectanglePaint.setStyle(Paint.Style.FILL);
        mBackRectanglePaint.setColor(mBackColor);

        mBackgroundColorPaint = new Paint();
        mBackgroundColorPaint.setStyle(Paint.Style.FILL);
        ColorDrawable background = (ColorDrawable) getBackground();
        mBackgroundColorPaint.setColor(background != null ? background.getColor() : mBarSelectSolidColor);
//        mBackgroundColorPaint.setColor(background != null ? background.getColor() : Color.WHITE);


        // bar paint setting
        mBarPaint = new Paint();
        mBarPaint.setStyle(Paint.Style.FILL);
        mBarPaint.setAntiAlias(true);
        mBarPaint.setDither(true);
        mBarPaint.setStrokeWidth(mBarCircleWidth);
        mBarPaint.setColor(mBarColor);

        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);
//        mCirclePaint.setStrokeWidth(mBarCircleWidth);
        mCirclePaint.setColor(mBarSelectColor);


        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setDither(true);
        mLinePaint.setStrokeWidth(4f);
        mLinePaint.setColor(mLineColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBars(canvas);

        // draw finger path
        if (mPaintFingerPath) {
            canvas.drawPath(mFingerPath, mFingerPaint);
        }
    }

    private void drawBars(Canvas canvas) {

        drawBackgroundCircle(canvas);

        drawLinkLine(canvas);

        for (Bar bar : mBars) {

            if (bar.getLevel() != 0) {
                canvas.drawCircle(bar.getCenterX(), bar.getCenterY(), mBarCircleRadius, mCirclePaint);
                canvas.drawCircle(bar.getCenterX(), bar.getCenterY(), mBarCircleRadius - mBarCircleWidth, mBackgroundColorPaint);
            }
        }
    }

    private void makeItBeautiful(Canvas canvas) {
        float left = 0;
        float top = mViewHeight;
        float right = mViewWidth;
        float bottom = mViewHeight - mLevelUnitY / 2;

        canvas.drawRect(left, top, right, bottom, mBackgroundColorPaint);
    }

    private void drawBackgroundRectangle(Canvas canvas, float left, float right) {
//        canvas.drawRect(left, mViewHeight, right, 0, mBackgroundPaint);

        for (int i = 0; i < mBarMaxLevel; i++) {
            float top = (mLevelUnitY / 2) + mLevelUnitY * i;
            float bottom = mLevelUnitY * i;

            canvas.drawRect(left, top, right, bottom, mBackRectanglePaint);
        }
    }

    private void drawLinkLine(Canvas canvas) {
        for (int i = 0; i < mBars.length - 1; i++) {
            Bar current = mBars[i];
            if (current.getCenterX() != 0 & current.getCenterY() != 0) {
                for (int j = i + 1; j < mBars.length; j++) {
                    Bar next = mBars[j];
                    if (next.getCenterX() != 0 & next.getCenterY() != 0) {
//                        canvas.drawLine(current.getCenterX(), current.getCenterY(), next.getCenterX(), next.getCenterY(), mBarPaint);
                        canvas.drawLine(current.getCenterX(), current.getCenterY(), next.getCenterX(), next.getCenterY(), mLinePaint);
                        break;
                    }
                }
            }
        }
    }

    //畫背景圓型
    private void drawBackgroundCircle(Canvas canvas) {

        for (int i = 0; i < mBarCount; i++) {

            Log.v("hank", "drawBackgroundCircle -> mBarCount:" + i);

            float currentX = mSideWidth + (mLevelUnitX / 2) + mLevelUnitX * i;

            for (int j = 0; j < mBarMaxLevel; j++) {
                Log.v("hank", "drawBackgroundCircle -> mBarMaxLevel:" + j);
//                float currentY = (mLevelUnitY / 2) + mLevelUnitY * (mBarMaxLevel - j);
                float currentY = (mLevelUnitY / 2) + mLevelUnitY * (mBarMaxLevel - j - 1);

                canvas.drawCircle(currentX, currentY, mBarCircleRadius, mBackRectanglePaint);
                canvas.drawCircle(currentX, currentY, mBarCircleWidth, mBarPaint);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

//        mViewWidth = w;
        mViewWidth = w - ((int) mSideWidth * 2);
        mViewHeight = h;

        mLevelUnitX = (float) mViewWidth / (float) mBarCount;
        mLevelUnitY = (float) mViewHeight / (float) mBarMaxLevel;

        mBarSpace = ((float) mViewWidth - (mBarWidth * mBarCount)) / (mBarCount - 1);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mTouchable) return false;
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }

        return true;
    }

    private void updateBar(float x, float y) {

        int whichBar = Math.max(Math.min((int) ((x - mSideWidth) / mLevelUnitX), mBarCount - 1), 0);

        // level upside down
//        int whichLevel = mBarMaxLevel - Math.max(Math.min((int) ((y / mLevelUnitY)), mBarMaxLevel), 0);
        int whichLevel = Math.max((mBarMaxLevel - Math.max(Math.min((int) ((y / mLevelUnitY)), mBarMaxLevel), 0)), mBarMinLevel); // add min level check.

//        Log.d(TAG, "bar: " + whichBar + ", level: " + whichLevel);

        mBars[whichBar].setLevel(whichLevel);

        float centerX = mSideWidth + (mLevelUnitX / 2) + mLevelUnitX * whichBar;
        float centerY = (mLevelUnitY / 2) + mLevelUnitY * (mBarMaxLevel - whichLevel);

        mBars[whichBar].setCenterX(centerX);
        mBars[whichBar].setCenterY(centerY);

        // notify listener
        if (mLevelChangedListener != null) {
            mLevelChangedListener.onLevelChanged(whichBar, whichLevel);
        }
    }

    private void touch_start(float x, float y) {
        mFingerPath.reset();
        mFingerPath.moveTo(x, y);
        mFingerPath.lineTo(x + 1, y + 1); // draw a point on start
        mCurrentX = x;
        mCurrentY = y;

        updateBar(mCurrentX, mCurrentY);
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mCurrentX);
        float dy = Math.abs(y - mCurrentY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mFingerPath.quadTo(mCurrentX, mCurrentY, (x + mCurrentX) / 2, (y + mCurrentY) / 2);
            mCurrentX = x;
            mCurrentY = y;

            updateBar(mCurrentX, mCurrentY);
        }
    }

    private void touch_up() {
//        Log.d(TAG, "Touch up.");
//        mFingerPath.lineTo(mCurrentX, mCurrentY);

        mFingerPath.reset();
    }

    private static class Bar {
        int level = 0;

        float centerX;
        float centerY;

        public Bar() {
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public float getCenterX() {
            return centerX;
        }

        public void setCenterX(float centerX) {
            this.centerX = centerX;
        }

        public float getCenterY() {
            return centerY;
        }

        public void setCenterY(float centerY) {
            this.centerY = centerY;
        }
    }

    private static float dp2px(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public interface LevelChangedListener {
        void onLevelChanged(int bar, int level);
    }
}