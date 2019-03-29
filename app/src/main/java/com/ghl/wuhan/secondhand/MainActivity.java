package com.ghl.wuhan.secondhand;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghl.wuhan.secondhand.Fragment.Fragment1;
import com.ghl.wuhan.secondhand.Fragment.Fragment2;
import com.ghl.wuhan.secondhand.Fragment.Fragment3;
import com.ghl.wuhan.secondhand.Fragment.Fragment4;


public class MainActivity extends AppCompatActivity {
//    private String TAG = "TAG";
   
    //滑动页面的数量
//    private List<View> tabViews;

    private TextView mTextMessage;
    private BottomNavigationView bottomNavigationView;
    private Fragment1 fragment1;
    private Fragment2 fragment2;
    private Fragment3 fragment3;
    private Fragment4 fragment4;
    private Fragment[] fragments;
    private int lastfragment;//用于记录上个选择的Fragment

    //引导页部分属性
    private Handler myhandler = new Handler();
    private ImageView iv_logo;

    //初始化fragment和fragment数组
    private void initFragment() {

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();
        fragments = new Fragment[]{fragment1, fragment2, fragment3, fragment4};
        lastfragment = 0;

        getSupportFragmentManager().beginTransaction().replace(R.id.mainview, fragment1).show(fragment1).commit();
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
    }

    //判断选择的菜单
    private BottomNavigationView.OnNavigationItemSelectedListener changeFragment = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Log.i("test", "item.getItemId() is :" + item.getItemId());

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


    //    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
    //            = new BottomNavigationView.OnNavigationItemSelectedListener() {
    //
    //        @Override
    //        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    //            switch (item.getItemId()) {
    //                case R.id.navigation_home:
    //                    mTextMessage.setText(R.string.title_home);
    //                    return true;
    //                case R.id.navigation_dashboard:
    //                    mTextMessage.setText(R.string.title_dashboard);
    //                    return true;
    //                case R.id.navigation_notifications:
    //                    mTextMessage.setText(R.string.title_notifications);
    //                    return true;
    //            }
    //            return false;
    //        }
    //
    //    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化用这个方法
        initFragment();
/**************************************引导页部分***********************************************************/
//        tabViews = new ArrayList<>();
//
//        //初始化数据(引导页部分)
//
//        initData();
//        view_page = (ViewPager) findViewById(R.id.view_page);
//        //设置适配器
//        view_page.setAdapter(new PagerAdapter() {
//            @Override
//            public int getCount() {
//                return tabViews.size();//
//            }
//
//            @Override
//            public boolean isViewFromObject(View view, Object object) {
//                return view == object;
//            }
//
//            @Override
//            public Object instantiateItem(ViewGroup container, int position) {
//                container.addView(tabViews.get(position));
//                //滑到最后一个页面，显示button
//                if (position == tabViews.size() - 1) {
//                    Button button = (Button) tabViews.get(position).findViewById(R.id.button);
//                    button.setVisibility(View.VISIBLE);
//                    button.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Toast.makeText(MainActivity.this, "你要的操作", Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//                return tabViews.get(position);
//            }
//
//            @Override
//            public void destroyItem(ViewGroup container, int position, Object object) {
//                container.removeView(tabViews.get(position));
//            }
//        });
//    }
//
//    private void initData() {
//        //引入一个布局，三个共用，设置不同的背景图片
//        LayoutInflater tabs = LayoutInflater.from(MainActivity.this);
//        View tab1 = tabs.inflate(R.layout.tab_01, null);
//        tab1.setBackgroundResource(R.drawable.yindao2);
//        View tab2 = tabs.inflate(R.layout.tab_01, null);
//        tab2.setBackgroundResource(R.drawable.yindao3);
//        View tab3 = tabs.inflate(R.layout.tab_01, null);
//        tab3.setBackgroundResource(R.drawable.yindao4);
//        // 添加到列表里
//        tabViews.add(tab1);
//        tabViews.add(tab2);
//        tabViews.add(tab3);
    }
//    /*****************************************************************************************************/

}
