package com.example.bookkeeping.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.StateListAnimator;
import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookkeeping.R;
import com.example.bookkeeping.activities.RecordActivity;
import com.example.bookkeeping.entities.MyData;
import com.example.bookkeeping.listeners.MyUniversalListener;

import java.text.SimpleDateFormat;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyRecyclerViewHolder> {
    Context context;
    List<MyData> myDatum;
    //自定义回调
    MyUniversalListener myUniversalListener;
    //true 是record，false 是main
    boolean sign;

    //给外部set
    public void setMyUniversalListener(MyUniversalListener myUniversalListener) {
        this.myUniversalListener = myUniversalListener;
    }

    public MyRecyclerViewAdapter(Context context, List<MyData> myDatum, boolean sign) {
        this.sign = sign;
        this.context = context;
        this.myDatum = myDatum;
    }

    @NonNull
    @Override
    public MyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_recycler_view_item, null);

        return new MyRecyclerViewHolder(view);
    }

    //绑定数据
    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewHolder holder, int position) {
        MyData myData = myDatum.get(position);

        holder.img.setImageResource(myData.img);
        holder.category.setText(myData.category);
        //remark 需要判断
        if (!myData.remark.equals("添加备注")) {
            holder.remark.setText(myData.remark);
        }

        String money = myData.money;
        if (money.equals(".00"))
            holder.money.setText("0");
        else
            holder.money.setText(money);

        //自定义回调
        if (myUniversalListener != null) {
            //组件可长按
            RelativeLayout relativeLayout = holder.itemView.findViewById(R.id.mrvi_rlt);

            //在外面访问吧，都写了那么多自己的了
            relativeLayout.setOnLongClickListener(v -> {
                myUniversalListener.MyOnLongClickItemListener(holder, position);
                return true;
            });
            relativeLayout.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale_anim);
                //重复两次，add插值器
                animation.setInterpolator(new CycleInterpolator(2f));
                relativeLayout.startAnimation(animation);

// 以view中心为缩放点，由初始状态放大两倍
//                ScaleAnimation animation = new ScaleAnimation(
//                        1.0f, 2.0f, 1.0f, 2.0f,
//                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
//                );
//                animation.setDuration(3000);
//                relativeLayout.startAnimation(animation);








                //抬高
//                relativeLayout.setElevation(18f);
                relativeLayout.animate().translationZ(18f).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        relativeLayout.animate().translationZ(1.0f).setDuration(500);
                    }
                }).start();
            });
        }
        /**
         String s="2021年08月12日 45:41:45";
         String s1 = s.substring(0, s.length() - 3);
         out.println(s1);
         格式化一下
         */
        String s = myData.dateTime;
        if (sign) {
            holder.dateTime.setText(s.substring(0, s.length() - 3));
        } else
            holder.dateTime.setText("今天 " + s.substring(12, s.length() - 3));
        //将带秒得time存起来
        holder.fullDateTime.setText(s);
    }

    //根据getItemCount()跑多少次onCreateViewHolder()
    @Override
    public int getItemCount() {
        int a = 0;
        try {
            a = myDatum.size();
        } catch (Exception e) {
        }
        return a;
    }

    public class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView category, remark, money, dateTime, fullDateTime;

        public MyRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.img = itemView.findViewById(R.id.mrvi_img);
            this.category = itemView.findViewById(R.id.mrvi_tv_category);
            this.remark = itemView.findViewById(R.id.mrvi_tv_remark);
            this.money = itemView.findViewById(R.id.mrvi_tv_money);
            this.dateTime = itemView.findViewById(R.id.mrvi_tv_dateTime);
            this.fullDateTime = itemView.findViewById(R.id.mrvi_tv_fullDateTime);
        }

    }
}
