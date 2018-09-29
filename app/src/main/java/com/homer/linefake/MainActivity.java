package com.homer.linefake;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setTitle(getString(R.string.app_name) + ": Login");
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

    public void onClickEmailSignIn(View view){}

    public void onClickRegister(View view){
        Intent intent = new Intent(this, MemberActivity.class);
        startActivity(intent);
    }
}
