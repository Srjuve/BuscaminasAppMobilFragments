package com.example.buscaminas.fragments;

import android.content.Intent;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.Fragment;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buscaminas.R;

import java.util.Timer;

public class LogFrag extends Fragment {
    int numColums;
    boolean checkTime;
    String alias;
    int minePercentage;
    int num_mines;

    @Override
    public void onCreate(Bundle savedInstanceState){
        //Set the game activity needed data
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.logfrag,container,false);
        startLogView(view);
        if(savedInstanceState!=null) {
            TextView log_data_view = view.findViewById(R.id.log_events);
            log_data_view.setText(savedInstanceState.getString(getString(R.string.LogData_key)));
        }

        return view;
    }

    private void startLogView(View view){
        //Start the log view(Without being recovered)
        TextView log_data = view.findViewById(R.id.log_initialInfo);
        Intent data = getActivity().getIntent();
        this.numColums = data.getIntExtra(getString(R.string.gridsize_key), 5);
        this.minePercentage = data.getIntExtra(getString(R.string.mine_percent_key),15);
        this.checkTime = data.getBooleanExtra(getString(R.string.time_control_key),false);
        this.alias = data.getStringExtra(getString(R.string.alias_key));
        this.num_mines = this.numColums*this.numColums*this.minePercentage/100;
        String logTitle = getString(R.string.log_view_start_text,alias,minePercentage+"%",this.num_mines);
        if(this.checkTime){
            logTitle+=getString(R.string.time_control_enabled);
        }else{
            logTitle+=getString(R.string.time_control_disabled);
        }
        log_data.setText(logTitle);
        TextView log_data_view = view.findViewById(R.id.log_events);
        log_data_view.setMovementMethod(new ScrollingMovementMethod());
    }


    public void setLogData(int position, Date startDate, Date endDate){
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.simple_date_format));
        TextView log_data_view = getView().findViewById(R.id.log_events);
        String last_log_data = log_data_view.getText().toString();
        log_data_view.setText(last_log_data+String.valueOf(position)+"\n"+formatter.format(startDate)+"\n"+formatter.format(endDate)+"\n");
    }

    public void setLogData(int position, Date startDate, Date endDate, int current_time){
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.simple_date_format));
        int x=position/numColums;
        int y=position%numColums;
        TextView log_data_view = getView().findViewById(R.id.log_events);
        String last_log_data = log_data_view.getText().toString();
        log_data_view.setText(last_log_data+getString(R.string.log_text,String.valueOf(x),String.valueOf(y),formatter.format(startDate),formatter.format(endDate),String.valueOf(current_time)));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //We save some needed data like game state, time, etc
        super.onSaveInstanceState(savedInstanceState);
        TextView log_data_view = getView().findViewById(R.id.log_events);
        savedInstanceState.putString(getString(R.string.LogData_key),log_data_view.getText().toString());
    }
}
