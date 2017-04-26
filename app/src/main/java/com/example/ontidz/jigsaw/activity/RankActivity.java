package com.example.ontidz.jigsaw.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ontidz.jigsaw.R;
import com.example.ontidz.jigsaw.common.Grade;
import com.example.ontidz.jigsaw.common.Login;

/**
 * Created by ontidz on 2017/4/12.
 */

public class RankActivity extends AppCompatActivity {

    private String userName = Login.getUserName();
    private String[] data = {
            "",
            "",
            "",
            "",
            "",
            ""
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("排行榜");

        Grade.setGrade(userName, data, RankActivity.this, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case android.R.id.home:
                finish();
                break;
            case R.id.logout:
                Login.setIsLogin(false);
                Login.setUserName("");
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
