package com.ghl.wuhan.secondhand.find_activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.me_activity.CustomHelper;
import com.ghl.wuhan.secondhand.me_activity.Dialogchoosephoto;
import com.ghl.wuhan.secondhand.me_activity.UserVO;
import com.ghl.wuhan.secondhand.me_activity.user_login;
import com.google.gson.Gson;

import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.model.TResult;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class sale extends TakePhotoActivity {

    //属性定义部分
    private ImageView iv_back;
    //打印日志
    private String TAG = "TAG";
    //拍照
    private ImageView image_touxiang;
    private String host;
    private boolean ChooseImage = false;
    private CustomHelper customHelper;
    //销售商品
    private int opType = 90003;
    private String goodsID;//ID
    private String goodsType;//商品所属类
    private String goodsName;//商品名
    private float price = 0.1f;// 价格
    private String unit = "台"; //单位
    private float quality = 1.0f;//数量
    private String userid;//发布人ID
    private byte[] goodsImg;//商品图片
    private String uname = "lt";
    private String uphone = "15827630494";
    private int sex = 0;
    private String qq = "2926509946";
    private String weixin = "000";
    private String token;
    private Button btn_submit;
    private EditText et_goodsName, et_price;

    //获取商品类型
    private Spinner getGoodsType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //这两句必须放在super.onCreate(savedInstanceState);之后， setContentView(contentView);之前
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_user_register, null);

        setContentView(R.layout.activity_sale);





        //初始化
        iv_back = (ImageView) findViewById(R.id.iv_back);
        image_touxiang = (ImageView) findViewById(R.id.image_touxiang);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        et_goodsName = (EditText) findViewById(R.id.et_goodsName);
        et_price = (EditText) findViewById(R.id.et_price);
        getGoodsType = (Spinner) findViewById(R.id.goods_Type);

        host = getResources().getString(R.string.host);
        customHelper = CustomHelper.of(contentView);

        //spanner
        getGoodsType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                goodsType = (String) getGoodsType.getSelectedItem();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //取消发布商品
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //点击拍照或选择照片
        image_touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Dialogchoosephoto(sale.this) {
                    @Override
                    public void btnPickByTake() {
                        ChooseImage = true;
                        //拍照
                        customHelper.onClick("takephoto", getTakePhoto());
                    }

                    @Override
                    public void btnPickBySelect() {
                        ChooseImage = true;
                        //相册
                        customHelper.onClick("selectphoto", getTakePhoto());
                    }
                }.show();
            }
        });

        //点击发布商品
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //获取用户名和密码参数
                        String goodsName = et_goodsName.getText().toString().trim();//trim()的作用是去掉字符串左右的空格
                        float price = Float.parseFloat(et_price.getText().toString());
                        Log.i(TAG, "成功获取goodsName==" + goodsName);
                        Log.i(TAG, "成功获取price==" + price);
                        //将存储在sp中的token拿到
                        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                        String token = pref.getString("token", "");
                        String uname = pref.getString("uname", "");
                        userid = uname;
                        Log.i(TAG, "成功获取token==" + token);
                        Log.i(TAG, "成功获取uname==" + uname);

                        sale(opType, goodsID, goodsType, goodsName, price, unit, quality, userid, goodsImg, uname, uphone, sex, qq, weixin, token);
                    }
                }).start();
            }
        });
    }

    //将传入的参数转换成Json串使用
    private void sale(int opType, String goodsID, String goodsType, String goodsName, float price,
                          String unit, float quality, String userid,
                          byte[] goodImg, String uname, String uphone,
                          int sex, String qq, String weixin, String token) {

        SaleBO userBO = new SaleBO();
        String uuid = UUID.randomUUID().toString();
        userBO.setUid(uuid);
        userBO.setOpType(opType);
        userBO.setGoodsID(goodsID);
        userBO.setGoodsType(goodsType);
        userBO.setGoodsName(goodsName);
        userBO.setPrice(price);
        userBO.setUnit(unit);
        userBO.setQuality(quality);
        userBO.setUserid(userid);
        userBO.setGoodImg(goodImg);
        userBO.setUname(uname);
        userBO.setUphone(uphone);
        userBO.setSex(sex);
        userBO.setQq(qq);
        userBO.setToken(token);


        Gson gson = new Gson();
        String userJsonStr = gson.toJson(userBO, SaleBO.class);
        Log.i(TAG, "jsonStr is :" + userJsonStr);
        String url = "http://118.89.217.225:8080/Proj20/sale";
        sendRequest(url, userJsonStr);

    }

    //发送http请求
    public void sendRequest(String url, String jsonStr) {

        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        Log.i(TAG, "成功创建OkHttpClient对象.......");
        RequestBody requestBody = new FormBody.Builder()
                .add("reqJson", jsonStr)
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

                    final String s = response.body().string();
                    Log.d(TAG, "response.body().string()==" + s);

                    //将response.code()转换成对象
                    UserVO userVO = new UserVO();
                    Gson gson = new Gson();
                    userVO = gson.fromJson(s, UserVO.class);
                    int flag = userVO.getFlag();

                    if (flag == 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(sale.this, "商品发布成功！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent intent = new Intent(sale.this, user_login.class);
                        startActivity(intent);
                    }
                    if (flag == 40001) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(sale.this, "照片过大，商品发布失败！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if (flag == 40003) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(sale.this, "SALE_GOODS_FAILED_PARAMS", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }



                }
            }
        });//此处省略回调方法

    }

    @Override
    public void takeCancel() {
        super.takeCancel();
        Log.i(TAG, "RegisterActivity : takeCancel");
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        Log.i(TAG, "RegisterActivity : takeFail");
    }

    @Override
    public void takeSuccess(TResult result) {
        Log.i(TAG, "RegisterActivity : takeSuccess");
        super.takeSuccess(result);
        for (int i = 0, j = result.getImages().size(); i < j - 1; i += 2) {
            Glide.with(this).load(new File(result.getImages().get(i).getCompressPath())).into(image_touxiang);
            Glide.with(this).load(new File(result.getImages().get(i + 1).getCompressPath())).into(image_touxiang);
        }
        if (result.getImages().size() % 2 == 1) {
            Glide.with(this).load(new File(result.getImages().get(result.getImages().size() - 1).getCompressPath())).into(image_touxiang);
        }


    }

}