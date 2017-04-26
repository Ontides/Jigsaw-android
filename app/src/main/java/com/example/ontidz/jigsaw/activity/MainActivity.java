package com.example.ontidz.jigsaw.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.ontidz.jigsaw.R;
import com.example.ontidz.jigsaw.common.Login;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView rankText;
    private TextView startGameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("拼拼");


        rankText = (TextView)findViewById(R.id.rank);
        startGameText = (TextView)findViewById(R.id.start_game);

        rankText.setOnClickListener(this);
        startGameText.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.rank:
                if (!Login.isLogin()) {
                    String target = "rank";
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("target", target);
                    startActivity(intent);
                }else{
                    startActivity(new Intent(this,RankActivity.class));
                }
                break;
            case R.id.start_game:
                if (!Login.isLogin()) {
                    String target = "game";
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("target", target);
                    startActivity(intent);
                }else{
                    GameActivity.startActivity(this,1);
                }
                break;
            default:
                break;
        }
    }
}
