package com.homer.linefake;

import android.util.Log;

import java.util.ArrayList;
/*
DbHelper.getInstance().deleteOwner(); need wait
*/
class DbHelper {
    static MemberWithFriend owner = new MemberWithFriend();
    static ArrayList<Member> friendList = new ArrayList<>(); // gather info from friendSet
    static Member master = new Member();
    static Member guest = new Member();
    private ArrayList<Member> memberTable = new ArrayList<>();
    private ArrayList<Integer []> friendTable = new ArrayList<>();
    private ArrayList<ChatMsg> chatMsgTable = new ArrayList<>();
    static int multipleBack = 0;
    volatile int waitCount = 0;   // volatile is very important.

    static int useSQL = 1; // =0 : use ArrayList, =1 use SQLite  =2 use Firebase
    SqlDbHelper sqlDbHelper = null;
    FireDbHelper fireDbHelper = null;

    private static DbHelper ourInstance;

    private DbHelper() {}
    //  http://givemepass-blog.logdown.com/posts/288939-sigleton-pattern
    static DbHelper getInstance() {
        if (ourInstance == null) {
            ourInstance = new DbHelper();
        }
        return ourInstance;
    }
    void resetWaitCount(){ waitCount = 0; }
    void setWaitCount(){ waitCount = 50; }

    void addMember(Member x){
        if(useSQL==1) {
            sqlDbHelper.memberTable.addMember(x);
            return;
        }
        if(useSQL==2) {
            fireDbHelper.memberTable.addMember(x);
            return;
        }
        memberTable.add(x);
    }

    // MbrID == -1 mean error
    Member queryMemberById(int memberId){
        if(useSQL==1) {
            return sqlDbHelper.memberTable.queryMemberById(memberId);
        }
        if(useSQL==2) {
            return fireDbHelper.memberTable.queryMemberById(memberId);
        }
        Member m = new Member();
        m.setMbrID(-1);
        for(Member x : memberTable){
            if(x.getMbrID() == memberId){
                x.copyTo(m);
                break;
            }
        }
        return m;
    }

    ArrayList<Member> queryMemberByEmail(String partEmail){
        if(useSQL==1) {
            return sqlDbHelper.memberTable.queryMemberByEmail(partEmail,false);
        }
        if(useSQL==2) {
            return fireDbHelper.memberTable.queryMemberByEmail(partEmail,false);
        }
        ArrayList<Member> mList = new ArrayList<>();
        for(Member x : memberTable){
            if(x.getMbrEmail().contains(partEmail)){
                Member m = new Member();
                x.copyTo(m);
                mList.add(m);
            }
        }
        return mList;
    }
    ArrayList<Member> queryMemberByEmailExact(String Email){
        if(useSQL==1) {
            return sqlDbHelper.memberTable.queryMemberByEmail(Email,true);
        }
        if(useSQL==2) {
            return fireDbHelper.memberTable.queryMemberByEmail(Email,true);
        }
        ArrayList<Member> mList = new ArrayList<>();
        for(Member x : memberTable){
            if(x.getMbrEmail().equals(Email)){
                Member m = new Member();
                x.copyTo(m);
                mList.add(m);
            }
        }
        return mList;
    }

    void updateMember(Member input){
        if(useSQL==1) {
            sqlDbHelper.memberTable.updateMember(input);
            return;
        }
        if(useSQL==2) {
            fireDbHelper.memberTable.updateMember(input);
            return;
        }
        int memberId = input.getMbrID();
        for(Member x:memberTable){
            if(x.getMbrID() == memberId){
                x.copyFrom(input);
                break;
            }
        }
    }

    void deleteMember(int memberId){
        if(useSQL==1) {
            sqlDbHelper.memberTable.deleteMember(memberId);
            return;
        }
        if(useSQL==2) {
            fireDbHelper.memberTable.deleteMember(memberId);
            return;
        }
        for(Member x:memberTable){
            if(x.getMbrID() == memberId){
                memberTable.remove(x);
                break;
            }
        }
    }

    int generateMbrID(){ return memberTable.size() + 1; }

