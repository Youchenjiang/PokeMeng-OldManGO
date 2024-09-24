package com.PokeMeng.OldManGO.Challenge;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.PokeMeng.OldManGO.R;

//CircularProgressBar：https://blog.csdn.net/qq_38436214/article/details/130078072
public class ChallengeNowProgressBar extends View {
    int mRadius;    //半徑
    int mStrokeWidth;   //進度條寬度
    int mProgressbarBgColor;    //進度條背景顏色
    int mProgressColor; //進度條進度顏色
    int mStartAngle = -90;    //開始角度
    float mCurrentAngle = -90;    //當前角度
    int mEndAngle = 360;    //結束角度
    float mMaxProgress; //最大進度
    float mCurrentProgress; //當前進度
    String mText;   //文字
    int mTextColor; //文字顏色
    float mTextSize;    //文字大小
    long mDuration = 2000;  //動畫的執行時長
    boolean isAnimation = true;    //是否執行動畫
    RectF rectF;

    public ChallengeNowProgressBar(Context context) {
        this(context, null);
    }

    public ChallengeNowProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    TypedArray array;
    public ChallengeNowProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        try {
            array= context.obtainStyledAttributes(attrs, R.styleable.ChallengeNowProgressBar);
        }catch (Exception e){
            Log.e("CircularProgressBar",e.toString());
        }
        mRadius = array.getDimensionPixelSize(R.styleable.ChallengeNowProgressBar_radius, 80);
        mStrokeWidth = array.getDimensionPixelSize(R.styleable.ChallengeNowProgressBar_strokeWidth, 8);
        mProgressbarBgColor = array.getColor(R.styleable.ChallengeNowProgressBar_progressbarBackgroundColor, ContextCompat.getColor(context, R.color.orange));
        mProgressColor = array.getColor(R.styleable.ChallengeNowProgressBar_progressbarColor, ContextCompat.getColor(context, R.color.gray));
        mMaxProgress = array.getInt(R.styleable.ChallengeNowProgressBar_maxProgress, 100);
        mCurrentProgress = array.getInt(R.styleable.ChallengeNowProgressBar_progress, 0);
        String text = array.getString(R.styleable.ChallengeNowProgressBar_text);
        mText = text == null ? "" : text;
        mTextColor = array.getColor(R.styleable.ChallengeNowProgressBar_textColor, ContextCompat.getColor(context, R.color.black));
        mTextSize = array.getDimensionPixelSize(R.styleable.ChallengeNowProgressBar_textSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
        array.recycle();
        rectF = new RectF();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 0;
        int height = 0;
        // Calculate width based on widthMeasureSpec
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST: // wrap_content
                width = mRadius * 2 + mStrokeWidth * 2; // Add stroke width to ensure the entire circle is visible
                break;
            case MeasureSpec.EXACTLY: // match_parent or specific dp
                width = MeasureSpec.getSize(widthMeasureSpec);
                break;
        }
        // Calculate height based on heightMeasureSpec
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST: // wrap_content
                height = mRadius * 2 + mStrokeWidth * 2; // Add stroke width to ensure the entire circle is visible
                break;
            case MeasureSpec.EXACTLY: // match_parent or specific dp
                height = MeasureSpec.getSize(heightMeasureSpec);
                break;
        }
        // Set the measured width and height
        setMeasuredDimension(width, height);
    }
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        int centerX = getWidth() / 2;
        rectF.left = mStrokeWidth;
        rectF.top = mStrokeWidth;
        rectF.right = centerX * 2 - mStrokeWidth;
        rectF.bottom = centerX * 2 - mStrokeWidth;

        //繪製進度條背景
        drawProgressbarBg(canvas, rectF);
        //繪製進度
        drawProgress(canvas, rectF);
        //繪製中心文本
        drawCenterText(canvas, centerX);
    }
    private void drawProgressbarBg(Canvas canvas, RectF rectF) {    //繪製進度條背景
        Paint mPaint = new Paint();
        //畫筆的填充樣式，Paint.Style.STROKE 描邊
        mPaint.setStyle(Paint.Style.STROKE);
        //圓弧的寬度
        mPaint.setStrokeWidth(mStrokeWidth);
        //抗鋸齒
        mPaint.setAntiAlias(true);
        //畫筆的顏色
        mPaint.setColor(mProgressbarBgColor);
        //畫筆的樣式 Paint.Cap.Round 圓形
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        //開始畫圓弧
        canvas.drawArc(rectF, mStartAngle, mEndAngle, false, mPaint);
    }
    private void drawProgress(Canvas canvas, RectF rectF) { //繪製進度
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mStrokeWidth);
        paint.setColor(mProgressColor);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        if (!isAnimation) {
            mCurrentAngle = 360 * (mCurrentProgress / mMaxProgress);
        }
        canvas.drawArc(rectF, mStartAngle, mCurrentAngle, false, paint);
    }
    private void drawCenterText(Canvas canvas, int centerX) {   //繪製中心文字
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mTextColor);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(mTextSize);
        paint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.edukai5));

        String[] lines = mText.split("\n"); // Split text into lines
        Rect textBounds = new Rect();
        paint.getTextBounds(mText, 0, mText.length(), textBounds);
        int lineHeight = textBounds.height(); // Height of a single line
        float totalTextHeight = lineHeight * lines.length; // Total height of all lines

        // Calculate the Y position for the first line, to center the text block vertically
        float startY = (getHeight() - totalTextHeight) / 2 + lineHeight;

        for (String line : lines) {
            canvas.drawText(line, centerX, startY, paint);
            startY += lineHeight; // Move to the next line position
        }
    }
    public void setProgress(float progress) {   //設置當前進度
        if (progress < 0) {
            throw new IllegalArgumentException("Progress value can not be less than 0");
        }
        if (progress > mMaxProgress) {
            progress = mMaxProgress;
        }
        mCurrentProgress = progress;
        mCurrentAngle = 360 * (mCurrentProgress / mMaxProgress);
        int start = 0;
        setAnimator(start, mCurrentAngle);
    }
    public void setText(String text) {  //設置文本
        mText = text;
    }
    public void setDuration(long duration) {   //設置動畫的執行時長
        if (duration <= 0) throw new IllegalArgumentException("Duration value can not be less than 0");
        mDuration = duration;
    }
    public void setTextColor(int color) {   //設置文本的顏色
        if (color <= 0) {
            throw new IllegalArgumentException("Color value can not be less than 0");
        }
        mTextColor = color;
    }
    public void setTextSize(float textSize) {   //設置文本的大小
        if (textSize <= 0) {
            throw new IllegalArgumentException("textSize can not be less than 0");
        }
        mTextSize = textSize;
    }

    private void setAnimator(float start, float target) {   //設置動畫 @param start  開始位置 @param target 結束位置
        isAnimation = true;
        ValueAnimator animator = ValueAnimator.ofFloat(start, target);
        animator.setDuration(mDuration);
        animator.setTarget(mCurrentAngle);
        //動畫更新監聽
        animator.addUpdateListener(valueAnimator -> {
            mCurrentAngle = (float) valueAnimator.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }
}