package com.sun.TaskKiller;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import java.text.DateFormat;

public class Setting{
	public static final int ACTION_DETAIL = 4;
	public static final int ACTION_IGNORE = 3;
	public static final int ACTION_KILL = 0;
	public static final int ACTION_MENU = 5;
	public static final int ACTION_SELECT = 1;
	public static final int ACTION_SWITCH = 2;
	public static long AUTO_KILL_FREQUENCY = 0L;
	public static int AUTO_KILL_LEVEL = 0;
	public static final int AUTO_KILL_LEVEL_AGGRESSIVE = 2;
	public static final int AUTO_KILL_LEVEL_CRAZY = 3;
	public static final int AUTO_KILL_LEVEL_DISABLE = 0;
	public static final int AUTO_KILL_LEVEL_SAFE = 1;
	public static int CLICK_ACTION = 0;
	private static SharedPreferences.Editor Editor;
	public static boolean IGNORE_SERVICE_FRONT_APP = false;
	public static boolean INCLUDE_AUTOKILL_FEATURE = false;
	public static boolean IS_AUTOSTART_ENABLE = false;
	public static boolean IS_BUTTON_AT_TOP = false;
	public static boolean IS_FROYO_OR_LATER = false;
	public static boolean IS_LOG_ENABLE = false;
	public static boolean IS_NOTIFICATION_ENABLE = false;
	public static int ITEM_HEIGHT = 0;
	public static final String KEY_AUTOSTART = "IsAutoStartEnabled";
	public static final String KEY_AUTO_KILL_FREQUENCY = "AutoKillFrequecyValue";
	public static final String KEY_AUTO_KILL_LEVEL = "AutoKillLevelValue";
	public static final String KEY_CLICK_ACTION = "ClickActionValue";
	public static final String KEY_IGNORE_SERVICE_FRONT_APP = "IgnoreServiceFrontApp";
	public static final String KEY_IS_BUTTON_AT_TOP = "IsButtonAtTop";
	public static final String KEY_IS_FROYO_OR_LATER = "IsFroyoOrLater";
	public static final String KEY_ITEM_HEIGHT = "ItemHeight";
	public static final String KEY_LONG_PRESS_ACTION = "LongPressActionValue";
	public static final String KEY_NOTIFICATION = "IsNotificationEnabled";
	public static final String KEY_PREFS_NAME = "AdvTaskKillerSettings";
	public static final String KEY_SECURITY_LEVEL = "SecurityLevel";
	public static final String KEY_VERSION_CODE = "VersionCode";
	public static int LONG_PRESS_ACTION = 0;
	private static final String LR = "\r\n";
	public static final long ONE_HOUR = 3600000L;
	public static int SECURITY_LEVEL = 0;
	public static final int SECURITY_LEVEL_HIGH = 0;
	public static final int SECURITY_LEVEL_LOW = 10;
	public static final int SECURITY_LEVEL_MEDIUM = 5;
	public static SharedPreferences Settings;
	public static int VERSION_CODE;

	static{
		INCLUDE_AUTOKILL_FEATURE = true;
		IS_NOTIFICATION_ENABLE = true;
		IS_AUTOSTART_ENABLE = true;
		IS_BUTTON_AT_TOP = true;
		ITEM_HEIGHT = 36;
		CLICK_ACTION = 1;
		LONG_PRESS_ACTION = 5;
		AUTO_KILL_LEVEL = 0;
		AUTO_KILL_FREQUENCY = 3600000L;
		SECURITY_LEVEL = 0;
		VERSION_CODE = 0;
		IGNORE_SERVICE_FRONT_APP = false;
		IS_FROYO_OR_LATER = false;
	}

	public Setting(SharedPreferences paramSharedPreferences, Context paramContext){
		restoreSettings(paramContext);
	}

	public static String GetAllValues(){
		return new StringBuilder("AutoKillFrequecyValue ").append(String.valueOf(AUTO_KILL_FREQUENCY))
				.append("\r\n").append("AutoKillLevelValue ")
				.append(String.valueOf(AUTO_KILL_LEVEL)).append("\r\n")
				.append("IsNotificationEnabled ").append(String.valueOf(IS_NOTIFICATION_ENABLE))
				.append("\r\n").append("SecurityLevel ").append(String.valueOf(SECURITY_LEVEL))
				.append("\r\n").append("LongPressActionValue ")
				.append(String.valueOf(LONG_PRESS_ACTION)).append("\r\n").append("ClickActionValue ")
				.append(String.valueOf(CLICK_ACTION)).append("\r\n").append("ItemHeight ")
				.append(String.valueOf(ITEM_HEIGHT)).append("\r\n").append("IsAutoStartEnabled ")
				.append(String.valueOf(IS_AUTOSTART_ENABLE)).append("\r\n").append("VersionCode ")
				.append(String.valueOf(VERSION_CODE)).append("\r\n").append("IsFroyoOrLater ")
				.append(String.valueOf(IS_FROYO_OR_LATER)).append("\r\n").toString() + "IgnoreServiceFrontApp " +
				String.valueOf(IGNORE_SERVICE_FRONT_APP) + "\r\n";
	}

