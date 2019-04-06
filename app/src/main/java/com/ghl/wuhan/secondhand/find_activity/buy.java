package com.ghl.wuhan.secondhand.find_activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.ghl.wuhan.secondhand.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class buy extends AppCompatActivity {
   //属性定义
    private String TAG = "TAG";
    private ImageView iv_back;
    private Button btn_submit;
    private String token;
    private int opType = 90004;

    //查询列表中的属性
    ListView lv_showGoods;
    List<Goods> resultGoodsList = new ArrayList<Goods>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        //初始化部分
        lv_showGoods = (ListView) findViewById(R.id.lv_showGoods);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        //取消求购
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //点击查询
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
                        String token = preferences.getString("token", "");
                        Log.i(TAG, "从sp获取到的token" + token);
                        purchase(token, opType);
                    }
                }).start();
            }
        });

    }

    //将对象转换成json串
    private void purchase(String token, int opType) {
        Goods goods = new Goods();
        goods.setToken(token);
        goods.setOpType(opType);

        //将获取的对象转换成Json串
        Gson gson = new Gson();
        String buyJsonStr = gson.toJson(goods, Goods.class);
        Log.i(TAG, "查询商品中buyJsonStr is :" + buyJsonStr);
        String url = "http://118.89.217.225:8080/Proj20/buy";
        sendRequest(url, buyJsonStr);


    }

    //发送http请求
    private void sendRequest(String url, String JsonStr) {

        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        RequestBody requestBody = new FormBody.Builder()
                .add("reqJson", JsonStr)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "获取数据失败了" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d(TAG, "获取数据成功了");
                    Log.d(TAG, "response.code()==" + response.code());

                    final String jsonData = response.body().string();
                    Log.d(TAG, "查询商品中的response.body().string()==" + jsonData);

                    Gson gson = new Gson();
                    Log.i(TAG, "开始解析jsonData");
                    ResponseBuy responseBuy = gson.fromJson(jsonData, ResponseBuy.class);
                    Log.i(TAG, "结束解析jsonData");
                    Log.i(TAG, "结束解析responseBuy:" + responseBuy);
                    //Log.i(TAG,"查询商品的列表："+ responseBuy.getGoodList().get(0));
                    final int flag = responseBuy.getFlag();
                    Log.i(TAG, "flag==" + flag);
                    resultGoodsList = responseBuy.getGoodsList();
                    Log.i(TAG, "resultGoodsList==" + resultGoodsList);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (flag == 200) {
                                ArrayAdapter<Goods> adapter = new GoodsAdapter(buy.this, R.layout.goods_item, resultGoodsList);
                                lv_showGoods.setAdapter(adapter);
                            }
                        }
                    });
                }
            }
        });//此处省略回调方法

    }
}
