package com.example.camerademo.utils;

import android.graphics.Color;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camerademo.App;

/**
 * 单例toast
 * Created by wangzhenxing on 16/11/03.
 */
public class ToastUtil {

	private static Toast sToast;

	public static void show(String msg) {
		if (TextUtils.isEmpty(msg)) return;
		if (sToast == null) {
			sToast = Toast.makeText(App.getInstance(), msg, Toast.LENGTH_SHORT);
//			sToast.setGravity(Gravity.CENTER, 0, 0);
		}
		sToast.setText(msg);
		sToast.show();
	}

	public static void show(int msg) {
		if (msg <= 0) return;
		if (sToast == null) {
			sToast = Toast.makeText(App.getInstance(), msg, Toast.LENGTH_SHORT);
//			sToast.setGravity(Gravity.CENTER, 0, 0);
		}
		sToast.setText(msg);
		sToast.show();
	}

	public static void showWarnMessage(String msg) {
		if (TextUtils.isEmpty(msg)) return;
		Toast toast = Toast.makeText(App.getInstance(), msg, Toast.LENGTH_SHORT);
		TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
		v.setTextColor(Color.RED);
		toast.show();
	}
}
