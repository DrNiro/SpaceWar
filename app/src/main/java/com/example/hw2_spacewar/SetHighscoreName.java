package com.example.hw2_spacewar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Set;

public class SetHighscoreName extends AppCompatActivity {
    private View view = null;

    private EditText hs_EDT_setName;
    public Button hs_BTN_ok;

    private String name;
    private String controlState;
    private String speed;
    private int coins;
    private int distance;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_highscore_name);

        hs_EDT_setName = findViewById(R.id.hs_EDT_setName);
        hs_BTN_ok = findViewById(R.id.hs_BTN_ok);

        controlState = getIntent().getStringExtra(Constants.KEY_STATE);
        speed = getIntent().getStringExtra(Constants.KEY_SPEED);
        distance = getIntent().getIntExtra(Constants.KEY_DISTANCE, 0);
        coins = getIntent().getIntExtra(Constants.KEY_COINS,0);

        hs_BTN_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = hs_EDT_setName.getText().toString();
                Intent myIntent = new Intent(SetHighscoreName.this, GameOver.class);
                myIntent.putExtra(Constants.KEY_NAME, name);
                myIntent.putExtra(Constants.KEY_COINS, coins);
                myIntent.putExtra(Constants.KEY_STATE, controlState);
                myIntent.putExtra(Constants.KEY_SPEED, speed);
                myIntent.putExtra(Constants.KEY_DISTANCE, distance);
                startActivity(myIntent);
                SetHighscoreName.this.finish();
            }
        });
    }

}
