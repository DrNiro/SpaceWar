package com.example.hw2_spacewar;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class Mode extends AppCompatActivity {

    private ImageView mode_IMG_x_back;
    private ToggleButton mode_TBTN_control;
    private ToggleButton mode_TBTN_speed;
    private TextView mode_LBL_control;
    private TextView mode_LBL_speed;

    private HideBars hideBars = new HideBars();

    private String controlState;
    private String speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);

//        Declare Toggle Buttons and X-back button.
        mode_IMG_x_back = findViewById(R.id.mode_IMG_x_back);
        mode_TBTN_control = findViewById(R.id.mode_TBTN_control);
        mode_TBTN_speed = findViewById(R.id.mode_TBTN_speed);
        mode_LBL_control = findViewById(R.id.mode_LBL_control);
        mode_LBL_speed = findViewById(R.id.mode_LBL_speed);

//        get data to update with last selected values.
        controlState = getIntent().getStringExtra(Constants.KEY_STATE);
        speed = getIntent().getStringExtra(Constants.KEY_SPEED);

        if(controlState.equalsIgnoreCase(Constants.STATE_BUTTONS)) {
            mode_TBTN_control.setChecked(false);
            if(speed.equalsIgnoreCase(Constants.SPEED_SLOW)) {
                mode_TBTN_speed.setChecked(false);
            } else {
                mode_TBTN_speed.setChecked(true);
            }
        } else {
            mode_TBTN_control.setChecked(true);
            mode_TBTN_speed.setVisibility(View.INVISIBLE);
            mode_LBL_speed.setVisibility(View.INVISIBLE);
        }

        mode_TBTN_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode_TBTN_control.isChecked()) {
                    mode_LBL_speed.setVisibility(View.GONE);
                    mode_TBTN_speed.setVisibility(View.GONE);
                } else {
                    mode_LBL_speed.setVisibility(View.VISIBLE);
                    mode_TBTN_speed.setVisibility(View.VISIBLE);
                }
            }
        });


        mode_IMG_x_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPause();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBars.hideSystemUI(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        controlState = mode_TBTN_control.getText().toString();
        speed = mode_TBTN_speed.getText().toString();
        Intent myIntent = new Intent(Mode.this, StartGame.class);
        myIntent.putExtra(Constants.KEY_STATE, controlState);
        myIntent.putExtra(Constants.KEY_SPEED, speed);
        startActivity(myIntent);
        Mode.this.finish();
    }
}
