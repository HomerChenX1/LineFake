package com.homer.linefake;

// Default database URL  : https://linefake-ad479.firebaseio.com/
// your-project-id : linefake-ad479
//Default hosting subdomain — your-project-id.firebaseapp.com

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

class FireDbHelper {
    private FirebaseDatabase db;
    FriendTable friendTable = new FriendTable();

    class FriendTable {
        String TABLE_NAME = "FriendTable";
        String [] nCOLS = {"mbrId","friendSet"};

        void create(){
            ArrayList<String> temp = new ArrayList();
            //TODO check table exist or not ?

            DatabaseReference myRef = db.getReference(TABLE_NAME + "/nCOLS");
            for(String s: nCOLS) temp.add(s);
            myRef.setValue(temp);
        }

        void addFriend(int ownerId, int masterId){
            // to ownerId's friend add masterId
            // to masterId's friend add ownerId
            // TODO send notify to masterId that ownerId is add
        }
        int deleteFriend(int ownerId, int masterId) {
            // to ownerId's friend delete masterId
            // build query then delete
            // to masterId's friend delete ownerId
            // TODO send notify to masterId that owneridis disappear
            return 0;
        }
        Integer[] queryFriend(int ownerId){
            // read ownerId's friend then send out
            return new Integer[] {0,1,2,3};
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
    }
    void onDestroy(){ db.goOffline(); }
}
