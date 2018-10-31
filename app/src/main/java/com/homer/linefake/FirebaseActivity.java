package com.homer.linefake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class FirebaseActivity extends AppCompatActivity {
    private TextView vMessages;
    private FireDbHelper fireDbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
        vMessages = findViewById(R.id.firebase_messages);
        // test FireBase realtime Database

        fireDbHelper = new FireDbHelper();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // db offline
    }

    @Override
    protected void onResume() {
        super.onResume();
        // db online
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // db offline
    }
}
