package com.example.bookkeeping.dialogs;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class MyDeleteAlertDialog extends AlertDialog.Builder {

    public MyDeleteAlertDialog(@NonNull Context context) {
        super(context);
        setMessage("确认要删除吗！");
        setNegativeButton("取消",(dialog, which) -> {
            dialog.dismiss();
        });
        create();
    }
}
