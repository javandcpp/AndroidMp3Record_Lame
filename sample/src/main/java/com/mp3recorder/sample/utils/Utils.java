/*
 * Copyright 2017 Huawque
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * https://github.com/javandoc/AndroidMp3Record_Lame
 *
 */
package com.mp3recorder.sample.utils;

import java.io.File;




import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.mp3recorder.sample.BaseApplication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {
	/**
	 * 创建目录
	 * @param path
	 */
	private static int SCREEN_WIDTH = 0;
	private static int SCREEN_HEIGHT = 0;
	public static void createDirs(File path) {
		if (path != null && !path.exists()) {
			path.mkdirs();
		}
	}
	public static int getScreenWidth() {
		if (SCREEN_WIDTH == 0) {
			readScreenSize();
		}
		return SCREEN_WIDTH;
	}

	public static int getScreenHeight() {
		if (SCREEN_HEIGHT == 0) {
			readScreenSize();
		}
		return SCREEN_HEIGHT;
	}
	public static int getStatusBarHeigth(Context context) {
		int statusBarHeight = 0;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}
	public static String getDid() {
		TelephonyManager tm = (TelephonyManager) BaseApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimSerialNumber();
	}

	private static void readScreenSize() {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager manager = (WindowManager) BaseApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(dm);
		SCREEN_WIDTH = dm.widthPixels;
		SCREEN_HEIGHT = dm.heightPixels;
		boolean isFlag = (BaseApplication.getInstance().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
		if (SCREEN_HEIGHT < SCREEN_WIDTH && !isFlag) {
			SCREEN_HEIGHT = SCREEN_HEIGHT ^ SCREEN_WIDTH;
			SCREEN_WIDTH = SCREEN_HEIGHT ^ SCREEN_WIDTH;
			SCREEN_HEIGHT = SCREEN_HEIGHT ^ SCREEN_WIDTH;
		}
	}


	/**
	 * 文件是否存在
	 * @param file
	 * @return
	 */
	public static boolean isFileExist(File file) {
		if (file != null && file.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * 根据手机分辨率从dp转成px
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/** 
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f) - 15;
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale（DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(float pxValue, float fontScale) {
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale（DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(float spValue, float fontScale) {
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 检测sdcard是否可用
	 * @return true为可用，否则为不可用
	 */
	public static boolean sdCardIsAvailable() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED))
			return false;
		return true;
	}

	/**
	 * 验证手机号格式是否正确
	 * 
	 * @param mobileNumber
	 * @return
	 */
	public static boolean validateMobileNumber(String mobileNumber) {
		if (matchingText("^((13[0-9])|(17[^4,\\D])|(15[^4,\\D])|(18[^1^4,\\D]))\\d{8}", mobileNumber)) {
			return true;
		}
		return false;
	}

	/**
	 * 验证字符串,是否适合某种格式
	 * @param expression 正则表达式
	 * @param text 操作的字符串
	 * @return
	 */
	private static boolean matchingText(String expression, String text) {
		Pattern p = Pattern.compile(expression); // 正则表达式
		Matcher m = p.matcher(text); // 操作的字符串
		boolean b = m.matches();
		return b;
	}

	/**
	 * 检查网络状态
	 */
	public static boolean isNetworkAvailable(Context context) {
		try {
			android.net.ConnectivityManager cManager = (android.net.ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			android.net.NetworkInfo info = cManager.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				return true;
			}
			else {
				return false;
			}
		}catch (SecurityException e) {
			return false;
		}catch (Exception e) {
			return false;
		}
	}

	/**
	 * 检查网络状态是否真正连接成功
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		android.net.ConnectivityManager connectivity = (android.net.ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); //获取系统网络连接管理器
		if (connectivity == null) { //如果网络管理器为null
			return false; //返回false表明网络无法连接
		}
		else {
			android.net.NetworkInfo[] info = connectivity.getAllNetworkInfo(); //获取所有的网络连接对象
			if (info != null) { //网络信息不为null时
				for (int i = 0; i < info.length; i++) { //遍历网路连接对象
					if (info[i].isConnected()) { //当有一个网络连接对象连接上网络时
						return true; //返回true表明网络连接正常
					}
				}
			}
		}
		return false;
	}

	public static boolean isMobileNetworkAvailable(Context context) {
		//获取应用上下文
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		//获取系统的连接服务
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		//获取网络的连接情况
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			//判断3G网
			return true;
		}
		return false;
	}

	/**
	 * 版本号
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		try {
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			String name = appInfo.metaData.getString("version_name");
			if (name != null) {
				return name;
			}
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		}
		catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 编译版本号
	 * @param context
	 * @return
	 */
	public static String getVersionCode(Context context) {
		try {
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			String name = appInfo.metaData.getString("versionCode");
			if (name != null) {
				return name;
			}
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode+"";
		}
		catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 渠道号
	 * @param context
	 * @param metaName
	 * @return
	 */
	public static String getChannel(Context context, String metaName) {
		try {
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			return appInfo.metaData.getString(metaName);

		}
		catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 字符串转成int
	 * @param str
	 * @return
	 */
	public static int parseStr2Int(String str) {
		if (TextUtils.isEmpty(str)) {
			return -1;
		}

		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return -1;
		} catch (Exception e){
			return -1;
		}
	}

	/**
	 * 字符串转成int
	 * @param str
	 * @return
	 */
	public static float parseStr2Float(String str) {
		if (str == null) {
			return -1;
		}
		try {
			return Float.parseFloat(str);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * 判断字符串是否是合法的16进制串
	 * @author: Xue Wenchao
	 * @param str
	 * @return
	 * @return: boolean
	 * @date: 2014-1-21 上午10:13:23
	 */
	public static boolean isHexString(String str) {
		if (str == null) {
			return false;
		}
		return Pattern.matches("^[0-9a-fA-F]++$", str);
	}

	/**
	 * 字符串转成Long
	 * @param str
	 * @return
	 */
	public static long parseStr2Long(String str) {
		if (str == null) {
			return -1;
		}
		try {
			return Long.parseLong(str);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * 隐藏输入键盘
	 * @param view
	 * @param context
	 */
	public static void hideSoftInput(EditText view, Context context) {
		InputMethodManager inputMeMana = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMeMana.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static void hideSoftInput(IBinder binder, Context context){
		InputMethodManager inputMeMana = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMeMana.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 显示软键盘
	 */
	public static void showSoftInput(Context context) {
		InputMethodManager inputMeMana = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMeMana.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str != null && str.length() > 0) {
			return false;
		}
		return true;
	}

	/**
	 * 计算字符个数，一个汉字算两个
	 * @param s
	 * @return
	 */
	public static int countWord(String s) {
		if (s == null || s.length() == 0) {
			return 0;
		}
		int n = s.length(), a = 0, b = 0;
		int len = 0;
		char c;
		for (int i = 0; i < n; i++) {
			c = s.charAt(i);
			if (Character.isSpaceChar(c)) {
				++b;
			}
			else if (isAscii(c)) {
				++a;
			}
			else {
				++len;
			}
		}
		return len + (int) Math.ceil((a + b) / 2.0);
	}

	public static boolean isAscii(char c) {
		return c <= 0x7f;
	}

	/**
	 * 验证邮箱地址是否合法
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		}
		catch (Exception e) {
			flag = false;
		}

		return flag;
	}

	/**
	 * 过滤文本中的html脚本信息
	 * @param inputString
	 * @return
	 */
	public static String Html2Text(String inputString) {
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;
		java.util.regex.Pattern p_html1;
		java.util.regex.Matcher m_html1;
		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
			String regEx_html1 = "<[^>]+";
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签    

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签    

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签    

			p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll(""); // 过滤html标签    

			textStr = htmlStr;

		}
		catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}

		return textStr;// 返回文本字符串    
	}

	/**
	 * 写图片到SD卡
	 * @param bitmap
	 * @param filePath
	 * @throws IOException
	 */
	public static void saveBitmap(Bitmap bitmap, String filePath) {
		File file = new File(filePath);
		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)) {
				out.flush();
				out.close();
			}
		}
		catch (FileNotFoundException e) {
		}
		catch (IOException e) {
		}
	}

	/**
	 * 从网络下载图片并保存到指定路径
	 * @param imgUrl
	 * @param filePath
	 */
	public static void downloadImageAndSave(String imgUrl, String filePath) {
		URL url;
		InputStream is = null;
		FileOutputStream fos = null;
		URLConnection conn;
		try {
			url = new URL(imgUrl);
			conn = url.openConnection();
			is = conn.getInputStream();
			fos = new FileOutputStream(new File(filePath));
			Utils.copyStream(is, fos);
		}
		catch (Exception e) {
		}
		finally {
			try {
				is.close();
				fos.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 拷贝流
	 * @param is
	 * @param os
	 */
	public static void copyStream(InputStream is, OutputStream os) throws IOException {
		if (is == null || os == null) {
			return;
		}
		BufferedInputStream bufIs;
		boolean shouldClose = false;
		if (is instanceof BufferedInputStream) {
			bufIs = (BufferedInputStream) is;
		}
		else {
			bufIs = new BufferedInputStream(is);
			shouldClose = true;
		}

		int bufLen = 102400;
		byte[] buf = new byte[bufLen];
		int len;
		while (true) {
			len = bufIs.read(buf);
			if (len < 0) {
				break;
			}
			os.write(buf, 0, len);
		}
		if (shouldClose) {
			bufIs.close();
		}
	}

	/**
	 * 得到屏幕宽度
	 * @param context
	 * @return
	 */
	public static int getWinWidth(Activity context) {
		return context.getWindowManager().getDefaultDisplay().getWidth();
	}

	/**
	 * 得到屏幕高度
	 * @param context
	 * @return
	 */
	public static int getWinHight(Activity context) {
		return context.getWindowManager().getDefaultDisplay().getHeight();
	}

	/**
	 *  按字节截取字符串
	 * @param str
	 * @param subSLength
	 * @return
	 */
	public static String subStr(String str, int subSLength){
		if(TextUtils.isEmpty(str)){
			return "";
		}
		final int len = str.length();
        StringBuffer newStr = new StringBuffer();
		int counter = 0;

		for (int i = 0; i < len; i++) {
			char sigleItem = str.charAt(i);
			if(counter<subSLength){
				newStr.append(sigleItem);
			}
			if (isAlphanumeric(sigleItem)) {
				counter++;
			}
			else if (Character.isLetter(sigleItem)) {
				counter = counter + 2;
			}
			else {
				counter++;
			}

		}
		if(counter<=subSLength){
			return str;
		}else{
			newStr.append("...");
			return newStr.toString();
		}
	}

	public static int calculateCharLength(String src) {
		int counter = -1;
		if (src != null) {
			counter = 0;
			final int len = src.length();
			for (int i = 0; i < len; i++) {
				char sigleItem = src.charAt(i);
				if (isAlphanumeric(sigleItem)) {
					counter++;
				}
				else if (Character.isLetter(sigleItem)) {
					counter = counter + 2;
				}
				else {
					counter++;
				}
			}
		}
		else {
			counter = -1;
		}

		return counter;
	}

	/** 
	 * 判断字符是否为英文字母或者阿拉伯数字. 
	 *  
	 * @param ch char字符 
	 * @return true or false 
	 */
	public static boolean isAlphanumeric(char ch) {
		// 常量定义   
		final int DIGITAL_ZERO = 0;
		final int DIGITAL_NINE = 9;
		final char MIN_LOWERCASE = 'a';
		final char MAX_LOWERCASE = 'z';
		final char MIN_UPPERCASE = 'A';
		final char MAX_UPPERCASE = 'Z';

		if ((ch >= DIGITAL_ZERO && ch <= DIGITAL_NINE) || (ch >= MIN_LOWERCASE && ch <= MAX_LOWERCASE)
				|| (ch >= MIN_UPPERCASE && ch <= MAX_UPPERCASE)) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * decode js用escape编码的字符串
	 * @method: unEscape
	 * @description:
	 * @author: DongFuhai
	 * @param src
	 * @return
	 * @return: String
	 * @date: 2013-10-14 下午5:57:56
	 */
	public static String unEscape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				}
				else {
					ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			}
			else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				}
				else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}
    
	/**
	 * 发送自定义消息
	 */
    public static void sendEmptyMsg(Handler handler, int what) {
    	if(handler == null)
    		return;
    	
        handler.sendEmptyMessage(what);
    }
    
    /**
	 * 发送自定义消息
	 */
    public static void sendEmptyMsgDelayed(Handler handler, int what, long delayMillis) {
    	if(handler == null)
    		return;
    	
        handler.sendEmptyMessageDelayed(what, delayMillis);
    }
    
	/**
	 * 发送自定义消息
	 */
    public static void sendMsg(Handler handler, int what) {
    	if(handler == null)
    		return;
    	
        Message message = new Message();
        message.what = what;
        handler.sendMessage(message);
    }
	
	/**
	 * 发送自定义消息
	 */
    public static void sendMsg(Handler handler, int what, Object obj) {
    	if(handler == null)
    		return;
    	
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        handler.sendMessage(message);
    }
    
    /**
	 * 发送自定义消息
	 */
    public static void sendMsgDelayed(Handler handler, int what, Object obj, long delayMillis) {
    	if(handler == null)
    		return;
    	
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        handler.sendMessageDelayed(message, delayMillis);
    }
	
	/**
	 * 发送自定义消息
	 */
    public static void sendMsg(Handler handler, int what, int arg1, Object obj) {
    	if(handler == null)
    		return;
    	
        Message message = new Message();
        message.what = what;
        message.arg1 = arg1;
        message.obj = obj;
        handler.sendMessage(message);
    }
    
	/**
	 * 发送自定义消息
	 */
    public static void sendMsg(Handler handler, int what, int arg1, int arg2, Object obj) {
    	if(handler == null)
    		return;
    	
        Message message = new Message();
        message.what = what;
        message.arg1 = arg1;
        message.arg2 = arg2;
        message.obj = obj;
        handler.sendMessage(message);
    }


	public static byte[] hexStringToBytes(String hexString) {
		if (TextUtils.isEmpty(hexString)) {
			return new byte[0];
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

		}
		return d;
	}
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}


	/**
	 * 数据ip转ip
	 * @param ip
	 * @return
	 */
	public static String convertInt2Ip(int ip) {
		long raw = ip;
		byte[] b = new byte[] { (byte) raw, (byte) (raw >> 8), (byte) (raw >> 16), (byte) (raw >> 24) };
		try {
			return InetAddress.getByAddress(b).getHostAddress();
		}
		catch (Exception e) {
			return null;
		}
	}

	public static int numberNotNull(Integer i){
		return i==null?0:i.intValue();
	}
	public static long numberNotNull(Long l){
		return l==null?0:l.longValue();
	}
	public static short numberNotNull(Short s){
		return s==null?0:s.shortValue();
	}



	public static Point getNavigationBarSize(Context context) {

		Point appUsableSize = getAppUsableScreenSize(context);

		Point realScreenSize = getRealScreenSize(context);

		// navigation bar on the right

		if (appUsableSize.x < realScreenSize.x) {

			return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
		}

		// navigation bar at the bottom

		if (appUsableSize.y < realScreenSize.y) {

			return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);

		}
		return new Point();
	}

	public static Point getAppUsableScreenSize(Context context) {

		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		Display display = windowManager.getDefaultDisplay();

		Point size = new Point();

		display.getSize(size);

		return size;

	}

	public static Point getRealScreenSize(Context context) {

		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		Display display = windowManager.getDefaultDisplay();

		Point size = new Point();

		if (Build.VERSION.SDK_INT >= 17) {

			display.getRealSize(size);

		} else if (Build.VERSION.SDK_INT >= 14) {

			try {

				size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);

				size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);

			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			} catch (NoSuchMethodException e) {
			}

		}

		return size;
	}


	public static void copyFile(File sourceFile, File targetFile) throws Exception {
		FileInputStream in = new FileInputStream(sourceFile);
		byte[] buffer = new byte[131072];
		boolean len = true;
		FileOutputStream out = new FileOutputStream(targetFile);

		int len1;
		while((len1 = in.read(buffer)) != -1) {
			out.write(buffer, 0, len1);
		}

		out.flush();
		out.close();
		in.close();
	}

	/**
	 * 判断是否在后台
	 * @param context
	 * @return
     */
	public static boolean isBackground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 判断是否在前台
	 * @param context
	 * @return
     */
	public static boolean isForeground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}

	public static  void openBrowser(Context ctx , String url) {
		if(null == ctx || TextUtils.isEmpty(url)) {
			return ;
		}
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(url);
		intent.setData(content_url);
		ctx.startActivity(intent);
	}

}
