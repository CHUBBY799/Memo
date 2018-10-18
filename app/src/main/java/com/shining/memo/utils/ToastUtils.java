package com.shining.memo.utils;

import android.content.Context;
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

	public static boolean isShow = true;

	/**
	 * 短时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showSuccessShort(Context context, CharSequence message)
	{
		Toast toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_SHORT);
		View layout = View.inflate(context, R.layout.toast_notice,null);
		TextView textView = layout.findViewById(R.id.toast_info);
		textView.setText(message);
		toast.setView(layout);
		toast.setGravity(Gravity.CENTER|Gravity.BOTTOM,0,110);
		toast.show();
	}

	public static void showFailedShort(Context context, CharSequence message)
	{
		Toast toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_SHORT);
		View layout = View.inflate(context, R.layout.toast_notice,null);
		ImageView imageView = layout.findViewById(R.id.toast_image);
		TextView textView = layout.findViewById(R.id.toast_info);
		imageView.setImageDrawable(context.getDrawable(R.drawable.cancel_icon));
		textView.setText(message);
		toast.setView(layout);
		toast.setGravity(Gravity.CENTER|Gravity.BOTTOM,0,110);
		toast.show();
	}

	public static void showShort(Context context, CharSequence message){
		Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
	}

}