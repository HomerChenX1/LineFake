package com.homer.linefake;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;

//AsyncTask<Params, Progress, Result>
// Params: type for doInBackground
// Progress: type for onProgressUpdate
// Result : type for doInBackground and onPostExecute
// new CheckEnd(this).execute("firstMessage"); for forground execute
// ProgressDialog is depressed, need to change to ProcessBar. But change to ProcessBar may not induce onResume
class CheckEnd extends AsyncTask<String, Integer, Long> {
    private ProgressDialog progressBar;
    private int cntDown = 50;
    private boolean toShow = true;

    public CheckEnd() { super(); }
    public CheckEnd(Context context, int cntDown) {
        this.cntDown = cntDown;
        progressBar = new ProgressDialog(context);
        toShow = true;
    }

    public CheckEnd(int cntDown) {
        toShow = false;
        this.cntDown = cntDown;
        progressBar = null;
    }

    @Override
    protected void onPreExecute() {
        // In UI thread
        //執行前 設定可以在這邊設定
        super.onPreExecute();
        //初始化進度條並設定樣式及顯示的資訊。
        if(toShow) {
            progressBar.setMessage("Loading...");
            progressBar.setCancelable(false);
            progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBar.show();
        }

    }

    protected boolean checkEnd(String... strings){ return false; }

    @Override
    protected Long doInBackground(String... strings) {
        // In non-UI thread
        //執行中 在背景做事情
        int progress = 0;
        for(int i=0;i<cntDown;i++){
            try {
                Thread.sleep(50);
                if(checkEnd(strings)){
                    publishProgress(100);
                    break;
                }
                else publishProgress(progress+=2);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        //取得更新的進度
        if(toShow)
            progressBar.setProgress(values[0]);

    }

    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
        //當完成的時候，把進度條消失
        if(toShow)
            progressBar.dismiss();
    }
}

class QueryFriendCheckEnd extends CheckEnd{
    public QueryFriendCheckEnd(Context context) {
        super(context, 50);
    }
    @Override
    protected boolean checkEnd(String... strings) {
        if(FireDbHelper.queryFriendTotalCount == 0){
            EventBus.getDefault().post(new EbusEvent(strings[0]));
            return true;
        }
        return super.checkEnd(strings);
    }
}

class GenerateChannelCheckEnd extends CheckEnd{
    public GenerateChannelCheckEnd(Context context) {
        super(context,50);
    }
    @Override
    protected boolean checkEnd(String... strings) {
        if((FireDbHelper.genChannelCnt1==0)&&(FireDbHelper.genChannelCnt2==0)){
            EventBus.getDefault().post(new EbusEvent(strings[0]));
            return true;
        }
        return super.checkEnd(strings);
    }
}

class WaitCountCheckEnd1 extends CheckEnd{
    public WaitCountCheckEnd1(Context context, int cntDown) {
        super(context, cntDown);
    }

    @Override
    protected boolean checkEnd(String... strings) {
        if(DbHelper.getInstance().waitCount<=0){
            EventBus.getDefault().post(new EbusEvent(strings[0]));
            return true;
        }
        return super.checkEnd(strings);
    }
}

class doEmailLoginFBCheckEnd extends CheckEnd{
    public doEmailLoginFBCheckEnd(int cntDown) {
        super(cntDown);
    }

    public doEmailLoginFBCheckEnd(Context context, int cntDown) {
        super(context, cntDown);
    }

    @Override
    protected boolean checkEnd(String... strings) {
        if(DbHelper.getInstance().fireDbHelper.queryMbrByEmailCnt<=0){
            EventBus.getDefault().post(new EbusEvent(strings[0]));
            // DbHelper.getInstance().fireDbHelper.queryMbrByEmailList
            return true;
        }
        return super.checkEnd(strings);
    }
}

class doEmailLoginFB1CheckEnd extends CheckEnd{
    public doEmailLoginFB1CheckEnd(int cntDown) {
        super(cntDown);
    }
    public doEmailLoginFB1CheckEnd(Context context, int cntDown) {
        super(context, cntDown);
    }

    @Override
    protected boolean checkEnd(String... strings) {
        if(DbHelper.getInstance().fireDbHelper.queryFriendTotalCount<=0){
            EventBus.getDefault().post(new EbusEvent(strings[0]));
            return true;
        }
        return super.checkEnd(strings);
    }
}

class deleteOwnerCheckEnd extends CheckEnd{
    public deleteOwnerCheckEnd(Context context) {
        super(context, 75);
    }
    @Override
    protected boolean checkEnd(String... strings) {
        if((DbHelper.getInstance().fireDbHelper.delChatMsgMbrIdListCnt1 == 0) &&
                (DbHelper.getInstance().fireDbHelper.deleteFriendCnt != 0)){
            EventBus.getDefault().post(new EbusEvent(strings[0]));
            return true;
        }
        return super.checkEnd(strings);
    }
}
