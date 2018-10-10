package com.homer.linefake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class InfoActivity extends AppCompatActivity {
    private TextView vMessages;
    private ImageView vIcon;
    private TextView vAlias;
    private Button vFriendTools;
    private ListView vFriendSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setTitle(getString(R.string.app_name) + ":  Owner Info");

        // LinearLayout only use setOnClickListener. Do not set onClick in XML file
        LinearLayout app_layer = (LinearLayout) findViewById (R.id.info_update);
        app_layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(InfoActivity.this, MemberActivity.class);
                Intent intent = new Intent(v.getContext(), MemberActivity.class);
                intent.putExtra("member_mode",1);
                startActivity(intent);
                // Toast.makeText(InfoActivity.this, "Click available", Toast.LENGTH_LONG).show();
            }
        });
        findViews();
        vIcon.setImageResource(DbHelper.owner.getMbrIconIdx());
        vAlias.setText(DbHelper.owner.getMbrAlias());
    }

    private void findViews() {
        vMessages = findViewById(R.id.info_messages);
        vIcon = findViewById(R.id.info_icon);
        vAlias = findViewById(R.id.info_alias);
        vFriendTools = findViewById(R.id.info_friend_tools);

        vFriendSet = findViewById(R.id.info_friend_set);
        vFriendSet.setAdapter(new FriendAdapter(this, DbHelper.friendList));
    }

    public void onClickFriendTools(View view){
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(this,"onRestart wakeup!" ,Toast.LENGTH_LONG).show();
        vIcon.invalidate();
        vAlias.invalidate();
        vFriendSet.invalidate();
    }
}
