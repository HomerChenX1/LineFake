package com.homer.linefake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static com.homer.linefake.DbHelper.owner;
/*
DbHelper.getInstance().deleteOwner();
*/
public class MemberActivity extends AppCompatActivity {
    private TextView vMessages;
    private TextView vId;
    private EditText vAlias;
    private EditText vPhone;
    private EditText vEmail;
    private EditText vPassword;
    private Button vRegisterBtn;
    private Button vDeleteBtn;
    private int mode; // 0:register, 1:update

    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        setTitle(getString(R.string.app_name) + ": Member");
        findViews();
        Intent intent = getIntent();
        mode = intent.getIntExtra("member_mode", -1);
        vMessages.setText("mode is:" + mode);
        if(mode == 1){
            // update mode vAlias vPhone vEmail vPassword
            vId.setText(getString(R.string.prompt_id) + ":" + String.valueOf(owner.getMbrID()));
            vAlias.setText(owner.getMbrAlias());
            vPhone.setText(owner.getMbrPhone());
            vEmail.setText(owner.getMbrEmail());
            vPassword.setText(owner.getMbrPassword());
            vRegisterBtn.setText(getString(R.string.action_update));
        }
        else
        {
            vDeleteBtn.setVisibility(View.GONE);
        }
    }
    private void findViews() {
        vMessages = findViewById(R.id.mbr_messages); // copied
        vId = findViewById(R.id.mbr_id);
        vAlias = findViewById(R.id.mbr_alias);
        vPhone = findViewById(R.id.mbr_phone);
        vEmail = findViewById(R.id.mbr_email);  // copied
        vPassword = findViewById(R.id.mbr_password); // copied
        vRegisterBtn = findViewById(R.id.mbr_register_btn);
        vDeleteBtn = findViewById(R.id.mbr_delete_btn);
    }

    String chkEntries(){
        int temp;
        // vAlias
        vAlias.setError(null);
        String alias = vAlias.getText().toString().trim();
        // Check for a valid password, if the user entered one.
        temp = Member.isAliasValid(alias);
        switch(temp){
            case 0:
                DbHelper.owner.setMbrAlias(alias);
                break;
            case 1:
                vAlias.setError(getString(R.string.error_field_required));
                return "vAlias";
            case 2:
                vAlias.setError(getString(R.string.error_invalid_field));
                return "vAlias";
        }

        // vPhone
        vPhone.setError(null);
        String phone = vPhone.getText().toString().trim();
        // Check for a valid password, if the user entered one.
        temp = Member.isPhoneValid(phone);
        switch(temp){
            case 0:
                DbHelper.owner.setMbrPhone(phone);
                break;
            case 1:
                vPhone.setError(getString(R.string.error_field_required));
                return "vPhone";
            case 2:
                vPhone.setError(getString(R.string.error_invalid_field));
                return "vPhone";
        }

        // for password processing
        vPassword.setError(null);
        String password = vPassword.getText().toString().trim();
        // Check for a valid password, if the user entered one.
        temp = Member.isPasswordValid(password);
        switch (temp) {
            case 0:
                DbHelper.owner.setMbrPassword(password);
                break;
            case 1:
                vPassword.setError(getString(R.string.error_field_required));
                return "vPassword";
            case 2:
                vPassword.setError(getString(R.string.error_invalid_password));
                return "vPassword";
        }

        // for email processing
        vEmail.setError(null);
        String email = vEmail.getText().toString().trim();
        temp = Member.isEmailValid(email);
        switch(temp) {
            case 0:
                DbHelper.owner.setMbrEmail(email);
                break;
            case 1:
                vEmail.setError(getString(R.string.error_field_required));
                return "vEmail";
            case 2:
                vEmail.setError(getString(R.string.error_invalid_email));
                return "vEmail";
            case 3:
                vEmail.setError(getString(R.string.error_duplicate_email));
                return "vEmail";
            default:
                DbHelper.getInstance().queryMemberByEmailExact(email);
        }
        return "done";
    }
    // mbr_register_btn
    public void onClickMbrRegister(View view){
        View vFocus = null;
        boolean cancel = true;

        String mString = chkEntries();
        // Log.d("MemberActivity","mString:"+mString);
        switch(mString){
            case "vAlias":
                vFocus = vAlias;
                break;
            case "vPhone":
                vFocus = vPhone;
                break;
            case "vPassword":
                vFocus = vPassword;
                break;
            case "vEmail":
                vFocus = vEmail;
                break;
            case "done":
                cancel = false;
                break;
        }
        if (cancel) {
            // There was an error; don't attempt register/update and focus the first
            // form field with an error.
            vFocus.requestFocus();
            return;
        }
        //Log.d("Homerfb", DbHelper.owner.toString());
        if(mode == 1){
            // mode == 1 update, up owner to memberTable
            DbHelper.getInstance().updateMember(DbHelper.owner);
            finish();
        }else{
            ArrayList<Member> temp = DbHelper.getInstance().queryMemberByEmailExact(DbHelper.owner.getMbrEmail());
            if(DbHelper.useSQL==2) new doEmailLoginFBCheckEnd(50).execute("doEmailLoginFB");
        }
    }
    // mbr_cancel_btn
    public void onClickMbrCancel(View view){
        vId.setText(mode == 1 ? getString(R.string.prompt_id) + ":" + String.valueOf(owner.getMbrID()) : "");
        vAlias.setText(mode == 1 ? owner.getMbrAlias() : "");
        vPhone.setText(mode == 1 ? owner.getMbrPhone() : "");
        vEmail.setText(mode == 1 ? owner.getMbrEmail() : "");
        vPassword.setText(mode == 1 ? owner.getMbrPassword() : "");
        vAlias.requestFocus();
    }
    // when register mode, no delete button
    public void onClickMbrDelete(View view){
        // jump to the bottom activity will induced backpress error
        // restart the application
        DbHelper.getInstance().deleteOwner();
        if(DbHelper.useSQL==2)
            new deleteOwnerCheckEnd(this).execute("deleteOwner");
        else restart();
    }
    void restart1(){
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
    void restart(){
        // use multiple backpress
        DbHelper.multipleBack = 1;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EbusEvent event) {
        switch (event.getEventMsg()) {
            case "countFinish":
                Log.d("MemberActivity", "Now it happenes countFinish");
                break;
            case "doEmailLoginFB":
                // now in register mode
                int temp = DbHelper.getInstance().fireDbHelper.queryMbrByEmailList.size();
                //Log.d("MemberActivity", "doEmailLoginFB:" + temp);
                if(temp!=0){
                    vEmail.setError(getString(R.string.error_duplicate_email));
                    vEmail.requestFocus();
                    // why finish
                } else{
                    // mode == 0 register, add owner to memberTable,  friendTable with admin, delete owner.ID = 0 , to login
                    //use intent will generate backpress error
                    //jump to the bottom activity will induced backpress error
                    //Toast.makeText(view.getContext(), String.valueOf(DbHelper.getInstance().generateMbrID()), Toast.LENGTH_LONG).show();
                    DbHelper.getInstance().registerMember(DbHelper.owner);
                    // DbHelper.owner.setMbrID(0); do not add this for multiple update
                }
                finish();
                break;
            case "deleteOwner":
                Log.d("MemberActivity", "deleteOwner finish");
                owner.setMbrID(0);
                restart();
                break;
            default:
                break;
        }
    }
}
