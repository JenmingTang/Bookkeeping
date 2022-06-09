package com.example.bookkeeping;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.bookkeeping.activities.RecordActivity;

public class ExpandMainActivity extends MainActivity {
    Context context;
    //底部更多
    Button more;
    //popupWindowItem
    LinearLayout lL, lLTwo;
    //充气
    LayoutInflater from;

    public ExpandMainActivity(Context context,Button more) {
        this.context = context;

        from = LayoutInflater.from(context);

        this.more = more;
        //点击后操作
        more.setOnClickListener(v -> clickAction(v));

    }

    View pwcm;
    PopupWindow p;
    private void clickAction(View v) {
        pwcm = from.inflate(R.layout.popup_window_content_main, null);

        p = new PopupWindow(pwcm);
        //点击外部关闭
        p.setFocusable(true);
        p.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        p.setHeight(-2);
        p.showAtLocation(v.getRootView(), Gravity.BOTTOM, 0, 0);


        //弹出后的item操作
        popupWindowItemOperating();
    }

    private void popupWindowItemOperating() {
        lL = pwcm.findViewById(R.id.pwcm_ll);
        lLTwo = pwcm.findViewById(R.id.pwcm_ll_two);

        lL.setOnClickListener(l -> {
            //账单记录
            queryAllRecordAndShow();
        });
        lLTwo.setOnClickListener(l2 -> {
            //about
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("关于")
                    .setPositiveButton("嗯，你是唐健铭", (dialog1, which) -> {
                        dialog1.dismiss();
                    })
                    .setMessage(R.string.main_more_about)
                    .create();
            dialog.show();
        });
    }

    private void queryAllRecordAndShow() {
        //账单记录
        Intent intent = new Intent(context, RecordActivity.class);
        intent.putExtra("ExpandMainActivity", "ExpandMainActivity");
        context.startActivity(intent);
    }

}
