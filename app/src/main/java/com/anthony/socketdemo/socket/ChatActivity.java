package com.anthony.socketdemo.socket;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.anthony.socketdemo.R;
import com.anthony.socketdemo.socket.adapter.MessageAdapter;

import com.anthony.socketdemo.socket.model.ResponseMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;


import io.socket.client.IO;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ChatActivity extends AppCompatActivity {

    //目前為Socket.IO上的公開測試連結
    private static final String _SERVER_URL = "https://socket-io-chat.now.sh/";

    private RecyclerView recyclerViewMessage;
    private EditText editTextMessage;
    private ArrayList<ResponseMessage> mMessages = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private String userName;

    private Socket socket;

    private Boolean isConnected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");

        messageAdapter = new MessageAdapter(this, mMessages);


        recyclerViewMessage = (RecyclerView) findViewById(R.id.recycler_view_message);
        recyclerViewMessage.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessage.setAdapter(messageAdapter);

        editTextMessage = (EditText) findViewById(R.id.edit_text_message);

        Button buttonSend = (Button) findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextMessage.getText().toString().equals("")) {
                    attemptSend();
                } else {
                    Toast.makeText(ChatActivity.this, "請輸入訊息", Toast.LENGTH_SHORT).show();
                }
            }
        });


        try {
            //初始化
            socket = IO.socket(_SERVER_URL);
        } catch (URISyntaxException e) {
            Toast.makeText(this, "初始化錯誤", Toast.LENGTH_SHORT).show();
            finish();
        }

        //註冊Sockete各種監聽
        registeredSocket();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //離開頁面
        leave();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //註銷Socket監聽
        unRegisteredSocket();
    }

    /*
     *註冊Sockete各種監聽
     */
    private void registeredSocket() {


        Log.e("EVENT",Socket.EVENT_CONNECT);

        Log.e("EVENT",Socket.EVENT_DISCONNECT);

        Log.e("EVENT",Socket.EVENT_CONNECT_ERROR);

        Log.e("EVENT",Socket.EVENT_CONNECT_TIMEOUT);


        //登入監聽
        socket.on("login", onLogin);
        //連線監聽
        socket.on(Socket.EVENT_CONNECT, onConnect);
        //斷開連線監聽
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        //連線異常監聽
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        //連線逾時監聽
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        //新訊息監聽
        socket.on("new message", onNewMessage);
        //建立連線
        socket.connect();

        // 執行新增用戶
        socket.emit("add user", userName);
    }
    //---------

    /*
     *註銷Socket各種監聽
     */
    private void unRegisteredSocket() {

        socket.disconnect();

        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.off("new message", onNewMessage);
        socket.off("login", onLogin);

    }
    //-------

    /*
     *連線監聽
     */
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isConnected) {
                        if (userName != null)

                            isConnected = true;

                            Toast.makeText(ChatActivity.this, "連線成功", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    };
    //----------

    /*
     *斷開連線監聽
     */
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    isConnected = false;
                    Toast.makeText(ChatActivity.this,
                            "已斷開連線", Toast.LENGTH_LONG).show();
                }

            });
        }
    };
    //-----------


    /*
     *連線異常監聽
     */
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ChatActivity.this,
                            "連線異常", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
    };
    //------------

    /*
     *訊息監聽
     */
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        Toast.makeText(ChatActivity.this,
                                e.toString(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    //新增訊息
                    addMessage(username, message);

                }
            });
        }
    };

    /*
     *登入監聽
     */
    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(ChatActivity.this,
                            userName + " 已登入", Toast.LENGTH_LONG).show();
                }

            });
        }
    };
    //---------

    /*
     *新增訊息
     */
    private void addMessage(String username, String message) {
        mMessages.add(new ResponseMessage.Builder(ResponseMessage._TYPE_MESSAGE)
                .username(username).message(message).build());
        messageAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }
    //------


    /*
     *滾至底部
     */
    private void scrollToBottom() {
        recyclerViewMessage.scrollToPosition(messageAdapter.getItemCount() - 1);
    }
    //-----

    /*
     *離開頁面
     */
    private void leave() {
        userName = null;
        //斷開連線
        socket.disconnect();

    }
    //------

    /*
     *訊息傳送
     */
    private void attemptSend() {

        if (userName == null) return;
        if (!socket.connected()) return;

        String message = editTextMessage.getText().toString();

        editTextMessage.setText("");

        // 傳送訊息
        socket.emit("new message", message);

        addMessage(userName, message);

    }

}
