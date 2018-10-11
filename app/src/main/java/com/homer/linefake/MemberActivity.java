package com.homer.linefake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.homer.linefake.DbHelper.owner;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
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
    // mbr_register_btn
    public void onClickMbrRegister(View view){
        View vFocus = null;
        boolean cancel = false;
        int temp;

        // vAlias
        vAlias.setError(null);
        String alias = vAlias.getText().toString().trim();
        // Check for a valid password, if the user entered one.
        temp = Member.isAliasValid(alias);
        if(temp!=0){
            // vPassword.setError(getString(R.string.error_invalid_password));
            vFocus = vAlias;
            cancel = true;
        } else DbHelper.owner.setMbrAlias(alias);
        switch (temp){
            case 1:
                vAlias.setError(getString(R.string.error_field_required));
                break;
            case 2:
                vAlias.setError(getString(R.string.error_invalid_field));
        }

        // vPhone
        vPhone.setError(null);
        String phone = vPhone.getText().toString().trim();
        // Check for a valid password, if the user entered one.
        if(temp==0) {
            temp = Member.isPhoneValid(phone);
            if (temp != 0) {
                // vPassword.setError(getString(R.string.error_invalid_password));
                vFocus = vPhone;
                cancel = true;
            } else DbHelper.owner.setMbrPhone(phone);
            switch (temp) {
                case 1:
                    vPhone.setError(getString(R.string.error_field_required));
                    break;
                case 2:
                    vPhone.setError(getString(R.string.error_invalid_field));
            }
        }

        // for email processing
        vEmail.setError(null);
        String email = vEmail.getText().toString().trim();
        // Check for a valid email address.
        if(temp==0) {
            temp = Member.isEmailValid(email);
            if (temp != 0) {
                vFocus = vEmail;
                cancel = true;
            } else DbHelper.owner.setMbrEmail(email);
            switch (temp) {
                case 1:
                    vEmail.setError(getString(R.string.error_field_required));
                    break;
                case 2:
                    vEmail.setError(getString(R.string.error_invalid_email));
            }
        }

        // for password processing
        vPassword.setError(null);
        String password = vPassword.getText().toString().trim();
        // Check for a valid password, if the user entered one.
        if(temp==0) {
            temp = Member.isPasswordValid(password);
            if (temp != 0) {
                // vPassword.setError(getString(R.string.error_invalid_password));
                vFocus = vPassword;
                cancel = true;
            } else DbHelper.owner.setMbrPassword(password);
            switch (temp) {
                case 1:
                    vPassword.setError(getString(R.string.error_field_required));
                    break;
                case 2:
                    vPassword.setError(getString(R.string.error_invalid_password));
            }
        }

        if (cancel) {
            // There was an error; don't attempt register/update and focus the first
            // form field with an error.
            vFocus.requestFocus();
        } else {
            // perform the user register/update attempt.
            vMessages.setText("start line 144");
            StringBuilder sb = new StringBuilder();
            sb.append("ID:").append(DbHelper.owner.getMbrID()).append(" ICON:").append(DbHelper.owner.getMbrIconIdx())
                    .append(" Alias:").append(DbHelper.owner.getMbrAlias()).append(" Phone:").append(DbHelper.owner.getMbrPhone())
                    .append(" EMail:").append(DbHelper.owner.getMbrEmail()).append(" Pwd:").append(DbHelper.owner.getMbrPassword())
                    .append(" Owner friends:").append(DbHelper.owner.getFriendSet().length);
            vMessages.setText(sb.toString());
//            if(mode == 0){
//                // mode == 0 register, add owner to memberTable,  friendTable with admin, delete owner.ID = 0 , to login
            // use intent will generate backpress error
            // jump to the bottom activity will induced backpress error
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
//            }
//            else{
//                // mode == 1 update, up owner to memberTable
            // jump to the bottom activity will induced backpress error
//                Intent intent = new Intent(this, InfoActivity.class);
//                startActivity(intent);
//            }
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
        // delete memberTable by owner.ID
        // delete friendTable by owner.ID  ID[0],ID[1]
        // delete chatMsgTable by owner.ID IDfrom IDto
        // channel clear
        // owner.friendset clear
        // delete owner.ID = 0 master.ID = 0
        // jump to the bottom activity will induced backpress error
//        Intent intent = new Intent(MemberActivity.this, MainActivity.class);
//        startActivity(intent);
    }
}
