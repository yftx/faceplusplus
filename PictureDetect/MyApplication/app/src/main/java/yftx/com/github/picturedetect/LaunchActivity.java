package yftx.com.github.picturedetect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import yftx.com.github.picturedetect.localdetect.LocalDetectActivity;

public class LaunchActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        findViewById(R.id.picture_detect).setOnClickListener(this);
        findViewById(R.id.local_detect).setOnClickListener(this);
        findViewById(R.id.api_test).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.picture_detect:
                open(FaceDetectActivity.class);
                break;
            case R.id.local_detect:
                open(LocalDetectActivity.class);
                break;
            case R.id.api_test:
                open(ApiTestActivity.class);
        }

    }

    private void open(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}
