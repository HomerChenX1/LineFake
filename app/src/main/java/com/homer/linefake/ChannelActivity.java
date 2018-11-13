package com.homer.linefake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;

/*
DbHelper.getInstance().generateChannel(DbHelper.master.getMbrID()
void watchChat(boolean owner){
* */
// will use RecyclerView
public class ChannelActivity extends AppCompatActivity {
    private TextView vMessages;
    private ImageView vMasterIcon;
    private TextView vMasterAlias;
    private TextView vOwnerAlias;
    private ImageView vOwnerIcon;
    private EditText vMasterChat;
    // private Button vMasterChatBtn;
    private EditText vOwnerChat;
    // private Button vOwnerChatBtn;

    private RecyclerView vChannel;
    private ChnAdapter chnAdapter;
    ArrayList<ChatMsg> channel;

    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        findViews();
        vMessages.setText("Init:" + DbHelper.owner.toString() + "\n" + DbHelper.master.toString());
        vMasterIcon.setImageResource(DbHelper.master.getMbrIconIdx());
        vMasterAlias.setText(DbHelper.master.getMbrAlias());
        vOwnerAlias.setText(DbHelper.owner.getMbrAlias());
        vOwnerIcon.setImageResource(DbHelper.owner.getMbrIconIdx());
        channel = DbHelper.getInstance().generateChannel(DbHelper.master.getMbrID(), DbHelper.owner.getMbrID());
        new GenerateChannelCheckEnd(this).execute("generateChannel");
        vMessages.setText("channel length:" + channel.size());


        // 設置RecyclerView為列表型態
        vChannel.setLayoutManager(new LinearLayoutManager(this));
        // 設置格線
        vChannel.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // 將資料交給adapter
        chnAdapter = new ChnAdapter(channel);

        // 設置adapter給recycler_view
        vChannel.setAdapter(chnAdapter);
        vChannel.scrollToPosition(channel.size()-1);
    }

    private void findViews() {
        vMessages = findViewById(R.id.channel_messages);
        vMasterIcon = findViewById(R.id.channel_master_icon);
        vMasterAlias = findViewById(R.id.channel_master_alias);
        vOwnerAlias = findViewById(R.id.channel_owner_alias);
        vOwnerIcon = findViewById(R.id.channel_owner_icon);
        vMasterChat = findViewById(R.id.channel_master_chat);
        // vMasterChatBtn = findViewById(R.id.channel_master_chat_add);
        vOwnerChat = findViewById(R.id.channel_owner_chat);
        // vOwnerChatBtn = findViewById(R.id.channel_owner_chat_add);

        // 連結元件
        vChannel = findViewById(R.id.channel_recycler);

    }

    public void onClickMasterChat(View view){
        String temp = vMasterChat.getText().toString().trim();
        Toast.makeText(view.getContext(), "MasterChat", Toast.LENGTH_SHORT).show();
        if(TextUtils.isEmpty(temp)){
            vMasterChat.requestFocus();
            return;
        }
        ChatMsg cm = new ChatMsg(DbHelper.master.getMbrID(), DbHelper.owner.getMbrID(), ChatMsg.chatTypeText, temp);
        chnAdapter.addChatMsgToChn(cm);
        DbHelper.getInstance().addChat(cm);
        vChannel.scrollToPosition(channel.size()-1);
        vMessages.setText(cm.toString());
    }

    public void onClickOwnerChat(View view){
        String temp = vOwnerChat.getText().toString().trim();
        Toast.makeText(view.getContext(), "OwnerChat", Toast.LENGTH_SHORT).show();
        if(TextUtils.isEmpty(temp)){
            vOwnerChat.requestFocus();
            return;
        }
        ChatMsg cm = new ChatMsg(DbHelper.owner.getMbrID(), DbHelper.master.getMbrID(), ChatMsg.chatTypeText, temp);
        chnAdapter.addChatMsgToChn(cm);
        DbHelper.getInstance().addChat(cm);
        vChannel.scrollToPosition(channel.size()-1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO : remove watch 2 listeners
        //ValueEventListener listener = new ValueEventListener();
        //ref.addValueEventListener(listener);
        //ref.removeEventListener(listener);
        
        eventBus.unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EbusEvent event) {
        switch (event.getEventMsg()) {
            case "countFinish":
                Log.d("MainActivity", "Now it happenes countFinish");
                break;
            case "generateChannel":
                DbHelper.getInstance().fireDbHelper.genChannelList1.addAll(DbHelper.getInstance().fireDbHelper.genChannelList2);
                Collections.sort(DbHelper.getInstance().fireDbHelper.genChannelList1);
                chnAdapter.notifyDataSetChanged();
                //Log.d("HomerfbGenChannel", DbHelper.getInstance().fireDbHelper.genChannelList1.get(0).toString());
                //Log.d("HomerfbGenChannel", DbHelper.getInstance().fireDbHelper.genChannelList1.get(1).toString());
                //Log.d("HomerfbGenChannel", DbHelper.getInstance().fireDbHelper.genChannelList1.get(2).toString());
                //Log.d("HomerfbGenChannel", DbHelper.getInstance().fireDbHelper.genChannelList1.get(3).toString());

                Log.d("HomerfbWatchChat", "watch start!");
                DbHelper.getInstance().fireDbHelper.chatMsgTable.watchChat(false);
                break;
            default:
                break;
        }
    }
}
