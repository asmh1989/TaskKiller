package com.sun.TaskKiller;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rechild.advancedtaskkillerpro.R;

public class AutoStartReceiver extends BroadcastReceiver{
	private static NotificationManager mNotificationManager;

	public static void ClearNotification(Context paramContext){
		if (mNotificationManager != null){
			mNotificationManager.cancelAll();
			mNotificationManager = null;
		}
	}

	public static void RefreshNotification(Context paramContext){
		if (Setting.IS_NOTIFICATION_ENABLE){
			if (mNotificationManager == null){
				mNotificationManager = CommonLibrary.buildNotification(paramContext,
						AdvancedTaskKiller.class, "Open Advanced Task Killer Pro", "Menu->Settings to disable this.",
						R.drawable.notification, R.layout.status_bar_notifications);
			}
		}else{
			if (mNotificationManager != null){
				mNotificationManager.cancelAll();
				mNotificationManager = null;
			}
		}
	}

	public void onReceive(Context paramContext, Intent paramIntent){
		Setting.Settings = paramContext.getSharedPreferences("AdvTaskKillerSettings", 0);
		Setting.restoreSettings(paramContext);
		if (Setting.IS_AUTOSTART_ENABLE){
			Intent localIntent = new Intent(paramContext, AdvancedTaskKiller.class);
			localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			paramContext.startService(localIntent);
			if (Setting.IS_NOTIFICATION_ENABLE)
				RefreshNotification(paramContext);
			if ((Setting.AUTO_KILL_LEVEL > 0) && (CommonLibrary.NextRun == null)){
				CommonLibrary.ScheduleAutoKill(paramContext, false, Setting.AUTO_KILL_FREQUENCY);
			}
		}
	}
}
