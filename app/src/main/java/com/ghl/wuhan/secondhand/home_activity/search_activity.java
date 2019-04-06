package com.ghl.wuhan.secondhand.home_activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.ghl.wuhan.secondhand.R;

import java.util.ArrayList;
import java.util.List;

public class search_activity extends AppCompatActivity {

    //属性定义
    private String TAG = "TAG";
    private Button btn_back;
    private SearchView sv_search;
    private String token;
    private int opType = 90006;
    //查询列表中的属性
    ListView lv_showUser;
    List<User> resultUserList = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //初始化
        btn_back = (Button) findViewById(R.id.btn_back);
        //用户取消查询
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
