package com.example.ontidz.jigsaw.common;

import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.ontidz.jigsaw.R;
import com.example.ontidz.jigsaw.activity.GameActivity;
import com.example.ontidz.jigsaw.common.Info;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ontidz on 2017/4/24.
 */

public class Grade{
    public static Grade grade;
    private static int result;
    public static int setGrade(String userName, final String[] data, final AppCompatActivity activity, final boolean ifRefresh){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(Info.serverAddress + "/" + userName)
                .build();
        Call call = client.newCall(request);

        result = 0;
        //异步调用,并设置回调函数
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                result = -1;
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();
                Gson gson = new Gson();
                Grade.grade = gson.fromJson(res, Grade.class);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(ifRefresh){
                            for (int i = 0; i < 6; i++){
                                data[i] = "Level"+(i+1) + " 总排名 " + Grade.grade.getLevel(i+1).rank;
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, data);
                            ListView singleRanks = (ListView) activity.findViewById(R.id.single_ranks);
                            singleRanks.setAdapter(adapter);
                        }else{
                            GameActivity.startActivity(activity,1);
                        }
                    }
                });
            }
        });
        return result;
    }

    public static void modifyGrade(String name, int level, final String grade, final AppCompatActivity activity){
        Grade.grade.getLevel(level).grade = grade;
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("grade", grade)
                .add("level", String.valueOf(level))
                .build();
        final Request request = new Request.Builder()
                .url(Info.serverAddress + "/" + name)
                .put(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }

    public GradeDetail level1;
    public GradeDetail level2;
    public GradeDetail level3;
    public GradeDetail level4;
    public GradeDetail level5;
    public GradeDetail level6;

    public GradeDetail getLevel(int i){
        switch (i){
            case 1:
                return level1;
            case 2:
                return level2;
            case 3:
                return level3;
            case 4:
                return level4;
            case 5:
                return level5;
            case 6:
                return level6;
            default:
                return new GradeDetail();
        }
    }
    public class GradeDetail{
        public String grade;
        public String rank;
    }
}