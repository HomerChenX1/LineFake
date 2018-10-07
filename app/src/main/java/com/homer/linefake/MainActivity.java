package com.homer.linefake;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_name) + ": Login");
        eMailAutoList = getResources().getStringArray(R.array.email_auto_list);
        findViews();
        vMessages.setText(eMailAutoList[0]+"\n"+eMailAutoList[1]+"\n"+eMailAutoList[2]+"\n"+eMailAutoList[3]);
        DbHelper.getInstance().initDbHelper();
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
            showProgress(true);
            vMessages.setText("Password:" + email + "." + password + "\n");
            DbHelper.guest.setMbrPassword(password).setMbrEmail(email);
            int x = DbHelper.getInstance().doEmailLogin(DbHelper.guest);
            // vMessages.setText("Password:" + email + "." + password + "\n");
            StringBuilder sb = new StringBuilder();
            sb.append("ID:").append(DbHelper.guest.getMbrID()).append(" ICON:").append(DbHelper.guest.getMbrIconIdx())
                    .append(" Alias:").append(DbHelper.guest.getMbrAlias()).append(" Phone:").append(DbHelper.guest.getMbrPhone())
                    .append(" EMail:").append(DbHelper.guest.getMbrEmail()).append(" Pwd:").append(DbHelper.guest.getMbrPassword());
            vMessages.setText(sb.toString());
            showProgress(false);
            switch(x){
                case 1:
                    Toast.makeText(this,"E-Mail not found !",Toast.LENGTH_LONG).show();
                    vEmail.setError(getString(R.string.error_not_exist_email));
                    vEmail.requestFocus();
                    break;
                case 2:
                    Toast.makeText(this,"wrong PWD !",Toast.LENGTH_LONG).show();
                    vPassword.setError(getString(R.string.error_incorrect_password));
                    vPassword.requestFocus();
                    break;
                default:
                    DbHelper.guest.copyTo(DbHelper.owner);
                    //Toast.makeText(this,"Login success!" + R.drawable.p02 ,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, InfoActivity.class);
                    startActivity(intent);
            }

        }
    }

    private void showProgress(final boolean show) {
        vProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        // others must be hidden
        // mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public void onClickRegister(View view){
        Intent intent = new Intent(this, MemberActivity.class);
        intent.putExtra("member_mode",0);
        startActivity(intent);
    }
}
