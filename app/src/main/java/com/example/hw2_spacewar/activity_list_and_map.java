package com.example.hw2_spacewar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

public class activity_list_and_map extends FragmentActivity implements OnMapReadyCallback {

    private HideBars hideBars = new HideBars();

    private ScoreListFragment listFragment;
    private GoogleMap mMap;

    private ImageView highscore_BTN_back;
    private TextView highscore_LBL_lastScore;

    private String jsList;
    private ScoreList scoreList;

    private int distance;
    private String controlState;
    private String speed;
    private int coins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_and_map);

        listFragment = new ScoreListFragment();
        listFragment.setCallback(myCallback);

        highscore_BTN_back = findViewById(R.id.highscore_BTN_back);
        highscore_LBL_lastScore = findViewById(R.id.highscore_LBL_lastScore);

        coins = getIntent().getIntExtra(Constants.KEY_COINS, 0);
        distance = getIntent().getIntExtra(Constants.KEY_DISTANCE, 0);
        String showDistance = "Last Score: " + distance + " m";
        highscore_LBL_lastScore.setText(showDistance);

        controlState = getIntent().getStringExtra(Constants.KEY_STATE);
        speed = getIntent().getStringExtra(Constants.KEY_SPEED);
        jsList = getIntent().getStringExtra(Constants.KEY_HIGHSCORE_LIST);
        scoreList = new Gson().fromJson(jsList, ScoreList.class);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_LAY_top, listFragment);
        transaction.commit();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if(mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        highscore_BTN_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(activity_list_and_map.this, GameOver.class);
                backIntent.putExtra(Constants.KEY_FROM_HIGHSCORE, true);
                backIntent.putExtra(Constants.KEY_STATE, controlState);
                backIntent.putExtra(Constants.KEY_SPEED, speed);
                backIntent.putExtra(Constants.KEY_DISTANCE, distance);
                backIntent.putExtra(Constants.KEY_COINS, coins);
                startActivity(backIntent);
                activity_list_and_map.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBars.hideSystemUI(this);
    }

    CallBack_ActivityList myCallback = new CallBack_ActivityList() {
        @Override
        public void setMapLocation(LatLng location) {
            mMap.clear();
            if(location.latitude == 1 && location.longitude == 1) {
                mMap.addMarker(new MarkerOptions().position(location).title("Location not found. Go fish"));
            } else {
                mMap.addMarker(new MarkerOptions().position(location));
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backIntent = new Intent(activity_list_and_map.this, GameOver.class);
        backIntent.putExtra(Constants.KEY_FROM_HIGHSCORE, true);
        backIntent.putExtra(Constants.KEY_STATE, controlState);
        backIntent.putExtra(Constants.KEY_SPEED, speed);
        backIntent.putExtra(Constants.KEY_DISTANCE, distance);
        backIntent.putExtra(Constants.KEY_COINS, coins);
        startActivity(backIntent);
        activity_list_and_map.this.finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng currentLocation;
        // Add a marker in Sydney and move the camera
        if(scoreList.getScoreList().size() > 0) {
            currentLocation = new LatLng(scoreList.getScoreList().get(0).getLatitude(), scoreList.getScoreList().get(0).getLongitude());
        } else {
            currentLocation = new LatLng(32.1, 34.8);
        }
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("1st place"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 7.5f));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
    }
}
