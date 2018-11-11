package com.homer.linefake;

// Default database URL  : https://linefake-ad479.firebaseio.com/
// your-project-id : linefake-ad479
//Default hosting subdomain — your-project-id.firebaseapp.com

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.AndroidRuntimeException;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class FireDbHelper {
    private FirebaseDatabase db;
    MemberTable memberTable = new MemberTable();
    FriendTable friendTable = new FriendTable();
    ChatMsgTable chatMsgTable = new ChatMsgTable();
    int deleteFriendCnt = 0;
    static int queryFriendTotalCount = 100;
    ArrayList<Integer> queryFriendList = new ArrayList<>();
    
    static int delChatMsgMbrIdListCnt1 = 100;

    static int delChatMsgMbrIdListCnt2 = 100;
    static int delChatMsgMbrIdListCnt3 = 100;

    static int genChannelCnt1 = 100;
    ArrayList<ChatMsg> genChannelList1 = new ArrayList<>();
    static int genChannelCnt2 = 100;
    ArrayList<ChatMsg> genChannelList2 = new ArrayList<>();

    static int queryMbrByIdCnt = 100;
    ArrayList<Member> queryMbrByIdList = new ArrayList<>();
    static int queryMbrByEmailCnt = 100;
    ArrayList<Member> queryMbrByEmailList = new ArrayList<>();

    /* ********************************************************** */
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
            queryFriendList.clear();
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

    /* ********************************************************** */
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
            genChannelCnt1 = 100;
            genChannelList1.clear();
            genChannelCnt2 = 100;
            genChannelList2.clear();

            DatabaseReference myRef = db.getReference(TABLE_NAME);
            myRef.orderByChild("mbrIdFrom").equalTo(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    genChannelCnt1 = (int) dataSnapshot.getChildrenCount();
                    //Log.d("HomerfbGenChannel1", "total cnt:" + genChannelCnt1 );
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        --genChannelCnt1;
                        try {
                            //mbrIdTo == mbrId
                            int pKey = ds.child("mbrIdTo").getValue(Integer.class);
                            if (pKey == masterId) {
                                //Log.d("HomerfbGenChannel1", "Id:"+ ds.child("chatId").getValue(Integer.class) +":"+genChannelCnt1);
                                ChatMsg temp = new ChatMsg();
                                temp.setChatId(ds.child("chatId").getValue(Integer.class));
                                temp.setTimeStart(ds.child("timeStartLong").getValue(Long.class));
                                temp.setMbrIdFrom(ds.child("mbrIdFrom").getValue(Integer.class));
                                temp.setMbrIdTo(ds.child("mbrIdTo").getValue(Integer.class));
                                temp.setChatType(ds.child("chatType").getValue(Integer.class));
                                temp.setTxtMsg(ds.child("txtMsg").getValue(String.class));
                                genChannelList1.add(temp);
                                //Log.d("HomerfbGenChannel1",temp.toString());
                            }
                        }catch(NullPointerException e){
                            continue;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("HomerfbGenChannel1", "onCancelled", databaseError.toException());
                }
            });

            myRef.orderByChild("mbrIdFrom").equalTo(masterId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    genChannelCnt2 = (int) dataSnapshot.getChildrenCount();
                    //Log.d("HomerfbGenChannel2", "total cnt:" + genChannelCnt2 );
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        --genChannelCnt2;
                        try {
                            //mbrIdTo == mbrId
                            int pKey = ds.child("mbrIdTo").getValue(Integer.class);
                            if (pKey == ownerId) {
                                //Log.d("HomerfbGenChannel2", "Id:"+ ds.child("chatId").getValue(Integer.class) +":"+genChannelCnt2);
                                ChatMsg temp = new ChatMsg();
                                temp.setChatId(ds.child("chatId").getValue(Integer.class));
                                temp.setTimeStart(ds.child("timeStartLong").getValue(Long.class));
                                temp.setMbrIdFrom(ds.child("mbrIdFrom").getValue(Integer.class));
                                temp.setMbrIdTo(ds.child("mbrIdTo").getValue(Integer.class));
                                temp.setChatType(ds.child("chatType").getValue(Integer.class));
                                temp.setTxtMsg(ds.child("txtMsg").getValue(String.class));
                                genChannelList2.add(temp);
                                //Log.d("HomerfbGenChannel2",temp.toString());
                            }
                        }catch(NullPointerException e){
                            continue;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("HomerfbGenChannel2", "onCancelled", databaseError.toException());
                }
            });
            // need to wait complete when genChannelCnt1==0 and genChannelCnt2==0
            if((genChannelCnt1==0)&&(genChannelCnt2==0)){
                genChannelList1.addAll(genChannelList2);
                Collections.sort(genChannelList1);
                // Log.d("HomerfbGenChannel", genChannelList1.get(0).toString());
                // Log.d("HomerfbGenChannel", genChannelList1.get(1).toString());
            }
            return genChannelList1;
        }
    }
    /* ********************************************************** */
    class MemberTable extends FriendTable {
        MemberTable() {
            TABLE_NAME = "MemberTable";
            nCOLS = new String[]{"mbrID", "mbrIconIdx", "mbrAlias", "mbrPhone", "mbrEmail", "mbrPassword"};
        }
        void addMember(Member x){
            int key = randInt();
            x.setMbrID(key);
            x.setMbrIconIdx();
            DatabaseReference myRef = db.getReference(TABLE_NAME + "/" + String.valueOf(key));
            myRef.setValue(x);
            // Log.d("HomerfbAddMember", x.toString());
        }
        void updateMember(Member x){
            DatabaseReference myRef = db.getReference(TABLE_NAME + "/" + String.valueOf(x.getMbrID()));
            myRef.setValue(x);
            // Log.d("HomerfbUpdateMember", x.toString());
        }
        void deleteMember(int memberId){
            DatabaseReference myRef = db.getReference(TABLE_NAME + "/" + String.valueOf(memberId));
            myRef.setValue(null);
            // Log.d("HomerfbDeleteMember", "mBrId:"+memberId);
        }

        Member queryMemberById(int memberId){
            queryMbrByIdCnt = 100;
            queryMbrByIdList.clear();
            DatabaseReference myRef = db.getReference(TABLE_NAME);
            myRef.orderByChild("mbrID").equalTo(memberId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    queryMbrByIdCnt = (int) dataSnapshot.getChildrenCount();
                    // Log.d("HomerfbQueryMbrById", "total cnt:" + queryMbrByIdCnt );
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        --queryMbrByIdCnt;
                        queryMbrByIdList.add(ds.getValue(Member.class));
                        // Log.d("HomerfbQueryMbrById",queryMbrByIdList.get(0).toString());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("HomerfbQueryMbrById", "onCancelled", databaseError.toException());
                }
            });
            // TODO: need to wait the real value
            return null;
        }

        void queryMemberByIdItem(int memberId){
            DatabaseReference myRef = db.getReference(TABLE_NAME);
            myRef.orderByChild("mbrID").equalTo(memberId).addListenerForSingleValueEvent(new ValueEventListener() {
                int queryMbrByIdCnt = 50;
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    queryMbrByIdCnt = (int) dataSnapshot.getChildrenCount();
                    // Log.d("HomerfbQueryMbrById", "total cnt:" + queryMbrByIdCnt );
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        --queryMbrByIdCnt;
                        synchronized (queryMbrByIdList){
                            queryMbrByIdList.add(ds.getValue(Member.class));
                        }
                        // Log.d("HomerfbQueryMbrById",queryMbrByIdList.get(0).toString());
                    }
                    if(queryMbrByIdCnt == 0) EventBus.getDefault().post(new EbusEvent("genFriendList"));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("HomerfbQueryMbrById", "onCancelled", databaseError.toException());
                }
            });
            // TODO: need to wait the real value
            return;
        }

        void genFriendList(Integer [] aList) {
            synchronized (queryMbrByIdList) {
                queryMbrByIdList.clear();
            }
            for(int id : aList){
                // friendList.add(queryMemberById(id));
                queryMemberByIdItem(id);
            }

        }
        ArrayList<Member> queryMemberByEmail(final String partEmail, final boolean exact){
            // firebase do not have contains function, so read all
            queryMbrByEmailCnt = 100;
            queryMbrByEmailList.clear();
            DatabaseReference myRef = db.getReference(TABLE_NAME);
            myRef.orderByChild("mbrEmail").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    queryMbrByEmailCnt = (int) dataSnapshot.getChildrenCount();
                    // Log.d("HomerfbQueryMbrByEmail", "total cnt:" + queryMbrByEmailCnt );
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        boolean isAdded = false;
                        --queryMbrByEmailCnt;
                        // Log.d("HomerfbQueryMbrByEmail","key:"+ds.getKey()+":"+queryMbrByEmailCnt);
                        if(ds.getKey().equals("nCOLS")) continue;
                        try {
                            Member temp = ds.getValue(Member.class);
                            if(exact){
                                if(temp.getMbrEmail().equals(partEmail)) isAdded = true;
                            }
                            else{
                                if(temp.getMbrEmail().contains(partEmail)) isAdded = true;
                            }
                            if(isAdded){
                                queryMbrByEmailList.add(temp);
                                // Log.d("HomerfbQueryMbrByEmail",temp.toString());
                            }
                        } catch (AndroidRuntimeException e){
                            continue;
                        }
                    }
                    Log.d("HomerfbQueryMbrByEmail","query cnt:"+queryMbrByEmailList.size());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("HomerfbQueryMbrByEmail", "onCancelled", databaseError.toException());
                }
            });
            //TODO : need wait
            return queryMbrByEmailList;
        }
    }

    /* ********************************************************** */
    public FireDbHelper() {
        db = FirebaseDatabase.getInstance();
        onCreate();
    }

    public FirebaseDatabase getDb() { return db; }

    public void onCreate() {
        // build tables
        memberTable.create();
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
