package com.homer.linefake;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class SqlDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "LineFake.db";
    private static final int DB_VERSION = 1;
    private FriendTable friendTable = new FriendTable();
    private ChatMsgTable chatMsgTable = new ChatMsgTable();

    class FriendTable {
        protected String TABLE_NAME = "FriendTable";
        protected String TABLE_COLS = "( id INTEGER PRIMARY KEY AUTOINCREMENT);";
        protected String TABLE_CREATE;

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
        //query
        //update
        //delete
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
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        friendTable.upgradeTable(db,i,i1);
        chatMsgTable.upgradeTable(db,i,i1);
    }
}
