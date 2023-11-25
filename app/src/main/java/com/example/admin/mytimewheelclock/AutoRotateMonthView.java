package com.example.admin.mytimewheelclock;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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

import com.example.admin.mytimewheelclock.utils.DateUtils;
import com.example.admin.mytimewheelclock.utils.SizeUtils;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class AutoRotateMonthView extends View {

    private Context mContext;
    private Paint mPaint, mSelectedPaint;
    private float mDayDegree;
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
    private String monthArray[] = new String[12];
    private Timer mTimer = new Timer();
    private int circleRadius = 0;
    private DecimalFormat format = new DecimalFormat("#");
    private DecimalFormat decimalformat = new DecimalFormat("#0.00");
    @SuppressLint("HandlerLeak")
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
        ValueAnimator rotation_15A = ObjectAnimator.ofFloat(this, "rotation", 360 - startDegrees, 360 - mDayDegree);
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

    public AutoRotateMonthView(Context context) {
        super(context);
        this.mContext = context;
        initPainter();
    }

    public AutoRotateMonthView(Context context, AttributeSet attrs) {
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
        isShowHalf=ta.getBoolean(R.styleable.TimeView_isShowHalf,true);
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

    /**
     * 是否显示一半
     */
    private boolean isShowHalf=true;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = SizeUtils.measureSize(mContext, widthMeasureSpec);
        int sizeH = SizeUtils.measureSize(mContext, heightMeasureSpec);
        setMeasuredDimension(sizeH, sizeH);
        if (isShowHalf)setTranslationX(-size / 2);
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
        if (isShowHalf)drawCirclePoint(canvas);
        //画时、分、秒针数字
        drawHourNumber(canvas);
    }


    /**
     * 月的时间轴一次转动的角度
     */
    private float mDayFirstDegree = 12.0f;

    /**
     * 当月有多少天
     */
    private int currentMonthAllDayCount = 30;

    /**
     * @param day                     第几天
     * @param currentMonthAllDayCount 当月有多少天
     */
    public AutoRotateMonthView setTime(int day, int currentMonthAllDayCount) {
        if (currentMonthAllDayCount > 31 || currentMonthAllDayCount < 28) {
            Toast.makeText(getContext(), "月份-不合法", Toast.LENGTH_SHORT).show();
            return this;
        }
        this.currentMonthAllDayCount = currentMonthAllDayCount;
        for (int i = 0; i < monthArray.length; i++) {
            int currentDay = day + i;
            currentDay = currentDay > 12 ? currentDay - 12 : currentDay;
            monthArray[i] = (currentDay + "月");
        }
        mDayFirstDegree = Float.parseFloat(decimalformat.format((360.0f / monthArray.length)));
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
            if (mDayDegree == 360) {
                mDayDegree = 0;
            }
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            int seconds = calendar.get(Calendar.SECOND);
            // 最后一天例如 2019年12月31日23点0分0秒
            if (day == currentMonthAllDayCount && hours == 23 && seconds == 59 && minutes == 59) {//到达了一天了
                startDegrees = mDayDegree;
                if (month == 12) {
                    //改变年
                    if (changeTimeListener != null) {
                        changeTimeListener.changeYear(year + 1);
                    }
                }
                //判断下一个月的共有多少天，和本月的不同就更新天数时间轴
                if (month < 12) {
                    int count0 = DateUtils.getDays(year, month);
                    int count1 = DateUtils.getDays(year, month + 1);
                    if (count0 != count1) {
                        //改变年
                        if (changeTimeListener != null) {
                            changeTimeListener.changeDay(count1);
                        }
                    }
                }
                mDayDegree = mDayDegree + mDayFirstDegree;
                mhandler.sendEmptyMessage(0);
            }

        }
    };


    /**
     * 画小时 数字
     *
     * @param canvas
     */
    private void drawHourNumber(Canvas canvas) {
        if (isDrawText) {
            if (TextUtils.isEmpty(monthArray[0])) return;
            //旋转圆盘
            canvas.save();
            mPaint.setColor(textColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(1);
            mPaint.setTextSize(textSize);
            canvas.translate(getWidth() / 2, getHeight() / 2);
            float currentDegree = 0;
            for (int i = 0; i < monthArray.length; i++) {
                Rect textBound = new Rect();//创建一个矩形
                String text = monthArray[i];
                //将文字装在上面创建的矩形中，即这个矩形就是文字的边框
                mPaint.getTextBounds(text, 0, text.length(), textBound);
                //当前文字 绘制的水平角度
                float curAllDegree = Float.parseFloat(format.format((currentDegree + 360 - mDayDegree)));
                if (isSetSelectedText && curAllDegree % 360 == 0) {
                    canvas.drawText(text, getMaxTextWidth("2019年") + drawNumberSpace, textBound.height() / 2, mSelectedPaint);
                } else {
                    canvas.drawText(text, getMaxTextWidth("2019年") + drawNumberSpace, textBound.height() / 2, mPaint);
                }
                canvas.rotate(mDayFirstDegree);
                currentDegree = currentDegree + mDayFirstDegree;
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

    //----------------------------对外开放的接口---------------------------------
    public interface OnChangeTimeListener {
        void changeYear(int year);

        void changeDay(int count);
    }

    public OnChangeTimeListener changeTimeListener;

    public void setChangeTimeListener(OnChangeTimeListener changeTimeListener) {
        this.changeTimeListener = changeTimeListener;
    }
}
