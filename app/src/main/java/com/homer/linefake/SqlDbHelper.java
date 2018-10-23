package com.homer.linefake;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class SqlDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "LineFake.db";
    private static final int DB_VERSION = 1;
    private FriendTable friendTable = new FriendTable();
    private ChatMsgTable chatMsgTable = new ChatMsgTable();
    private SQLiteDatabase db;

    class FriendTable {
        String TABLE_NAME = "FriendTable";
        String TABLE_COLS = "( OWNERID INTEGER PRIMARY KEY NOT NULL, MASTERID INTEGER NOT NULL );";
        String [] nCOLS = {"OWNERID","MASTERID"};
        String TABLE_CREATE;

        // CREATE TABLE ( ownerId INTEGER PRIMARY KEY NOT NULL, masterId INTEGER NOT NULL );
        FriendTable() { genTABLE_CREATE(); }

        void genTABLE_CREATE(){ TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + TABLE_COLS; }

        void create(SQLiteDatabase db){ db.execSQL(TABLE_CREATE); }

        void upgradeTable(SQLiteDatabase db, int i, int i1) {
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
            Log.d("sqlAddFriend : ", pKey1 + ":" + pKey2);
        }

        //DELETE FROM table_name WHERE OWNERID=ownerId AND MASTERID=masterId or OWNERID=masterId AND MASTERID=ownerId;
        int deleteFriend(int ownerId, int masterId) {
            //SQLiteDatabase db = getWritableDatabase();
            int temp = -1;
            String whereClause1 = nCOLS[0] + " = ?  and " + nCOLS[1] + " = ? " ;
            String whereClause2 = nCOLS[1] + " = ?  and " + nCOLS[2] + " = ? " ;
            String whereClause = whereClause1 + " OR " +whereClause2;
            String[] whereArgs = {String.valueOf(ownerId), String.valueOf(masterId), String.valueOf(ownerId), String.valueOf(masterId)};
            temp = db.delete(TABLE_NAME, whereClause, whereArgs);
            Log.d("sqlDeleteFriend : ", whereClause + ":" + temp);
            return temp;
        }
        //query
        //update
    }
    class ChatMsgTable extends FriendTable {

        ChatMsgTable() {
            TABLE_NAME = "ChatMsgTable";
            TABLE_COLS = "( id INTEGER PRIMARY KEY AUTOINCREMENT);";
            genTABLE_CREATE();
        }

        @Override
        public String toString() {
            return "ChatMsgTable{" +
                    "CREATE='" + TABLE_CREATE + '\'' +
                    '}';
        }
    }

    public SqlDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        friendTable.create(db);
        chatMsgTable.create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        friendTable.upgradeTable(db, oldVersion, newVersion);
        chatMsgTable.upgradeTable(db, oldVersion, newVersion);
    }

    public SQLiteDatabase getDb() { return db; }

    public void setDb(boolean writable) {
        db = writable ? getWritableDatabase() : getReadableDatabase() ;
    }

    // put into onDestroy() in the activity
    void onDestroy(){ close(); }
}
