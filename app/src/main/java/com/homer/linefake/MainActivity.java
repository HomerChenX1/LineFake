package com.homer.linefake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Collections;
/* Firebase finish */
public class MainActivity extends AppCompatActivity {
    private ProgressBar vProgress;
    // private TextInputLayout vEmailLayout; not necessary
    private AutoCompleteTextView vEmail;
    // private TextInputLayout vPasswordLayout; not necessary
    private EditText vPassword;
    // private Button vEmailSignInBtn;  not necessary
    // private Button vRegisterBtn;  not necessary
    private TextView vMessages;
    private String[] eMailAutoList ;

    private EventBus eventBus;
    private int genFriendListCnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        setTitle(getString(R.string.app_name) + ": Login");
        eMailAutoList = getResources().getStringArray(R.array.email_auto_list);
        findViews();
        // vMessages.setText(eMailAutoList[0]+"\n"+eMailAutoList[1]+"\n"+eMailAutoList[2]+"\n"+eMailAutoList[3]);
        // ChatMsg testChat = new ChatMsg(2, 1, ChatMsg.chatTypeText, "OK32!");
        // vMessages.setText(testChat.toString());

        switch (DbHelper.useSQL){
            case 1: // use SQLite
                // if database file exist, delete it
                File dbFile = this.getDatabasePath(SqlDbHelper.DB_NAME);
                String sMessage = dbFile.toString();

                if(dbFile.exists()) {
                    sMessage = sMessage + "\nThe db file exists. " + SqlDbHelper.DB_NAME;
                    if(this.deleteDatabase(SqlDbHelper.DB_NAME)){
                        // Toast.makeText(this,"The db file is deleted:" + SqlDbHelper.DB_NAME,Toast.LENGTH_LONG).show();
                        sMessage = sMessage + ". Then deleted";
                    }
                }
                vMessages.setText(sMessage);
                // if database file not exist, run here
                DbHelper.getInstance().sqlDbHelper = new SqlDbHelper(this);
                DbHelper.getInstance().sqlDbHelper.setDb(true);
                break;
            case 2:
                DbHelper.getInstance().fireDbHelper = new FireDbHelper();
                break;
            default:
                // use memory by ArrayList
                DbHelper.getInstance().initDbHelper();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        showProgress(false);
    }

    private void findViews() {
        vProgress = findViewById(R.id.login_progress);

        // vEmailLayout = findViewById(R.id.login_email_layout);
        vEmail = findViewById(R.id.login_email);
        // special adapter case : android.R.layout.simple_dropdown_item_1line, else will use examples/ch05
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, eMailAutoList);
        vEmail.setAdapter(adapter);

        // vPasswordLayout = findViewById(R.id.login_password_layout);
        vPassword = findViewById(R.id.login_password);

