package com.ghl.wuhan.secondhand.me_activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ghl.wuhan.secondhand.HttpUtil;
import com.ghl.wuhan.secondhand.R;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class user_query extends AppCompatActivity {
    //属性定义
    private Button bt_query;
    private String TAG = "TAG";
    //用户修改密码需要传如的参数
    private String token; // 查询或更新用户时，需要用到token
    private int opType = 90006 ;//操作类型
    private SharedPreferences pref;
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_query);

            //初始化
            bt_query = (Button) findViewById(R.id.bt_query);
            pref = getSharedPreferences("data",MODE_PRIVATE);

            //点击用户查询
            bt_query.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String token = pref.getString("token","");
                    resetPassword(token,opType);
                }
            });
    }
    //将对象转换成Json串
    private void resetPassword(String token,int opType){
        UserBO userBO = new UserBO();
        userBO.setToken(token);
        userBO.setOpType(opType);

        Gson gson = new Gson();
        String setJsonStr = gson.toJson(userBO,UserBO.class);
        Log.i(TAG,"重置密码中的resetJsonStr为："+setJsonStr);
        String url = "http://118.89.217.225:8080/Proj20/user";
        HttpUtil.sendOkHttpRequest(url,setJsonStr, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "获取数据失败了" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d(TAG, "获取数据成功了");
                    Log.d(TAG, "response.code()==" + response.code());

                    final String s = response.body().string();
                    Log.d(TAG, "response.body().string()==" + s);
                    //解析s获取flag
                    UserVO userVO = new UserVO();
                    Gson gson1 = new Gson();
                    userVO = gson1.fromJson(s, UserVO.class);
                    int flag = userVO.getFlag();
                    Log.i(TAG, "用户查询中成功获取flag==" + flag);
                    if(flag==200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(user_query.this,"查询成功",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            }
        });
    }
}
