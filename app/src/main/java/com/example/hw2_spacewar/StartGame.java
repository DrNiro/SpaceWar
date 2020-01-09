package com.example.hw2_spacewar;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class StartGame extends AppCompatActivity {

    private ImageView main_BTN_start;
    private ImageView main_BTN_mode;

    private String controlState;
    private String speed;

    private HideBars hideBars = new HideBars();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        main_BTN_start = findViewById(R.id.main_BTN_start);
        main_BTN_mode = findViewById(R.id.main_BTN_mode);

        main_BTN_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(StartGame.this, MainActivity_5Lanes.class);
                startIntent.putExtra(Constants.KEY_START_GAME, true);
                startIntent.putExtra(Constants.KEY_STATE, controlState);
                startIntent.putExtra(Constants.KEY_SPEED, speed);
                startActivity(startIntent);
            }
        });

        main_BTN_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent modeIntent = new Intent(StartGame.this, Mode.class);
                modeIntent.putExtra(Constants.KEY_STATE, controlState);
                modeIntent.putExtra(Constants.KEY_SPEED, speed);
                startActivity(modeIntent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBars.hideSystemUI(this);
        controlState = getIntent().getStringExtra(Constants.KEY_STATE);
        speed = getIntent().getStringExtra(Constants.KEY_SPEED);
        if(controlState == null) {
            controlState = Constants.STATE_BUTTONS;
            speed = Constants.SPEED_SLOW;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

}
