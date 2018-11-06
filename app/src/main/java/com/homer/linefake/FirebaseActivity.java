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
    private boolean inDebug = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
        vMessages = findViewById(R.id.firebase_messages);
        // setup EventBus
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
        eventBus = EventBus.getDefault();
        eventBus.register(this);

        // test FireBase realtime Database
        fireDbHelper = new FireDbHelper();
        if(inDebug) {
            fireDbHelper.friendTable.addFriend(1, 2);
            fireDbHelper.friendTable.addFriend(1, 3);
            fireDbHelper.friendTable.addFriend(2, 3);
            // fireDbHelper.friendTable.deleteFriend(1,2);
            fireDbHelper.friendTable.queryFriend(3);
            new QueryFriendCheckEnd(this).execute("queryFriend");
        }
        if(inDebug) {
            ChatMsg testChatMsg = new ChatMsg(1, 2, ChatMsg.chatTypeText, "This is test12!");
            fireDbHelper.chatMsgTable.addChat(testChatMsg);
            fireDbHelper.chatMsgTable.addChat(new ChatMsg(2, 1, ChatMsg.chatTypeText, "OK21!"));
            fireDbHelper.chatMsgTable.addChat(new ChatMsg(1, 3, ChatMsg.chatTypeText, "This is test13!"));
            fireDbHelper.chatMsgTable.addChat(new ChatMsg(3, 1, ChatMsg.chatTypeText, "OK31!"));
            fireDbHelper.chatMsgTable.addChat(new ChatMsg(1, 4, ChatMsg.chatTypeText, "This is test14!"));
            fireDbHelper.chatMsgTable.addChat(new ChatMsg(4, 1, ChatMsg.chatTypeText, "OK41!"));
            fireDbHelper.chatMsgTable.addChat(new ChatMsg(2, 3, ChatMsg.chatTypeText, "This is test23!"));
            fireDbHelper.chatMsgTable.addChat(new ChatMsg(3, 2, ChatMsg.chatTypeText, "OK32!"));
            // fireDbHelper.chatMsgTable.deleteChat(testChatMsg.getChatId());
            // Log.d("HomerfbDeleteChat", "idcmd=" + testChatMsg.getChatId());
        }
        fireDbHelper.chatMsgTable.deleteChatMsgByMbrId(1);
        // new QueryFriendCheckEnd(this).execute("queryFriend");
        // new QueryFriendCheckEnd(this).execute("queryFriend");

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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
            case "queryFriend":
                FireDbHelper.queryFriendTotalCount = 100;
                // do List refresh
                String result = "result:";
                for(int i: fireDbHelper.queryFriendList){

                    result = result + ":" + i;
                }
                vMessages.setText(result);
                // set counter back
                FireDbHelper.queryFriendTotalCount = 100;
                break;
            default:
                break;
        }

    }
}
