package com.homer.linefake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/* Firebase finish */
public class InfoActivity extends AppCompatActivity {
    private TextView vMessages;
    private ImageView vIcon;
    private TextView vAlias;
    // private Button vFriendTools;
    private ListView vFriendSet;
    private FriendAdapter friendAdapter;

    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setTitle(getString(R.string.app_name) + ":  Owner Info");

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        // LinearLayout only use setOnClickListener. Do not set onClick in XML file
        LinearLayout app_layer = findViewById (R.id.info_update);
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
        // vFriendTools = findViewById(R.id.info_friend_tools);

        vFriendSet = findViewById(R.id.info_friend_set);
        friendAdapter = new FriendAdapter(this, DbHelper.friendList);
        vFriendSet.setAdapter(friendAdapter);
        vFriendSet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // to do
                Member member = (Member) parent.getItemAtPosition(position);
                // String text = member.getMbrAlias();
                // Toast.makeText(view.getContext(), text, Toast.LENGTH_SHORT).show();
                member.copyTo(DbHelper.master);
                Intent intent = new Intent(view.getContext(), ChannelActivity.class);
                startActivity(intent);
            }
        });
//        vFriendSet.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
//                // to do
//                Member member = (Member) parent.getItemAtPosition(position);
//                String text = member.getMbrEmail();
//                Toast.makeText(view.getContext(), text, Toast.LENGTH_SHORT).show();
//                // true 表示不再丟給 onItemClick 處理，false 則會再執行 onItemClick，如果有的話
//                return true;
//            }
//        });
    }

    public void onClickFriendTools(View view){
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        // for backpress button
        super.onRestart();
        if(DbHelper.multipleBack>0){
            // For member deleted
            --DbHelper.multipleBack;
            finish();
        }
        Toast.makeText(this,"onRestart wakeup!" ,Toast.LENGTH_LONG).show();
        vIcon.setImageResource(DbHelper.owner.getMbrIconIdx());
        vAlias.setText(DbHelper.owner.getMbrAlias());
        friendAdapter.refresh(DbHelper.friendList);
        // vIcon.invalidate();
        // vAlias.invalidate();
        // vFriendSet.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EbusEvent event) {
        switch (event.getEventMsg()) {
            case "countFinish":
                Log.d("MainActivity", "Now it happenes countFinish");
                break;
            default:
                break;
        }
    }
}
