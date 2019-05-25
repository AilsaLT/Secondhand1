package com.ghl.wuhan.secondhand.find_activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.ghl.wuhan.secondhand.HttpUtil;
import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.dialog.DialogUIUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.ghl.wuhan.secondhand.dialog.DialogUIUtils.dismiss;

public class buy extends AppCompatActivity {
   //属性定义
    private String TAG = "TAG";
    private ImageView iv_back;
    private Button btn_submit;
    private String token;
    private int opType = 90004;

    private Dialog progressDialog;
    /*
    *向后台传页数，数据条数
    * @parameter
    * currentPages:页数
    * datums:数据条数
    * */
    private int currentPages,datums = 5;



//   private SwipeRefreshLayout mSwipeRefreshLayout;
//   private List<String> mDatas = new ArrayList<>();
//    private RefreshAdapter mRefreshAdapter;
//    private LinearLayoutManager mLinearLayoutManager;



    //查询列表中的属性
//    ListView lv_showGoods;
    RecyclerView recyclerView;
    List<Goods> resultGoodsList = new ArrayList<Goods>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_buy);

        //初始化部分
//        lv_showGoods = (ListView) findViewById(R.id.lv_showGoods);
            recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        //下拉刷新
//        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        //刷新
//        ButterKnife.bind(this);
//        initView();
//        initData();
//        initListener();

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
                //设置进度条
                progressDialog = DialogUIUtils.showLoadingDialog(buy.this,"正在查询中，请耐心等待......");
                progressDialog.show();
                //点击物理返回键是否可取消dialog
                progressDialog.setCancelable(true);
                //点击dialog之外 是否可取消
                progressDialog.setCanceledOnTouchOutside(false);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
                        String token = preferences.getString("token", "");
                        Log.i(TAG, "从sp获取到的token==" + token);
                        purchase(token, opType,currentPages,datums);
                    }
                }).start();
            }
        });




    }

    //将对象转换成json串
    private void purchase(String token, int opType,int currentPages,int datums) {
        Goods goods = new Goods();
        goods.setToken(token);
        goods.setOpType(opType);
        goods.setCurrentPages(currentPages);
        goods.setDatums(datums);

        //将获取的对象转换成Json串
        Gson gson = new Gson();
        String buyJsonStr = gson.toJson(goods, Goods.class);
        Log.i(TAG, "查询商品中buyJsonStr is :" + buyJsonStr);
        String url = "http://47.105.183.54:8080/Proj20/buy";
        HttpUtil.sendOkHttpRequest(url,buyJsonStr, new okhttp3.Callback() {
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

                                //                                ArrayAdapter<Goods> adapter = new GoodsAdapter(buy.this, R.layout.goods_item, resultGoodsList);
                                //                                lv_showGoods.setAdapter(adapter);


//                                ArrayAdapter<Goods> adapter = new GoodsAdapter(buy.this, R.layout.goods_item, resultGoodsList);
//                                lv_showGoods.setAdapter(adapter);

                                LinearLayoutManager layoutManager = new LinearLayoutManager(buy.this);
                                recyclerView.setLayoutManager(layoutManager);
                                Goods_Adapter adapter = new Goods_Adapter(resultGoodsList);
                                recyclerView.setAdapter(adapter);

                                dismiss(progressDialog);


                            }
                        }
                    });
                }
            }
        });


    }
//    //刷新
//    private void initView() {
//
//        mSwipeRefreshLayout.setColorSchemeColors(Color.RED,Color.BLUE,Color.GREEN);
//    }
//    private void initData() {
//
//        for (int i = 0; i < 10; i++) {
//
//            mDatas.add(" Item "+i);
//        }
//
//        initRecylerView();
//    }
//    private void initRecylerView() {
//
//        mRefreshAdapter = new RefreshAdapter(this,mDatas);
//        mLinearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
//
//
//
//        //添加动画
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//
//        //添加分割线
//        mRecyclerView.addItemDecoration(new RefreshItemDecoration(this,RefreshItemDecoration.VERTICAL_LIST));
//
//        mRecyclerView.setLayoutManager(mLinearLayoutManager);
//        mRecyclerView.setAdapter(mRefreshAdapter);
//    }
//    private void initListener() {
//
//        initPullRefresh();
//
//        initLoadMoreListener();
//
//    }


}
