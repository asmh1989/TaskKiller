package com.sun.TaskKiller;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver
{
	private static final String TAG = "ATK";
	private static ActivityManager mActivityManager;

	public void onReceive(Context paramContext, Intent paramIntent){
		Log.e("ATK", "Start auto kill");
		CommonLibrary.NextRun = new Date(Setting.AUTO_KILL_FREQUENCY + System.currentTimeMillis());
		mActivityManager = (ActivityManager)paramContext.getSystemService("activity");
		int i = 0;
		switch (Setting.AUTO_KILL_LEVEL){
		case 0:
		default:
			break;
		case 1:
			i = 500;
			break;
		case 2:
			i = 400;
			break;
		case 3:
			i = 100;
			break;
		}
		Toast.makeText(paramContext, String.valueOf(CommonLibrary.KillProcess(
				paramContext, CommonLibrary.GetRunningProcess(paramContext, mActivityManager, i, true), mActivityManager))
				+ " Apps Killed", 0).show();
		Log.e("ATK", "Auto kill end");
	}
}