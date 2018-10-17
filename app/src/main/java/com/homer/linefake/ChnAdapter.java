package com.homer.linefake;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class ChnAdapter extends RecyclerView.Adapter<ChnAdapter.ViewHolder>
{
    private List<ChatMsg> mDataSet;

    public static enum ITEM_TYPE
    {
        STYLE_MASTER,
        STYLE_OWNER
    }

    public ChnAdapter(List<ChatMsg> data)
    {
        mDataSet = data;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
        protected String changeTimeStart(String str, int i){
            List<String> items = Arrays.asList(str.split("\\s*,\\s*"));
            if((i & ChatMsg.chatTypeRead) >0 ){
                items.set(0,"read\n");
            }else{
                items.set(0,"\n");
            }
            return items.toString();
        }
    }
    public class ViewHolderMaster extends ViewHolder {
        ImageView chnItemMasterIcon;
        TextView chnItemMasterTxtMsg;
        TextView chnItemMasterTimeStart;

        public ViewHolderMaster(View v)
        {
            super(v);
            chnItemMasterIcon = v.findViewById(R.id.chn_item_master_icon);
            chnItemMasterTxtMsg = v.findViewById(R.id.chn_item_master_txt_msg);
            chnItemMasterTimeStart = v.findViewById(R.id.chn_item_master_time_start);
        }
    }
    public class ViewHolderOwner extends ViewHolder {
        TextView chnItemOwnerTxtMsg;
        TextView chnItemOwnerTimeStart;

        public ViewHolderOwner(View v)
        {
            super(v);
            chnItemOwnerTxtMsg = v.findViewById(R.id.chn_item_owner_txt_msg);
            chnItemOwnerTimeStart = v.findViewById(R.id.chn_item_owner_time_start);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        ViewHolder mViewHolder = null;
        if (viewType == ITEM_TYPE.STYLE_MASTER.ordinal())
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chn_item_master, parent, false);
            mViewHolder = new ViewHolderMaster(view);
        }
        else if(viewType == ITEM_TYPE.STYLE_OWNER.ordinal())
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chn_item_owner, parent, false);
            mViewHolder = new ViewHolderOwner(view);
        }

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        ChatMsg cm = mDataSet.get(position);
        if (holder instanceof ViewHolderMaster)
        {
            // set item values of master
            ((ViewHolderMaster) holder).chnItemMasterIcon.setImageResource(DbHelper.master.getMbrIconIdx());
            ((ViewHolderMaster) holder).chnItemMasterTxtMsg.setText(cm.getTxtMsg());
            ((ViewHolderMaster) holder).chnItemMasterTimeStart.setText(cm.getTimeStart());
        }
        else if (holder instanceof ViewHolderOwner)
        {
            // set item values of owner
            ((ViewHolderOwner) holder).chnItemOwnerTxtMsg.setText(cm.getTxtMsg());
            ((ViewHolderOwner) holder).chnItemOwnerTimeStart.setText(cm.getTimeStart());
        }
    }

    @Override
    public int getItemCount()
    {
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        // if mData.getMbrIDfrom == master
        ChatMsg cm = mDataSet.get(position);
        if(cm.getMbrIdFrom() == DbHelper.master.getMbrID())
        {
            return ITEM_TYPE.STYLE_MASTER.ordinal();
        }
        return ITEM_TYPE.STYLE_OWNER.ordinal();
    }
}
