package com.homer.linefake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {
    private TextView vMessages;
    private ListView vFriendSet;
    private FriendAdapter friendAdapter;
    private TextView vMsgEmail;
    private TextView vMsgId;

    private ListView vFindSet;
    private FriendAdapter findAdapter;
    private ArrayList<Member> findList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        findViews();
        vMessages.setText("Init:" + DbHelper.owner.toString());
    }

    private void findViews() {
        vMessages = findViewById(R.id.friends_messages);
        vMsgEmail = findViewById(R.id.friends_email);
        vMsgId = findViewById(R.id.friends_id);

        vFriendSet = findViewById(R.id.friends_set_list);
        friendAdapter = new FriendAdapter(this, DbHelper.friendList);
        vFriendSet.setAdapter(friendAdapter);
        registerForContextMenu(vFriendSet);

        vFindSet = findViewById(R.id.friends_search_list);
        findAdapter = new FriendAdapter(this, findList);
        vFindSet.setAdapter(findAdapter);
        registerForContextMenu(vFindSet);
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
        if(v == vFindSet){
            menu.setHeaderIcon(R.mipmap.ic_launcher);
            menu.setHeaderTitle("Action：");
            //參數1:群組id, 參數2:itemId, 參數3:item順序, 參數4:item名稱
            menu.add(0, 2, 0, getString(R.string.action_friend_id));
            menu.add(0, 3, 0, getString(R.string.action_friend_delete));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case 0:
                // Toast.makeText(this, "menuInfo.position:" + menuInfo.position + " Add", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Only act in administrator mode", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Member member = (Member) vFriendSet.getItemAtPosition(menuInfo.position);
                // Toast.makeText(this, member.getMbrAlias()+ ":" + " Del", Toast.LENGTH_SHORT).show();
                // delete member.id from   friendTable -> owner.friendSet -> friendList
                // int i = DbHelper.getInstance().deleteFriendOfOwner(member.getMbrID());
                //Toast.makeText(this, String.valueOf(i) + ":" + " Del", Toast.LENGTH_SHORT).show();
                DbHelper.getInstance().deleteFriendOfOwner(member.getMbrID());

                DbHelper.getInstance().deleteChatMsgByMbrId(DbHelper.owner.getMbrID(), member.getMbrID());

                friendAdapter.refresh(DbHelper.friendList);
                break;
            case 2:
                // Toast.makeText(this, "Add find result", Toast.LENGTH_SHORT).show();
                Member memberSelect = (Member) vFindSet.getItemAtPosition(menuInfo.position);
                // Toast.makeText(this, memberSelect.getMbrAlias()+ ":" + " Add", Toast.LENGTH_SHORT).show();
                int i = DbHelper.getInstance().addFriendOfOwner(memberSelect.getMbrID());
                Toast.makeText(this, String.valueOf(i) + ":" + " add by ID", Toast.LENGTH_SHORT).show();
                friendAdapter.refresh(DbHelper.friendList);
                break;
            case 3:
                Toast.makeText(this, "Only act in administrator mode", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
    public void onClickAddById(View view){
        // Toast.makeText(this, "onClickAddById", Toast.LENGTH_SHORT).show();
        String temp = vMsgId.getText().toString().trim();
        if(TextUtils.isEmpty(temp)){ return; }
        int id = Integer.parseInt(temp);
        int i = DbHelper.getInstance().addFriendOfOwner(id);
        Toast.makeText(this, String.valueOf(i) + ":" + " add by ID", Toast.LENGTH_SHORT).show();
        friendAdapter.refresh(DbHelper.friendList);
    }
    public void onClickFindByEmail(View view){
        // Toast.makeText(this, "onClickFindByEmail", Toast.LENGTH_SHORT).show();
        String partEmail = vMsgEmail.getText().toString().trim();
        if(TextUtils.isEmpty(partEmail)){ return; }
        ArrayList<Member> mList = DbHelper.getInstance().queryMemberByEmail(partEmail);
        if(mList.size() > 0) {
            findList = mList;
            findAdapter.refresh(findList);
        }
//        int i = DbHelper.getInstance().addFriendOfOwner(partEmail);
//        Toast.makeText(this, String.valueOf(i) + ":" + " add by e-mail", Toast.LENGTH_SHORT).show();
//        friendAdapter.refresh(DbHelper.friendList);
    }
}
