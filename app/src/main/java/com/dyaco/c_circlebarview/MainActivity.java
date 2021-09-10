package com.dyaco.c_circlebarview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private CircleBarView circleBarView;
    private TextView tvBar, tvLevel;
    private String TAG = "hank";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        circleBarView = findViewById(R.id.circleBarView);
        tvBar = findViewById(R.id.tvBar);
        tvLevel = findViewById(R.id.tvLevel);


        circleBarView.setLevelChangedListener(new CircleBarView.LevelChangedListener() {
            @Override
            public void onLevelChanged(int bar, int level) {
                Log.v(TAG, "onLevelChanged() bar:" + bar + ", level:" + level);
                tvBar.setText(String.valueOf(bar));
                tvLevel.setText(String.valueOf(level));
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View mDecorView = getWindow().getDecorView();

        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}