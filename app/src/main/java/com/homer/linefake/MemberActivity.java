package com.homer.linefake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MemberActivity extends AppCompatActivity {
    private TextView vMessages;
    private TextView vId;
    private EditText vAlias;
    private EditText vPhone;
    private EditText vEmail;
    private EditText vPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        setTitle(getString(R.string.app_name) + ": Member");
        findViews();
    }
    private void findViews() {
        vMessages = findViewById(R.id.mbr_messages); // copied
        vId = findViewById(R.id.mbr_id);
        vAlias = findViewById(R.id.mbr_alias);
        vPhone = findViewById(R.id.mbr_phone);
        vEmail = findViewById(R.id.mbr_email);  // copied
        vPassword = findViewById(R.id.mbr_password); // copied
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
        }
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
            }
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
            }
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
            //showProgress(true);
            vMessages.setText("Password:" + email + "." + password);
            // mAuthTask = new UserLoginTask(email, password);
            // mAuthTask.execute((Void) null);
            //showProgress(false);
        }

    }
    // mbr_cancel_btn
    public void onClickMbrCancel(View view){
        vMessages.setText("");
        vId.setText("");
        vAlias.setText("");
        vPhone.setText("");
        vEmail.setText("");
        vPassword.setText("");
    }
    // when register mode, no delete button
    public void onClickMbrDelete(View view){

    }
}
