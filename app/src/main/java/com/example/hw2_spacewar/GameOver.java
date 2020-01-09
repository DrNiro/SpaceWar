package com.example.hw2_spacewar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

public class GameOver extends AppCompatActivity {

    private HideBars hideBars = new HideBars();

    private static final int REQUEST_LOCATION_CODE = 1000;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private ImageView end_BTN_retry;
    private ImageView end_BTN_mainMenu;

    private ImageView end_BTN_highscore;

    private TextView end_LBL_scoreTag;

    private String name;
    private String controlState;
    private String speed;
    private int coins;
    private int distance;
    private boolean fromHighscoreActivity;
    private double lati;
    private double longi;

    private MySharedPreferences prefs;
    private ScoreList scoreList;

    private String jsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        name = getIntent().getStringExtra(Constants.KEY_NAME);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

        prefs = new MySharedPreferences(this);
//        Get Score list from sharedPreferences saved as JSON String.
        jsList = prefs.getString(Constants.PREFS_KEY_SCORE_LIST, "");
//        if JSON doesn't exist, create new list.
        if(jsList.equalsIgnoreCase("")) // Add default constant string.
            scoreList = new ScoreList();
        else
            scoreList = new Gson().fromJson(jsList, ScoreList.class);

//        Get score from game.
        coins = getIntent().getIntExtra(Constants.KEY_COINS,0);
        fromHighscoreActivity = getIntent().getBooleanExtra(Constants.KEY_FROM_HIGHSCORE, false);

        end_LBL_scoreTag = findViewById(R.id.end_LBL_scoreTag);
        end_LBL_scoreTag.setText(coins + "");

        end_BTN_retry = findViewById(R.id.end_BTN_retry);
        end_BTN_mainMenu = findViewById(R.id.end_BTN_mainMenu);
        end_BTN_highscore = findViewById(R.id.end_BTN_highscore);

        controlState = getIntent().getStringExtra(Constants.KEY_STATE);
        speed = getIntent().getStringExtra(Constants.KEY_SPEED);
        distance = getIntent().getIntExtra(Constants.KEY_DISTANCE, 0);

        end_BTN_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent retryIntent = new Intent(GameOver.this, MainActivity_5Lanes.class);
                retryIntent.putExtra(Constants.KEY_START_GAME, true);
                retryIntent.putExtra(Constants.KEY_STATE, controlState);
                retryIntent.putExtra(Constants.KEY_SPEED, speed);
                startActivity(retryIntent);
                GameOver.this.finish();
            }
        });

        end_BTN_mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menuIntent = new Intent(GameOver.this, StartGame.class);
                menuIntent.putExtra(Constants.KEY_STATE, controlState);
                menuIntent.putExtra(Constants.KEY_SPEED, speed);
                startActivity(menuIntent);
                GameOver.this.finish();
            }
        });

        end_BTN_highscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent highsoreIntent = new Intent(GameOver.this, activity_list_and_map.class);
                highsoreIntent.putExtra(Constants.KEY_HIGHSCORE_LIST, jsList);
                highsoreIntent.putExtra(Constants.KEY_DISTANCE, distance);
                highsoreIntent.putExtra(Constants.KEY_STATE, controlState);
                highsoreIntent.putExtra(Constants.KEY_SPEED, speed);
                highsoreIntent.putExtra(Constants.KEY_COINS, coins);
                startActivity(highsoreIntent);
                GameOver.this.finish();
            }
        });

    }

    private void getLocation() {
        //check permission of the user to his current location
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            return;
        }
        //set location
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    lati = currentLocation.getLatitude();
                    longi = currentLocation.getLongitude();
                    setNewScore();
                } else {
                    lati = 1;
                    longi = 1;
                    setNewScore();
                }
            }
        });
    }

    public void setNewScore() {
        if(!fromHighscoreActivity) {
            Score score = new Score("", distance, coins, lati, longi);
            if(scoreList.checkTopTen(score)) {
                score.setName(name);
                scoreList.addNewScore(score);
            }
        }

//        Create JSON from the updated list, and save it to prefs.
        jsList = new Gson().toJson(scoreList);
        prefs.putString(Constants.PREFS_KEY_SCORE_LIST, jsList);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_LOCATION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLocation();
//            }
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBars.hideSystemUI(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent myIntent = new Intent(GameOver.this, StartGame.class);
        myIntent.putExtra(Constants.KEY_STATE, controlState);
        myIntent.putExtra(Constants.KEY_SPEED, speed);
        startActivity(myIntent);
        GameOver.this.finish();
    }
}
