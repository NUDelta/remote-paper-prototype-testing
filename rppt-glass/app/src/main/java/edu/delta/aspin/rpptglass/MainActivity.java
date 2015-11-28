package edu.delta.aspin.rpptglass;

import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.GestureDetector;

import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.ResultListener;

public class MainActivity extends Activity implements MeteorCallback {

    private static final String TAG = "RPPT MainActivity";
    private static final String METEOR_URL = "ws://10.0.2.2:3000/websocket";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v(TAG, "attemptin connection");
        MeteorSingleton.createInstance(this, METEOR_URL);
        MeteorSingleton.getInstance().setCallback(this);
    }


    @Override
    public void onConnect(boolean b) {
        Log.v(TAG, String.format("Connected to Meteor server at: %s", METEOR_URL));
        MeteorSingleton.getInstance().call(
                "getStreamData",
                new Object[]{"hello", "publisher"},
                new ResultListener() {
                    @Override
                    public void onSuccess(String s) {
                        Log.v(TAG, s); // This is JSON, requires parsing.
                    }

                    @Override
                    public void onError(String s, String s1, String s2) {

                    }
                }
        );
    }

    @Override
    public void onDisconnect(int i, String s) {
        Log.v(TAG, String.format("Disconnected from Meteor server at: %s", METEOR_URL));
    }

    @Override
    public void onDataAdded(String s, String s1, String s2) {

    }

    @Override
    public void onDataChanged(String s, String s1, String s2, String s3) {

    }

    @Override
    public void onDataRemoved(String s, String s1) {

    }

    @Override
    public void onException(Exception e) {
        Log.e(TAG, "Exception thrown: ", e);
    }
}
