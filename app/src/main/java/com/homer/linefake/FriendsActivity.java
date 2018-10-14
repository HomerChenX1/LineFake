package com.homer.linefake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        vFriendSet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // to do
                Member member = (Member) parent.getItemAtPosition(position);
                String text = member.getMbrAlias();
                Toast.makeText(view.getContext(), text, Toast.LENGTH_SHORT).show();
                // use content menu or popup menu
            }
        });
    }
}
