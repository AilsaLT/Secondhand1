package com.ghl.wuhan.secondhand.me_activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ghl.wuhan.secondhand.DialogUIUtils;
import com.ghl.wuhan.secondhand.HttpUtil;
import com.ghl.wuhan.secondhand.R;
import com.google.gson.Gson;

import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.model.TResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;

import static com.ghl.wuhan.secondhand.DialogUIUtils.dismiss;

public class user_register extends TakePhotoActivity {
    //属性定义
    private RelativeLayout rl_back;
    private String TAG = "TAG";
    //    private ImageView iv_tou;
    private String host;
    private boolean ChooseImage = false;
    private CustomHelper customHelper;
    private CircleImageView icon_image;

    //向后台提交用户名和密码
    //    private TextView responseText;

    private EditText et_uname, et_password, et_qr;//用户名,密码和密码的确认
    private Button btn_register;//提交用户名和密码使用
    private final int opType = 90001;//操作类型

    private String uphone;
    private int sex;
    private Dialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "************onCreate begin********");
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_user_register, null);
        //这两句必须放在super.onCreate(savedInstanceState);之后， setContentView(contentView);之前
        setContentView(contentView);

        //初始化
        //        responseText = (TextView) findViewById(R.id.responseText);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        et_uname = (EditText) findViewById(R.id.et_uname);
        et_password = (EditText) findViewById(R.id.et_password);
        et_qr = (EditText) findViewById(R.id.et_qr);
        btn_register = (Button) findViewById(R.id.btn_register);
        //        iv_tou = (ImageView) findViewById(R.id.iv_tou);
        icon_image = (CircleImageView) findViewById(R.id.icon_image);

        Log.i(TAG, "************onCreate init********");

        //取消注册
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //点击注册
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "************click btn success********");
                //获取用户名和密码参数
                final String uname = et_uname.getText().toString().trim();//trim()的作用是去掉字符串左右的空格
                final String upassword = et_password.getText().toString().trim();
                final String uqr = et_qr.getText().toString().trim();
                Log.i(TAG, "uname is :" + uname);
                Log.i(TAG, "upassword is :" + upassword);
                if(uname.isEmpty()&&upassword.isEmpty()&&uqr.isEmpty()){
                    Toast.makeText(user_register.this,"请输入注册信息",Toast.LENGTH_SHORT).show();
                }else if(uname.isEmpty()){
                    Toast.makeText(user_register.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }else if(upassword.isEmpty()){
                    Toast.makeText(user_register.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }else if(uqr.isEmpty()){
                    Toast.makeText(user_register.this,"请再次确认密码",Toast.LENGTH_SHORT).show();
                }else if(upassword.equals(uqr)==false){
                    Toast.makeText(user_register.this,"两次输入密码不一致",Toast.LENGTH_SHORT).show();
                }else {
                    //进度条的设置
                   progressDialog = DialogUIUtils.showLoadingDialog(user_register.this,"正在注册......");
                    progressDialog.show();
                    //bitmap转byte[]
                    Resources res = getResources();
                    Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.bt);
                    final byte[] uimages = Bitmap2Bytes(bmp);
                    Log.i(TAG, "获取图片成功.......");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            register(opType, uname, upassword, uimages, uphone, sex);
                        }
                    }).start();
                }
//                Log.i(TAG, "register()函数调用成功");
            }
        });


        /*以下代码为点击头像，调用相机实现拍照的功能，或者从相册中获取照片，获取自己想要的头像*/
        host = getResources().getString(R.string.host);
        customHelper = CustomHelper.of(contentView);

        icon_image.setOnClickListener(new View.OnClickListener() {
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
            Glide.with(this).load(new File(result.getImages().get(i).getCompressPath())).into(icon_image);
            Glide.with(this).load(new File(result.getImages().get(i + 1).getCompressPath())).into(icon_image);
        }
        if (result.getImages().size() % 2 == 1) {
            Glide.with(this).load(new File(result.getImages().get(result.getImages().size() - 1).getCompressPath())).into(icon_image);
        }

        //获取照片的路径
        File path = new File(getExternalCacheDir(),"picture.jpg");
        Log.i(TAG,"照片的路径为："+path);
        //将路经转换成Uri
        Uri imageUri = Uri.fromFile(path);
        Log.i(TAG,"照片的Uri为："+imageUri);
        //打印的结果：照片的路径为：
        // /storage/emulated/0/Android/data/com.ghl.wuhan.secondhand/cache/picture.jpg
        //照片的Uri为：
        // file:///storage/emulated/0/Android/data/com.ghl.wuhan.secondhand/cache/picture.jpg


    }

    //将对象转换成json串
    private void register(int opType, String uname, String upassword, byte[] uimages, String uphone, int sex) {

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
        Log.i(TAG, "userJsonStr :" + userJsonStr);


        String url = "http://118.89.217.225:8080/Proj20/register";
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

                    //将response.body().string()转换成对象
                    UserVO userVO = new UserVO();
                    Gson gson = new Gson();
                    userVO = gson.fromJson(s, UserVO.class);
                    int flag = userVO.getFlag();

                    if (flag == 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(user_register.this, "您已注册成功！", Toast.LENGTH_SHORT).show();
                                //使进度条不可见
                                dismiss(progressDialog);
                            }
                        });

                        Intent intent = new Intent(user_register.this, user_login.class);
                        startActivity(intent);

                    }
                    if (flag == 10002)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(user_register.this, "该用户名已存在，注册失败！", Toast.LENGTH_SHORT).show();
                                //使进度条不可见
                                dismiss(progressDialog);
                            }
                        });
                    if (flag == 10003)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(user_register.this, "用户名为空，注册失败！", Toast.LENGTH_SHORT).show();
                                //使进度条不可见
                                dismiss(progressDialog);
                            }
                        });
                    if (flag == 10004)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(user_register.this, "密码为空，注册失败！", Toast.LENGTH_SHORT).show();
                                //使进度条不可见
                                dismiss(progressDialog);
                            }
                        });
                    if (flag == 88888)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(user_register.this, "json parase error,please check your josn str.", Toast.LENGTH_SHORT).show();
                                //使进度条不可见
                                dismiss(progressDialog);
                            }
                        });
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



}