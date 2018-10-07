package com.homer.linefake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setTitle(getString(R.string.app_name) + ":  Owner Info");

        // LinearLayout only use setOnClickListener. Do not set onClick in XML file
        LinearLayout app_layer = (LinearLayout) findViewById (R.id.info_update);
        app_layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(InfoActivity.this, MemberActivity.class);
                Intent intent = new Intent(v.getContext(), MemberActivity.class);
                intent.putExtra("member_mode",1);
                startActivity(intent);
                // Toast.makeText(InfoActivity.this, "Click available", Toast.LENGTH_LONG).show();
            }
        });
    }
}
