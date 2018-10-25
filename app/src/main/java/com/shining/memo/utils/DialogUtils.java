package com.shining.memo.utils;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.shining.memo.R;


public class DialogUtils {
    public static void showDialog(Context context, String title, String message
            , DialogInterface.OnClickListener positive
            , DialogInterface.OnClickListener negative){
        AlertDialog dialog = new AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(true)
        .setPositiveButton(R.string.confirm,positive)
                .setNegativeButton(R.string.cancel,negative).create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.item_btn_text));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getColor(R.color.item_btn_text));

    }
}
