package com.homer.linefake;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Member {
    private int mbrID = 0;
    private int mbrIconIdx = 0;
    private String mbrAlias;
    private String mbrPhone;
    private String mbrEmail;
    private String mbrPassword;

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private static Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public int getMbrID() { return mbrID; }
    public Member setMbrID(int mbrID) {
        this.mbrID = mbrID;
        return(this);
    }

    public int getMbrIconIdx() { return mbrIconIdx; }
    // public Member setMbrIconIdx(int mbrIconIdx) {
    public Member setMbrIconIdx() {
        // mbrIconIdx is not used
        int [] pics = {
                R.drawable.p02,
                R.drawable.p03,
                R.drawable.p04,
                R.drawable.p05,
                R.drawable.p06,
                R.drawable.p07,
                R.drawable.p08,
                R.drawable.p01
        };

        int i = 7;

        if(mbrID > 0) {
            i = (mbrID - 1) % 7;
        }
        this.mbrIconIdx = pics[i];
        return(this);
    }

    public String getMbrAlias() { return mbrAlias; }
    public Member setMbrAlias(String mbrAlias) {
        this.mbrAlias = mbrAlias;
        return(this);
    }

    public String getMbrPhone() { return mbrPhone; }
    public Member setMbrPhone(String mbrPhone) {
        this.mbrPhone = mbrPhone;
        return(this);
    }

    public String getMbrEmail() { return mbrEmail; }
    public Member setMbrEmail(String mbrEmail) {
        this.mbrEmail = mbrEmail;
        return(this);
    }

    public String getMbrPassword() { return mbrPassword; }
    public Member setMbrPassword(String mbrPassword) {
        this.mbrPassword = mbrPassword;
        return(this);
    }

    public void copyFrom(Member x){
        mbrID = x.getMbrID();
        mbrIconIdx = x.getMbrIconIdx();
        mbrAlias = x.getMbrAlias();
        mbrPhone = x.getMbrPhone();
        mbrEmail = x.getMbrEmail();
        mbrPassword = x.getMbrPassword();
    }

    public void copyTo(Member x){
        x.setMbrID(mbrID);
        x.setMbrIconIdx();
        x.setMbrAlias(mbrAlias);
        x.setMbrPhone(mbrPhone);
        x.setMbrEmail(mbrEmail);
        x.setMbrPassword(mbrPassword);
    }

    static int isEmailValid(String email) {
        //TODO: Replace this with your own logic
        if(TextUtils.isEmpty(email)){ return 1; }
        // if(!email.contains("@")){ return 2; }
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches()){ return 2; }

        return 0;
    }
    static int isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        if(TextUtils.isEmpty(password)){ return 1; }
        if(password.length() < 6){ return 2; }
        return 0;
    }
    static int isAliasValid(String alias) {
        //TODO: Replace this with your own logic
        if(TextUtils.isEmpty(alias)){ return 1; }
        if(alias.length() < 4){ return 2; }
        return 0;
    }
    static int isPhoneValid(String phone) {
        //TODO: Replace this with your own logic
        if(TextUtils.isEmpty(phone)){ return 1; }
        if(phone.length() < 6){ return 2; }
        return 0;
    }

}

class MemberWithFriend extends Member {
    private Set<Integer> friendSet = new HashSet<>();

    public Integer[] getFriendSet() { return friendSet.toArray(new Integer[friendSet.size()]); }
    public Member setFriendSet(Integer x) {
        this.friendSet.add(x);
        return this;
    }
}

class ChatMsg {
    private int chatId;
    private long timeStart;
    private long timeStop;
    private int mbrIdFrom;
    private int mbrIdTo;
    private int chatType;
    private String txtMsg;

    public int getChatId() { return chatId; }
    public ChatMsg setChatId(int chatId) {
        this.chatId = chatId;
        return this;
    }

    public long getTimeStart() { return timeStart; }
    public ChatMsg setTimeStart(long timeStart) {
        this.timeStart = timeStart;
        return this;
    }

    public long getTimeStop() { return timeStop; }
    public ChatMsg setTimeStop(long timeStop) {
        this.timeStop = timeStop;
        return this;
    }

    public int getMbrIdFrom() { return mbrIdFrom; }
    public ChatMsg setMbrIdFrom(int mbrIdFrom) {
        this.mbrIdFrom = mbrIdFrom;
        return this;
    }

    public int getMbrIdTo() { return mbrIdTo; }
    public ChatMsg setMbrIdTo(int mbrIdTo) {
        this.mbrIdTo = mbrIdTo;
        return this;
    }

    public int getChatType() { return chatType; }
    public ChatMsg setChatType(int chatType) {
        this.chatType = chatType;
        return this;
    }

