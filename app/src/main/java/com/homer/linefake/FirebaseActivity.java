package com.homer.linefake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/*
* http://givemepass.blogspot.com/2015/06/eventbus.html?m=1
* https://www.itread01.com/articles/1487024375.html
* https://m.jb51.net/article/113886.htm
 */
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
        fireDbHelper.friendTable.addFriend(1,2);
        fireDbHelper.friendTable.addFriend(1,3);
        fireDbHelper.friendTable.addFriend(2,3);
        fireDbHelper.friendTable.deleteFriend(1,2);
        fireDbHelper.vMessages = vMessages;
        Integer[] temp = fireDbHelper.friendTable.queryFriend(3);
        String result = "result:";
        for(int i : temp){
            result = result + i;
        }
        vMessages.setText(result);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // db offline
        Log.d("HomerfbAct", "onPause:" + FireDbHelper.queryFriendTotalCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // db online
        Log.d("HomerfbAct", "onResume:" + FireDbHelper.queryFriendTotalCount);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("HomerfbAct", "onRestart:" + FireDbHelper.queryFriendTotalCount);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("HomerfbAct", "onStart:" + FireDbHelper.queryFriendTotalCount);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // db offline
    }
}
