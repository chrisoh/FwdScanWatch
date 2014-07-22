package com.chrisoh.fwdscanwatch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.TextView;

import coh.wear.lib.GifWebView;

public class FwdScanActivity  extends Activity {

    private static final String TAG = "FaceActivity";

    private TextView mTextView;
    private GifWebView fwdScanView;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fwd_scan);

        fwdScanView = new GifWebView(this, "file:///res/drawable/animated_fwd_scan.gif");


//        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
//        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
//            @Override
//            public void onLayoutInflated(WatchViewStub stub) {
//                mTextView = (TextView) stub.findViewById(R.id.text);
//               //Log.d(TAG, "TextView: " + mTextView.getText() + " view=" + mTextView);
//            }
//        });


    }
}