package com.homer.linefake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/*
* http://givemepass.blogspot.com/2015/06/eventbus.html?m=1
* https://www.itread01.com/articles/1487024375.html
* https://m.jb51.net/article/113886.htm
 */
public class FirebaseActivity extends AppCompatActivity {
    private TextView vMessages;
    private FireDbHelper fireDbHelper = null;
    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
        vMessages = findViewById(R.id.firebase_messages);
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        // test FireBase realtime Database

        fireDbHelper = new FireDbHelper();
        fireDbHelper.friendTable.addFriend(1,2);
        fireDbHelper.friendTable.addFriend(1,3);
        fireDbHelper.friendTable.addFriend(2,3);
        fireDbHelper.friendTable.deleteFriend(1,2);
        fireDbHelper.friendTable.queryFriend(3);
        new QueryFriendCheckEnd(this).execute("firstMessage");

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
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // db offline
        eventBus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EbusEvent event){
        switch(event.getEventMsg()){
            case "firstMessage":
                String result = "result:";
                for(int i: fireDbHelper.queryFriendList){

                    result = result + ":" + i;
                }
                vMessages.setText(result);
                break;
            default:
                break;
        }

    }
}
