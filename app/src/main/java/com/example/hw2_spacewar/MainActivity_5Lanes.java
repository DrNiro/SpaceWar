package com.example.hw2_spacewar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity_5Lanes extends AppCompatActivity {
    //    Sensors declare
    private SensorManager sensorManager;
    private Sensor sensorMotion;
    SensorEventListener sensorTurnListener;
    private int sensorTurnSensitivity = 4;
    private float mostLeftLane = 4.5f;
    private float leftLane = 2f;
    private float rightLane = -leftLane;
    private float mostRightLane = -mostLeftLane;
    private float sensorSpeedSensitivity = 3.5f;
    private float aliensSpeedFactorBySensor = 1;

    private HideBars hideBars = new HideBars();

    private MySharedPreferences prefs;
    private String jsList;
    private ScoreList scoreList;

//    Sound object declare
    private MediaPlayer mp_coin_collision;
    private MediaPlayer mp_alien_collision;

    private int numberOfLanes = 5;
//    private String controlState = Constants.STATE_BUTTONS;
    private String controlState;
    private String speed;
    private int score = 0;
    private int distance = 0;

    //    The starting position of aliens and coins.
    private int startPos = -400;

    //    Time in milliseconds between updates in the Runnable object.
    private int handlerDelay = 30;
    //    boolean to keep track of the state of the game. (true - Run / false - Pause).
    private boolean isGameOn = false;
    private boolean isGameOver = false;
    private boolean isJustRan = true;
    private boolean isHighScore = false;

    //    position of the spaceship: 1 - most left lane, 2 - left lane, 3 - center lane, 4 - right lane, 5 - most right lane.
    public int position = 3;
    //    Hit points for the spaceship.
    public int lifeCount = 3;

    //    The layout of the spaceship and the aliens.
    private RelativeLayout main_LOT_body_5;

    //    Declaration of the spaceship and the "buttons" controlling it.
    private ImageView char_IMG_spaceship_5;
    private ImageView control_BTN_left_5;
    private ImageView control_BTN_right_5;

    //    Declare aliens
    private ImageView most_left_IMG_alien_5;
    private ImageView left_IMG_alien_5;
    private ImageView center_IMG_alien_5;
    private ImageView right_IMG_alien_5;
    private ImageView most_right_IMG_alien_5;

//    Declare coins
    private ImageView most_left_IMG_coin;
    private ImageView left_IMG_coin;
    private ImageView center_IMG_coin;
    private ImageView right_IMG_coin;
    private ImageView most_right_IMG_coin;

    //    Declare hearts. Each heart is 1 hit point.
    private ImageView top_panel_ICN_heart1_5;
    private ImageView top_panel_ICN_heart2_5;
    private ImageView top_panel_ICN_heart3_5;

    //    Declare score text to change during the game.
    private TextView main_LBL_score_5;
    private TextView main_LBL_distance;
    private TextView main_LBL_pause_5;

    //    Declare pause "button" to control the state of the game.
    private ImageView top_panel_ICN_pause_5;
    private ImageView top_panel_ICN_play_5;

    //    Declare the hearts and aliens arrays outside the onCreate() in order to have access along all runtime.
    //    hearts array
    final View[] hearts = new View[3];

    //    all aliens, and all coins arrays.
    View[] aliens = new View[numberOfLanes];
    View[] coins = new View[numberOfLanes];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_5_lanes);

        findViews();

        assignAllArrays();

        controlState = getIntent().getStringExtra(Constants.KEY_STATE);
        speed = getIntent().getStringExtra(Constants.KEY_SPEED);

        mp_coin_collision = MediaPlayer.create(this, R.raw.mp_coin_collision);
        mp_alien_collision = MediaPlayer.create(this, R.raw.mp_alien_collision);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            sensorMotion = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else if(controlState.equalsIgnoreCase(Constants.STATE_TILT)) {
            // TODO: 12/9/2019 Popup message telling no required sensor.
            Log.d("No sensor", "Sorry, device don't have required sensor. You can't play this game on tilt mode.");
        }

        sensorTurnListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                if(x < mostRightLane && position != 5)
                    position = moveSpaceshipToPosition(5);
                else if(mostRightLane < x && x < rightLane && position != 4)
                    position = moveSpaceshipToPosition(4);
                else if(rightLane < x && x < leftLane && position != 3)
                    position = moveSpaceshipToPosition(3);
                else if(leftLane < x && x < mostLeftLane && position != 2)
                    position = moveSpaceshipToPosition(2);
                else if(mostLeftLane < x && position != 1)
                    position = moveSpaceshipToPosition(1);

                if(y < sensorSpeedSensitivity && aliensSpeedFactorBySensor != 2)
                    aliensSpeedFactorBySensor = 2;
                if(y >= sensorSpeedSensitivity && aliensSpeedFactorBySensor != 1)
                    aliensSpeedFactorBySensor = 1;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        if(controlState.equalsIgnoreCase(Constants.STATE_TILT)) {
            control_BTN_right_5.setVisibility(View.INVISIBLE);
            control_BTN_left_5.setVisibility(View.INVISIBLE);
        } else {
            control_BTN_right_5.setVisibility(View.VISIBLE);
            control_BTN_left_5.setVisibility(View.VISIBLE);
        }

