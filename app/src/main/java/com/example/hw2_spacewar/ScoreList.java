package com.example.hw2_spacewar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ScoreList implements Comparator<Score> {
    private final static int SIZE = 10;

    private ArrayList<Score> scoreList;
    private int maxListSize;

    public ScoreList() {
        setScoreList(new ArrayList<Score>());
        setListSize();
    }

    public ScoreList(ArrayList<Score> scoreList) {
        setScoreList(scoreList);
        setListSize();
    }

    public ArrayList<Score> getScoreList() {
        return scoreList;
    }

    public void setScoreList(ArrayList<Score> scoreList) {
        this.scoreList = scoreList;
    }

    public int getMaxListSize() {
        return maxListSize;
    }

    public void setListSize() {
        this.maxListSize = SIZE;
    }

    public void addNewScore(Score newScore) {
        ArrayList<Score> newList = getScoreList();
        if(checkTopTen(newScore) && this.getScoreList().size() >= this.getMaxListSize())
            newList.remove(SIZE-1);
        newList.add(newScore);
        Collections.sort(newList, this);
        setScoreList(newList);
    }

    public boolean checkTopTen(Score score) {
        if(this.getScoreList().size() >= this.getMaxListSize()) {
            if(this.getScoreList().get(SIZE-1).compareTo(score) < 0) {
                return false;
            }
            return true;
        }
        return true;
    }

    public boolean checkTopTen(int coins) {
        if(this.getScoreList().size() >= this.getMaxListSize()) {
            if(this.getScoreList().get(SIZE-1).getDistance() > coins) {
                return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public int compare(Score score, Score t1) {
        return score.compareTo(t1);
    }
}
