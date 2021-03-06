package com.example.buscaminas.fragments;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import android.app.Fragment;

import com.example.buscaminas.adapters.CustomButtonAdapter;
import com.example.buscaminas.gameactivities.EndInfoActivity;
import com.example.buscaminas.gamedata.MineSearchGame;
import com.example.buscaminas.R;
import com.example.buscaminas.listeners.GridCellOnClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GridFrag extends Fragment{
    MineSearchGame gameInstance;
    int numColums;
    boolean checkTime;
    String alias;
    int minePercentage;
    GridView gridv;
    CustomButtonAdapter gridAdapter;
    CountDownTimer timer;
    Timer timerFalse;
    int time_counter;
    int maxTime;
    int num_mines;
    Date startDate;

    @Override
    public void onCreate(Bundle savedInstanceState){
        //Set the game activity needed data
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null) {
            //If we do not restore a game activity instance we create/get the needed data
            Intent data = getActivity().getIntent();
            this.numColums = data.getIntExtra(getString(R.string.gridsize_key), 5);
            this.minePercentage = data.getIntExtra(getString(R.string.mine_percent_key),15);
            this.checkTime = data.getBooleanExtra(getString(R.string.time_control_key),false);
            this.alias = data.getStringExtra(getString(R.string.alias_key));
            this.num_mines = this.numColums*this.numColums*this.minePercentage/100;
            this.gameInstance = createGameInstance(data);
            this.time_counter=getResources().getInteger(R.integer.time_limit);
            this.maxTime=this.time_counter;
            this.startDate = new Date();
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.gridfrag,container,false);
        if(savedInstanceState!=null)
            restoreInstanceState(savedInstanceState,view);
        return view;
    }

    public void restoreInstanceState(Bundle savedInstanceState, View view){
        //We restore the data of an already created instance
        this.gameInstance = (MineSearchGame)savedInstanceState.getSerializable(getString(R.string.saved_game_state_key));
        this.maxTime = savedInstanceState.getInt(getString(R.string.max_time_key));
        this.numColums = this.gameInstance.getGridSize();
        this.minePercentage = savedInstanceState.getInt(getString(R.string.mine_percent_key));
        this.num_mines = this.numColums*this.numColums*this.minePercentage/100;
        this.checkTime = savedInstanceState.getBoolean(getString(R.string.check_time_key));
        this.time_counter = savedInstanceState.getInt(getString(R.string.time_counter_key));
        this.startDate = (Date)savedInstanceState.getSerializable(getString(R.string.start_date_key));
        TextView time_view = view.findViewById(R.id.time_text);
        TextView time_following_text = view.findViewById(R.id.seconds_text);
        if(this.checkTime){
            time_view.setTextColor(ContextCompat.getColor(getActivity(),R.color.red));
            time_following_text.setTextColor(ContextCompat.getColor(getActivity(),R.color.red));
            createCountDownTimer(view);
        }else{
            time_view.setTextColor(ContextCompat.getColor(getActivity(),R.color.blue));
            time_following_text.setTextColor(ContextCompat.getColor(getActivity(),R.color.blue));
            createSimpleTimer(view);
        }
        TextView undiscovered_view = view.findViewById(R.id.undiscovered_text);
        undiscovered_view.setText(getString(R.string.cells_to_discover,this.gameInstance.getUndiscoveredCount()));
        startGridView(view, (GridCellOnClickListener.GameClickListener) getActivity());
    }

    public void startGame(GridCellOnClickListener.GameClickListener listener){
        //Start a new game(Without being recovered)
        TextView time_view = getView().findViewById(R.id.time_text);
        TextView time_following_text = getView().findViewById(R.id.seconds_text);
        if(this.checkTime){
            time_view.setTextColor(ContextCompat.getColor(getActivity(),R.color.red));
            time_following_text.setTextColor(ContextCompat.getColor(getActivity(),R.color.red));
            createCountDownTimer(getView());
        }else{
            time_view.setTextColor(ContextCompat.getColor(getActivity(),R.color.blue));
            time_following_text.setTextColor(ContextCompat.getColor(getActivity(),R.color.blue));
            createSimpleTimer(getView());
        }
        TextView undiscovered_view = (TextView)getView().findViewById(R.id.undiscovered_text);
        undiscovered_view.setText(getString(R.string.cells_to_discover,this.gameInstance.getUndiscoveredCount()));//String.valueOf(this.gameInstance.getUndiscoveredCount())+" casillas por descubrir");
        startGridView(getView(),listener);
    }



    private void createSimpleTimer(View view){
        //Create timer used when we do not need to control time
        this.timerFalse =new Timer();
        TextView time_value = view.findViewById(R.id.time_text);
        time_value.setText(String.valueOf(this.time_counter));
        Activity actualAct = getActivity();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                actualAct.runOnUiThread(() -> {
                    TextView time_value1 = view.findViewById(R.id.time_text);
                    int value = Integer.parseInt(time_value1.getText().toString());
                    value -= 1;
                    time_value1.setText(String.valueOf(value));
                });
            }
        };
        this.timerFalse.scheduleAtFixedRate(task,0,1000);
    }

    private void createCountDownTimer(View view){
        //Create timer when we do need to control the time
        MineSearchGame game = this.gameInstance;
        String minePercentage = String.valueOf(this.minePercentage)+"%";
        int max_time = this.time_counter;;
        Activity context = getActivity();
        this.timer=new CountDownTimer(this.time_counter*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TextView time_view = (TextView)view.findViewById(R.id.time_text);
                time_view.setText(String.valueOf(time_counter));
                time_counter-=1;
            }

            private void createToast(String message){
                LayoutInflater inflater = context.getLayoutInflater();
                View layout = inflater.inflate(R.layout.endtoast,(ViewGroup)context.findViewById(R.id.toast_layout));
                TextView text = layout.findViewById(R.id.toast_text_id);
                text.setText(message);
                Toast toast = new Toast(context);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }

            @Override
            public void onFinish() {
                createToast(getString(R.string.Time_exceeded_Message));
                String alias = gameInstance.getUserAlias();
                int timeTaken = max_time-time_counter;
                Intent data = new Intent(getActivity(), EndInfoActivity.class);
                data.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                int undiscovered = gameInstance.getUndiscoveredCount();
                setDayHourData(data);
                data.putExtra(getString(R.string.End_log_data_key),getString(R.string.timeout_log_message,alias,minePercentage,num_mines,timeTaken,undiscovered));
                startActivity(data);
                getActivity().finish();
            }
        }.start();
    }

    private void startGridView(View view, GridCellOnClickListener.GameClickListener listener){
        //Creating the used grid view
        this.gridv = view.findViewById(R.id.game_grid);
        this.gridv.setNumColumns(this.numColums);
        if(this.checkTime) {
            this.gridAdapter = new CustomButtonAdapter(getActivity(),  this.gameInstance, this.timer, this.maxTime, listener, this.startDate);
        }else{
            this.gridAdapter = new CustomButtonAdapter(getActivity(), this.gameInstance, this.timerFalse, this.maxTime, listener, this.startDate);
        }
        this.gridv.setAdapter(gridAdapter);
    }

    private MineSearchGame createGameInstance(Intent data){
        MineSearchGame searchGame = new MineSearchGame(this.numColums,this.alias,this.minePercentage);
        return searchGame;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        //We save some needed data like game state, time, etc
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(getString(R.string.saved_game_state_key),this.gameInstance);
        TextView time = getView().findViewById(R.id.time_text);
        savedInstanceState.putInt(getString(R.string.time_counter_key),Integer.parseInt(time.getText().toString()));
        savedInstanceState.putBoolean(getString(R.string.check_time_key),this.checkTime);
        savedInstanceState.putInt(getString(R.string.max_time_key),this.maxTime);
        savedInstanceState.putInt(getString(R.string.mine_percent_key),this.minePercentage);
        savedInstanceState.putSerializable(getString(R.string.start_date_key),this.startDate);
        if(this.checkTime){
            this.timer.cancel();
        }else{
            this.timerFalse.cancel();
        }
    }




    private void setDayHourData(Intent data){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(getActivity().getString(R.string.date_format));
        data.putExtra(getActivity().getString(R.string.EndInfo_timeData_key),dateFormat.format(calendar.getTime()));
    }


}
