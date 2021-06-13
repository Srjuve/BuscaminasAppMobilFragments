package com.example.buscaminas.gameactivities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.buscaminas.R;

public class EndInfoActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent data = getIntent();
        String DayHourData = data.getStringExtra(getApplicationContext().getString(R.string.EndInfo_timeData_key));
        String LogData = data.getStringExtra(getApplicationContext().getString(R.string.End_log_data_key));

        TextView DayHourTextView = findViewById(R.id.time_values);
        TextView LogTextView = findViewById(R.id.log_values);

        DayHourTextView.setText(DayHourData);
        LogTextView.setText(LogData);

        Button sendMailButton = findViewById(R.id.send_mail_button);
        Button startGameButton = findViewById(R.id.start_game_result_button);
        Button exitButton = findViewById(R.id.exit_result_button);

        sendMailButton.setOnClickListener(this);
        startGameButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //Depending on the clicked button we prepare the data to be sent with mail, start a new game or exit the aplication
        switch(v.getId()){
            case R.id.send_mail_button:
                Intent in = new Intent(Intent.ACTION_SENDTO);
                in.setType(getString(R.string.mail_type));
                in.setData(Uri.parse(getString(R.string.mail_uri_data)));
                TextView mailreView = findViewById(R.id.mail_re);
                TextView LogTextView = findViewById(R.id.log_values);
                TextView DayHourView = findViewById(R.id.time_values);
                in.putExtra(Intent.EXTRA_EMAIL,new String[]{mailreView.getText().toString()}); //Modificar destinatari
                in.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.mail_subject));
                in.putExtra(Intent.EXTRA_TEXT,DayHourView.getText().toString()+" "+LogTextView.getText().toString());
                startActivity(in);
                break;
            case R.id.start_game_result_button:
                in = new Intent(this, GameConfigActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                finish();
                break;
            case R.id.exit_result_button:
                finish();
                break;
        }
    }
}
