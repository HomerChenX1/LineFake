package com.homer.linefake;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private ProgressBar vProgress;
    private TextInputLayout vEmailLayout;
    private EditText vEmail;
    private TextInputLayout vPasswordLayout;
    private EditText vPassword;
    private Button vEmailSignInBtn;
    private Button vRegisterBtn;
    private TextView vMessages;
    private String[] eMailAutoList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setTitle(getString(R.string.app_name) + ": Login");
        eMailAutoList = getResources().getStringArray(R.array.email_auto_list);
        vMessages.setText(eMailAutoList[0]+"\n"+eMailAutoList[1]+"\n"+eMailAutoList[2]+"\n"+eMailAutoList[3]);
    }
    private void findViews() {
        vProgress = findViewById(R.id.login_progress);

        vEmailLayout = findViewById(R.id.login_email_layout);
        vEmail = findViewById(R.id.login_email);

        vPasswordLayout = findViewById(R.id.login_password_layout);
        vPassword = findViewById(R.id.login_password);

        vEmailSignInBtn = findViewById(R.id.login_email_sign_in_btn);
        vRegisterBtn = findViewById(R.id.login_register_btn);
        vMessages = findViewById(R.id.login_messages);
    }

    public void onClickEmailSignIn(View view){
        View vFocus = null;
        boolean cancel = false;

        // for password processing
        vPassword.setError(null);
        String password = vPassword.getText().toString().trim();
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            vPassword.setError(getString(R.string.error_invalid_password));
            vFocus = vPassword;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            vFocus.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            vMessages.setText("Password:" + password);
            // mAuthTask = new UserLoginTask(email, password);
            // mAuthTask.execute((Void) null);
            showProgress(false);
        }
    }
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 6;
    }
    private void showProgress(final boolean show) {
        vProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        // others must be hidden
        // mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public void onClickRegister(View view){
        Intent intent = new Intent(this, MemberActivity.class);
        startActivity(intent);
    }
}
