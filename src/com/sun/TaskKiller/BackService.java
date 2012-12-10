package com.sun.TaskKiller;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BackService extends Service{
	public static final String AUTO_KILL_ACTION = "com.rechild.advancedtaskkillerpro.action.autokill";
	public static final String CMD_CANCEL = "cancel";
	public static final String KILL_ALL_ACTION = "com.rechild.advancedtaskkillerpro.action.killall";
	public static final String TAG = "ATK";
	private ScreenOffReceiver mScreenOfReceiver = new ScreenOffReceiver();
	ActivityManager mActivityManager = null;

	public IBinder onBind(Intent paramIntent){
		return null;
	}

	public void onCreate(){
		super.onCreate();
		mActivityManager = ((ActivityManager)getApplicationContext().getSystemService("activity"));
		IntentFilter localIntentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
		registerReceiver(mScreenOfReceiver, localIntentFilter);
	}

	public void onDestroy(){
		super.onDestroy();
		unregisterReceiver(mScreenOfReceiver);
	}

	public void onStart(Intent paramIntent, int paramInt){
		super.onStart(paramIntent, paramInt);
		if (paramIntent != null){
			String str = paramIntent.getAction();
			if ((str == null) || (!str.endsWith("killall")))
				return;
			Log.e("ATK", "widget kill start");
			int i = CommonLibrary.KillProcess(this, CommonLibrary.GetRunningProcess(this, mActivityManager, true), mActivityManager, false);
			try{
				Thread.sleep(100L);
				long l = CommonLibrary.getAvaliableMemory(mActivityManager);
				Toast.makeText(this, String.valueOf(i) + " Apps Killed, " + CommonLibrary.MemoryToString(l) + " memory available", 0).show();
				Log.e("ATK", "widget kill end");
			}
			catch (InterruptedException localInterruptedException){
				localInterruptedException.printStackTrace();
			}
		}
	}
}