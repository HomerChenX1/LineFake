package com.homer.linefake;

import android.graphics.drawable.Icon;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        findViews();
        vMessages.setText("Init:" + DbHelper.owner.toString() + "\n" + DbHelper.master.toString());
        vMasterIcon.setImageResource(DbHelper.master.getMbrIconIdx());
        vMasterAlias.setText(DbHelper.master.getMbrAlias());
        vOwnerAlias.setText(DbHelper.owner.getMbrAlias());
        vOwnerIcon.setImageResource(DbHelper.owner.getMbrIconIdx());
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
    }

    public void onClickMasterChat(View view){
        String temp = vMasterChat.getText().toString().trim();
        if(TextUtils.isEmpty(temp)){
            vMasterChat.requestFocus();
            return;
        }
        Toast.makeText(view.getContext(), "MasterChat", Toast.LENGTH_SHORT).show();
    }

    public void onClickOwnerChat(View view){
        String temp = vOwnerChat.getText().toString().trim();
        if(TextUtils.isEmpty(temp)){
            vOwnerChat.requestFocus();
            return;
        }
        Toast.makeText(view.getContext(), "OwnerChat", Toast.LENGTH_SHORT).show();
    }
}
