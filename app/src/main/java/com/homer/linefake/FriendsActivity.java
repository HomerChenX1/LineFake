package com.homer.linefake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsActivity extends AppCompatActivity {
    private TextView vMessages;
    private ListView vFriendSet;
    private FriendAdapter friendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        findViews();
        vMessages.setText("Init:" + DbHelper.owner.toString());
    }

    private void findViews() {
        vMessages = findViewById(R.id.friends_messages);

        vFriendSet = findViewById(R.id.friends_set_list);
        friendAdapter = new FriendAdapter(this, DbHelper.friendList);
        vFriendSet.setAdapter(friendAdapter);

        registerForContextMenu(vFriendSet);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo); // ??? in front or return
        if (v == vFriendSet) {
            menu.setHeaderIcon(R.mipmap.ic_launcher);
            menu.setHeaderTitle("Action：");
            //參數1:群組id, 參數2:itemId, 參數3:item順序, 參數4:item名稱
            menu.add(0, 0, 0, getString(R.string.action_friend_id));
            menu.add(0, 1, 0, getString(R.string.action_friend_delete));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case 0:
                Toast.makeText(this, "menuInfo.position:" + menuInfo.position + " Add", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(this, "menuInfo.position:" + menuInfo.position + " Del", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
}
