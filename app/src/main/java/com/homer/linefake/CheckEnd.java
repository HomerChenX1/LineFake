package com.homer.linefake;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

//AsyncTask<Params, Progress, Result>
// Params: type for doInBackground
// Progress: type for onProgressUpdate
// Result : type for doInBackground and onPostExecute
// new CheckEnd(this).execute("firstMessage"); for forground execute
// ProgressDialog is depressed, need to change to ProcessBar. But change to ProcessBar may not induce onResume
class CheckEnd extends AsyncTask<String, Integer, Long> {
    private ProgressDialog progressBar;

    public CheckEnd() { super();}
    public CheckEnd(Context context) {
        progressBar = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        // In UI thread
        //執行前 設定可以在這邊設定
        super.onPreExecute();
        //初始化進度條並設定樣式及顯示的資訊。
        progressBar.setMessage("Loading...");
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.show();

    }

    protected boolean checkEnd(String... strings){ return false; }

    @Override
    protected Long doInBackground(String... strings) {
        // In non-UI thread
        //執行中 在背景做事情
        int progress = 0;
        for(int i=0;i<25;i++){
            try {
                Thread.sleep(100);
                if(checkEnd(strings)){
                    publishProgress(100);
                    break;
                }
                else publishProgress(progress+=4);
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
        progressBar.setProgress(values[0]);

    }

    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
        //當完成的時候，把進度條消失
        progressBar.dismiss();
    }
}

class QueryFriendCheckEnd extends CheckEnd{
    public QueryFriendCheckEnd(Context context) {
        super(context);
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
