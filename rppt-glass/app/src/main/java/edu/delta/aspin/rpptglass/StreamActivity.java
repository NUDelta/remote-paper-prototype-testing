package edu.delta.aspin.rpptglass;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;

public class StreamActivity extends Activity implements Session.SessionListener, Publisher.PublisherListener, Subscriber.SubscriberListener {

    private String KEY = "";
    private String SESSION = "";
    private String TOKEN = "";

    private Session mSession;
    private Publisher mPublisher;
    private RelativeLayout mPublisherViewContainer;

    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);
        setupGestureDetector();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        KEY = intent.getStringExtra("key");
        SESSION = intent.getStringExtra("session");
        TOKEN = intent.getStringExtra("token");

        mPublisherViewContainer = (RelativeLayout) findViewById(R.id.publisher_view);
        if (mSession == null) {
            mSession = new Session(this, KEY, SESSION);
            mSession.setSessionListener(this);
            mSession.connect(TOKEN);
        }

        // TODO: Handle stream deletion
        // TODO: Add pausing stream
        // TODO: Handle closing Glass app
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stream, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pause:

                return true;
            case R.id.quit:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    @Override
    public void onConnected(Session session) {
        if (mPublisher == null) {
            mPublisher = new Publisher(this, "publisher");
            mPublisher.setPublisherListener(this);

            mPublisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(480, 320);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            mPublisherViewContainer.addView(mPublisher.getView(), layoutParams);

            mSession.publish(mPublisher);
        }
    }

    @Override
    public void onDisconnected(Session session) {
        if (mPublisher != null) {
            mPublisherViewContainer.removeView(mPublisher.getView());
        }

        mPublisher = null;
        mSession = null;
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {

    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {

    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

    }

    @Override
    public void onConnected(SubscriberKit subscriberKit) {

    }

    @Override
    public void onDisconnected(SubscriberKit subscriberKit) {

    }

    @Override
    public void onError(SubscriberKit subscriberKit, OpentokError opentokError) {

    }
}
