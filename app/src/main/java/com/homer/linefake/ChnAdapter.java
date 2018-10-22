package com.homer.linefake;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ChnAdapter extends RecyclerView.Adapter<ChnAdapter.ViewHolder>
{
    private List<ChatMsg> mDataSet;

    public enum ITEM_TYPE
    {
        STYLE_MASTER,
        STYLE_OWNER
    }

    ChnAdapter(List<ChatMsg> data)
    {
        mDataSet = data;
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        ViewHolder(View itemView) {
            super(itemView);
        }
        String changeTimeStart(String str, int i){
            String [] items = str.split(" ");
            // System.out.println("changeTimeStart"+items[0] + "End");
            String retStr = (i & ChatMsg.chatTypeRead) >0 ? "read\n":"\n" ;
            return retStr + items[1];
        }

        void onLongClickEvent(View view) {
            final int pos = getAdapterPosition();
            final View mView = view;
            // String str = mDataSet.get(pos).getTxtMsg();
            // Toast.makeText(view.getContext(), "onLongClick:" + str, Toast.LENGTH_SHORT).show();

            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.popup_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    // System.out.println(item.getTitle());
                    // ChannelActivity.vMessages.setText(item.getTitle() + ":" + pos);
                    // Toast.makeText(mView.getContext(), item.getTitle() + ":" + pos, Toast.LENGTH_SHORT).show();
                    switch (item.getItemId()){
                        case R.id.chn_item_delete:
                            // Toast.makeText(mView.getContext(), "Delete ChatMsg here", Toast.LENGTH_SHORT).show();
                            removeChatMsgFromChn(pos);
                            break;
                        case R.id.chn_item_block:
                        default:
                            Toast.makeText(mView.getContext(), R.string.action_chn_block_item, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
    }
    public class ViewHolderMaster extends ViewHolder {
        ImageView chnItemMasterIcon;
        TextView chnItemMasterTxtMsg;
        TextView chnItemMasterTimeStart;

        ViewHolderMaster(View v)
        {
            super(v);
            chnItemMasterIcon = v.findViewById(R.id.chn_item_master_icon);
            chnItemMasterTxtMsg = v.findViewById(R.id.chn_item_master_txt_msg);
            chnItemMasterTimeStart = v.findViewById(R.id.chn_item_master_time_start);
            chnItemMasterTxtMsg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onLongClickEvent(view);
                    return true;
                }
            });
        }
    }
    public class ViewHolderOwner extends ViewHolder {
        TextView chnItemOwnerTxtMsg;
        TextView chnItemOwnerTimeStart;

        ViewHolderOwner(View v)
        {
            super(v);
            chnItemOwnerTxtMsg = v.findViewById(R.id.chn_item_owner_txt_msg);
            chnItemOwnerTimeStart = v.findViewById(R.id.chn_item_owner_time_start);
            chnItemOwnerTxtMsg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onLongClickEvent(view);
                    return true;
                }
            });
        }
    }

    @Override
    @Nullable
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        ChatMsg cm = mDataSet.get(position);
        if (holder instanceof ViewHolderMaster)
        {
            // set item values of master
            ((ViewHolderMaster) holder).chnItemMasterIcon.setImageResource(DbHelper.master.getMbrIconIdx());
            ((ViewHolderMaster) holder).chnItemMasterTxtMsg.setText(cm.getTxtMsg());
            String str = holder.changeTimeStart(cm.getTimeStart(), ChatMsg.chatTypeRead);
            ((ViewHolderMaster) holder).chnItemMasterTimeStart.setText(str);
        }
        else if (holder instanceof ViewHolderOwner)
        {
            // set item values of owner
            ((ViewHolderOwner) holder).chnItemOwnerTxtMsg.setText(cm.getTxtMsg());
            String str = holder.changeTimeStart(cm.getTimeStart(), ChatMsg.chatTypeRead);
            ((ViewHolderOwner) holder).chnItemOwnerTimeStart.setText(str);
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

    // 新增項目
    void addChatMsgToChn(ChatMsg cm) {
        mDataSet.add(cm);
        // need to change chatMsgTable
        notifyItemInserted(mDataSet.size() - 1);
    }

    // 刪除項目
    void removeChatMsgFromChn(int position){
        // delete chatMsgTable   deleteChat(int chatId)
        DbHelper.getInstance().deleteChat(mDataSet.get(position).getChatId());
        mDataSet.remove(position);
        notifyItemRemoved(position);
    }

}