    void addFriend(int ownerId, int masterId){
        if(useSQL == 1){
            sqlDbHelper.friendTable.addFriend(ownerId, masterId);
            return;
        }
        if(useSQL == 2){
            fireDbHelper.friendTable.addFriend(ownerId, masterId);
            return;
        }
        Integer [] x1 = { ownerId, masterId};
        Integer [] x2 = { masterId, ownerId };
        friendTable.add(x1);
        friendTable.add(x2);
    }

    int deleteFriend(int ownerId, int masterId) {
        if(useSQL==1){
            return sqlDbHelper.friendTable.deleteFriend(ownerId, masterId);
        }
        if(useSQL==2){
            return fireDbHelper.friendTable.deleteFriend(ownerId, masterId);
        }
        ArrayList<Integer [] > items = new ArrayList<>();
        for(Integer [] x : friendTable) {
            if ((x[0] == ownerId)&& (x[1] == masterId)) {
                items.add(x);
            }
            if ((x[1] == ownerId)&& (x[0] == masterId)) {
                items.add(x);
            }
        }
        int i = friendTable.size();
        friendTable.removeAll(items);
        i -= friendTable.size();
        return i;
    }

    Integer[] queryFriend(int ownerId){
        if(useSQL==1){
            return sqlDbHelper.friendTable.queryFriend(ownerId);
        }
        if(useSQL==2){
            return fireDbHelper.friendTable.queryFriend(ownerId);
        }
        ArrayList<Integer> temp = new ArrayList<>();
        for (Integer[] x : friendTable) {
            if (ownerId == x[0]) {
                temp.add(x[1]);
                // owner.setFriendSet(x[1]);
            }
        }
        Integer [] temp2 = temp.toArray(new Integer[0]);
        return temp2;
    }

    void addChat(ChatMsg x){
        if(useSQL==1){
            sqlDbHelper.chatMsgTable.addChat(x);
            return;
        }
        if(useSQL==2){
            fireDbHelper.chatMsgTable.addChat(x);
            return;
        }
        chatMsgTable.add(x);
    }

    void deleteChat(int chatId){
        if(useSQL==1){
            sqlDbHelper.chatMsgTable.deleteChat(chatId);
            return;
        }
        if(useSQL==2){
            fireDbHelper.chatMsgTable.deleteChat(chatId);
            return;
        }
        for(ChatMsg x:chatMsgTable){
            if(x.getChatId() == chatId){
                chatMsgTable.remove(x);
                break;
            }
        }
    }

    void deleteChatMsgByMbrId(int mbrId){
        if(useSQL==1){
            sqlDbHelper.chatMsgTable.deleteChatMsgByMbrId(mbrId);
            return;
        }
        if(useSQL==2){
            fireDbHelper.chatMsgTable.deleteChatMsgByMbrId(mbrId);
            return;
        }
        ArrayList<ChatMsg> tempList = new ArrayList<>();
        for(ChatMsg x : chatMsgTable){
            if((x.getMbrIdFrom()==mbrId)||(x.getMbrIdTo()==mbrId)) tempList.add(x);
        }
        chatMsgTable.removeAll(tempList);
    }
    void deleteChatMsgByMbrId(int ownerId, int mbrId){
        if(useSQL==1){
            sqlDbHelper.chatMsgTable.deleteChatMsgByMbrId(ownerId, mbrId);
            return;
        }
        if(useSQL==2){
            fireDbHelper.chatMsgTable.deleteChatMsgByMbrId(ownerId, mbrId);
            return;
        }
        ArrayList<ChatMsg> tempList = new ArrayList<>();
        for(ChatMsg x : chatMsgTable){
            if((x.getMbrIdFrom()==ownerId)&&(x.getMbrIdTo()==mbrId)) tempList.add(x);
            if((x.getMbrIdFrom()==mbrId)&&(x.getMbrIdTo()==ownerId)) tempList.add(x);
        }
        chatMsgTable.removeAll(tempList);
    }

