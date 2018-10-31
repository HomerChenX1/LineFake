package com.homer.linefake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseActivity extends AppCompatActivity {
    private TextView vMessages;
    private StringBuilder msgBody = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
        vMessages = findViewById(R.id.firebase_messages);
        // test FireBase realtime Database
        // Default database URL  : https://linefake-ad479.firebaseio.com/
        // your-project-id : linefake-ad479
        //Default hosting subdomain — your-project-id.firebaseapp.com

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//
//        myRef.setValue("Hello, World!");
        DatabaseReference myRef = database.getReference("memberTable");
        myRef = database.getReference("friendTable");
        myRef.setValue("Hello, World!");
        myRef = database.getReference("chatMsgTable");
        myRef.setValue("Hello, World!");
        myRef = database.getReference("rcvBus");
        myRef.setValue("Hello, World!");

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
        // dboffline
    }
}
