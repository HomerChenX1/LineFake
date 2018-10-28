package com.homer.linefake;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class SqlDbHelper extends SQLiteOpenHelper {
    static final String DB_NAME = "LineFake.db";
    private static final int DB_VERSION = 1;
    FriendTable friendTable = new FriendTable();
    ChatMsgTable chatMsgTable = new ChatMsgTable();
    MemberTable memberTable = new MemberTable();
    private SQLiteDatabase db;

    class FriendTable {
        String TABLE_NAME = "FriendTable";
        String TABLE_COLS = "( " +
                // "_id INTEGER PRIMARY KEY(more columns) AUTOINCREMENT, " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "OWNERID INTEGER NOT NULL, " +
                "MASTERID INTEGER NOT NULL " +
                ");";
        String [] nCOLS = {"OWNERID","MASTERID"};
        String TABLE_CREATE;

        FriendTable() { genTABLE_CREATE(); }

        void genTABLE_CREATE(){ TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + TABLE_COLS; }

        void create(SQLiteDatabase db){ db.execSQL(TABLE_CREATE); }

        void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        @Override
        public String toString() {
            return "FriendTable{" +
                    "CREATE='" + TABLE_CREATE + '\'' +
                    '}';
        }

        //insert
        void addFriend(int ownerId, int masterId){
            // db = getWritableDatabase();
            long pKey1 = -1;
            long pKey2 = -1;
            ContentValues values = new ContentValues();
            // INSERT INTO FriendTable ( ownerId, masterId ) VALUES ( ownerId, masterId );
            values.put(nCOLS[0], ownerId);
            values.put(nCOLS[1], masterId);
            pKey1 = db.insert(TABLE_NAME, null, values);
            // INSERT INTO FriendTable ( ownerId, masterId ) VALUES ( masterId, ownerId );
            values.put(nCOLS[0], masterId);
            values.put(nCOLS[1], ownerId);
            pKey2 = db.insert(TABLE_NAME, null, values);
            // Log.d("HomersqlAddFriend : ", pKey1 + ":" + pKey2);
        }

        //DELETE FROM table_name WHERE OWNERID=ownerId AND MASTERID=masterId or OWNERID=masterId AND MASTERID=ownerId;
        int deleteFriend(int ownerId, int masterId) {
            //SQLiteDatabase db = getWritableDatabase();
            int temp = -1;
            String whereClause1 = "( " + nCOLS[0] + " = ? and " + nCOLS[1] + " = ? " + ")";
            String whereClause2 = "( " + nCOLS[1] + " = ? and " + nCOLS[0] + " = ? " + ")";
            String whereClause = whereClause1 + " OR " +whereClause2;
            String[] whereArgs = {String.valueOf(ownerId), String.valueOf(masterId), String.valueOf(ownerId), String.valueOf(masterId)};
            temp = db.delete(TABLE_NAME, whereClause, whereArgs); // return value is the deleted count
            //Log.d("HomersqlDeleteFriend", whereClause + ":" + temp);
            return temp;
        }
        //query
        Integer[] queryFriend(int ownerId){
            ArrayList<Integer> temp = new ArrayList<>();

            // SELECT MASTERID FROM FriendTable WHERE OWNERID = ownerId
            String where = nCOLS[0] + "=" + ownerId;
            String[] columns = { nCOLS[1] };

            Cursor cursor = db.query(TABLE_NAME, columns, where,
                    null, null, null, null, null);
            while (cursor.moveToNext()) {
                temp.add(cursor.getInt(cursor.getColumnIndex(nCOLS[1])));
            }
            cursor.close();
            // Log.d("HomersqlQueryFriend : ", "return count:" + temp.size());
            return temp.toArray(new Integer[0]);
        }
        //update
    }

    class ChatMsgTable extends FriendTable {

        ChatMsgTable() {
            TABLE_NAME = "ChatMsgTable";
            TABLE_COLS = "( " + "CHATID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "TIMESTART LONG NOT NULL, " +
                    "MBRIDFROM INTEGER NOT NULL, " +
                    "MBRIDTO INTEGER NOT NULL, " +
                    "CHATTYPE INTEGER NOT NULL, " +
                    "TXTMSG TEXT " +
                    " );" ;
            genTABLE_CREATE();
        }

        @Override
        public String toString() {
            return "ChatMsgTable{" +
                    "CREATE='" + TABLE_CREATE + '\'' +
                    '}';
        }
        void addChat(ChatMsg x){
            ContentValues values = new ContentValues();
            values.put("TIMESTART", x.getTimeStartLong());
            values.put("MBRIDFROM", x.getMbrIdFrom());
            values.put("MBRIDTO", x.getMbrIdTo());
            values.put("CHATTYPE", x.getChatType());
            values.put("TXTMSG", x.getTxtMsg());
            long pKey1 = db.insert(TABLE_NAME, null, values);
            x.setChatId((int)pKey1);
            // Log.d("HomersqlAddChat", pKey1 + ":" + x.getMbrIdFrom() + ":" + x.getMbrIdTo() + ":" + x.getTimeStart());
        }
        void deleteChat(int chatId){
            String whereClause = "CHATID = ?";
            String[] whereArgs = {String.valueOf(chatId)};
            int temp = db.delete(TABLE_NAME, whereClause, whereArgs); // return value is the deleted count
            // Log.d("HomersqlDeleteChat", temp + ":" + chatId);
        }
        void deleteChatMsgByMbrId(int mbrId){
            // if((x.getMbrIdFrom()==mbrId)||(x.getMbrIdTo()==mbrId))
            String whereClause = "(MBRIDFROM = ?) or (MBRIDTO  = ?)";
            String[] whereArgs = {String.valueOf(mbrId), String.valueOf(mbrId)};
            int temp = db.delete(TABLE_NAME, whereClause, whereArgs); // return value is the deleted count
            // Log.d("HomersqlDeleteChatMbr ", whereClause + ":" + temp);
        }
        void deleteChatMsgByMbrId(int ownerId, int mbrId){
            String whereClause1 = "(MBRIDFROM = ? AND MBRIDTO = ?) ";
            String whereClause = whereClause1 + "OR " + whereClause1;
            String[] whereArgs = {String.valueOf(ownerId), String.valueOf(mbrId),
                    String.valueOf(mbrId), String.valueOf(ownerId) };
            int temp = db.delete(TABLE_NAME, whereClause, whereArgs); // return value is the deleted count
            // Log.d("HomersqlDeleteChatMbr2 ", whereClause + ":" + temp);
        }

        ArrayList<ChatMsg> generateChannel(int masterId, int ownerId){
            ArrayList<ChatMsg> temp = new ArrayList<>();
            // query ChatMsg which include masterId, int ownerId
            String whereClause1 = "( MBRIDFROM = ? and MBRIDTO = ? )";
            String whereClause2 = "( MBRIDTO = ? and MBRIDFROM = ? )";
            String whereClause = whereClause1 + " OR " +whereClause2;
            String[] columns = { "CHATID" , "TIMESTART" , "MBRIDFROM" , "MBRIDTO" , "CHATTYPE" , "TXTMSG" };
            String[] selArgs = { String.valueOf(masterId), String.valueOf(ownerId), String.valueOf(masterId), String.valueOf(ownerId) };

            Cursor cursor = db.query(TABLE_NAME, columns, whereClause,
                    selArgs, null, null, null, null);
            while (cursor.moveToNext()) {
                ChatMsg cm = new ChatMsg();
                cm.setChatId(cursor.getInt(0));
                cm.setTimeStart(cursor.getLong(1));
                cm.setMbrIdFrom(cursor.getInt(2));
                cm.setMbrIdTo(cursor.getInt(3));
                cm.setChatType(cursor.getInt(4));
                cm.setTxtMsg(cursor.getString(5));

                temp.add(cm);
            }
            cursor.close();
            // Log.d("HomersqlGenChn", "return count:" + temp.size());
            return temp;
        }
    }

    class MemberTable extends FriendTable {

        MemberTable() {
            TABLE_NAME = "MemberTable";
            TABLE_COLS = "( " + "MBRID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "MBRICONIDX INTEGER NOT NULL, " +
                    "MBRALIAS TEXT NOT NULL, " +
                    "MBRPHONE TEXT NOT NULL, " +
                    "MBREMAIL TEXT NOT NULL, " +
                    "MBRPASSWORD TEXT NOT NULL " +
                    " );";
            genTABLE_CREATE();
            nCOLS = new String[] {"MBRID","MBRICONIDX","MBRALIAS","MBRPHONE",
                    "MBREMAIL","MBRPASSWORD"};
        }

        @Override
        public String toString() {
            return "MemberTable{" +
                    "CREATE='" + TABLE_CREATE + '\'' +
                    '}';
        }

        void addMember(Member x){
            ContentValues values = new ContentValues();
            // values.put(nCOLS[0], x.getMbrId());
            // TODO: modify getMbrIconIdx
            values.put(nCOLS[1], 0);
            values.put(nCOLS[2], x.getMbrAlias());
            values.put(nCOLS[3], x.getMbrPhone());
            values.put(nCOLS[4], x.getMbrEmail());
            values.put(nCOLS[5], x.getMbrPassword());

            long pKey1 = db.insert(TABLE_NAME, null, values);
            x.setMbrID((int)pKey1);
            x.setMbrIconIdx();
            // Log.d("HomersqlAddMbr", pKey1 + ":" + x.getMbrID()+ ":" + x.getMbrIconIdx());
        }

        void updateMember(Member x){
            // UPDATE table_name SET column1 = value1, column2 = value2...., columnN = valueN WHERE [condition];
            ContentValues values = new ContentValues();
            values.put(nCOLS[1], x.getMbrIconIdx());
            values.put(nCOLS[2], x.getMbrAlias());
            values.put(nCOLS[3], x.getMbrPhone());
            values.put(nCOLS[4], x.getMbrEmail());
            values.put(nCOLS[5], x.getMbrPassword());

            String whereClause = "MBRID = ?";
            String[] whereArgs = { String.valueOf(x.getMbrID())};
            int i = db.update(TABLE_NAME, values, whereClause, whereArgs);
            //Log.d("HomersqlUpdMbr", whereClause + ":" + x.getMbrID()+ ":" + i);
        }

        void deleteMember(int memberId){
            String whereClause = "MBRID = ?";
            String[] whereArgs = {String.valueOf(memberId) };
            int temp = db.delete(TABLE_NAME, whereClause, whereArgs); // return value is the deleted count
            // Log.d("HomersqlDeleteMbr ", whereClause + ":" + temp);
        }

        Member queryMemberById(int memberId){
            ArrayList<Member> temp = new ArrayList<>();
            String whereClause = "MBRID = ?";
            String[] selArgs = {String.valueOf(memberId) };

            Cursor cursor = db.query(TABLE_NAME, nCOLS, whereClause,
                    selArgs, null, null, null, null);
            while (cursor.moveToNext()) {
                Member m = new Member();
                m.setMbrID(cursor.getInt(0));
                // TODO modify setMbrIconIdx , cursor.getInt(1)
                m.setMbrIconIdx();
                m.setMbrAlias(cursor.getString(2));
                m.setMbrPhone(cursor.getString(3));
                m.setMbrEmail(cursor.getString(4));
                m.setMbrPassword(cursor.getString(5));
                temp.add(m);
            }
            //Log.d("HomersqlQueryMbrID ", whereClause + ":" + temp.size());
            // temp.size() must be 1 because memberId is unique. If zero, nothing find
            if(temp.size() == 0){
                Member m = new Member();
                m.setMbrID(-1);
                temp.add(m);
            }
            return temp.get(0);
        }

        ArrayList<Member> queryMemberByEmail(String partEmail,boolean exact){
            ArrayList<Member> temp = new ArrayList<>();
            String whereClause = "MBREMAIL LIKE ?";
            String[] selArgs = { "%" + partEmail + "%"};
            if(exact){
                whereClause = "MBREMAIL = ?" ;
                selArgs = new String[] { partEmail };
            }

            Cursor cursor = db.query(TABLE_NAME, nCOLS, whereClause,
                    selArgs, null, null, null, null);

            while (cursor.moveToNext()) {
                Member m = new Member();
                m.setMbrID(cursor.getInt(0));
                // TODO modify setMbrIconIdx , cursor.getInt(1)
                m.setMbrIconIdx();
                m.setMbrAlias(cursor.getString(2));
                m.setMbrPhone(cursor.getString(3));
                m.setMbrEmail(cursor.getString(4));
                m.setMbrPassword(cursor.getString(5));
                temp.add(m);
            }
            // Log.d("HomersqlQueryEmail ", whereClause + ":" + temp.size());
            return temp;
        }

    }


        public SqlDbHelper(Context context) {
        //Context context, String name, SQLiteDatabase.CursorFactory factory, int version
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        friendTable.create(db);
        chatMsgTable.create(db);
        memberTable.create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        friendTable.upgradeTable(db, oldVersion, newVersion);
        chatMsgTable.upgradeTable(db, oldVersion, newVersion);
        memberTable.upgradeTable(db, oldVersion, newVersion);
    }

    public SQLiteDatabase getDb() { return db; }

    public void setDb(boolean writable) {
        db = writable ? getWritableDatabase() : getReadableDatabase() ;
    }

    // put into onDestroy() in the activity
    void onDestroy(){
        close();
    }
}