	public static String getAutoKillInfo(){
		String str;
		if (AUTO_KILL_LEVEL == 0){
			str = "";
		}else{
			if (AUTO_KILL_FREQUENCY != 0L)
				str = "Auto-Kill: " + DateFormat.getTimeInstance(3).format(CommonLibrary.NextRun);
			else
				str = "Auto-Kill: " + "ON";
		}
		return str;
	}

	public static void restoreSettings(Context paramContext){
		SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(paramContext);
		if (localSharedPreferences.contains("IsNotificationEnabled"))
			IS_NOTIFICATION_ENABLE = localSharedPreferences.getBoolean("IsNotificationEnabled", false);
		if (localSharedPreferences.contains("IsAutoStartEnabled"))
			IS_AUTOSTART_ENABLE = localSharedPreferences.getBoolean("IsAutoStartEnabled", false);
		if (localSharedPreferences.contains("ClickActionValue"))
			CLICK_ACTION = Integer.valueOf(localSharedPreferences.getString("ClickActionValue", String.valueOf(1))).intValue();
		if (localSharedPreferences.contains("LongPressActionValue"))
			LONG_PRESS_ACTION = Integer.valueOf(localSharedPreferences.getString("LongPressActionValue", String.valueOf(5))).intValue();
		if (localSharedPreferences.contains("AutoKillLevelValue"))
			AUTO_KILL_LEVEL = Integer.valueOf(localSharedPreferences.getString("AutoKillLevelValue", String.valueOf(0))).intValue();
		if (localSharedPreferences.contains("AutoKillFrequecyValue"))
			AUTO_KILL_FREQUENCY = Long.valueOf(localSharedPreferences.getString("AutoKillFrequecyValue", String.valueOf(AUTO_KILL_FREQUENCY))).longValue();
		if (localSharedPreferences.contains("ItemHeight"))
			ITEM_HEIGHT = localSharedPreferences.getInt("ItemHeight", ITEM_HEIGHT);
		if (localSharedPreferences.contains("SecurityLevel"))
			SECURITY_LEVEL = Integer.valueOf(localSharedPreferences.getString("SecurityLevel", String.valueOf(SECURITY_LEVEL))).intValue();
		if (localSharedPreferences.contains("IsButtonAtTop"))
			IS_BUTTON_AT_TOP = localSharedPreferences.getBoolean("IsButtonAtTop", true);
		if (localSharedPreferences.contains("VersionCode"))
			VERSION_CODE = localSharedPreferences.getInt("VersionCode", 0);
		if (localSharedPreferences.contains("IgnoreServiceFrontApp"))
			IGNORE_SERVICE_FRONT_APP = localSharedPreferences.getBoolean("IgnoreServiceFrontApp", false);
		if (localSharedPreferences.contains("IsFroyoOrLater"))
			IS_FROYO_OR_LATER = localSharedPreferences.getBoolean("IsFroyoOrLater", false);
		if (Editor != null)
			return;
		Editor = localSharedPreferences.edit();
	}

	public static void setAutoKillFrequency(long paramLong){
		AUTO_KILL_FREQUENCY = paramLong;
		Editor.putLong("AutoKillFrequecyValue", paramLong);
		Editor.commit();
	}

	public static void setAutoStart(boolean paramBoolean){
		IS_AUTOSTART_ENABLE = paramBoolean;
		Editor.putBoolean("IsAutoStartEnabled", paramBoolean);
		Editor.commit();
	}

	public static void setClickAction(int paramInt){
		CLICK_ACTION = paramInt;
		Editor.putInt("ClickActionValue", paramInt);
		Editor.commit();
	}

	public static void setIgnoreServiceFrontApp(boolean paramBoolean){
		IGNORE_SERVICE_FRONT_APP = paramBoolean;
		Editor.putBoolean("IgnoreServiceFrontApp", paramBoolean);
		Editor.commit();
	}

	public static void setIsFroyoOrLater(boolean paramBoolean){
		IS_FROYO_OR_LATER = paramBoolean;
		Editor.putBoolean("IsFroyoOrLater", paramBoolean);
		Editor.commit();
	}

	public static void setKillLevel(int paramInt){
		AUTO_KILL_LEVEL = paramInt;
		Editor.putInt("AutoKillLevelValue", paramInt);
		Editor.commit();
	}

	public static void setLongPressAction(int paramInt){
		LONG_PRESS_ACTION = paramInt;
		Editor.putInt("LongPressActionValue", paramInt);
		Editor.commit();
	}

	public static void setNotification(boolean paramBoolean){
		IS_NOTIFICATION_ENABLE = paramBoolean;
		Editor.putBoolean("IsNotificationEnabled", paramBoolean);
		Editor.commit();
	}

	public static void setSecurityLevel(int paramInt){
		SECURITY_LEVEL = paramInt;
		Editor.putInt("SecurityLevel", paramInt);
		Editor.commit();
	}

	public static void setVersionCode(int paramInt){
		VERSION_CODE = paramInt;
		Editor.putInt("VersionCode", paramInt);
		Editor.commit();
	}
}
