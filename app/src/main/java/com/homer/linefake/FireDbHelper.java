package com.homer.linefake;

// Default database URL  : https://linefake-ad479.firebaseio.com/
// your-project-id : linefake-ad479
//Default hosting subdomain — your-project-id.firebaseapp.com

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//AsyncTask<Params, Progress, Result>
// Params: type for doInBackground
// Progress: type for onProgressUpdate
// Result : type for doInBackground and onPostExecute
// ProgressDialog is depressed, need to change to ProcessBar. But change to ProcessBar may not induce onResume
class GetImage extends AsyncTask<String , Integer , Bitmap> {
    private ProgressDialog progressBar;
    private TextView vMessages;

    public GetImage(TextView vMessages) {
        this.vMessages = vMessages;
    }

    @Override
    protected void onPreExecute() {
        // In UI thread
        //執行前 設定可以在這邊設定
        super.onPreExecute();
        // Log.d("HomerfbOnPreExecute" , String.valueOf(Thread.currentThread().getId()));
        progressBar = new ProgressDialog(vMessages.getContext());
        progressBar.setMessage("Loading...");
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.show();
        //初始化進度條並設定樣式及顯示的資訊。
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        // In non-UI thread
        //執行中 在背景做事情
        int progress = 0;
        // for (String urlStr : params) {
        for(int i=0; i<20; i++){
            try{
                Thread.sleep(100);
                if(FireDbHelper.queryFriendTotalCount<=0){
                    Log.d("HomerfbQueryFriend", "sleep break!");
                    break;
                }
                progress = 150 / FireDbHelper.queryFriendTotalCount;
                publishProgress(progress);
            }
            catch (InterruptedException e){
                Log.d("HomerfbQueryFriend", "sleep interrupted");
                e.printStackTrace();
                break;
            }
        }
        // }
        publishProgress(100);
        //最後達到100%
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // In UI thread , every time publishProgress called, this routine will be executed
        //執行中 可以在這邊告知使用者進度
        super.onProgressUpdate(values);
        // Log.d("HomerfbOnProgressUpdate", String.valueOf(Thread.currentThread().getId()));
        progressBar.setProgress(values[0]);
        //取得更新的進度
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        // In UI thread
        //執行後 完成背景任務
        super.onPostExecute(bitmap);
        // Log.d("HomerfbOnPostExecute", String.valueOf(Thread.currentThread().getId()));
        progressBar.dismiss();
        //當完成的時候，把進度條消失
        Log.d("HomerfbOnPostExecute", "finish!!");
        vMessages.setText(vMessages.getText().toString() + "\nFinish");
    }
}


class FireDbHelper {
    private FirebaseDatabase db;
    FriendTable friendTable = new FriendTable();
    static int deleteFriendCnt = 0;
    static int queryFriendTotalCount = 100;
    // static Context will induced a memory leak
    TextView vMessages = null;

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
                public void onDataChange(DataSnapshot dataSnapshot) {
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
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("HomerfbDeleteFriend", "onCancelled", databaseError.toException());
                }
            });

            // to masterId's friend delete ownerId
            query = myRef.child(String.valueOf(masterId));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
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
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("HomerfbDeleteFriend", "onCancelled", databaseError.toException());
                }
            });
            // Log.d("HomerfbDeleteFriend", "total del:" + deleteFriendCnt);
            // TODO send notify to masterId that owneridis disappear
            return deleteFriendCnt; // this deleteFriendCnt  will be wrong because async
        }

        Integer[] queryFriend(final int ownerId){
            final ArrayList<Integer> temp = new ArrayList<>();

            // read ownerId's friend then send out
            queryFriendTotalCount = 100;
            DatabaseReference myRef = db.getReference(TABLE_NAME);
            Query query = myRef.child(String.valueOf(ownerId));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    queryFriendTotalCount = (int) dataSnapshot.getChildrenCount();
                    Log.d("HomerfbQueryFriend", "total cnt:" + queryFriendTotalCount);
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
//                        long temp2 = (long)ds.getValue();
//                        temp.add(new Integer((int)temp2));
                        int temp2 = ds.getValue(Integer.class);
                        temp.add(temp2);
                        queryFriendTotalCount--;
                        Log.d("HomerfbQueryFriend", "cnt:" + temp.size() + ":" + temp2 + ":" + queryFriendTotalCount);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("HomerfbQueryFriend", "onCancelled", databaseError.toException());
                }
            });
            // the problem is need to wait complete temp.size() == queryFriendTotalCount
            // queryFriendTotalCount == temp.size()
            new GetImage(vMessages).execute("http://i.imgur.com/Uki7N9T.jpg");

            // directly pass here
            // Log.d("HomerfbQueryFriend", "final:" + temp.size());
            return temp.toArray(new Integer[0]);
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
