package com.ghl.wuhan.secondhand.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.me_activity.user_login;
import com.ghl.wuhan.secondhand.me_activity.user_set;

import static android.content.Context.MODE_PRIVATE;
import static android.text.TextUtils.isEmpty;


public class Me extends Fragment {
private ImageView iv_deng;
    private String TAG = "TAG";
    private SharedPreferences pref;
    private RelativeLayout rl_deng,rl_deng1;
    private TextView tv_deng1;
    private ImageView iv_set;

    private boolean login;//标志是否登录成功

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_me,container,false);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化
        iv_deng=(ImageView)getActivity().findViewById(R.id.iv_deng);
        tv_deng1=getActivity().findViewById(R.id.tv_deng1);
        rl_deng=getActivity().findViewById(R.id.rl_deng);
        rl_deng1=getActivity().findViewById(R.id.rl_deng1);
        iv_set=getActivity().findViewById(R.id.iv_set);
        rl_deng.setVisibility(View.GONE);
        rl_deng1.setVisibility(View.GONE);






        String extra = getArguments().getString("extra");
        Log.i(TAG,"extra的值为：");
        pref = getActivity().getSharedPreferences("data", MODE_PRIVATE);
        String uname = pref.getString("uname","");
        if(isEmpty(extra)){
            rl_deng.setVisibility(rl_deng.VISIBLE);
        }else{
            rl_deng1.setVisibility(rl_deng1.VISIBLE);
            tv_deng1.setText(uname);
        }

        login = pref.getBoolean("login",false);
        Log.i(TAG,"login 为："+login);
        if(login == true){
            //设置
            iv_set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),user_set.class);
                    startActivity(intent);
                }
            });
        }else{

            Toast.makeText(getActivity(),"",Toast.LENGTH_SHORT).show();
        }
             //设置
                    iv_set.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(),user_set.class);
                            startActivity(intent);
                        }
                    });

        //登录
        iv_deng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),user_login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }
}
