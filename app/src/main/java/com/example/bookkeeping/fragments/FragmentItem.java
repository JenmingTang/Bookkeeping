package com.example.bookkeeping.fragments;

import android.content.Context;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bigkoo.pickerview.TimePickerView;
import com.example.bookkeeping.R;
import com.example.bookkeeping.activities.ViewPagerActivity;
import com.example.bookkeeping.adapters.MyGridViewAdapter;
import com.example.bookkeeping.db.Db;
import com.example.bookkeeping.entities.GridViewItem;
import com.example.bookkeeping.entities.MyData;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * A simple {@link androidx.fragment.app.Fragment} subclass.
 * Use the {@link FragmentItem#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentItem extends Fragment {
    //完成
    int sign;
    //sql操作，装头部数据的变量
    int headImg;
    //只做一次的操作，初始化一下头部img和gridView的首个颜色
    String flag = "";
    //头部img
    ImageView img;
    //头部text
    TextView tvTwo;

    //键盘
    KeyboardView keyboardView;
    Keyboard keyboard;
    //上下文
    Context context;
    //为其时间组件赋初值并添加回调方法
    TextView tvTime;
    //键盘事件操作textView
    TextView tvMoney;
    //添加备注回调方法
    TextView remark;
    //充好气的view_pager_item
    View inflate;
    //自动获取焦点,先找到组件
    EditText et;
    //网格视图
    GridView gv;
    //gridView数据
    List<GridViewItem> gridViewItems;

    String[] names = {"其他", "购物车", "交通", "食物", "娱乐", "住房", "红包"};
    int[] imgs = {
            R.mipmap.other,
            R.mipmap.shoping_car,
            R.mipmap.bus,
            R.mipmap.food,
            R.mipmap.game,
            R.mipmap.house,
            R.mipmap.red_packet,
    };
    int[] imgs_red_blue;

    {
        gridViewItems = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            gridViewItems.add(new GridViewItem(imgs[i], names[i]));
        }
    }

    //保存GridViewItem的previous（上一个项）
    public View previousGridViewItem;
    public int previousGridViewItemPosition;
    public long previousGridViewItemId;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentItem(Context context, int[] imgs_red_blue) {
        if (imgs_red_blue[0] == R.mipmap.other_red) {
            flag = "one";
            sign = 1;
        }
        if (imgs_red_blue[0] == R.mipmap.other_blue) {
            flag = "two";
            sign=2;
        }

        this.imgs_red_blue = imgs_red_blue;
        this.context = context;
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_one.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentItem newInstance(String param1, String param2) {
        FragmentItem fragment = new FragmentItem(null, null);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflate = inflater.inflate(R.layout.view_pager_item, container, false);
        //自定义键盘操作
        keyboardOperating(inflate);
        //为其时间组件赋初值并添加回调方法
        timeComponentOperating(inflate);
        //添加备注回调方法
        addRemarkCallbackMethod(inflate, inflater);
        //加载GridView操作
        loadGridViewOprating(inflate);

        return inflate;
    }

    //加载GridView操作
    private void loadGridViewOprating(View inflate) {
        //加载GridView操作
        gv = inflate.findViewById(R.id.vpi_gv);
        MyGridViewAdapter myGridViewAdapter = new MyGridViewAdapter(gridViewItems, context);
        gv.setAdapter(myGridViewAdapter);
        //只做一次的操作，初始化一下头部img和gridView的首个颜色
        if (flag.equals("one")) {
            //头部img
            img = (ImageView) inflate.findViewById(R.id.vpi_img_two);
            img.setImageResource(R.mipmap.other_red);
            headImg = R.mipmap.other_red;
            flag = "null";
        }
        if (flag.equals("two")) {
            //头部img
            img = (ImageView) inflate.findViewById(R.id.vpi_img_two);
            img.setImageResource(R.mipmap.other_blue);
            headImg = R.mipmap.other_blue;
            flag = "null";
        }


        //头部text
        tvTwo = inflate.findViewById(R.id.vpi_tv);

        //GridView回调操作
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (previousGridViewItem != null) {
                    ImageView img = previousGridViewItem.findViewById(R.id.gvi_img);
                    img.setImageResource(imgs[previousGridViewItemPosition]);
                }
                //保存GridViewItem的previous（上一个项）
                previousGridViewItem = view;
                previousGridViewItemPosition = position;
                previousGridViewItemId = id;
                //改变item的子组件imageView的资源
                ImageView imgTwo = (ImageView) view.findViewById(R.id.gvi_img);
                imgTwo.setImageResource(imgs_red_blue[position]);
                //传数据更换头部的img和text
                //头部img
                img.setImageResource(imgs_red_blue[position]);
                //装
                headImg = imgs_red_blue[position];
                tvTwo.setText(names[position]);
            }
        });
    }

    //添加备注回调方法
    private void addRemarkCallbackMethod(View inflate, LayoutInflater inflater) {
        remark = inflate.findViewById(R.id.vpi_tv_three);
        //点击备注
        remark.setOnClickListener(v -> {
            View view = inflater.inflate(R.layout.popup_window_content, null);

            PopupWindow popupWindow = new PopupWindow(
                    view,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true);
            popupWindow.showAtLocation(v.getRootView(), Gravity.BOTTOM, 0, 0);


            //自动获取焦点,先找到组件
            autoJumpKeyboard(view);
            //点击确定按钮备注的回调
            Button button = view.findViewById(R.id.pwc_btn);
            //点击确定后
            button.setOnClickListener(v1 -> {

                popupWindow.dismiss();
                String s = et.getText().toString();
                if (s.isEmpty()) {
                    remark.setText("");
                    remark.setHint("添加备注");
                    return;
                }
                remark.setText(s);
            });
        });
    }

    //自动获取焦点,先找到组件
    public void autoJumpKeyboard(View view) {
        et = view.findViewById(R.id.pwc_et);
        et.requestFocus();
        //页面还没加载就跑完了，要等一下
        new Thread(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //页面还没加载就跑完了，要等一下
            InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(et, 0);
        }).start();
    }

    //如果不设置时间就用这个
    Date defaultDate;
    Date resultDate;

    //为其时间组件赋初值并添加回调方法
    private void timeComponentOperating(View inflate) {
        tvTime = inflate.findViewById(R.id.vpi_tv_four);
        defaultDate = new Date();

        //没选之前的time，没有秒
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        //没选之前的time，没有秒
        tvTime.setText(simpleDateFormat.format(defaultDate));
        //使用死的日历
        Calendar startDate = Calendar.getInstance();
        startDate.set(2020, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2040, 11, 31);
        Calendar defaultCalendar = Calendar.getInstance();
        //时间选择器视图
        TimePickerView tpk = new TimePickerView.Builder(
                //上下文
                context,
                new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        // 这里回调的v,就是show()方法里面所添加的 View 参数，
                        // 如果show的时候没有添加参数，v则为null
                        //格式化date对象
                        tvTime.setText(formatData(date));
                        //存
                        resultDate = date;
                    }
                }
                //在Builder构造方法后
                //年月日时分秒的显示与否，不设置则默认全部显示，这里可自行定制
        ).setType(new boolean[]{true, true, true, true, true, false})
                .setLabel("年", "月", "日", "时", "分", "")
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确认")//确认按钮文字
                .setContentSize(18)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleText("请选择日期时间")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                .setTitleColor(Color.rgb(128, 128, 128))//标题文字颜色
                .setSubmitColor(Color.BLACK)//确定按钮文字颜色
                .setCancelColor(Color.BLACK)//取消按钮文字颜色
                .setTitleBgColor(Color.rgb(240, 240, 240))//标题背景颜色 Night mode
                .setBgColor(Color.rgb(240, 240, 240))//滚轮背景颜色 Night mode
