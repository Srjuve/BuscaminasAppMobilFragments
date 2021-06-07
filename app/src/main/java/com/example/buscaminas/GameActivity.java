package com.example.buscaminas;


import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import java.util.Date;

public class GameActivity extends FragmentActivity implements GridCellOnClickListener.GameClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        //Set the game activity needed data
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if(savedInstanceState==null) {
            GridFrag frag = (GridFrag) getFragmentManager().findFragmentById(R.id.game_frag);
            frag.startGame(this);
        }
    }

    public void onGameClick(int position, Date startDate, Date endDate){
        LogFrag logf = (LogFrag) getFragmentManager().findFragmentById(R.id.log_frag);
        if(logf != null && logf.isInLayout()){
            logf.setLogData(position,startDate,endDate);
        }
    }

    public void onGameClick(int position, Date startDate, Date endDate, int current_time){
        LogFrag logf = (LogFrag) getFragmentManager().findFragmentById(R.id.log_frag);
        if(logf != null && logf.isInLayout()){
            logf.setLogData(position,startDate,endDate,current_time);
        }
    }

}
