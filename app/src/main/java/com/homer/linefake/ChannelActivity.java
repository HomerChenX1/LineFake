package com.homer.linefake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ChannelActivity extends AppCompatActivity {
    private TextView vMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        findViews();
        vMessages.setText("Init:" + DbHelper.owner.toString() + "\n" + DbHelper.master.toString());
    }

    private void findViews() {
        vMessages = findViewById(R.id.channel_messages);
    }
}
