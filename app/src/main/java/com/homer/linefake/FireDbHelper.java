package com.homer.linefake;

// Default database URL  : https://linefake-ad479.firebaseio.com/
// your-project-id : linefake-ad479
//Default hosting subdomain — your-project-id.firebaseapp.com

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

class FireDbHelper {
    private FirebaseDatabase db;
    FriendTable friendTable = new FriendTable();
    ChatMsgTable chatMsgTable = new ChatMsgTable();
    int deleteFriendCnt = 0;
    static int queryFriendTotalCount = 100;
    ArrayList<Integer> queryFriendList = new ArrayList<>();
    static int destChatMsgMbrIdListCnt1 = 100;
    static int destChatMsgMbrIdListCnt2 = 100;
    ArrayList<Integer> destChatMsgMbrIdList1 = new ArrayList<>();
    ArrayList<ChatMsg> destChatMsgMbrIdList2 = new ArrayList<>();

    class FriendTable {
        String TABLE_NAME = "FriendTable";
        String [] nCOLS = {"mbrId","friendSet"};

        void create(){
            ArrayList<String> temp = new ArrayList<>();
            //TODO check table exist or not ?

            DatabaseReference myRef = db.getReference(TABLE_NAME + "/nCOLS");
            for(String s: nCOLS) temp.add(s);
            myRef.setValue(temp);
        }

        void addFriend(int ownerId, int masterId){
            // to ownerId's friend add masterId
            DatabaseReference myRef = db.getReference(TABLE_NAME + "/" + ownerId);
            DatabaseReference pushedRef = myRef.push();
            String pKey1 = pushedRef.getKey();
            pushedRef.setValue(masterId);

            // to masterId's friend add ownerId
            myRef = db.getReference(TABLE_NAME + "/" + masterId);
            pushedRef = myRef.push();
            String pKey2 = pushedRef.getKey();
            pushedRef.setValue(ownerId);
            //Log.d("HomerfbAddFriend", pKey1 + ":" + pKey2);
            // TODO send notify to masterId that ownerId is add
        }
        int deleteFriend(final int ownerId, final int masterId) {
            deleteFriendCnt = 0;
            // to ownerId's friend delete masterId
            // Log.d("HomerfbDeleteFriend", "org:" + ownerId + ":" + masterId );
            DatabaseReference myRef = db.getReference(TABLE_NAME);
            Query query = myRef.child(String.valueOf(ownerId));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        if((long)ds.getValue() == masterId) {
                            deleteFriendCnt++;
                            // Log.d("HomerfbDeleteFriend", "child:" + ds.getValue());
                            // Log.d("HomerfbDeleteFriend", "total del:" + deleteFriendCnt);
                            ds.getRef().removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("HomerfbDeleteFriend", "onCancelled", databaseError.toException());
                }
            });

