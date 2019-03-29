package com.ghl.wuhan.secondhand.me_activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ghl.wuhan.secondhand.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class user_login extends AppCompatActivity {
    private String TAG = "TAG";
    private final int opType = 90002;

//    private TextView response_Text;
    private EditText et_uname, et_password;//用户名,密码
    private TextView tv_zhu;
    private TextView tv_reset;
    private ImageView iv_back;
    private TextView tv_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

       tv_zhu= (TextView) findViewById(R.id.tv_zhu);
        tv_reset=(TextView)findViewById(R.id.tv_reset);

        tv_login = (TextView)findViewById(R.id.tv_login);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        et_uname=(EditText)findViewById(R.id.et_uname);
        et_password=(EditText)findViewById(R.id.et_password);
//        response_Text=(TextView)findViewById(R.id.response_Text);

        //注册
        tv_zhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(user_login.this,user_register.class);
                startActivity(intent);
            }
        });

        //重设密码
        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(user_login.this,reset_password_activity.class);
                startActivity(intent);
            }
        });
        //登录
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"运行到tv_logon的点击事件中********");
//                sendPostHttpRequest();
                final String uname = et_uname.getText().toString().trim();//trim()的作用是去掉字符串左右的空格
                String upassword = et_password.getText().toString().trim();

                Log.i(TAG,"uname is :"+uname);
                Log.i(TAG,"upassword is :"+upassword);

//                Resources res = getResources();
//                Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.p1);//从drawable中取一个图片（以后大家需要从相册中取，或者相机中取）。
//                byte[] uimages = Bitmap2Bytes(bmp);

                register(opType, uname ,upassword.toString() );


            }
        });

       //返回
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }





    private void register(int opType,String uname ,String upassword ) {

        UserBO userBO = new UserBO();
        String uuid = UUID.randomUUID().toString();
        userBO.setUid(uuid);
        userBO.setUname(uname);
        userBO.setUpassword(upassword);
        userBO.setOpType(opType);
        //userBO.setUimage(uimages);

        Gson gson = new Gson();
        String userJsonStr = gson.toJson(userBO,UserBO.class);
        Log.i(TAG,"jsonStr is :"+userJsonStr);

        //String url = "http://118.89.217.225/share/29";
        // String url = "http://192.168.7.225:8080/TenProj/login";
//        String url = "http://192.168.2.114:8081/Proj20/login";
        String url = "http://118.89.217.225:8080/Proj20/login";
        sendRequest(url, userJsonStr);

    }

    public void sendRequest(String url,String jsonStr){

        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
//        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        // String jsonStr = "{\"username\":\"lisi\",\"nickname\":\"李四\"}";//json数据.
        RequestBody requestBody = new FormBody.Builder()
                .add("reqJson",jsonStr)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG,"获取数据失败了"+e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){//回调的方法执行在子线程。
                    Log.d(TAG,"获取数据成功了");
                    Log.d(TAG,"response.code()=="+response.code());

                    final String s = response.body().string();
                    Log.d(TAG,"response.body().string()=="+s);
                    //将获取的token解析成对象
                    UserBO userBO = new UserBO();
                    Gson gson =new Gson();
                    userBO = gson.fromJson(s,UserBO.class);
                    String token = userBO.getToken();
//                    String name = userBO.getUname();
                    Log.i(TAG,"成功获取token=="+token);
//                    Log.i(TAG,"成功获取name=="+name);

                    //解析s获取flag
                    UserVO userVO = new UserVO();
                    Gson gson1 = new Gson();
                    userVO = gson1.fromJson(s,UserVO.class);
                    int flag = userVO.getFlag();
                    Log.i(TAG,"成功获取flag=="+flag);
                    //flag=200登录成功，将token进行存储
                    String uname = et_uname.getText().toString();
                    if(flag == 200){
                        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                        editor.putString("token",token);
                        editor.putString("uname",uname);
                        editor.commit();
                        Log.i(TAG,"成功存储token=="+token);
                        Log.i(TAG,"成功存储uname=="+uname);

                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(user_login.this,s,Toast.LENGTH_SHORT).show();
                        }
                    });



                }
            }
        });//此处省略回调方法

    }



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