    public String getTxtMsg() { return txtMsg; }
    public ChatMsg setTxtMsg(String txtMsg) {
        this.txtMsg = txtMsg;
        return this;
    }
}
public class DbHelper {
    public static MemberWithFriend owner = new MemberWithFriend();
    public static Member master = new Member();
    public static Member guest = new Member();
    private ArrayList<Member> memberTable = new ArrayList<>();
    private ArrayList<Integer []> friendTable = new ArrayList<>();
    private ArrayList<ChatMsg> chatMsgTable = new ArrayList<>();
    private ArrayList<ChatMsg> channel = new ArrayList<>(); // only contains ChatMsg between owner and master

    private static DbHelper ourInstance;

    private DbHelper() {}
    //  http://givemepass-blog.logdown.com/posts/288939-sigleton-pattern
    public static DbHelper getInstance() {
        if (ourInstance == null) {
            ourInstance = new DbHelper();
        }
        return ourInstance;
    }
    void addMember(Member x){ memberTable.add(x); }

    void updateMember(Member input){
        int memberId = input.getMbrID();
        for(Member x:memberTable){
            if(x.getMbrID() == memberId){
                x.copyFrom(input);
                break;
            }
        }
    }

    void deleteMember(int memberId){
        for(Member x:memberTable){
            if(x.getMbrID() == memberId){
                memberTable.remove(x);
                break;
            }
        }
    }

    void addFriend(int ownerId, int masterId){
        Integer [] x1 = { ownerId, masterId};
        Integer [] x2 = { masterId, ownerId };
        friendTable.add(x1);
        friendTable.add(x2);
    }

    void deleteFriend(int ownerId, int masterId){
        for(Integer [] x : friendTable){
            if((x[0] == ownerId) && (x[1] == masterId)){
                friendTable.remove(x);
            }
            if((x[1] == ownerId) && (x[0] == masterId)){
                friendTable.remove(x);
            }
        }
    }
    void addChat(ChatMsg x){ chatMsgTable.add(x); }

    void deleteChat(int chatId){
        for(ChatMsg x:chatMsgTable){
            if(x.getChatId() == chatId){
                chatMsgTable.remove(x);
                break;
            }
        }
    }

    void addChannel(ChatMsg x){ channel.add(x); }

    void deleteChannel(int chatId){
        for(ChatMsg x:channel){
            if(x.getChatId() == chatId){
                channel.remove(x);
                break;
            }
        }
    }
    void initDbHelper(){
        // create DB if DB not exist
        // create tables: memberTable, friendTable, chatMsgTable
        // channel is internal use, create when channel is opened (master and owner)
        //  add memberTable
        Member x = new Member();
        x.setMbrID(1).setMbrIconIdx().setMbrAlias("admin").setMbrEmail("admin@null.com")
                .setMbrPassword("111111").setMbrPhone("0111111");
        addMember(x);

        x = new Member();
        x.setMbrID(2).setMbrIconIdx().setMbrAlias("owner").setMbrEmail("owner@null.com")
                .setMbrPassword("222222").setMbrPhone("0222222");
        addMember(x);

        x = new Member();
        x.setMbrID(3).setMbrIconIdx().setMbrAlias("master").setMbrEmail("master@null.com")
                .setMbrPassword("333333").setMbrPhone("0333333");
        addMember(x);

        x = new Member();
        x.setMbrID(4).setMbrIconIdx().setMbrAlias("guest").setMbrEmail("guest@null.com")
                .setMbrPassword("444444").setMbrPhone("0444444");
        addMember(x);

        // add friendTable (1,2,3,4)  (2,3,4)
        addFriend(1,2);
        addFriend(1,3);
        addFriend(1,4);
        addFriend(2,3);
        // addFriend(3,4);

        // add chatMsgTable
        // when login , create channel, implememt in login

    }

    int doEmailLogin(Member obj){
        String objEmail = obj.getMbrEmail();
        int idx = 0;
        for(idx = 0; idx < memberTable.size() ;idx++){
            if(memberTable.get(idx).getMbrEmail().equals(objEmail))
                break;
        }
        if(idx >= memberTable.size())
            return 1; // not found
        if(memberTable.get(idx).getMbrPassword().equals(obj.getMbrPassword()))
            memberTable.get(idx).copyTo(obj);
        else return 2; // pwd is incorrect
        obj.copyTo(owner);
        // build owner.friendSet from friendTable
        int src = owner.getMbrID();
        for(Integer [] x : friendTable){
            if(src == x[0]){
                owner.setFriendSet(x[1]);
            }
        }
        return 0;
    }
}
