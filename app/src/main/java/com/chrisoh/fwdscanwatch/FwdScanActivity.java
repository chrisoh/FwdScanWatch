package com.chrisoh.fwdscanwatch;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FwdScanActivity extends Activity implements DisplayManager.DisplayListener{


    private static final String TIME_FORMAT_12 = "hh:mm";
    private static final String TIME_FORMAT_24 = "HH:mm";
    private static final String PERIOD_FORMAT = "a";
    private static final String TIMEZONE_FORMAT = "zzz";
    private static final String DATESTAMP_FORMAT = "EEE, dd MMM yyyy";
    private static final String TIMESTAMP_FORMAT = "HH:mm:ss Z";

    private final static IntentFilter intentFilter;
    static {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }

    private BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            updateTime();
        }

    };

    private boolean isDimmed = false;
    private TextView timeText;
    private TextView dateText;
    private FrameLayout main;
    private ImageView fwdScanAnimatedImageView;


    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Date date = new Date();
            TimeZone tz = TimeZone.getDefault();
            updateSeconds(date, tz);
            handler.postDelayed(this, 1000);
        }
    };

    private DisplayManager displayManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_face);

        main = (FrameLayout) findViewById(R.id.fwdScanLayout);
        timeText = (TextView) findViewById(R.id.timeTextView);
        dateText = (TextView) findViewById(R.id.dateTextView);

        Typeface watchTypeFace = Typeface.createFromAsset(getAssets(), "lcars_font.ttf");

        timeText.setTypeface(watchTypeFace);
        dateText.setTypeface(watchTypeFace);

        fwdScanAnimatedImageView = (ImageView) findViewById(R.id.fwdScanImageView);
        fwdScanAnimatedImageView.setBackgroundResource(R.drawable.anim);
        fwdScanAnimatedImageView.post(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable frameAnimation =
                        (AnimationDrawable) fwdScanAnimatedImageView.getBackground();
                frameAnimation.start();
            }
        });

        displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        displayManager.registerDisplayListener(this, null);

        registerReceiver(timeReceiver, intentFilter);

        updateTime();

        handler.post(runnable);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();  // Fix001 - Super was not called
    }

    @Override
    public void onDisplayChanged(int displayId) {
        Display display = displayManager.getDisplay(displayId);
        if(display == null) {
            // No display found for this ID, treating this as an onScreenOff() but you could remove this line
            // and swallow this exception quietly. What circumstance means 'there is no display for this id'?
            onScreenOff();
            return;
        }
        switch(display.getState()){
            case Display.STATE_DOZING:
                onScreenDim();
                break;
            case Display.STATE_OFF:
                onScreenOff();
                break;
            default:
                //  Not really sure what to so about Display.STATE_UNKNOWN, so
                //  we'll treat it as if the screen is normal.
                onScreenAwake();
                break;
        }
    }

    @Override public void onDisplayAdded(int displayId) {

    }

    @Override public void onDisplayRemoved(int displayId) {
        onWatchFaceRemoved();
    }

    public void onScreenDim() {
        isDimmed = true;

        int blue = getResources().getColor(R.color.blue);
        int black = getResources().getColor(R.color.black);

        main.setBackground(null);
        main.setBackgroundColor(black);

        timeText.setTextColor(blue);
        fwdScanAnimatedImageView.setVisibility(View.INVISIBLE);
//        period.setTextColor(silver);
//        timezone.setVisibility(View.INVISIBLE);
//        datestamp.setVisibility(View.GONE);
//        timestamp.setVisibility(View.GONE);

        handler.removeCallbacks(null);
    }

    public void onScreenAwake() {
        isDimmed = false;

        int white = getResources().getColor(R.color.white);

        main.setBackground(getResources().getDrawable(R.drawable.background));
//
//        time.setTextColor(white);
//        period.setTextColor(white);
//        timezone.setVisibility(View.VISIBLE);
//        datestamp.setVisibility(View.VISIBLE);
//        timestamp.setVisibility(View.VISIBLE);

        handler.post(runnable);
    }

    public void onScreenOff() {

    }

    public void onWatchFaceRemoved() {

    }

    private void updateTime() {
        Date date = new Date();
        TimeZone tz = TimeZone.getDefault();

        SimpleDateFormat timeSdf;
        if(DateFormat.is24HourFormat(this)) {
            timeSdf = new SimpleDateFormat(TIME_FORMAT_24);
//            period.setVisibility(View.INVISIBLE);
        } else {
            timeSdf = new SimpleDateFormat(TIME_FORMAT_12);
//            period.setVisibility(View.VISIBLE);
        }

        timeSdf.setTimeZone(tz);
        timeText.setText(timeSdf.format(date));

        SimpleDateFormat periodSdf = new SimpleDateFormat(PERIOD_FORMAT);
        periodSdf.setTimeZone(tz);
//        period.setText(periodSdf.format(date));

        SimpleDateFormat timezoneSdf = new SimpleDateFormat(TIMEZONE_FORMAT);
        timezoneSdf.setTimeZone(tz);
//        timezone.setText(tz.getDisplayName(tz.inDaylightTime(date), TimeZone.SHORT, Locale.getDefault()));

        SimpleDateFormat datestampSdf = new SimpleDateFormat(DATESTAMP_FORMAT);
        datestampSdf.setTimeZone(tz);
        dateText.setText(datestampSdf.format(date));
    }

    // this will be called by the runnable thread when the screen is awake
    private void updateSeconds(Date date, TimeZone tz) {
        SimpleDateFormat timestampSdf = new SimpleDateFormat(TIMESTAMP_FORMAT);
        timestampSdf.setTimeZone(tz);
//        timestamp.setText(timestampSdf.format(date));
    }


}