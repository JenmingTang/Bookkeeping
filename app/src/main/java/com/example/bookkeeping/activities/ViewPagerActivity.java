package com.example.bookkeeping.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.example.bookkeeping.R;
import com.example.bookkeeping.adapters.MyViewPagerAdapter;
import com.example.bookkeeping.fragments.FragmentItem;
import com.google.android.material.tabs.TabLayout;

public class ViewPagerActivity extends AppCompatActivity {
    androidx.fragment.app.Fragment[] fragments;
    //找到ViewPager
    ViewPager viewPager;
    //找到tabLayout绑定
    TabLayout tabLayout;
    //one,fragment
    int[] imgs_red = {
            R.mipmap.other_red,
            R.mipmap.shoping_car_red,
            R.mipmap.bus_red,
            R.mipmap.food_red,
            R.mipmap.game_red,
            R.mipmap.house_red,
            R.mipmap.red_packet_red,
    };
    //two,fragment
    int[] imgs_blue = {
            R.mipmap.other_blue,
            R.mipmap.shoping_car_blue,
            R.mipmap.bus_blue,
            R.mipmap.food_blue,
            R.mipmap.game_blue,
            R.mipmap.house_blue,
            R.mipmap.red_packet_blue,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        getWindow().setStatusBarColor(getResources().getColor(R.color.main_head));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_body));

        //初始化数据
        initData();
        //ViewPager操作
        viewPagerOprating();
    }


    private void viewPagerOprating() {
        //适配器
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(
                getSupportFragmentManager(),
                fragments
        );
        //找到ViewPager
        viewPager = findViewById(R.id.vp_vp);
        //设置适配器
        viewPager.setAdapter(adapter);
        //找到tabLayout绑定
        tabLayout = findViewById(R.id.vp_tl);
        //绑定在viewpager
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initData() {
        FragmentItem one = new FragmentItem(this, imgs_red);
        FragmentItem two = new FragmentItem(this, imgs_blue);
        fragments = new androidx.fragment.app.Fragment[]{one, two};

    }

    //点击左上角关闭活动
    public void clickToCloseEvent(View view) {
        finish();
    }

}