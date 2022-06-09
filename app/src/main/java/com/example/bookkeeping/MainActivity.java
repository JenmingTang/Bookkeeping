package com.example.bookkeeping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.PopupWindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookkeeping.activities.RecordActivity;
import com.example.bookkeeping.activities.ViewPagerActivity;
import com.example.bookkeeping.adapters.MyRecyclerViewAdapter;
import com.example.bookkeeping.db.Db;
import com.example.bookkeeping.dialogs.MyDeleteAlertDialog;
import com.example.bookkeeping.entities.MyData;
import com.example.bookkeeping.listeners.MyUniversalListener;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    //handler
    MyHandler handler = new MyHandler(Looper.myLooper());
    //SmartRefreshLayout操作
    SmartRefreshLayout smartRefreshLayout;
    ClassicsHeader classicsHeader;
    //主界面眼睛操作
    boolean eye = true;
    ImageView imageView;
    TextView textView, textView2;
    //主界面recycler操作
    RecyclerView recyclerView;
    //死日期
    public Date date;
    //主界面recycler操作data,myDatumToday今天的
    List<MyData> myDatumToday = new ArrayList<>();
    //当月的
    List<MyData> myDatumCurrentMonth = new ArrayList<>();
    //主界面今日支出和今日收入
    TextView spendToday, incomeToday;
    //主界面月收入和月支出操作
    TextView spendMonth, incomeMonth;
    //RecyclerViewAdapter
    MyRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(getResources().getColor(R.color.main_head));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_body));

        //SmartRefreshLayout操作
        srlOperating();
        //初始化组件
        initComponent();


        //死日期
        date = new Date();
        //去拿数据，List<MyData> myDatumToday,今天的
        myDatumTodayDate();
        //去拿数据，List<MyData> myDatumCurrentMonth，当月的
        myDatumCurrentMonthDate();


        //扩展main
        new ExpandMainActivity(this, findViewById(R.id.main_btn_two));
    }

    //主界面月支出和月收入操作
    private void monthlyExpenditureAndMonthlyIncomeOperations() {

        //主界面月支出和月收入操作,myDatumCurrentMonth
        if (myDatumCurrentMonth != null) {
            if (myDatumCurrentMonth.size() > 0) {
                //支出和收入
                float one = 0.0f, two = 0.0f;
                for (MyData m :
                        myDatumCurrentMonth) {
                    //==1为支出
                    if (m.sign == 1) {
                        float v = Float.parseFloat(m.money);
                        one += v;
                    } else {
                        float v = Float.parseFloat(m.money);
                        two += v;
                    }
                }
                //for出来后
                spendMonth.setText(String.valueOf(one));
                incomeMonth.setText(String.valueOf(two));
            } else {
                spendMonth.setText("0");
                incomeMonth.setText("0");
            }
        }
    }

    //我的基准当月日期
    private void myDatumCurrentMonthDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月");
        String s = format.format(date);
        //query database
        Db.threads.execute(() -> {
            Single<List<MyData>> single = Db.getDb(this).myDao().queryDayOrMonthFromMyDatumByLike(s + "%");

            single.subscribe(new SingleObserver<List<MyData>>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                }

                @Override
                public void onSuccess(@NonNull List<MyData> myData) {
                    System.out.println("onSuccess,myDatumCurrentMonth");
                    myDatumCurrentMonth.clear();
                    myDatumCurrentMonth.addAll(myData);

                    Message message = handler.obtainMessage();
                    message.arg2 = 1;
                    handler.sendMessage(message);
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    e.printStackTrace();
                }
            });
        });
    }

    //主界面今日收入和今日支出操作
    private void todaySIncomeAndTodaySExpenditureOperations() {
        //主界面今日收入和今日支出操作,myDatumToday
        if (myDatumToday != null) {
            if (myDatumToday.size() > 0) {
                //支出和收入
                float one = 0.0f, two = 0.0f;
                for (MyData m :
                        myDatumToday) {
                    //==1为支出
                    if (m.sign == 1) {
                        float v = Float.parseFloat(m.money);
                        one += v;
                    } else {
                        float v = Float.parseFloat(m.money);
                        two += v;
                    }
                }
                //for出来后


                spendToday.setText(String.valueOf(one));
                incomeToday.setText(String.valueOf(two));
            } else {
                spendToday.setText("0");
                incomeToday.setText("0");
            }
        }
    }

    //去拿数据，返回List<MyData>，今天的
    private void myDatumTodayDate() {
        //去query 数据库
        //取到今天的日期,date and calendar is dead
        String s = formatDate(date);

        Db.threads.execute(() -> {
            //记得加通配符 %，这是 like
            Single<List<MyData>> single = Db.getDb(this).myDao().queryDayOrMonthFromMyDatumByLike(s + "%");
            single.subscribe(new SingleObserver<List<MyData>>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                }

                @Override
                public void onSuccess(@NonNull List<MyData> myData) {
                    System.out.println("onSuccess,myDatumToday");
                    //观察者只观察myDatumToday的内存地址，只能addAll（），不然adapter。notifyDataSetChange没有用
                    myDatumToday.clear();
                    myDatumToday.addAll(myData);
                    //于UI线程沟通，通知适配器
                    Message message = handler.obtainMessage();
                    message.arg1 = 1;
                    handler.sendMessage(message);
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public static String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(date);
    }

    private void initComponent() {
        textView = findViewById(R.id.main_tv_three);
        textView2 = findViewById(R.id.main_tv_five);
        //主界面recycler操作
        recyclerView = findViewById(R.id.main_rccl);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        RecyclerView.VERTICAL,
                        false
                )
        );
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);


        //延迟在主线程跑
//        recyclerView.postDelayed(() -> {
            adapter = new MyRecyclerViewAdapter(this, myDatumToday, false);

            //自定义回调,adapter
            adapter.setMyUniversalListener((v, position) -> {
                MyDeleteAlertDialog deleteDialog = new MyDeleteAlertDialog(this);
                deleteDialog.setPositiveButton("确认", (dialog, which) -> {
                    TextView dateTime = v.itemView.findViewById(R.id.mrvi_tv_fullDateTime);
                    actionAfterConfirmingDeletion(dateTime);
                    dialog.dismiss();
                });
                deleteDialog.show();
            });
            //延迟1秒在主线程跑
            recyclerView.setAdapter(adapter);
//        }, 1000);

        spendToday = findViewById(R.id.main_tv_seven);
        incomeToday = findViewById(R.id.main_tv_nine);
        /**
         * 在指定的时间后运行。 runnable 将在用户界面线程上运行
         */
        //延迟在主线程跑
//        incomeToday.postDelayed(() -> {
            //主界面今日收入和今日支出操作
            todaySIncomeAndTodaySExpenditureOperations();
//        }, 1000);

        spendMonth = findViewById(R.id.main_tv_three);
        incomeMonth = findViewById(R.id.main_tv_five);
        //延迟在主线程跑
//        incomeMonth.postDelayed(() -> {
            //主界面月收入和月支出操作
            monthlyExpenditureAndMonthlyIncomeOperations();
//        }, 1000);
    }

    private void actionAfterConfirmingDeletion(TextView dateTime) {

        //确认删除后操作
        Db.threads.execute(() -> {


            MyData myData = new MyData(
                    0,
                    null,
                    null,
                    null,
                    //根据主键
                    dateTime.getText().toString(),
                    0
            );

            Completable completable = Db.getDb(this).myDao().deleteMyDatum(myData);
            try {
                completable.subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onComplete() {
//                        //将当前线程初始化为循环程序。 这使您有机会创建处理程序，
//                        Looper.prepare();
//                        //出错，加上前面的不错了
//                        Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        myDatumTodayDate();
                        //取月
                        myDatumCurrentMonthDate();

                        Message message = handler.obtainMessage();
                        message.arg2 = 1;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }
                });
            } catch (Exception e) {
                return;
            }
        });
    }


    //SmartRefreshLayout操作,下拉刷新操作
    private void srlOperating() {
        smartRefreshLayout = findViewById(R.id.main_smart_rl);

        classicsHeader = new ClassicsHeader(this);
        classicsHeader.setPrimaryColorId(R.color.main_body);
        classicsHeader.setAccentColorId(R.color.main_gray);

        smartRefreshLayout.setRefreshHeader(classicsHeader);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> refreshLayout.finishRefresh(150));
    }

    //主界面眼睛操作
    public void mainEye(View view) {
        imageView = (ImageView) view;
        if (eye) {
            imageView.setImageResource(R.mipmap.yanjing_close);
            textView.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.INVISIBLE);
            eye = false;
        } else {
            imageView.setImageResource(R.mipmap.yanjing);
            textView.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            eye = true;
        }
    }

    //主界面点+号后
    public void main_btn(View view) {
        Intent intent = new Intent(this, ViewPagerActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("onRestart");
        //查找日数据
        myDatumTodayDate();
        //月数据
        myDatumCurrentMonthDate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //如果token为空，则所有回调和消息将被删除。
        handler.removeCallbacksAndMessages(null);
    }

    public void mainSearch(View view) {
        startActivity(new Intent(this, RecordActivity.class));
    }

    class MyHandler extends android.os.Handler {
        public MyHandler(@androidx.annotation.NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@androidx.annotation.NonNull Message msg) {
            //日操作
            if (msg.arg1 == 1) {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    //今日支出
                    todaySIncomeAndTodaySExpenditureOperations();
                    //主界面月支出和月收入操作
                    monthlyExpenditureAndMonthlyIncomeOperations();
                }
            }
            //月操作
            if (msg.arg2 == 1) {
                if (adapter != null) {
                    //主界面月支出和月收入操作
                    monthlyExpenditureAndMonthlyIncomeOperations();
                }
            }
        }
    }
}