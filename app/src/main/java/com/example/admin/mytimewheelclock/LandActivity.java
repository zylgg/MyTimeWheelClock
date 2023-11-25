package com.example.admin.mytimewheelclock;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.example.admin.mytimewheelclock.utils.DateUtils;
import com.example.admin.mytimewheelclock.utils.SizeUtils;

import java.util.Calendar;

public class LandActivity extends AppCompatActivity {


    private AutoRotateSecondView timeView_second;
    private AutoRotateMinuteView timeView_minute;
    private AutoRotateHoursView timeView_hours;
    private AutoRotateWeekView timeView_week;
    private AutoRotateDayView timeView_day;
    private AutoRotateMonthView timeView_month;
    private TextView timeView_year;
    private View v_dividing2;
    private Button bt_skip_portrait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);

        timeView_second = (AutoRotateSecondView) findViewById(R.id.timeView_second);
        timeView_minute = (AutoRotateMinuteView) findViewById(R.id.timeView_minute);
        timeView_hours = (AutoRotateHoursView) findViewById(R.id.timeView_hours);
        timeView_week = findViewById(R.id.timeView_week);
        timeView_day = (AutoRotateDayView) findViewById(R.id.timeView_day);
        timeView_month = (AutoRotateMonthView) findViewById(R.id.timeView_month);
        timeView_year = (TextView) findViewById(R.id.timeView_year);
        v_dividing2 = findViewById(R.id.v_dividing2);
        bt_skip_portrait=findViewById(R.id.bt_skip_portrait);

        initListener();
        setData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timeView_second.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                timeView_second.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    private int getMaxTextWidth(String maxWText) {
      Paint mPaint=new Paint();
        mPaint.setColor(Color.parseColor("#999999"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);
        mPaint.setTextSize( SizeUtils.dp2px(this, 12));
        Rect textBound = new Rect();//创建一个矩形
        mPaint.getTextBounds(maxWText, 0, maxWText.length(), textBound);
        return textBound.width();
    }

    private void initListener() {
        bt_skip_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LandActivity.this,MainActivity.class));
            }
        });
        //设置因月份的改变，改变年和天的监听
        timeView_month.setChangeTimeListener(new AutoRotateMonthView.OnChangeTimeListener() {
            @Override
            public void changeYear(final int year) {
                timeView_year.post(new Runnable() {
                    @Override
                    public void run() {
                        timeView_year.setText("" + year);
                    }
                });
            }

            @Override
            public void changeDay(final int count) {
                timeView_day.post(new Runnable() {
                    @Override
                    public void run() {
                        timeView_day.setTime(1, count);
                    }
                });
            }
        });
        //秒钟绘制完后，设置水平渐变条的宽度
        final ViewTreeObserver viewTreeObserver = timeView_second.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.removeOnPreDrawListener(this);
                }
                ViewGroup.LayoutParams layoutParams = v_dividing2.getLayoutParams();
                layoutParams.width = timeView_second.getViewMaxWidth();
                v_dividing2.setLayoutParams(layoutParams);
                return true;
            }
        });
    }

    private void setData() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

//        Log.i("Calendar_years", "-" + year);
//        Log.i("Calendar_month", "-" + month);
        Log.i("Calendar_week", "-" + week);
//        Log.i("Calendar_day", "-" + day);
//        Log.i("Calendar_hours", "-" + hours);
//        Log.i("Calendar_minutes", "-" + minutes);
//        Log.i("Calendar_seconds", "-" + seconds);

        timeView_second.setTime(seconds).start();
        timeView_minute.setTime(minutes).start();
        timeView_hours.setTime(hours).start();
        timeView_week.setTime(week).start();
        timeView_day.setTime(day, DateUtils.getDays(year, month)).start();
        timeView_month.setTime(month, DateUtils.getDays(year, month)).start();
    }

}