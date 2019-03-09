package android.rr.apksapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, Dashboard.class));
                SplashActivity.this.finish();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        handler.postDelayed(runnable, 1500);
    }

    @Override
    protected void onStop() {
        super.onStop();

        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != handler)
            handler = null;

        if (null != runnable)
            runnable = null;
    }

}