        // vEmailSignInBtn = findViewById(R.id.login_email_sign_in_btn);
        // vRegisterBtn = findViewById(R.id.login_register_btn);
        vMessages = findViewById(R.id.login_messages);
    }

    public void onClickEmailSignIn(View view){
        View vFocus = null;
        boolean cancel = false;
        int temp;
        // for email processing
        vEmail.setError(null);
        String email = vEmail.getText().toString().trim();
        // Check for a valid email address.
        temp = Member.isEmailValid(email);
        if(temp!=0){
            vFocus = vEmail;
            cancel = true;
        }
        switch (temp){
            case 1:
                vEmail.setError(getString(R.string.error_field_required));
                break;
            case 2:
                vEmail.setError(getString(R.string.error_invalid_email));
        }

        // for password processing
        vPassword.setError(null);
        String password = vPassword.getText().toString().trim();
        // Check for a valid password, if the user entered one.
        if(temp==0) {
            temp = Member.isPasswordValid(password);
            if (temp != 0) {
                vFocus = vPassword;
                cancel = true;
            }
            switch (temp) {
                case 1:
                    vPassword.setError(getString(R.string.error_field_required));
                    break;
                case 2:
                    vPassword.setError(getString(R.string.error_invalid_password));
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            vFocus.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            if(DbHelper.useSQL!=2) showProgress(true);
            // vMessages.setText("Password:" + email + "." + password + "\n");
            DbHelper.guest.setMbrPassword(password).setMbrEmail(email);
            if(DbHelper.useSQL==2){
                //DbHelper.getInstance().waitCount = 50;
                new WaitCountCheckEnd1(this, 50).execute("countFinish");
                DbHelper.getInstance().doEmailLoginFB(DbHelper.guest);
                // new doEmailLoginFBCheckEnd(MainActivity.this,50).execute("doEmailLoginFB");
                // new doEmailLoginFBCheckEnd(50).execute("doEmailLoginFB");
                return;
            }
            int x = DbHelper.getInstance().doEmailLogin(DbHelper.guest);
            // vMessages.setText("Password:" + email + "." + password + "\n");
            StringBuilder sb = new StringBuilder();
            sb.append("ID:").append(DbHelper.guest.getMbrID()).append(" ICON:").append(DbHelper.guest.getMbrIconIdx())
                    .append(" Alias:").append(DbHelper.guest.getMbrAlias()).append(" Phone:").append(DbHelper.guest.getMbrPhone())
                    .append(" EMail:").append(DbHelper.guest.getMbrEmail()).append(" Pwd:").append(DbHelper.guest.getMbrPassword())
                    .append(" Owner friends:").append(DbHelper.owner.getFriendSet().length);
            vMessages.setText(sb.toString());
            showProgress(false);
            switch(x){
                case 1:
                    // Toast.makeText(this,"E-Mail not found !",Toast.LENGTH_LONG).show();
                    vEmail.setError(getString(R.string.error_not_exist_email));
                    vMessages.setText("");
                    vEmail.requestFocus();
                    break;
                case 2:
                    // Toast.makeText(this,"wrong PWD !",Toast.LENGTH_LONG).show();
                    vPassword.setError(getString(R.string.error_incorrect_password));
                    vMessages.setText("");
                    vPassword.requestFocus();
                    break;
                default:
                    // DbHelper.guest.copyTo(DbHelper.owner);
                    //Toast.makeText(this,"Login success!" + R.drawable.p02 ,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, InfoActivity.class);
                    startActivity(intent);
            }
        }
    }

    public void onClickEmailSignIn1(int x){
        switch(x){
            case 1:
                // Toast.makeText(this,"E-Mail not found !",Toast.LENGTH_LONG).show();
                vEmail.setError(getString(R.string.error_not_exist_email));
                vMessages.setText("");
                vEmail.requestFocus();
                break;
            case 2:
                // Toast.makeText(this,"wrong PWD !",Toast.LENGTH_LONG).show();
                vPassword.setError(getString(R.string.error_incorrect_password));
                vMessages.setText("");
                vPassword.requestFocus();
                break;
            default:
                Intent intent = new Intent(this, InfoActivity.class);
                startActivity(intent);
        }
    }

    private void showProgress(final boolean show) {
        vProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        // others must be hidden
        // mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onRestart() {
        // for backpress button, keep login empty
        super.onRestart();
        // Toast.makeText(this,"onRestart wakeup!" ,Toast.LENGTH_LONG).show();
        //clean screen
        vEmail.setError(null);  // do not know why need this
        vEmail.setText("");
        vPassword.setText("");
        vMessages.setText("");
        vEmail.requestFocus();
        //clean memory
        DbHelper.getInstance().resetDbHelper();
    }

    public void onClickRegister(View view){
        Intent intent = new Intent(this, MemberActivity.class);
        intent.putExtra("member_mode",0);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if((DbHelper.useSQL==1) && (DbHelper.getInstance().sqlDbHelper != null)) {
            DbHelper.getInstance().sqlDbHelper.onDestroy();
        }
        eventBus.unregister(this);
    }

    public void onClickFireBase(View view){
        if(DbHelper.useSQL !=0)
            DbHelper.getInstance().initDbHelper(); // running under debug
        else {
            Intent intent = new Intent(this, FirebaseActivity.class);
            startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EbusEvent event){
        switch(event.getEventMsg()){
            case "countFinish":
                Log.d("MainActivity", "Now it happenes countFinish");
                break;
            case "doEmailLoginFB":
                // Log.d("MainActivity", "doEmailLoginFB:" + DbHelper.getInstance().fireDbHelper.queryMbrByEmailList.size());
                int i = DbHelper.getInstance().doEmailLoginFB1(DbHelper.getInstance().fireDbHelper.queryMbrByEmailList);
                if(i!=0) onClickEmailSignIn1(i);
                // Log.d("MainActivity", "doEmailLoginFB:" + "retv=" + i);
                break;
            case "doEmailLoginFB1":
                // Log.d("MainActivity", "doEmailLoginFB1:" + DbHelper.getInstance().fireDbHelper.queryFriendList.size());
                DbHelper.getInstance().doEmailLoginFB2(DbHelper.getInstance().fireDbHelper.queryFriendList);
                genFriendListCnt = 0;
                break;
            case "genFriendList":
                genFriendListCnt++;
                // Log.d("MainActivity", "genFriendList:" + DbHelper.getInstance().fireDbHelper.queryMbrByIdList.size() +":"+genFriendListCnt);
                if(genFriendListCnt == DbHelper.owner.getFriendSetSize()){
                    // the last message
                    // Log.d("MainActivity", "genFriendList tot:" + DbHelper.getInstance().fireDbHelper.queryMbrByIdList.size());
                    DbHelper.friendList.addAll(DbHelper.getInstance().fireDbHelper.queryMbrByIdList);
                    onClickEmailSignIn1(0);
                    DbHelper.getInstance().resetWaitCount();
                }
                break;
            default:
                break;
        }
    }
}
