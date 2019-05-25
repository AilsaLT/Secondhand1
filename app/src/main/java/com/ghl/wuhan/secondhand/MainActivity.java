package com.ghl.wuhan.secondhand;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ghl.wuhan.secondhand.Fragment.Find;
import com.ghl.wuhan.secondhand.Fragment.Home;
import com.ghl.wuhan.secondhand.Fragment.Me;
import com.ghl.wuhan.secondhand.Fragment.Sort;


public class MainActivity extends AppCompatActivity {
    private String TAG = "TAG";
    //滑动页面的数量
    //    private List<View> tabViews;

    private TextView mTextMessage;
    private BottomNavigationView bottomNavigationView;
    private Home home;
    private Sort sort;
    private Find find;
    private Me me;
    private Fragment[] fragments;
    private int lastfragment;//用于记录上个选择的Fragment

    //初始化fragment和fragment数组
    private void initFragment() {

        Intent intent = getIntent();
        String extra = intent.getStringExtra("extra");
        Bundle bundle = new Bundle();
        bundle.putString("extra",extra);

        home = new Home();
        sort = new Sort();
        find = new Find();
        me = new Me();
        fragments = new Fragment[]{home, sort, find, me};
        lastfragment = 0;

        getSupportFragmentManager().beginTransaction().replace(R.id.mainview, home).show(home).commit();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        // 通过反射机制实现超过3图标时，不能显示文字的问题
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        //获取整个的NavigationView
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        //这里就是获取所添加的每一个Tab(或者叫menu)，
        View tab = menuView.getChildAt(3);
        BottomNavigationItemView itemView = (BottomNavigationItemView) tab;
        //加载我们的角标View，新创建的一个布局
        View badge = LayoutInflater.from(this).inflate(R.layout.im_badge, menuView, false);
        //添加到Tab上
        itemView.addView(badge);
        TextView textView = badge.findViewById(R.id.texT);
        textView.setText(String.valueOf(9));
        //无消息时可以将它隐藏即可
        textView.setVisibility(View.VISIBLE);


        bottomNavigationView.setOnNavigationItemSelectedListener(changeFragment);

        me.setArguments(bundle);
    }

    //判断选择的菜单
    private BottomNavigationView.OnNavigationItemSelectedListener changeFragment = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Log.i(TAG, "item.getItemId() is :" + item.getItemId());

            switch (item.getItemId()) {
                case R.id.navigation_home: {
                    if (lastfragment != 0) {
                        switchFragment(lastfragment, 0);
                        lastfragment = 0;

                    }

                    return true;
                }
                case R.id.navigation_dashboard: {
                    if (lastfragment != 1) {
                        switchFragment(lastfragment, 1);
                        lastfragment = 1;

                    }

                    return true;
                }
                case R.id.navigation_notifications: {
                    if (lastfragment != 2) {
                        switchFragment(lastfragment, 2);
                        lastfragment = 2;

                    }

                    return true;
                }
                case R.id.navigation_me: {
                    if (lastfragment != 3) {
                        switchFragment(lastfragment, 3);
                        lastfragment = 3;

                    }

                    return true;
                }

            }


            return false;
        }

        private void switchFragment(int lastfragment, int index) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
            if (fragments[index].isAdded() == false) {
                transaction.add(R.id.mainview, fragments[index]);

            }
            transaction.show(fragments[index]).commitAllowingStateLoss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化用这个方法
        initFragment();

    }


    //退出登录
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String tag = intent.getStringExtra("EXIT_TAG");
        if (tag != null&& !TextUtils.isEmpty(tag)) {
            if ("SINGLETASK".equals(tag)) {//退出程序
                finish();
            }
        }

    }




}
