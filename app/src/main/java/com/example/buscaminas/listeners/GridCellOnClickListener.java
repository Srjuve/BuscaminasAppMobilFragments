package com.example.buscaminas.listeners;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buscaminas.adapters.CustomButtonAdapter;
import com.example.buscaminas.gameactivities.EndInfoActivity;
import com.example.buscaminas.gamedata.MineSearchGame;
import com.example.buscaminas.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.Date;

public class GridCellOnClickListener implements View.OnClickListener{
    Activity actualContext;
    MineSearchGame gameInstance;
    int numColums;
    int minePercentage;
    String alias;
    CustomButtonAdapter gridAdapter;
    CountDownTimer timer;
    Timer timerFalse;
    int maxTime;
    int num_mines;
    int position;
    GameClickListener listener;
    Date startTime;
    public GridCellOnClickListener(Activity actualContext, MineSearchGame gameInstance,CustomButtonAdapter gridAdapter, Object timer, int position, int maxTime, GameClickListener listener,Date startTime){
        this.actualContext=actualContext;
        this.gameInstance=gameInstance;
        this.alias=gameInstance.getUserAlias();
        this.numColums=gameInstance.getGridSize();
        this.minePercentage=gameInstance.getMinePercentage();
        this.num_mines=gameInstance.getNumberOfMines();
        this.maxTime=maxTime;
        this.position=position;
        this.gridAdapter=gridAdapter;
        this.listener = listener;
        this.startTime = startTime;
        if(timer instanceof CountDownTimer){
            this.timer=(CountDownTimer)timer;
        }else if(timer instanceof Timer){
            this.timerFalse = (Timer)timer;
        }
    }

    @Override
    public void onClick(View v) {
        //Check if the clicked position is a bomb or not and make the necessary changes
        int x=this.position/numColums;
        int y=this.position%numColums;
        int result=this.gameInstance.discoverPosition(x,y);
        if(result!=-1 && result!=-2){
            changeCellsCountState(v);
            this.gridAdapter.notifyDataSetChanged();
            if(this.gameInstance.checkVictory()){
                cancelTimer();
                Intent data = initEndIntent();
                wonGameExit(v,data);
                startEndActivity(data);
            }
            Date newTime = new Date();
            if(this.timer==null) {
                listener.onGameClick(this.position, startTime, newTime);
                startTime.setTime(newTime.getTime());
            }else{
                TextView time =v.getRootView().findViewById(R.id.time_text);
                listener.onGameClick(this.position,startTime,newTime,Integer.parseInt(time.getText().toString()));
                startTime.setTime(newTime.getTime());
            }
        }else if(result==-1){
            cancelTimer();
            this.gridAdapter.notifyDataSetChanged();
            MediaPlayer player = MediaPlayer.create(actualContext,R.raw.explosion);
            player.start();
            Intent data = initEndIntent();
            lostGameExit(v,data,x,y);
            startEndActivity(data);
        }
    }

    private Intent initEndIntent(){
        Intent data = new Intent(this.actualContext, EndInfoActivity.class);
        data.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        setDayHourData(data);
        return data;
    }

    private void wonGameExit(View v, Intent data){
        //Prepare the data used if the user wins the game
        createToast(actualContext.getString(R.string.Victory_Message));
        TextView time_value_view = (TextView)v.getRootView().findViewById(R.id.time_text);
        int time_counter = Integer.parseInt(time_value_view.getText().toString());
        if(timer!=null) {
            data.putExtra(actualContext.getString(R.string.End_log_data_key), actualContext.getString(R.string.victory_time_check_message,this.alias,String.valueOf(this.minePercentage)+"%",this.num_mines,this.maxTime-time_counter,time_counter));
        }else{
            data.putExtra(actualContext.getString(R.string.End_log_data_key), actualContext.getString(R.string.victory_not_time_check_message,this.alias,String.valueOf(this.minePercentage)+"%",this.num_mines,this.maxTime-time_counter));
        }
    }

    private void lostGameExit(View v,Intent data,int x,int y){
        //Prepare the data used if the user loses the game
        createToast(actualContext.getString(R.string.Defeat_Message));
        TextView time_value_view = (TextView)v.getRootView().findViewById(R.id.time_text);
        int time_counter = Integer.parseInt(time_value_view.getText().toString());
        data.putExtra(actualContext.getString(R.string.End_log_data_key),actualContext.getString(R.string.defeat_check_message,this.alias,String.valueOf(this.minePercentage)+"%",this.num_mines,this.maxTime-time_counter,x,y,this.gameInstance.getUndiscoveredCount()));
    }

    private void createToast(String message){
        LayoutInflater inflater = this.actualContext.getLayoutInflater();
        View layout = inflater.inflate(R.layout.endtoast,(ViewGroup)this.actualContext.findViewById(R.id.toast_layout));
        TextView text = layout.findViewById(R.id.toast_text_id);
        text.setText(message);
        Toast toast = new Toast(this.actualContext);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    private void cancelTimer(){
        if(this.timer!=null) {
            this.timer.cancel();
        }else{
            this.timerFalse.cancel();
        }
    }

    private void startEndActivity(Intent data){
        this.actualContext.startActivity(data);
        this.actualContext.finish();
    }

    private void changeCellsCountState(View v){
        TextView undiscovered_view = (TextView)v.getRootView().findViewById(R.id.undiscovered_text);
        undiscovered_view.setText(actualContext.getString(R.string.cells_to_discover,this.gameInstance.getUndiscoveredCount()));
    }

    private void setDayHourData(Intent data){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(actualContext.getString(R.string.date_format));
        data.putExtra(actualContext.getString(R.string.EndInfo_timeData_key),dateFormat.format(calendar.getTime()));
    }

    public interface GameClickListener{
        void onGameClick(int position, Date startTime, Date endTime);
        void onGameClick(int position, Date startDate, Date endDate, int current_time);
    }

}
