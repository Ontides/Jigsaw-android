package com.example.ontidz.jigsaw.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ontidz.jigsaw.R;
import com.example.ontidz.jigsaw.common.Grade;
import com.example.ontidz.jigsaw.common.Info;
import com.example.ontidz.jigsaw.common.Login;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ontidz on 2017/4/12.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button loginButton;
    private TextView signUp;
    private EditText userNameText;
    private EditText passwordText;

    //访问目的页面
    String target;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("登录");

        Intent intent = getIntent();
        target = intent.getStringExtra("target");

        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);

        signUp = (TextView) findViewById(R.id.sign_up);
        signUp.setOnClickListener(this);

        userNameText = (EditText) findViewById(R.id.userNameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.login_button:

                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder()
                        .add("user", userNameText.getText().toString())
                        .add("password",passwordText.getText().toString())
                        .build();
                final Request request = new Request.Builder()
                        .url(Info.serverAddress + "/login")
                        .post(requestBody)
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(LoginActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String res = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (res){
                                    case "noUser":
                                        Log.e("w",res);
                                        Toast.makeText(LoginActivity.this, "用户名错误", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "true":
                                        Log.e("w",res);
                                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                        Login.setIsLogin(true);
                                        Login.setUserName(LoginActivity.this.userNameText.getText().toString());
                                        switch (target){
                                            case "rank":
                                                startActivity(new Intent(LoginActivity.this,RankActivity.class));
                                                break;
                                            case "game":
                                                Grade.setGrade(userNameText.getText().toString(), new String[1], LoginActivity.this, false);
                                                finish();
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "false":
                                        Log.e("w",res);
                                        Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Log.e("w",res);
                                        Toast.makeText(LoginActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });
                    }
                });
                break;
            case R.id.sign_up:
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                intent.putExtra("target", target);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
