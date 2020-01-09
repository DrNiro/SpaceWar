package com.example.hw2_spacewar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class ScoreListAdapter extends RecyclerView.Adapter<ScoreListAdapter.ViewHolder> {
    private List<Score> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    ScoreListAdapter(Context context, List<Score> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.highscore_view_holder, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Score score = mData.get(position);
        if(position == 9){
            holder.rankTextView.setText(String.valueOf(position + 1));
        } else {
            String o_num = "0" + String.valueOf(position + 1);
            holder.rankTextView.setText(o_num);
        }
        holder.playerNameTextView.setText(score.getName());
        String distance_with_m = score.getDistance() + " m";
        holder.scoreValueTextView.setText(distance_with_m);
        if(position == 0) {
            setColorForAllTexts(holder, R.color.firstPlace);
        } else if(position == 1) {
            setColorForAllTexts(holder, R.color.secondPlace);
        } else if(position == 2) {
            setColorForAllTexts(holder, R.color.thirdPlace);
        }
    }

    private void setColorForAllTexts(ViewHolder holder, int resourcesColor) {
        holder.rankTextView.setTextColor(ContextCompat.getColor(mInflater.getContext(), resourcesColor));
        holder.playerNameTextView.setTextColor(ContextCompat.getColor(mInflater.getContext(), resourcesColor));
        holder.scoreValueTextView.setTextColor(ContextCompat.getColor(mInflater.getContext(), resourcesColor));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView rankTextView;
        TextView playerNameTextView;
        TextView scoreValueTextView;

        ViewHolder(View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.holder_LBL_rank);
            playerNameTextView = itemView.findViewById(R.id.holder_LBL_name);
            scoreValueTextView = itemView.findViewById(R.id.holder_LBL_score);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Score getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