    void initDbHelper(){
        // create DB if DB not exist
        // create tables: memberTable, friendTable, chatMsgTable
        // channel is internal use, create when channel is opened (master and owner)
        //  add memberTable
        ArrayList<Integer> idList = new ArrayList<>();
        idList.add(0);
        Member temp = new Member(1,"admin","0111111",
                "admin@null.com","111111");
        addMember(temp);
        idList.add(temp.getMbrID());

        temp = new Member(2,"owner","0222222",
                "owner@null.com","222222");
        addMember(temp);
        idList.add(temp.getMbrID());

        temp = new Member(3,"master","0333333",
                "master@null.com","333333");
        addMember(temp);
        idList.add(temp.getMbrID());

        temp = new Member(4,"guest","0444444",
                "guest@null.com","444444");
        addMember(temp);
        idList.add(temp.getMbrID());

        // add friendTable (1,2,3,4)  (2,3,4)
        addFriend(idList.get(1),idList.get(2));
        addFriend(idList.get(1),idList.get(3));
        addFriend(idList.get(1),idList.get(4));
        addFriend(idList.get(2),idList.get(3));
        // addFriend(3,4);

        // add chatMsgTable : addChat
        addChat(new ChatMsg(idList.get(1),idList.get(2), ChatMsg.chatTypeText, "This is test12!"));
        addChat(new ChatMsg(idList.get(1),idList.get(3), ChatMsg.chatTypeText, "This is test13!"));
        addChat(new ChatMsg(idList.get(1),idList.get(4), ChatMsg.chatTypeText, "This is test14!"));
        addChat(new ChatMsg(idList.get(2),idList.get(3), ChatMsg.chatTypeText, "This is test23!"));

        addChat(new ChatMsg(idList.get(2),idList.get(1), ChatMsg.chatTypeText, "OK21!"));
        addChat(new ChatMsg(idList.get(3),idList.get(1), ChatMsg.chatTypeText, "OK31!"));
        addChat(new ChatMsg(idList.get(4),idList.get(1), ChatMsg.chatTypeText, "OK41!"));
        addChat(new ChatMsg(idList.get(3),idList.get(2), ChatMsg.chatTypeText, "OK32!"));
    }

    int doEmailLogin(Member obj){
        ArrayList<Member> temp = queryMemberByEmailExact(obj.getMbrEmail());
        switch (temp.size()){
            case 0:
                return 1; // not found
            case 1:
                // found
                if(temp.get(0).getMbrPassword().equals(obj.getMbrPassword())){
                    temp.get(0).copyTo(obj);
                }
                else return 2; // pwd is incorrect
                break;
            default:
                // somthing wrong, Members are duplicate
                Log.d("HomersqlEmailLog","Members are duplicate");
                return 1; // e-mail is incorrect
        }

        obj.copyTo(owner);
        // build owner.friendSet from friendTable
        owner.clearFriendSet(); // for backpress error
        friendList.clear();

        for(int i : queryFriend(owner.getMbrID())){
            owner.setFriendSet(i);
        }
        if (owner.getFriendSet().length != 0) genFriendList();
        return 0;
    }

    int doEmailLoginFB(Member obj){
        ArrayList<Member> temp = queryMemberByEmailExact(obj.getMbrEmail());
        new doEmailLoginFBCheckEnd(50).execute("doEmailLoginFB");
        //switch (temp.size()){
        return 1;
    }
    int doEmailLoginFB1(ArrayList<Member> temp){
        switch (temp.size()){
            case 0:
                return 1; // not found
            case 1:
                // found
                if(temp.get(0).getMbrPassword().equals(guest.getMbrPassword())){
                    temp.get(0).copyTo(guest);
                }
                else return 2; // pwd is incorrect
                break;
            default:
                // somthing wrong, Members are duplicate
                Log.d("HomersqlEmailLog","Members are duplicate");
                return 1; // e-mail is incorrect
        }
        guest.copyTo(owner);
        // build owner.friendSet from friendTable
        owner.clearFriendSet(); // for backpress error
        friendList.clear();
        queryFriend(owner.getMbrID());
        // wait
        // static int queryFriendTotalCount = 100;
        // ArrayList<Integer> queryFriendList = new ArrayList<>();
        new doEmailLoginFB1CheckEnd(50).execute("doEmailLoginFB1");
        return 0;
    }
    int doEmailLoginFB2(ArrayList<Integer> temp){
        for(int i : temp){
            owner.setFriendSet(i);
        }
        if (owner.getFriendSet().length != 0)
            fireDbHelper.memberTable.genFriendList(owner.getFriendSet());
        return 0;
    }

