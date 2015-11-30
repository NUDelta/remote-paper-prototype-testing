package edu.delta.aspin.rpptglass;

import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.gesture.GestureUtils;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.ResultListener;

public class MainActivity extends Activity implements MeteorCallback {

    private static final String TAG = "RPPT MainActivity";
    private static final String METEOR_URL = "ws://10.0.2.2:3000/websocket";

    private static final Integer QR_CODE_MODE = 0;

    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupGestureDetector();
        setupMeteorClient();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan:
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, QR_CODE_MODE);
//                startActivity(new Intent(PrototypeActivity.this, CameraActivity.class));
                return true;
            case R.id.quit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    private void setupGestureDetector() {
        mGestureDetector = new GestureDetector(this);
        mGestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                switch (gesture) {
                    case TAP:
                        openOptionsMenu();
                        return true;
                    default:
                        return false;
                }
            }
        });
        mGestureDetector.setFingerListener(new GestureDetector.FingerListener() {
            @Override
            public void onFingerCountChanged(int previousCount, int currentCount) {
            }
        });
        mGestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
            @Override
            public boolean onScroll(float displacement, float delta, float velocity) {
                return true;
            }
        });
    }

    private void setupMeteorClient() {
        // TODO: setup connection on application launch + close behavior
        // see http://stackoverflow.com/questions/1396133/android-how-to-do-something-on-app-launch
//        if (MeteorSingleton.getInstance() == null) {
//            MeteorSingleton.createInstance(this, METEOR_URL);
//        } else {
//            MeteorSingleton.getInstance().reconnect();
//        }

        MeteorSingleton.createInstance(this, METEOR_URL);
        MeteorSingleton.getInstance().setCallback(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QR_CODE_MODE) {
            Log.v(TAG, data.getStringExtra("SCAN_RESULT"));
        }
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
