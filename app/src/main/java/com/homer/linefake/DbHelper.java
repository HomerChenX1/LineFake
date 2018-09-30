package com.homer.linefake;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Member {
    private int mbrID = 3;
    private int mbrIconIdx = 3;
    private String mbrAlias = "guest";
    private String mbrPhone = "0333333";
    private String mbrEmail = "guest@null.com";
    private String mbrPassword = "333333";
    private Set<Integer> friendSet = new HashSet<>();

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private static Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public Set<Integer> getFriendSet() { return friendSet; }
    public Member setFriendSet(Set<Integer> friendSet) {
        this.friendSet = friendSet;
        return this;
    }

    public int getMbrID() { return mbrID; }
    public Member setMbrID(int mbrID) {
        this.mbrID = mbrID;
        return(this);
    }

    public int getMbrIconIdx() { return mbrIconIdx; }
    public Member setMbrIconIdx(int mbrIconIdx) {
        this.mbrIconIdx = mbrIconIdx;
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
    private ArrayList<Member> memberTable = new ArrayList<>();
    private ArrayList<Integer []> friendTable = new ArrayList<>();
    private ArrayList<ChatMsg> channel = new ArrayList<>();

    private static DbHelper ourInstance;

    private DbHelper() {}
    //  http://givemepass-blog.logdown.com/posts/288939-sigleton-pattern
    public static DbHelper getInstance() {
        if (ourInstance == null) {
            ourInstance = new DbHelper();
        }
        return ourInstance;
    }

}
