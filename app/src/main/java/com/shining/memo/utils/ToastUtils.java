package com.shining.memo.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shining.memo.R;


public class ToastUtils
{

	private ViewGroup.LayoutParams layoutParams;

	private ToastUtils()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	public static void showShort(Context context, CharSequence message){

		Toast toast = Toast.makeText(context,  "		"+message+"		", Toast.LENGTH_SHORT);
		View view = toast.getView();
		view.setBackground(context.getDrawable(R.drawable.toast_background));
		TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
		v.setTextColor(Color.WHITE);
		v.setTextSize(16);
		v.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		toast.setView(view);
		toast.show();
	}

}