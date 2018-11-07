package com.homer.linefake;

// Default database URL  : https://linefake-ad479.firebaseio.com/
// your-project-id : linefake-ad479
//Default hosting subdomain — your-project-id.firebaseapp.com

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class FireDbHelper {
    private FirebaseDatabase db;
    FriendTable friendTable = new FriendTable();
    ChatMsgTable chatMsgTable = new ChatMsgTable();
    int deleteFriendCnt = 0;
    static int queryFriendTotalCount = 100;
    ArrayList<Integer> queryFriendList = new ArrayList<>();
    
    static int delChatMsgMbrIdListCnt1 = 100;

    static int delChatMsgMbrIdListCnt2 = 100;
    static int delChatMsgMbrIdListCnt3 = 100;

    static int genChannelCnt = 100;
    ArrayList<ChatMsg> genChannelList = new ArrayList<>();

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
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("HomerfbDeleteFriend", "onCancelled", databaseError.toException());
                }
            });

            // to masterId's friend delete ownerId
            query = myRef.child(String.valueOf(masterId));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                public void onCancelled(@NonNull DatabaseError databaseError) {
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
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                public void onCancelled(@NonNull DatabaseError databaseError) {
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
            delChatMsgMbrIdListCnt1 = 100;
            // search x.getMbrIdFrom()==mbrId or
            DatabaseReference myRef = db.getReference(TABLE_NAME);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    delChatMsgMbrIdListCnt1 = (int) dataSnapshot.getChildrenCount();
                    // Log.d("HomerfbDelChatMsgMbrId", "total cnt:" + delChatMsgMbrIdListCnt1);
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        delChatMsgMbrIdListCnt1--;
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
                            // Log.d("HomerfbDelChatMsgMbrId", temp.toString());
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("HomerfbQueryFriend", "onCancelled", databaseError.toException());
                }
            });

        }

        void deleteChatMsgByMbrId(final int ownerId, final int mbrId){
            delChatMsgMbrIdListCnt2 = 100;
            DatabaseReference myRef = db.getReference(TABLE_NAME);
            myRef.orderByChild("mbrIdFrom").equalTo(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    delChatMsgMbrIdListCnt2 = (int) dataSnapshot.getChildrenCount();
                    // Log.d("HomerfbDelChatMsgMbrId2", "total cnt:" + delChatMsgMbrIdListCnt2 );
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        --delChatMsgMbrIdListCnt2;
//                        if(delChatMsgMbrIdListCnt2==0)
//                            Log.d("HomerfbDelChatMsgMbrId2","End");
                        try {
                            //mbrIdTo == mbrId
                            int pKey = ds.child("mbrIdTo").getValue(Integer.class);
                            if (pKey == mbrId) {
                                //Log.d("HomerfbDelChatMsgMbrId2", "Id:"+ ds.child("chatId").getValue(Integer.class) +":"+delChatMsgMbrIdListCnt2);
                                ds.getRef().removeValue();
                            }
                        }catch(NullPointerException e){
                            continue;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("HomerfbDelChatMsgMbrId2", "onCancelled", databaseError.toException());
                    }
                });

            delChatMsgMbrIdListCnt3 = 100;
            myRef.orderByChild("mbrIdTo").equalTo(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    delChatMsgMbrIdListCnt3 = (int) dataSnapshot.getChildrenCount();
                    //Log.d("HomerfbDelChatMsgMbrId3", "total cnt:" + delChatMsgMbrIdListCnt3 );
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        --delChatMsgMbrIdListCnt3;
//                        if(delChatMsgMbrIdListCnt3==0)
//                            Log.d("HomerfbDelChatMsgMbrId3","End");
                        try {
                            int pKey = ds.child("mbrIdFrom").getValue(Integer.class);
                            if (pKey == mbrId) {
                                //Log.d("HomerfbDelChatMsgMbrId3", "Id:"+ ds.child("chatId").getValue(Integer.class)+":"+delChatMsgMbrIdListCnt3);
                                ds.getRef().removeValue();
                            }
                        }catch(NullPointerException e){
                            continue;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("HomerfbDelChatMsgMbrId3", "onCancelled", databaseError.toException());
                }
            });
        }


        ArrayList<ChatMsg> generateChannel(final int masterId, final int ownerId){
            /*
                    static int genChannelCnt = 100;
                    ArrayList<ChatMsg> genChannelList = new ArrayList<>();
                    */
            genChannelCnt = 100;
            DatabaseReference myRef = db.getReference(TABLE_NAME);

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("HomerfbGenChannel", "onCancelled", databaseError.toException());
                }

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    genChannelCnt = (int) dataSnapshot.getChildrenCount();
                    // Log.d("HomerfbGenChannel", "total cnt:" + genChannelCnt );
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        --genChannelCnt;
                        try {
                            int pKey1 = ds.child("mbrIdFrom").getValue(Integer.class);
                            int pKey2 = ds.child("mbrIdTo").getValue(Integer.class);
                            if ((pKey1 == ownerId && pKey2 == masterId) || (pKey2 == ownerId && pKey1 == masterId)) {
                                //Log.d("HomerfbGenChannel", "From:" + pKey1 + " To:" + pKey2);

                                ChatMsg temp = new ChatMsg();
                                temp.setChatId(ds.child("chatId").getValue(Integer.class));
                                temp.setTimeStart(ds.child("timeStartLong").getValue(Long.class));
                                temp.setMbrIdFrom(ds.child("mbrIdFrom").getValue(Integer.class));
                                temp.setMbrIdTo(ds.child("mbrIdTo").getValue(Integer.class));
                                temp.setChatType(ds.child("chatType").getValue(Integer.class));
                                temp.setTxtMsg(ds.child("txtMsg").getValue(String.class));
                                genChannelList.add(temp);
                            }
                            //Log.d("HomerfbGenChannel","size:"+genChannelList.size() +":"+genChannelCnt);
                            if(genChannelCnt<=1){
                                // sort by timeStart
                                Collections.sort(genChannelList);
                                // Log.d("HomerfbGenChannel", genChannelList.get(0).toString());
                                // Log.d("HomerfbGenChannel", genChannelList.get(1).toString());
                            }
                        }catch(NullPointerException e){
                            continue;
                        }
                    }
                }
            });

            return genChannelList;
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
        return rand.nextInt((max - min) + 1) + min;
    }
}
