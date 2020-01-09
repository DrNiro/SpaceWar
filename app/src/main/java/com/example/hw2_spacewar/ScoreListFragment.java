package com.example.hw2_spacewar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class ScoreListFragment extends Fragment {

    private CallBack_ActivityList callBack_activityList;

    private View view = null;
    private MySharedPreferences prefs;

    private RecyclerView scoreListRecycleView;
    private ScoreList scoreList;
    private ScoreListAdapter adapter;

    public void setCallback(CallBack_ActivityList callback) {
        this.callBack_activityList = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.activity_score_list_fragment, container, false);
        }

        prefs = new MySharedPreferences(view.getContext());
        String jsList = prefs.getString(Constants.PREFS_KEY_SCORE_LIST, "");
        scoreList = new Gson().fromJson(jsList, ScoreList.class);

        scoreListRecycleView = view.findViewById(R.id.fragList_RCLV_scores);
        scoreListRecycleView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        adapter = new ScoreListAdapter(inflater.getContext(), scoreList.getScoreList());
        adapter.setClickListener(itemClickListener);
        scoreListRecycleView.setAdapter(adapter);

        return view;
    }

    public void setMapLocation(LatLng location) {
        callBack_activityList.setMapLocation(location);
    }

    public ScoreListAdapter.ItemClickListener itemClickListener = new ScoreListAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Score score = scoreList.getScoreList().get(position);
            setMapLocation(new LatLng(score.getLatitude(),score.getLongitude()));
        }
    };
}
