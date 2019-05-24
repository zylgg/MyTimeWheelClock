package com.example.admin.mytimewheelclock;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import com.example.admin.mytimewheelclock.utils.SizeUtils;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class AutoRotateMinuteView extends View {

    private Context mContext;
    private Paint mPaint, mSelectedPaint;
    private float mMinDegree;
    private int borderColor;
    private int textColor;
    private float textSize;
    private int centerPointColor;
    private float centerPointSize;
    private float borderWidth;
    private float centerPointRadiu;
    private String centerPointType = "circle";
    private int sleepTime;
    private boolean isDrawText;
    private String minuteArray[] = new String[60];
    private Timer mTimer = new Timer();
    private int circleRadius = 0;
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startRotation();
        }
    };

    /**
     * 是否设置选中文字，
     */
    private boolean isSetSelectedText = true;

    private void startRotation() {
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator rotation_15A = ObjectAnimator.ofFloat(this, "rotation", 360 - startDegrees, 360 - mMinDegree);
        rotation_15A.setInterpolator(new AccelerateInterpolator());
        rotation_15A.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedFraction = animation.getAnimatedFraction();
                //滚动一段后，就将上一个选中的文字置灰
                if (animatedFraction < 0.5 && animatedFraction >= 0.4) {
                    isSetSelectedText = false;
                    invalidate();
                }
            }
        });
        rotation_15A.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //结束滚动后，当前的文字设置选中
                isSetSelectedText = true;
                invalidate();
            }

        });
        animatorSet.play(rotation_15A);
        animatorSet.start();
    }

    public AutoRotateMinuteView(Context context) {
        super(context);
        this.mContext = context;
        initPainter();
    }

    public AutoRotateMinuteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(context, attrs);
        initPainter();
    }

    /**
     * 初始化各参数
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TimeView);
        borderColor = ta.getColor(R.styleable.TimeView_borderColor, Color.WHITE);
        borderWidth = ta.getDimension(R.styleable.TimeView_borderWidth,
                SizeUtils.dp2px(context, 1));
        textColor = ta.getColor(R.styleable.TimeView_textColor, Color.parseColor("#999999"));
        textSize = ta.getDimension(R.styleable.TimeView_textSize,
                SizeUtils.dp2px(context, 12));
        isDrawText = ta.getBoolean(R.styleable.TimeView_isDrawText, true);
        centerPointColor = ta.getColor(R.styleable.TimeView_centerPointColor, Color.WHITE);
        centerPointSize = ta.getDimension(R.styleable.TimeView_centerPointSize,
                SizeUtils.dp2px(context, 5));
        centerPointRadiu = ta.getDimension(R.styleable.TimeView_centerPointRadiu,
                SizeUtils.dp2px(context, 2));
        centerPointType = ta.getString(R.styleable.TimeView_centerPointType);
        if (TextUtils.isEmpty(centerPointType)) {
            centerPointType = "circle";
        }
        sleepTime = 1000;
        ta.recycle();
    }

    /**
     * 初始化画笔
     */
    private void initPainter() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        //文字选中的画笔
        mSelectedPaint = new Paint();
        mSelectedPaint.setAntiAlias(true);
        mSelectedPaint.setColor(Color.WHITE);
        mSelectedPaint.setStyle(Paint.Style.FILL);
        mSelectedPaint.setStrokeWidth(1);
        mSelectedPaint.setTextSize(textSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = SizeUtils.measureSize(mContext, widthMeasureSpec);
        int sizeH = SizeUtils.measureSize(mContext, heightMeasureSpec);
        setMeasuredDimension(sizeH, sizeH);
        setTranslationX(-size / 2);
    }

    /**
     * 绘制（时、分、秒）数字之间的间隔
     */
    private static int drawNumberSpace = 0;
    @Override
    protected void onDraw(Canvas canvas) {
        drawNumberSpace = SizeUtils.dp2px(getContext(), 12);
        circleRadius = getWidth() / 3;
        //画外边框
//        mPaint.setColor(borderColor);
//        mPaint.setStrokeWidth(2);
//        mPaint.setStyle(Paint.Style.STROKE);
//        Path path = new Path();
//        path.lineTo(getWidth(), 0);
//        path.lineTo(getWidth(), getHeight());
//        path.lineTo(0, getHeight());
//        path.close();
//        canvas.drawPath(path, mPaint);
        //外圆边界
//        drawCircleOut(canvas);
        //圆心
//        drawCirclePoint(canvas);
        //画时、分、秒针数字
        drawMinuteNumber(canvas);
    }

    /**
     * 应用打开初始化时间（例如1：30：30）
     *
     * @param min
     */
    public AutoRotateMinuteView setTime(int min) {
        if (min >= 60 || min < 0) {
            Toast.makeText(getContext(), "分-不合法", Toast.LENGTH_SHORT).show();
            return this;
        }

        for (int i = 0; i < minuteArray.length; i++) {
            int currentMinute = min + i;
            currentMinute = currentMinute >= 60 ? currentMinute - 60 : currentMinute;
            minuteArray[i] = (currentMinute + "分");
        }
        invalidate();
        return this;
    }

    /**
     * 时钟走起
     */
    public void start() {
        mTimer.schedule(task, 0, sleepTime);
    }

    public float startDegrees = 0;
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (mMinDegree == 360) {
                mMinDegree = 0;
            }

            Calendar calendar = Calendar.getInstance();
            int seconds = calendar.getTime().getSeconds();
            if (seconds == 0) {//到达了一分钟了
                startDegrees = mMinDegree;
                mMinDegree = mMinDegree + 6;
                mhandler.sendEmptyMessage(0);
            }
        }
    };


    /**
     * 画分钟 数字
     *
     * @param canvas
     */
    private void drawMinuteNumber(Canvas canvas) {
        if (isDrawText) {
            if (TextUtils.isEmpty(minuteArray[0])) return;
            //旋转圆盘
            canvas.save();
            mPaint.setColor(textColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(1);
            mPaint.setTextSize(textSize);
            canvas.translate(getWidth() / 2, getHeight() / 2);
            int currentDegree = 0;
            for (int i = 0; i < minuteArray.length; i++) {
                Rect textBound = new Rect();//创建一个矩形
                String text = minuteArray[i];
                //将文字装在上面创建的矩形中，即这个矩形就是文字的边框
                mPaint.getTextBounds(text, 0, text.length(), textBound);
                if (isSetSelectedText && (currentDegree + 360 - mMinDegree) % 360 == 0) {
                    canvas.drawText(text, circleRadius, textBound.height() / 2, mSelectedPaint);
                } else {
                    canvas.drawText(text, circleRadius, textBound.height() / 2, mPaint);
                }
                canvas.rotate(6);
                currentDegree = currentDegree + 6;
            }
            canvas.restore();
        }
    }

    private int getMaxTextWidth(String maxWText) {
        mPaint.setColor(textColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);
        mPaint.setTextSize(textSize);
        Rect textBound = new Rect();//创建一个矩形
        mPaint.getTextBounds(maxWText, 0, maxWText.length(), textBound);
        return textBound.width();
    }


    /**
     * 画圆圈
     *
     * @param canvas
     */
    private void drawCircleOut(Canvas canvas) {
        //半径是宽度的1/3
        mPaint.setColor(borderColor);
        mPaint.setStrokeWidth(borderWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextSize(textSize);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, circleRadius, mPaint);
    }


    /**
     * 画圆心
     *
     * @param canvas
     */
    private void drawCirclePoint(Canvas canvas) {
        mPaint.setColor(centerPointColor);
        mPaint.setStrokeWidth(centerPointSize);
        if (centerPointType.equals("rect")) {
            canvas.drawPoint(getWidth() / 2, getHeight() / 2, mPaint);
        } else if (centerPointType.equals("circle")) {
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, centerPointRadiu, mPaint);
        }
    }


}
