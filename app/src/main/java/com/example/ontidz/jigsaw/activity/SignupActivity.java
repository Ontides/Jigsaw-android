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
import android.widget.Toast;

import com.example.ontidz.jigsaw.R;
import com.example.ontidz.jigsaw.common.Grade;
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

public class SignupActivity extends AppCompatActivity  implements View.OnClickListener{

    private Button signUpButton;
    private EditText userNameText;
    private EditText passwordText;

    private String target;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("注册");

        target = getIntent().getStringExtra("target");

        signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(this);

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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_up_button:
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder()
                        .add("user", userNameText.getText().toString())
                        .add("password",passwordText.getText().toString())
                        .build();
                final Request request = new Request.Builder()
                        .url("http://45.77.12.32:8081/signup")
                        .post(requestBody)
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(SignupActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String res = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (res){
                                    case "hasUser":
                                        Log.e("w",res);
                                        Toast.makeText(SignupActivity.this, "该用户名已被使用！请更换", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "true":
                                        Log.e("w",res);
                                        Toast.makeText(SignupActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                        Login.setIsLogin(true);
                                        Login.setUserName(SignupActivity.this.userNameText.getText().toString());

                                        switch (target){
                                            case "rank":
                                                startActivity(new Intent(SignupActivity.this,RankActivity.class));
                                                break;
                                            case "game":
                                                Grade.setGrade(userNameText.getText().toString(), new String[1], SignupActivity.this, false);
                                                finish();
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case "false":
                                        Log.e("w",res);
                                        Toast.makeText(SignupActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Log.e("w",res);
                                        Toast.makeText(SignupActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });
                    }
                });
                break;
            default:
                break;
        }
    }
}
