package com.sun.TaskKiller;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ScreenOffReceiver extends BroadcastReceiver{
	private static final String TAG = "ATK";

	public void onReceive(Context paramContext, Intent paramIntent){
		int i = 0;
		switch (Setting.AUTO_KILL_LEVEL){
		default:
		case 0:
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
		if (Setting.AUTO_KILL_FREQUENCY == 0L){
			Log.e("ATK", "Screen off kill start");
			ActivityManager localActivityManager = (ActivityManager)paramContext.getSystemService("activity");
			Toast.makeText(paramContext, String.valueOf(CommonLibrary.KillProcess(paramContext, 
					CommonLibrary.GetRunningProcess(paramContext, localActivityManager, i, true), localActivityManager)) + " Apps Killed", 0).show();
			Log.e("ATK", "Screen off kill end");
		}

	}
}