package com.ghl.wuhan.secondhand.me_activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ghl.wuhan.secondhand.R;
import com.google.gson.Gson;

import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.model.TResult;

import java.io.ByteArrayOutputStream;
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

public class user_register extends TakePhotoActivity {
    //private ImageView iv_back;
    private String TAG = "TAG";
    private ImageView iv_tou;
    private String host;
    private boolean ChooseImage = false;
    private CustomHelper customHelper;
    //向后台提交用户名和密码
    private TextView responseText;
    private EditText et_uname, et_password, et_qr;//用户名,密码和密码的确认
    private Button btn_register;//提交用户名和密码使用
    private final int opType=90001;//操作类型
    private String uphone;
    private int sex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "************onCreate begin********");
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_user_register, null);
        //这两句必须放在super.onCreate(savedInstanceState);之后， setContentView(contentView);之前
        setContentView(contentView);

        /*************************************************************************************/
        /*以下代码使用okhttp和异步线程实现提交用户名和密码，并显示回来*/
        responseText = (TextView) findViewById(R.id.responseText);
        et_uname = (EditText) findViewById(R.id.et_uname);
        et_password = (EditText) findViewById(R.id.et_password);
//        et_qr = (EditText) findViewById(R.id.et_qr);
        btn_register = (Button) findViewById(R.id.btn_register);
        Log.i(TAG, "************onCreate init********");
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "************click btn success********");
                //获取用户名和密码参数
                final String uname = et_uname.getText().toString().trim();//trim()的作用是去掉字符串左右的空格
                final String upassword = et_password.getText().toString().trim();
//                String uqr = et_qr.getText().toString().trim();
                Log.i(TAG, "uname is :" + uname);
                Log.i(TAG, "upassword is :" + upassword);
//                Log.i(TAG, "et_qr is :" + uqr);
                //使用post方法发送一个http请求
                //将这些功能封装在sendPostHttpRequest()
//                sendPostHttpRequest();

                //从drawable中取一个图片（以后大家需要从相册中取，或者相机中取）。
                Resources res = getResources();
                Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.bt);
               final byte[] uimages = Bitmap2Bytes(bmp);
            Log.i(TAG,"获取图片成功.......");



                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        register(opType,uname, upassword, uimages,uphone,sex);
                    }
                }).start();

            Log.i(TAG,"register()函数调用成功");
            }
        });
        /*************************************************************************************/

        /*以下代码为点击头像，调用相机实现拍照的功能，或者从相册中获取照片，获取自己想要的头像*/
        host = getResources().getString(R.string.host);
        customHelper = CustomHelper.of(contentView);
        iv_tou = findViewById(R.id.iv_tou);
        iv_tou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Dialogchoosephoto(user_register.this) {
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
    }

    //    /***************************************************************************/
        private void register(int opType,String uname,String upassword, byte[]uimages,String uphone,int sex) {

            UserBO userBO = new UserBO();
            String uuid = UUID.randomUUID().toString();
            userBO.setUid(uuid);
            userBO.setUname(uname);
            userBO.setUpassword(upassword);
//           userBO.setUimage(uimages);
            userBO.setOpType(opType);
            userBO.setSex(sex);
            userBO.setUphone(uphone);

            Gson gson = new Gson();
            String userJsonStr = gson.toJson(userBO, UserBO.class);
            Log.i(TAG, "jsonStr is :" + userJsonStr);


//            String url = "http://192.168.2.114:8081/Proj20/register";
            String url = "http://118.89.217.225:8080/Proj20/register";
            sendRequest(url, userJsonStr);

        }


        public void sendRequest(String url, String jsonStr) {

            OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
            Log.i(TAG,"成功创建OkHttpClient对象.......");
//            MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
            RequestBody requestBody = new FormBody.Builder()
                    .add("reqJson",jsonStr)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Log.i(TAG,"body对象成功传入.....");
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG,"获取数据失败了"+e.toString());

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {//回调的方法执行在子线程。
                        Log.d(TAG, "获取数据成功了");
                        Log.d(TAG, "response.code()==" + response.code());

                        final String s = response.body().string();
                        Log.d(TAG, "response.body().string()==" + s);

                        //将response.body().string()转换成对象
                        UserVO userVO = new UserVO();
                        Gson gson = new Gson();
                        userVO = gson.fromJson(s,UserVO.class);
                        int flag =userVO.getFlag();
                        if(flag == 200){
                            Intent intent = new Intent(user_register.this,user_login.class);
                            startActivity(intent);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(user_register.this, "您已注册成功！", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        if(flag == 10002)
//                            Toast.makeText(user_register.this,"该用户名已存在，注册失败！",Toast.LENGTH_SHORT).show();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(user_register.this,"该用户名已存在，注册失败！",Toast.LENGTH_SHORT).show();
//                                Toast.makeText(user_register.this, s, Toast.LENGTH_SHORT).show();
                            }
                        });






//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(user_register.this, s, Toast.LENGTH_SHORT).show();
//                            }
//                        });


                    }
                }
            });//此处省略回调方法

        }

        // bitmp转bytes
        public byte[] Bitmap2Bytes(Bitmap bm) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        }

//    private void sendPostHttpRequest() {
//        //子线程
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //post
//                //发送请求
//                try {
//                    OkHttpClient client = new OkHttpClient();
//                    //创建请求
//                    RequestBody requestBody = new FormBody.Builder()
//                            .add("username", et_uname.getText().toString())//et_username.getText().toString()是获取et的内容
//                            .add("password", et_password.getText().toString())
//                            .build();
//                    Request request = new Request.Builder()
//                            .url("http://118.89.217.225:8080/Proj20/register")
//                            .post(requestBody)
//                            .build();
//                    Response response = client.newCall(request).execute();//execute()同步请求，需要try和catch
//                    //enqueue方法是异步请求
//                    final String responseData = response.body().string();
//                    //主线程
//                    //用runOnUiThread(new Runnable() 切回主线程去（一般看的见的界面都是主线程）
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(user_register.this, responseData, Toast.LENGTH_SHORT).show();
//
//                            responseText.setText(responseData);
//
//                        }
//                    });
//
//
//                    //showResponse(responseData);
//
//                } catch (Exception e) {
//                    //错误信息在这显示
//                    e.printStackTrace();
//                }
//
//            }
//        }).start();
//    }

    /***************************************************************************/
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
            Glide.with(this).load(new File(result.getImages().get(i).getCompressPath())).into(iv_tou);
            Glide.with(this).load(new File(result.getImages().get(i + 1).getCompressPath())).into(iv_tou);
        }
        if (result.getImages().size() % 2 == 1) {
            Glide.with(this).load(new File(result.getImages().get(result.getImages().size() - 1).getCompressPath())).into(iv_tou);
        }


    }
}