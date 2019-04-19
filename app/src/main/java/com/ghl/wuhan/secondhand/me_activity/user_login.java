package com.ghl.wuhan.secondhand.me_activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ghl.wuhan.secondhand.DialogUIUtils;
import com.ghl.wuhan.secondhand.HttpUtil;
import com.ghl.wuhan.secondhand.MainActivity;
import com.ghl.wuhan.secondhand.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Response;

import static com.ghl.wuhan.secondhand.DialogUIUtils.dismiss;

public class user_login extends AppCompatActivity {
    private String TAG = "TAG";
    private final int opType = 90002;

    //    private TextView response_Text;
    private EditText et_uname, et_password;//用户名,密码
    private TextView tv_register;
    private TextView tv_reset;
    private ImageView iv_back;
    private TextView tv_login;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPass;
    private Dialog progressDialog;//进度条


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);

        //初始化
        tv_register = (TextView) findViewById(R.id.tv_register);
        tv_reset = (TextView) findViewById(R.id.tv_reset);

        tv_login = (TextView) findViewById(R.id.tv_login);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        et_uname = (EditText) findViewById(R.id.et_uname);
        et_password = (EditText) findViewById(R.id.et_password);
<<<<<<< HEAD


=======
//
//        //记住密码
//        boolean isRemember = pref.getBoolean("remember_password",false);
//        if(isRemember) {
//            //将账号和密码都设置到文本框中
//            String uname = pref.getString("uname","");
//            String upassword = pref.getString("upassword","");
//            et_uname.setText(uname);
//            et_password.setText(upassword);
////            rememberPass.setChecked(true);
//        }
>>>>>>> 3cac62ea6001e3fbc90cc548242e9230b7a32e0a
        //注册
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(user_login.this, user_register.class);
                startActivity(intent);
            }
        });

        //重设密码
        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(user_login.this, reset_password_activity.class);
                startActivity(intent);
            }
        });

        //登录
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "运行到tv_logon的点击事件中********");
                final String uname = et_uname.getText().toString().trim();//trim()的作用是去掉字符串左右的空格
                String upassword = et_password.getText().toString().trim();

                Log.i(TAG, "uname is :" + uname);
                Log.i(TAG, "upassword is :" + upassword);

                if(uname.isEmpty()&&upassword.isEmpty()){
                    Toast.makeText(user_login.this,"请输入登录信息",Toast.LENGTH_SHORT).show();
                }else if(uname.isEmpty()){
                    Toast.makeText(user_login.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }else if(upassword.isEmpty()){
                    Toast.makeText(user_login.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    //设置进度条
                    progressDialog = DialogUIUtils.showLoadingDialog(user_login.this,"正在登录......");
                    progressDialog.show();
                    login(opType, uname, upassword.toString());
                }




            }
        });

        //取消登录
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //将对象转换成json串
    private void login(int opType, String uname, String upassword) {

        UserBO userBO = new UserBO();
        String uuid = UUID.randomUUID().toString();
        userBO.setUid(uuid);
        userBO.setUname(uname);
        userBO.setUpassword(upassword);
        userBO.setOpType(opType);
        //userBO.setUimage(uimages);

        Gson gson = new Gson();
        String userJsonStr = gson.toJson(userBO, UserBO.class);
        Log.i(TAG, "登录中loginJsonStr is :" + userJsonStr);

        String url = "http://118.89.217.225:8080/Proj20/login";
        //        sendRequest(url, userJsonStr);
        HttpUtil.sendOkHttpRequest(url, userJsonStr, new okhttp3.Callback() {
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

                    //将获取的token解析成对象
                    UserBO userBO = new UserBO();
                    Gson gson = new Gson();
                    userBO = gson.fromJson(s, UserBO.class);
                    String token = userBO.getToken();
                    Log.i(TAG, "登陆中成功获取token==" + token);


                    //解析s获取flag
                    UserVO userVO = new UserVO();
                    Gson gson1 = new Gson();
                    userVO = gson1.fromJson(s, UserVO.class);
                    int flag = userVO.getFlag();
                    Log.i(TAG, "登录中成功获取flag==" + flag);
                    //flag=200登录成功，将token进行存储
                    String uname = et_uname.getText().toString();
                    String upassword = et_password.getText().toString();
//
                    //如果选中了记住密码，则把用户名密码保存
                   editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    if (rememberPass.isChecked()) {
                        Log.i("TAG", "开始保存密码");
                        editor.putString("account", uname);
                        editor.putString("password", upassword);
                        editor.putBoolean("remember_password", true);
                    } else {
                        editor.clear();
                    }
                    editor.commit();

                    boolean isRemember = pref.getBoolean("remember_password",false);
                    if(isRemember) {
                        //将账号和密码都设置到文本框中
                        String account = pref.getString("account","");
                        String password = pref.getString("password","");
                        et_uname.setText(account);
                        et_password.setText(password);
                        rememberPass.setChecked(true);
                    }

                    if (flag == 200) {
                        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        editor.putString("token", token);
                        editor.commit();
                        Log.i(TAG, "登录中成功存储token==" + token);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(user_login.this, "登录成功！", Toast.LENGTH_SHORT).show();
                                dismiss(progressDialog);
                                Intent intent = new Intent(user_login.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    }

<<<<<<< HEAD
=======
//                    //记住密码
//                    if(rememberPass.isChecked()){//检查复选框是否被选中
//                        editor.putBoolean("remember_password",true);
//                        editor.putString("uname",uname);
//                        editor.putString("upassword",upassword);
//                    }else{
//                        editor.clear();
//                    }
//                    editor.commit();
                    
>>>>>>> 3cac62ea6001e3fbc90cc548242e9230b7a32e0a
                    if (flag == 20001) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(user_login.this, "用户名不能空，登录失败！", Toast.LENGTH_SHORT).show();
                                dismiss(progressDialog);
                            }
                        });

                    }
                    if (flag == 20002) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(user_login.this, "密码不能为空，登录失败！", Toast.LENGTH_SHORT).show();
                                dismiss(progressDialog);
                            }
                        });

                    }
                }
            }
        });

    }

    //发送http请求
    //    public void sendRequest(String url, String jsonStr) {
    //
    //        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
    //        RequestBody requestBody = new FormBody.Builder()
    //                .add("reqJson", jsonStr)
    //                .build();
    //        Request request = new Request.Builder()
    //                .url(url)
    //                .post(requestBody)
    //                .build();
    //        client.newCall(request).enqueue(new Callback() {
    //            @Override
    //            public void onFailure(Call call, IOException e) {
    //                Log.d(TAG, "获取数据失败了" + e.toString());
    //            }
    //
    //            @Override
    //            public void onResponse(Call call, Response response) throws IOException {
    //                if (response.isSuccessful()) {//回调的方法执行在子线程。
    //                    Log.d(TAG, "获取数据成功了");
    //                    Log.d(TAG, "response.code()==" + response.code());
    //
    //                    final String s = response.body().string();
    //                    Log.d(TAG, "response.body().string()==" + s);
    //
    //                    //将获取的token解析成对象
    //                    UserBO userBO = new UserBO();
    //                    Gson gson = new Gson();
    //                    userBO = gson.fromJson(s, UserBO.class);
    //                    String token = userBO.getToken();
    //                    Log.i(TAG, "登陆中成功获取token==" + token);
    //
    //
    //                    //解析s获取flag
    //                    UserVO userVO = new UserVO();
    //                    Gson gson1 = new Gson();
    //                    userVO = gson1.fromJson(s, UserVO.class);
    //                    int flag = userVO.getFlag();
    //                    Log.i(TAG, "登录中成功获取flag==" + flag);
    //                    //flag=200登录成功，将token进行存储
    //                    String uname = et_uname.getText().toString();
    //                    String upassword = et_password.getText().toString();
    //                    if (flag == 200) {
    //                        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
    //                        editor.putString("token", token);
    //                        editor.putString("uname", uname);
    //                        editor.commit();
    //                        Log.i(TAG, "登录中成功存储token==" + token);
    //                        Log.i(TAG, "登陆中成功存储uname==" + uname);
    //                        runOnUiThread(new Runnable() {
    //                            @Override
    //                            public void run() {
    //                                Toast.makeText(user_login.this, "登录成功！", Toast.LENGTH_SHORT).show();
    //                            }
    //                        });
    //
    //                    }
    //
    ////                    //记住密码
    ////                    if(rememberPass.isChecked()){//检查复选框是否被选中
    ////                        editor.putBoolean("remember_password",true);
    ////                        editor.putString("uname",uname);
    ////                        editor.putString("upassword",upassword);
    ////                    }else{
    ////                        editor.clear();
    ////                    }
    ////                    editor.commit();
    //
    //                    if (flag == 20001) {
    //                        runOnUiThread(new Runnable() {
    //                            @Override
    //                            public void run() {
    //                                Toast.makeText(user_login.this, "用户名不能空，登录失败！", Toast.LENGTH_SHORT).show();
    //                            }
    //                        });
    //
    //                    }
    //                    if (flag == 20002) {
    //                        runOnUiThread(new Runnable() {
    //                            @Override
    //                            public void run() {
    //                                Toast.makeText(user_login.this, "密码不能为空，登录失败！", Toast.LENGTH_SHORT).show();
    //                            }
    //                        });
    //
    //                    }
    //
    //
    //
    //
    //
    //
    //
    //                }
    //            }
    //        });//此处省略回调方法
    //
    //    }


    //    // bitmp转bytes
    //    public byte[] Bitmap2Bytes(Bitmap bm) {
    //        ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
    //        return baos.toByteArray();
    //    }


    //    // bytes转bitmap
    //    public Bitmap Bytes2Bimap(byte[] b) {
    //        if (b.length != 0) {
    //            return BitmapFactory.decodeByteArray(b, 0, b.length);
    //        } else {
    //            return null;
    //        }
    //    }

    //    // bitmap 缩放
    //    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
    //        int w = bitmap.getWidth();
    //        int h = bitmap.getHeight();
    //        Matrix matrix = new Matrix();
    //        float scaleWidth = ((float) width / w);
    //        float scaleHeight = ((float) height / h);
    //        matrix.postScale(scaleWidth, scaleHeight);
    //        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    //        return newbmp;
    //    }


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
    //                            .url("http://118.89.217.225:8080/Proj20/login")
    //                            .post(requestBody)
    //                            .build();
    //                    Response response = client.newCall(request).execute();
    //                    //execute()同步请求，需要try和catch
    //                    //enqueue方法是异步请求
    ////                    Gson gson = new Gson();
    ////                    String json = gson.fromJson(et_uname);
    //
    //                    final String responseData = response.body().string();
    //                    //主线程
    //                    //用runOnUiThread(new Runnable() 切回主线程去（一般看的见的界面都是主线程）
    //                    runOnUiThread(new Runnable() {
    //                        @Override
    //                        public void run() {
    ////                            Toast.makeText(user_login.this, responseData, Toast.LENGTH_SHORT).show();
    ////                            Huo huo = new Huo(et_uname.getText().toString(),et_password.getText().toString());
    ////                            Gson gson = new Gson();
    ////                            String jsonstr = gson.toJson(huo);
    ////                            Toast.makeText(user_login.this,jsonstr,Toast.LENGTH_SHORT).show();
    //                            response_Text.setText(responseData);
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

}
