package edu.delta.aspin.rpptglass;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.ResultListener;

public class MainActivity extends Activity implements MeteorCallback {

    private static final String TAG = "RPPT MainActivity";
    private static final String METEOR_URL = "ws://rppt.meteor.com/websocket";
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
        return mGestureDetector != null && mGestureDetector.onMotionEvent(event);
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
        if (!MeteorSingleton.hasInstance()) {
            MeteorSingleton.createInstance(this, METEOR_URL);
            MeteorSingleton.getInstance().setCallback(this);
        } else {
            MeteorSingleton.getInstance().reconnect();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final MainActivity self = this;
        if (requestCode == QR_CODE_MODE) {
            String syncCode = data.getStringExtra("SCAN_RESULT");
            Log.v(TAG, String.format("Scan result: %s", syncCode));
            MeteorSingleton.getInstance().call(
                    "getStreamData",
                    new Object[]{syncCode, "publisher"},
                    new ResultListener() {
                        @Override
                        public void onSuccess(String result) {
                            Log.v(TAG, String.format("Got result: %S", result));
                            Type type = new TypeToken<Map<String, String>>(){}.getType();
                            Gson gson = new Gson();
                            Map<String, String> credentials = gson.fromJson(result, type);

                            Intent intent = new Intent(self, StreamActivity.class);
                            intent.putExtra("key", credentials.get("key"));
                            intent.putExtra("session", credentials.get("session"));
                            intent.putExtra("token", credentials.get("token"));
                            startActivity(intent);
                        }

                        @Override
                        public void onError(String error, String reason, String details) {
                            // TODO: Handle this, usually results from no matching key
                            Log.w(TAG, String.format("Error: %s", error));
                            Log.w(TAG, String.format("Reason: %s", reason));
                            Log.w(TAG, String.format("Details: %s", details));
                        }
                    }
            );
        }
    }

    @Override
    public void onConnect(boolean signedInAutomatically) {
        Log.v(TAG, String.format("Connected to Meteor server at: %s", METEOR_URL));
    }

    @Override
    public void onDisconnect(int code, String reason) {
        Log.v(TAG, String.format("Disconnected from Meteor server at: %s. Reason: %s", METEOR_URL, reason));
    }

    @Override
    public void onDataAdded(String collectionName, String documentID, String newValuesJson) {

    }

    @Override
    public void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) {

    }

    @Override
    public void onDataRemoved(String collectionName, String documentID) {

    }

    @Override
    public void onException(Exception e) {
        Log.e(TAG, "Exception thrown: ", e);
    }
}
