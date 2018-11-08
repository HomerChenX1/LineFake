package com.homer.linefake;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
//import java.util.Locale; induce no affect, do not know why
import java.util.Set;
import java.util.TimeZone;
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

    public Member() { super();}

    public Member(int mbrID, String mbrAlias, String mbrPhone, String mbrEmail, String mbrPassword) {
        this.setMbrID(mbrID).setMbrIconIdx().setMbrAlias(mbrAlias).setMbrEmail(mbrEmail)
                .setMbrPassword(mbrPassword).setMbrPhone(mbrPhone);
    }

    int getMbrID() { return mbrID; }
    Member setMbrID(int mbrID) {
        this.mbrID = mbrID;
        return(this);
    }

    int getMbrIconIdx() { return mbrIconIdx; }
    // public Member setMbrIconIdx(int mbrIconIdx) {
    Member setMbrIconIdx() {
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

    String getMbrAlias() { return mbrAlias; }
    Member setMbrAlias(String mbrAlias) {
        this.mbrAlias = mbrAlias;
        return(this);
    }

    String getMbrPhone() { return mbrPhone; }
    Member setMbrPhone(String mbrPhone) {
        this.mbrPhone = mbrPhone;
        return(this);
    }

    String getMbrEmail() { return mbrEmail; }
    Member setMbrEmail(String mbrEmail) {
        this.mbrEmail = mbrEmail;
        return(this);
    }

    String getMbrPassword() { return mbrPassword; }
    Member setMbrPassword(String mbrPassword) {
        this.mbrPassword = mbrPassword;
        return(this);
    }

    void copyFrom(Member x){
        mbrID = x.getMbrID();
        mbrIconIdx = x.getMbrIconIdx();
        mbrAlias = x.getMbrAlias();
        mbrPhone = x.getMbrPhone();
        mbrEmail = x.getMbrEmail();
        mbrPassword = x.getMbrPassword();
    }

    void copyTo(Member x){
        x.setMbrID(mbrID);
        x.setMbrIconIdx();
        x.setMbrAlias(mbrAlias);
        x.setMbrPhone(mbrPhone);
        x.setMbrEmail(mbrEmail);
        x.setMbrPassword(mbrPassword);
    }

    static int isEmailValid(String email) {
        if(TextUtils.isEmpty(email)){ return 1; }
        // if(!email.contains("@")){ return 2; }
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches()){ return 2; }

        return 0;
    }
    static int isPasswordValid(String password) {
        if(TextUtils.isEmpty(password)){ return 1; }
        if(password.length() < 6){ return 2; }
        return 0;
    }
    static int isAliasValid(String alias) {
        if(TextUtils.isEmpty(alias)){ return 1; }
        if(alias.length() < 4){ return 2; }
        return 0;
    }
    static int isPhoneValid(String phone) {
        if(TextUtils.isEmpty(phone)){ return 1; }
        if(phone.length() < 6){ return 2; }
        return 0;
    }

    @Override
    public String toString() {
        return "Member{" +
                "ID=" + mbrID +
                ", Icon=" + mbrIconIdx +
                ", Alias='" + mbrAlias + '\'' +
                ", Phone='" + mbrPhone + '\'' +
                ", Email='" + mbrEmail + '\'' +
                ", Pwd='" + mbrPassword + '\'' +
                '}';
    }
}

class MemberWithFriend extends Member {
    private Set<Integer> friendSet = new HashSet<>();

    public MemberWithFriend() { super(); }

    Integer[] getFriendSet() { return friendSet.toArray(new Integer[0]); }
    Member setFriendSet(Integer x) {
        this.friendSet.add(x);
        return this;
    }
    int getFriendSetSize(){
        return friendSet.size();
    }
    void clearFriendSet(){ friendSet.clear();}
    void deleteFriendSet(Integer x){ friendSet.remove(x);}
}

class ChatMsg implements Comparable {
    private int chatId;
    private long timeStart;
    // private long timeStop;
    private int mbrIdFrom;
    private int mbrIdTo;
    private int chatType;
    private String txtMsg;
    // static final int chatTypeDel = 1;
    static final int chatTypeRead = 2;
    static final int chatTypeText = 4;
    // static final int chatTypePhone = 8;
    // static final int chatTypeVideo = 16;
    // static final int chatTypePhoto = 32;

    @Override
    public String toString() {
        return "ChatMsg{" +
                "Id=" + chatId +
                ", TStart=" + getTimeStart() +
                ", From=" + mbrIdFrom +
                ", To=" + mbrIdTo +
                ", Type=" + chatType +
                ", Msg='" + txtMsg + '\'' +
                '}';
    }

    public ChatMsg(){ super(); }
    ChatMsg(int mbrIdFrom, int mbrIdTo, int chatType, String txtMsg) {
        // this.chatId = chatId;
        //this.timeStart = timeStart;
        this.setChatId().setTimeStart().setMbrIdFrom(mbrIdFrom).setMbrIdTo(mbrIdTo)
                .setChatType(chatType).setTxtMsg(txtMsg);
    }

    int getChatId() { return chatId; }
    ChatMsg setChatId() {
        this.chatId = DbHelper.getInstance().generateChatMsgID();
        return this;
    }
    ChatMsg setChatId(int chatId) {
        this.chatId = chatId;
        return this;
    }

    String getTimeStart() {
        // SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        //String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStart))));
        //return sd;
        return sdf.format(new Date(Long.parseLong(String.valueOf(timeStart))));
    }
    Long getTimeStartLong(){ return timeStart; }
    ChatMsg setTimeStart() {
        this.timeStart = System.currentTimeMillis();
        return this;
    }
    ChatMsg setTimeStart(long timeStart) {
        this.timeStart = timeStart;
        return this;
    }

//    public long getTimeStop() { return timeStop; }
//    public ChatMsg setTimeStop(long timeStop) {
//        this.timeStop = timeStop;
//        return this;
//    }

    int getMbrIdFrom() { return mbrIdFrom; }
    ChatMsg setMbrIdFrom(int mbrIdFrom) {
        this.mbrIdFrom = mbrIdFrom;
        return this;
    }

    int getMbrIdTo() { return mbrIdTo; }
    ChatMsg setMbrIdTo(int mbrIdTo) {
        this.mbrIdTo = mbrIdTo;
        return this;
    }

    public int getChatType() { return chatType; }
    ChatMsg setChatType(int chatType) {
        this.chatType = chatType;
        return this;
    }

    String getTxtMsg() { return txtMsg; }
    ChatMsg setTxtMsg(String txtMsg) {
        this.txtMsg = txtMsg;
        return this;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return  (this.timeStart-((ChatMsg)o).getTimeStartLong())>0? 1 : -1;
    }
}