            // to masterId's friend delete ownerId
            query = myRef.child(String.valueOf(masterId));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        if((long)ds.getValue() == ownerId) {
                            deleteFriendCnt++;
                            // Log.d("HomerfbDeleteFriend", "child:" + ds.getValue());
                            // Log.d("HomerfbDeleteFriend", "total del:" + deleteFriendCnt);
                            ds.getRef().removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("HomerfbDeleteFriend", "onCancelled", databaseError.toException());
                }
            });
            // Log.d("HomerfbDeleteFriend", "total del:" + deleteFriendCnt);
            // TODO send notify to masterId that owneridis disappear
            return deleteFriendCnt; // this deleteFriendCnt  will be wrong because async
        }

        Integer[] queryFriend(final int ownerId){

            // read ownerId's friend then send out
            queryFriendTotalCount = 100;
            DatabaseReference myRef = db.getReference(TABLE_NAME);
            Query query = myRef.child(String.valueOf(ownerId));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    queryFriendTotalCount = (int) dataSnapshot.getChildrenCount();
                    // Log.d("HomerfbQueryFriend", "total cnt:" + queryFriendTotalCount);
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        int temp2 = ds.getValue(Integer.class);
                        queryFriendList.add(temp2);
                        queryFriendTotalCount--;
                        // Log.d("HomerfbQueryFriend", "cnt:" + queryFriendList.size() + ":" + temp2 + ":" + queryFriendTotalCount);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("HomerfbQueryFriend", "onCancelled", databaseError.toException());
                }
            });
            // the problem is need to wait complete temp.size() == queryFriendTotalCount
            // queryFriendTotalCount == temp.size()

            // directly pass here
            // Log.d("HomerfbQueryFriend", "final:" + temp.size());
            return queryFriendList.toArray(new Integer[0]);
        }
    }
    class ChatMsgTable extends FriendTable {
        ChatMsgTable() {
            TABLE_NAME = "ChatMsgTable";
            nCOLS = new String[] {"chatId", "timeStart", "mbrIdFrom", "mbrIdTo", "chatType", "txtMsg"};
        }

        void addChat(ChatMsg x){
            int chatId = randInt();
            x.setChatId(chatId);
            DatabaseReference myRef = db.getReference(TABLE_NAME + "/" + String.valueOf(chatId));
            myRef.setValue(x);
            // Log.d("HomerfbAddChat", x.toString());
        }
        void deleteChat(int chatId){
            DatabaseReference myRef = db.getReference(TABLE_NAME + "/" + String.valueOf(chatId));
            myRef.setValue(null);
            // Log.d("HomerfbDeleteChat", "id=" + chatId);
        }
        void deleteChatMsgByMbrId(final int mbrId){
            // ArrayList<ChatMsg> destChatMsgMbrIdList1 destChatMsgMbrIdList2
            destChatMsgMbrIdListCnt1 = 100;
            // search x.getMbrIdFrom()==mbrId or
            DatabaseReference myRef = db.getReference(TABLE_NAME);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    destChatMsgMbrIdListCnt1 = (int) dataSnapshot.getChildrenCount();
                    Log.d("HomerfbDelChatMsgMbrId", "total cnt:" + destChatMsgMbrIdListCnt1);
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        destChatMsgMbrIdListCnt1--;
                        // have key = ds.getKey()
                        // Now only item by item read,
                        // Integer temp = ds.child("chatId").getValue(Integer.class);
                        // ChatMsg temp = ds.getValue(ChatMsg.class); do not know why fail
                        ChatMsg temp = new ChatMsg();
                        Integer temp2 = ds.child("chatId").getValue(Integer.class);
                        if(temp2 != null) {
                            // IgnoreExtraProperties no effect, the timeStart to be saved as timeStartLong
                            // and make ds.getValue(ChatMsg.class) fail
                            temp.setChatId(ds.child("chatId").getValue(Integer.class));
                            temp.setTimeStart(ds.child("timeStartLong").getValue(Long.class));
                            temp.setMbrIdFrom(ds.child("mbrIdFrom").getValue(Integer.class));
                            temp.setMbrIdTo(ds.child("mbrIdTo").getValue(Integer.class));
                            temp.setChatType(ds.child("chatType").getValue(Integer.class));
                            temp.setTxtMsg(ds.child("txtMsg").getValue(String.class));
                            Log.d("HomerfbDelChatMsgMbrId", temp.toString());
                        }




//                        ChatMsg temp = ds.child(ds.getKey()).getValue(ChatMsg.class);
//
//                        if((temp.getMbrIdTo()==mbrId) || temp.getMbrIdFrom()==mbrId){
//                            destChatMsgMbrIdList1.add(temp.getChatId());
//                            Log.d("HomerfbDelChatMsgMbrId", "cnt:" + destChatMsgMbrIdList1.size() + ":" + ":" + destChatMsgMbrIdListCnt1);
//                            Log.d("HomerfbDelChatMsgMbrId", "From:" + temp.getMbrIdFrom() + "To:" + temp.getMbrIdTo());
//                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("HomerfbQueryFriend", "onCancelled", databaseError.toException());
                }
            });


            // search x.getMbrIdTo()==mbrId
            destChatMsgMbrIdListCnt2 = 100;

        }
        void deleteChatMsgByMbrId(int ownerId, int mbrId){
            // ownerId,  mbrId
            // mbrId ownerId
        }
        ArrayList<ChatMsg> generateChannel(int masterId, int ownerId){
            return null;
        }

    }

    public FireDbHelper() {
        db = FirebaseDatabase.getInstance();
        onCreate();
    }

    public FirebaseDatabase getDb() { return db; }

    public void onCreate() {
        // build tables
        friendTable.create();
        chatMsgTable.create();
    }
    void onDestroy(){ db.goOffline(); }

    public static int randInt() {
        int max = Integer.MAX_VALUE - 100 ;
        int min = 1;
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
