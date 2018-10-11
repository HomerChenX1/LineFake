package com.homer.linefake;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

class FriendAdapter extends BaseAdapter {
    Context context;
    List<Member> memberList;

    // DbHelper.friendList
    FriendAdapter(Context context, List<Member> memberList) {
        this.context = context;
        this.memberList = memberList;
    }

    public void refresh(ArrayList<Member> list) {
        memberList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return memberList.size();
    }

    @Override
    public View getView(int position, View itemView, ViewGroup parent) {
        if (itemView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            itemView = layoutInflater.inflate(R.layout.activity_friend_item, parent, false);
        }

        Member member = memberList.get(position);
        ImageView ivImage = (ImageView) itemView
                .findViewById(R.id.friend_item_icon);
        ivImage.setImageResource(member.getMbrIconIdx());

        TextView tvId = (TextView) itemView
                .findViewById(R.id.friend_item_id);
        tvId.setText(String.valueOf(member.getMbrID()));

        TextView tvName = (TextView) itemView
                .findViewById(R.id.friend_item_alias);
        tvName.setText(member.getMbrAlias());

        TextView tvPhone = (TextView) itemView
                .findViewById(R.id.friend_item_phone);
        tvPhone.setText(member.getMbrPhone());
        return itemView;
    }

    @Override
    public Object getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return memberList.get(position).getMbrID();
    }
}
