package com.example.bookkeeping.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.bookkeeping.MainActivity;
import com.example.bookkeeping.R;
import com.example.bookkeeping.adapters.MyRecyclerViewAdapter;
import com.example.bookkeeping.db.Db;
import com.example.bookkeeping.dialogs.MyDeleteAlertDialog;
import com.example.bookkeeping.entities.MyData;
import com.example.bookkeeping.listeners.MyUniversalListener;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class RecordActivity extends AppCompatActivity {
    //show data
    RecyclerView rv;
    //找到tv
    TextView tv;
    //装全部
    List<MyData> myDatumAll = new ArrayList<>();
    //handler
    Handler handler;

    MyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        getWindow().setStatusBarColor(getResources().getColor(R.color.main_head));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_body));

        //handler
        handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 1 = all, 2 = theDay
                if (msg.arg1 == 1) {
                    tv.setText("全部");
                    adapter.notifyDataSetChanged();
                }

                if (msg.arg1 == 2)
                    adapter.notifyDataSetChanged();
            }
        };
        //找到TextView
        tv = findViewById(R.id.ar_tv);
        //找到RecyclerView
        rv = findViewById(R.id.ar_rv);
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        //查询all数据
        queryData();

        adapter = new MyRecyclerViewAdapter(this, myDatumAll, true);
        adapter.setMyUniversalListener(new MyUniversalListener() {
            @Override
            public void MyOnLongClickItemListener(MyRecyclerViewAdapter.MyRecyclerViewHolder myRecyclerViewHolder, int position) {

                MyData myData = myDatumAll.get(position);

                MyDeleteAlertDialog deleteDialog = new MyDeleteAlertDialog(RecordActivity.this);
                deleteDialog.setPositiveButton("确认", (dialog, which) -> {
                    actionAfterConfirmingDeletion(myData);
                    dialog.dismiss();
                });
                deleteDialog.show();
            }
        });
        rv.setAdapter(adapter);
    }

    private void actionAfterConfirmingDeletion(MyData myData) {
        Db.threads.execute(() -> {
            Completable completable = Db.getDb(this).myDao().deleteMyDatum(myData);
            completable.subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                }

                @Override
                public void onComplete() {
                    String s = tv.getText().toString();
                    if (s.equals("全部"))
                        queryData();
                    else
                        queryTheDay(s);

                }

                @Override
                public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                }
            });
        });
    }

    //查询数据
    private void queryData() {
        Db.threads.execute(() -> {
            Single<List<MyData>> all = Db.getDb(this).myDao().queryAllFromMyDatum();
            all.subscribe(new SingleObserver<List<MyData>>() {
                @Override
                public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                }

                @Override
                public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<MyData> myData) {
                    myDatumAll.clear();
                    myDatumAll.addAll(myData);

                    Message message = handler.obtainMessage();
                    // 1 = all
                    message.arg1 = 1;
                    handler.sendMessage(message);
                }

                @Override
                public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                }
            });
        });
    }

    //点击通过日期查找
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void clickShowDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                Date date = c.getTime();
                String s = MainActivity.formatDate(date);
                //set textView
                tv.setText(s);
                //查询当天的
                queryTheDay(s);
            }
        });
        datePickerDialog.show();
    }

    //查询当天的
    private void queryTheDay(String s) {
        Db.threads.execute(() -> {
            Single<List<MyData>> single = Db.getDb(this).myDao().queryDayOrMonthFromMyDatumByLike(s + "%");
            single.subscribe(new SingleObserver<List<MyData>>() {
                @Override
                public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                }

                @Override
                public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<MyData> myData) {
                    myDatumAll.clear();
                    myDatumAll.addAll(myData);

                    Message message = handler.obtainMessage();
                    // 2 = theDay
                    message.arg1 = 2;
                    handler.sendMessage(message);
                }

                @Override
                public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                }
            });
        });
    }

    public void closeRecordActivity(View view) {
        finish();
    }
}