//        Buttons listeners.
        top_panel_ICN_pause_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isGameOn)
                    onPause();
                else
                    onResume();
            }
        });

        top_panel_ICN_play_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isGameOn)
                    onPause();
                else
                    onResume();
            }
        });

        control_BTN_left_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isGameOn && control_BTN_left_5.isShown()){
                    position = moveSpaceshipLeft();
                }
            }
        });

        control_BTN_right_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isGameOn && control_BTN_right_5.isShown()) {
                    position = moveSpaceshipRight();
                }
            }
        });

    } // End of onCreate()

//      Connect all objects needed from XML to activity
    private void findViews() {
        //  Left and right buttons.
        control_BTN_right_5 = findViewById(R.id.control_BTN_right_5);
        control_BTN_left_5 = findViewById(R.id.control_BTN_left_5);
        // Main layout (lanes and ship).
        main_LOT_body_5 = findViewById(R.id.main_LOT_body_5);
        //  Spaceship.
        char_IMG_spaceship_5 = findViewById(R.id.char_IMG_spaceship_5);

        //  All aliens
        most_left_IMG_alien_5 = findViewById(R.id.most_left_IMG_alien_5);
        left_IMG_alien_5 = findViewById(R.id.left_IMG_alien_5);
        center_IMG_alien_5 = findViewById(R.id.center_IMG_alien_5);
        right_IMG_alien_5 = findViewById(R.id.right_IMG_alien_5);
        most_right_IMG_alien_5 = findViewById(R.id.most_right_IMG_alien_5);

//        All coins
        most_left_IMG_coin = findViewById(R.id.most_left_IMG_coin);
        left_IMG_coin = findViewById(R.id.left_IMG_coin);
        center_IMG_coin = findViewById(R.id.center_IMG_coin);
        right_IMG_coin = findViewById(R.id.right_IMG_coin);
        most_right_IMG_coin = findViewById(R.id.most_right_IMG_coin);

        //  All hearts.
        top_panel_ICN_heart1_5 = findViewById(R.id.top_panel_ICN_heart1_5);
        top_panel_ICN_heart2_5 = findViewById(R.id.top_panel_ICN_heart2_5);
        top_panel_ICN_heart3_5 = findViewById(R.id.top_panel_ICN_heart3_5);
        //  Pause/play button.
        top_panel_ICN_pause_5 = findViewById(R.id.top_panel_ICN_pause_5);
        top_panel_ICN_play_5 = findViewById(R.id.top_panel_ICN_play_5);
        //  Score label.
        main_LBL_score_5 = findViewById(R.id.main_LBL_score_5);
//        Distance label
        main_LBL_distance = findViewById(R.id.main_LBL_distance);
        //  Pause label.
        main_LBL_pause_5 = findViewById(R.id.main_LBL_pause_5);
    }

    private void assignAllArrays() {
//        All aliens.
        aliens[0] = most_left_IMG_alien_5;
        aliens[1] = left_IMG_alien_5;
        aliens[2] = center_IMG_alien_5;
        aliens[3] = right_IMG_alien_5;
        aliens[4] = most_right_IMG_alien_5;

//        All coins
        coins[0] = most_left_IMG_coin;
        coins[1] = left_IMG_coin;
        coins[2] = center_IMG_coin;
        coins[3] = right_IMG_coin;
        coins[4] = most_right_IMG_coin;

//        All hearts.
        hearts[0] = top_panel_ICN_heart1_5;
        hearts[1] = top_panel_ICN_heart2_5;
        hearts[2] = top_panel_ICN_heart3_5;
    }

    private int moveSpaceshipToPosition(int position) {
        float layout_width = main_LOT_body_5.getWidth();
        float movingInterval = layout_width / numberOfLanes * (position-1);

        char_IMG_spaceship_5.setX(most_left_IMG_alien_5.getX() + movingInterval);
        return position;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isGameOn = false;
//        if go through game over, don't show pause elements
        if(!isGameOver) {
            top_panel_ICN_pause_5.setVisibility(View.INVISIBLE);
            top_panel_ICN_play_5.setVisibility(View.VISIBLE);
            main_LBL_pause_5.setVisibility(View.VISIBLE);
        }
        if(controlState.equalsIgnoreCase(Constants.STATE_TILT)) {
            sensorManager.unregisterListener(sensorTurnListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isGameOn = true;
        hideBars.hideSystemUI(this);
        top_panel_ICN_pause_5.setVisibility(View.VISIBLE);
        top_panel_ICN_play_5.setVisibility(View.INVISIBLE);
        main_LBL_pause_5.setVisibility(View.INVISIBLE);
        if(controlState.equalsIgnoreCase(Constants.STATE_TILT)) {
            sensorManager.registerListener(sensorTurnListener, sensorMotion, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            sensorManager.unregisterListener(sensorTurnListener);
        }
        play();
    }

    private void startRunningObstacles(final View[] aliensLine, final int startPos) {
        Handler handler = new Handler();
        Runnable myRun = new Runnable() {
            @Override
            public void run() {
                startRunningObstacles(aliensLine, startPos);
                moveObstacles(aliensLine[0], aliensLine[1], aliensLine[2], aliensLine[3], aliensLine[4], startPos);

                if(aliensLine[0].getY() - 250 <= MainActivity_5Lanes.this.startPos) {
                    updateVisibility(aliensLine[0], aliensLine[1], aliensLine[2], aliensLine[3], aliensLine[4], Constants.VIEW_KIND_ALIENS);
                }
                if(detectCollision(aliensLine)) {
                    lifeCount = manageLife(lifeCount, hearts);
                    if(lifeCount != 0) {
                        Toast.makeText(MainActivity_5Lanes.this, "Got hit", Toast.LENGTH_SHORT).show();
                    }
                    if(mp_alien_collision.isPlaying()) {
                        mp_alien_collision.stop();
                    }
                    mp_alien_collision.start();
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if(v != null) {
                        v.vibrate(500);
                    }
                }
            }
        };
        if(isGameOn) {
            handler.postDelayed(myRun, handlerDelay);
        }

    }

    private void startDropCoins() {
        Handler handler = new Handler();
        Runnable myRun = new Runnable() {
            @Override
            public void run() {
                startDropCoins();
                if(aliens[0].getY() != coins[0].getY())
                    moveObstacles(coins[0], coins[1], coins[2], coins[3], coins[4], startPos);

                if(most_left_IMG_coin.getY() - 250 <= startPos) {
                    updateVisibility(coins[0], coins[1], coins[2], coins[3], coins[4], Constants.VIEW_KIND_COINS);
                }
                if(detectCollision(coins)) {
                    Toast.makeText(MainActivity_5Lanes.this, "Collected", Toast.LENGTH_SHORT).show();
                    updateScore();
                    if(mp_coin_collision.isPlaying()) {
                        mp_coin_collision.stop();
                    }
                    mp_coin_collision.start();
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if(v != null) {
                        v.vibrate(500);
                    }
                }
            }
        };
        if(isGameOn) {
            handler.postDelayed(myRun, handlerDelay);
        }

    }

    public int rollRandomNumber(int min, int max) {
        return (int)(Math.random() * ((max - min) + 1)) + min;
    }

    private void moveObstacles(View most_left_view, View left_view, View center_view, View right_view, View most_right_view, int startPos) {
        if(controlState.equalsIgnoreCase(Constants.STATE_BUTTONS)) {
            if(speed.equalsIgnoreCase(Constants.SPEED_SLOW)) {
                aliensSpeedFactorBySensor = 1;
            } else {
                aliensSpeedFactorBySensor = 2;
            }
        }
        float movingInterval = 10 * aliensSpeedFactorBySensor;
        int height = main_LOT_body_5.getHeight();

        if(most_left_view.getY() > height) {
            most_left_view.setY(this.startPos);
        }
        most_left_view.setY(most_left_view.getY() + movingInterval * this.getResources().getDisplayMetrics().density);

        if(left_view.getY() > height) {
            left_view.setY(this.startPos);
        }
        left_view.setY(left_view.getY() + movingInterval * this.getResources().getDisplayMetrics().density);

        if(center_view.getY() > height) {
            center_view.setY(this.startPos);
        }
        center_view.setY(center_view.getY() + movingInterval * this.getResources().getDisplayMetrics().density);

        if(right_view.getY() > height) {
            right_view.setY(this.startPos);
        }
        right_view.setY(right_view.getY() + movingInterval * this.getResources().getDisplayMetrics().density);

        if(most_right_view.getY() > height) {
            most_right_view.setY(this.startPos);
        }
        most_right_view.setY(most_right_view.getY() + movingInterval * this.getResources().getDisplayMetrics().density);
    }

    private ArrayList<Integer> chooseRandViews(int min, int max, int quantity) {
        ArrayList<Integer> randArray = new ArrayList<>();

//        Fill arrayList with the numbers representing views in a group of 5. (no duplicates).
        while(randArray.size() < quantity) {
            for(int i = 0; i < quantity; i++) {
                int rand_alien = rollRandomNumber(min, max);
                if(!randArray.contains(rand_alien)) {
                    randArray.add(rand_alien);
                }
            }
        }
        return randArray;
    }

    private void updateVisibility(View most_left_view, View left_view, View center_view, View right_view, View most_right_view, String view_kind) {
        int min = 1;
        int max = numberOfLanes;
        int max_vis = numberOfLanes - 1;
//        quantity_rand - How many invisible aliens. (1 or 2)
        int quantity_rand;
        if(view_kind.equalsIgnoreCase(Constants.VIEW_KIND_ALIENS)) {
            quantity_rand = rollRandomNumber(min, max_vis);
        } else if(view_kind.equalsIgnoreCase(Constants.VIEW_KIND_COINS)) {
            quantity_rand = 4;
        } else {
            quantity_rand = 5;
        }
//        randArray - Which aliens to make invisible
        ArrayList<Integer> randArray = chooseRandViews(min, max, quantity_rand);

//        if the num corresponding to the alien is in the arrayList, make it invisible. else, visible.
        if(randArray.contains(1)) {
            most_left_view.setVisibility(View.INVISIBLE);
        } else {
            most_left_view.setVisibility(View.VISIBLE);
        }

        if(randArray.contains(2)) {
            left_view.setVisibility(View.INVISIBLE);
        } else {
            left_view.setVisibility(View.VISIBLE);
        }

        if(randArray.contains(3)) {
            center_view.setVisibility(View.INVISIBLE);
        } else {
            center_view.setVisibility(View.VISIBLE);
        }

        if(randArray.contains(4)) {
            right_view.setVisibility(View.INVISIBLE);
        } else {
            right_view.setVisibility(View.VISIBLE);
        }

        if(randArray.contains(5)) {
            most_right_view.setVisibility(View.INVISIBLE);
        } else {
            most_right_view.setVisibility(View.VISIBLE);
        }
    }

    private int moveSpaceshipLeft() {
        float layout_width = main_LOT_body_5.getWidth();
        float movingInterval = layout_width / numberOfLanes;

        if(position > 1) {
            char_IMG_spaceship_5.setX(char_IMG_spaceship_5.getX() - movingInterval);
            return (position-1);
        }
        return position;
    }

    private int moveSpaceshipRight() {
        float layout_width = main_LOT_body_5.getWidth();
        float movingInterval = layout_width / numberOfLanes;

        if(position < numberOfLanes) {
            char_IMG_spaceship_5.setX(char_IMG_spaceship_5.getX() + movingInterval);
            return (position+1);
        }
        return position;
    }

    private boolean detectCollision(View[] aliensLine) {
        View currentAlien;
        for(int i = 0; i < aliensLine.length; i++) {
            currentAlien = aliensLine[i];
            if(currentAlien.isShown()) {
                if ((i+1) == position &&
                        char_IMG_spaceship_5.getY() <= currentAlien.getY() + (75 * this.getResources().getDisplayMetrics().density)
                        && !(currentAlien.getY() > char_IMG_spaceship_5.getY() + (50 * this.getResources().getDisplayMetrics().density))) {
                    currentAlien.setVisibility(View.INVISIBLE);
                    return true;
                }
            }
        }
        return false;
    }

    private int manageLife(int lifeCount, View[] hearts) {
        if(lifeCount == 3) {
            hearts[2].setVisibility(View.INVISIBLE);
        } else if(lifeCount == 2) {
            hearts[1].setVisibility(View.INVISIBLE);
        } else if(lifeCount == 1) {
            hearts[0].setVisibility(View.INVISIBLE);
            isGameOver = true;
            gameOver();
        } else {
            return lifeCount;
        }
        return lifeCount - 1;
    }

    private void gameOver() {
        prefs = new MySharedPreferences(this);
        jsList = prefs.getString(Constants.PREFS_KEY_SCORE_LIST, "");

        if(jsList.equalsIgnoreCase("")){
            isHighScore = true;
        }
        else {
            scoreList = new Gson().fromJson(jsList, ScoreList.class);
            if(scoreList.checkTopTen(distance)) {
                isHighScore = true;
            }
        }

        if(isHighScore) {
            Intent highscoreIntent = new Intent(MainActivity_5Lanes.this, SetHighscoreName.class);
            highscoreIntent.putExtra(Constants.KEY_COINS, score);
            highscoreIntent.putExtra(Constants.KEY_STATE, controlState);
            highscoreIntent.putExtra(Constants.KEY_SPEED, speed);
            highscoreIntent.putExtra(Constants.KEY_DISTANCE, distance);
            startActivity(highscoreIntent);
            MainActivity_5Lanes.this.finish();
        } else {
            Intent myIntent = new Intent(MainActivity_5Lanes.this, GameOver.class);
            myIntent.putExtra(Constants.KEY_COINS, score);
            myIntent.putExtra(Constants.KEY_STATE, controlState);
            myIntent.putExtra(Constants.KEY_SPEED, speed);
            myIntent.putExtra(Constants.KEY_DISTANCE, distance);
            startActivity(myIntent);
            MainActivity_5Lanes.this.finish();
        }

    }

    private void startUpdateDistance() {
        Handler handler = new Handler();
        Runnable scoreRunner = new Runnable() {
            @Override
            public void run() {
                startUpdateDistance();
                updateDistance();
            }
        };
        if(isGameOn)
            handler.postDelayed(scoreRunner, 1000);
    }

    private void updateDistance() {
        distance += 100 * aliensSpeedFactorBySensor;
        String stringDistance = distance + " m";
        main_LBL_distance.setText(stringDistance);
    }

    private void updateScore() {
        score += 100;
        String stringScore = score + "";
        main_LBL_score_5.setText(stringScore);
    }

    //    Start playing with a half-a-second delay.
    private void play() {
        if(isGameOn) {
            Handler handler = new Handler();
            Runnable scoreRunner = new Runnable() {
                @Override
                public void run() {
                    if(isJustRan){
                        setStartPositions();
                        isJustRan = false;
                    }
                    startRunningObstacles(aliens, startPos);
                    startDropCoins();
                    startUpdateDistance();
                }
            };
            handler.postDelayed(scoreRunner, 500);
        }
    }

    private void setStartPositions() {
//        Set start position to all aliens.
        most_left_IMG_alien_5.setY(startPos);
        left_IMG_alien_5.setY(startPos);
        center_IMG_alien_5.setY(startPos);
        right_IMG_alien_5.setY(startPos);
        most_right_IMG_alien_5.setY(startPos);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent myIntent = new Intent(MainActivity_5Lanes.this, StartGame.class);
        myIntent.putExtra(Constants.KEY_STATE, controlState);
        myIntent.putExtra(Constants.KEY_SPEED, speed);
        startActivity(myIntent);
        MainActivity_5Lanes.this.finish();
    }
}