//                .setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR) + 20)//默认是1900-2100年
                .setDate(defaultCalendar)// 如果不设置的话，默认是系统时间
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .isDialog(true)//是否显示为对话框样式
                .build();

        tvTime.setOnClickListener(v -> {
            //将TimePickerView显示出来
            tpk.show(tvTime);
        });
    }


    //自定义键盘操作
    private void keyboardOperating(View inflate) {
        keyboardView = inflate.findViewById(R.id.vpi_kv);
        keyboard = new Keyboard(context, R.xml.keyboard);
        keyboardView.setBackgroundResource(R.color.main_body);
        keyboardView.setKeyboard(keyboard);
        //键盘监听事件
        keyboardMonitorEvent(inflate);

    }

    //键盘监听事件
    private void keyboardMonitorEvent(View inflate) {
        //键盘事件操作textView
        tvMoney = inflate.findViewById(R.id.vpi_tv_two);
        StringBuffer buffer = new StringBuffer();

        keyboardView.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onPress(int primaryCode) {
            }

            @Override
            public void onRelease(int primaryCode) {

            }

            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
                //字符数字（'0' ~ '9'）：48（'0'） ~ 57（'9'）
                switch (primaryCode) {
                    case 60:
                        if (buffer.length() > 0 && buffer.indexOf(".") == -1)
                            buffer.append(".");
                        break;
                    case -5:
                        try {
                            buffer.deleteCharAt(buffer.length() - 1);
                        } catch (Exception e) {
                        }
                        break;
                    case -4:
                        //完成有很多操作
                        endOperation();
                        break;
                    default:
                        buffer.append((char) primaryCode);
                }
                if (buffer.length() == 0)
                    tvMoney.setText("0");
                else
                    tvMoney.setText(buffer.toString());
            }

            @Override
            public void onText(CharSequence text) {

            }

            @Override
            public void swipeLeft() {

            }

            @Override
            public void swipeRight() {

            }

            @Override
            public void swipeDown() {

            }

            @Override
            public void swipeUp() {

            }
        });
    }

    //完成有很多操作
    private void endOperation() {
        //拿到数据存储到sql里
        /**
         * I/System.out: 2131492885   headImgID
         *     其他       tvTwo头名
         *     false      tvMoney钱
         *     0    tvMoney钱
         *     添加备注    remark
         *     null      resultDate
         */

        //持有数据库引用
        Db.threads.execute(() -> {
            if (resultDate == null)
                resultDate = defaultDate;

            double d = Double.parseDouble(tvMoney.getText().toString());
            DecimalFormat df = new DecimalFormat("#.00");
            String money = df.format(d);

            Completable completable = Db.getDb(context).myDao().insertMyDatum(
                    new MyData(
                            headImg,
                            tvTwo.getText().toString(),
                            remark.getText().toString(),
                            money,
                            formatData(resultDate),
                            //sign
                            sign)
            );
            //判断
            completable.subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                }

                @Override
                public void onComplete() {
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    Toast.makeText(
                            context.getApplicationContext(),
                            "出错了，可能日期时间有误",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        });

        //关闭掉活动
        ((ViewPagerActivity) context).finish();
    }

    //格式化date对象
    public static String formatData(Date HH_mm_ss) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return format.format(HH_mm_ss);
    }
}