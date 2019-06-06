package com.anthony.socketdemo.socket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anthony.socketdemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LoginActivity extends AppCompatActivity {

    private Socket socket;


    private EditText editTextName;

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        editTextName = (EditText) findViewById(R.id.edit_text_name);


        Button buttonLogin = (Button) findViewById(R.id.button_login);


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = editTextName.getText().toString();
                if (!userName.equals("")) {
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this,ChatActivity.class);
                    intent.putExtra("userName",userName);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "請輸入名稱", Toast.LENGTH_SHORT).show();
                }

            }
        });




    }



}