    void genFriendList(){
        for(int id : owner.getFriendSet()){
            friendList.add(queryMemberById(id));
        }
    }
    Member queryFriendListById(int memberId){
        Member m = new Member();
        for(Member x : friendList){
            if(x.getMbrID() == memberId){
                x.copyTo(m);
                break;
            }
        }
        return m;
    }
    int deleteFriendList(int memberId){
        ArrayList<Member> items = new ArrayList<>();
        for(Member x : friendList){
            if(x.getMbrID() == memberId){
                items.add(x);
            }
        }
        // int i = items.size();
        friendList.removeAll(items);
        return friendList.size();
    }

    void registerMember(Member input){
        // add to memberTable,   delete owner.ID = 0 , to login
        Member x = new Member();
        input.copyTo(x);
        // generate Member ID
        int idx = generateMbrID();
        x.setMbrID(idx).setMbrIconIdx();
        addMember(x);
        // friendTable with admin,
        addFriend(1,idx);
    }
    void deleteOwner(){
        int ID = owner.getMbrID();
        // delete memberTable by owner.ID
        deleteMember(ID);
        // delete friendTable by owner.ID
        for(int i : owner.getFriendSet()){
            deleteFriend(ID, i);
        }
        // owner.friendset clear
        owner.clearFriendSet();
        // delete chatMsgTable by owner.ID IDfrom IDto
        deleteChatMsgByMbrId(ID);
        // delete owner.ID = 0 master.ID = 0
        if(DbHelper.useSQL != 2 ) owner.setMbrID(0);
    }
    int deleteFriendOfOwner(int memberId){
        // delete member.id from   friendTable -> owner.friendset -> friendList
        int i = owner.getFriendSetSize();
        //  delete owner.friendSet
        owner.deleteFriendSet(memberId);
        i -= owner.getFriendSetSize();
        if(i > 0){
            // memberId existed in friendset
            // del friendList
            i = deleteFriendList(memberId);
            // del friendTable
            i = deleteFriend(owner.getMbrID(),memberId);

        }
        return i;
    }

    int addFriendOfOwner(int memberId) {
        // id exist?
        Member m = queryMemberById(memberId);
        if(useSQL == 2){
            return 0;
        }
        return addFriendOfOwner2(m);
    }
    int addFriendOfOwner2(Member m) {
        if(m.getMbrID() != -1) {
            // exist,
            // if exist in friendSet?
            int i = owner.getFriendSetSize();
            // owner.friendSet -> friendList ->  friendTable
            owner.setFriendSet(m.getMbrID());
            if((owner.getFriendSetSize() -i) > 0){
                // not exist in friendSet and add successful
                friendList.add(m);
                addFriend(owner.getMbrID(), m.getMbrID());
                return friendList.size();
            }
        }
        return 0;
    }

    int generateChatMsgID(){ return chatMsgTable.size() + 11; }
    ArrayList<ChatMsg> generateChannel(int masterId, int ownerId){
        if(useSQL==1) {
            return sqlDbHelper.chatMsgTable.generateChannel(masterId, ownerId);
        }
        if(useSQL==2) {
            return fireDbHelper.chatMsgTable.generateChannel(masterId, ownerId);
        }
        ArrayList<ChatMsg> temp = new ArrayList<>();
        for(ChatMsg x : chatMsgTable){
            int id1 = x.getMbrIdFrom();
            int id2 = x.getMbrIdTo();
            if((masterId == id1 && ownerId == id2) || (masterId == id2 && ownerId == id1)){
                temp.add(x);
            }
        }
        return temp;
    }
    void resetDbHelper(){
        owner = new MemberWithFriend();
        master = new Member();
        guest = new Member();
        multipleBack = 0;
        friendList.clear();
    }
}
