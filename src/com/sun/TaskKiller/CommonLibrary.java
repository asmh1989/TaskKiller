package com.sun.TaskKiller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Process;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class CommonLibrary{
	public static List<Integer> IgnoreOrUncheckUid;
	public static Date NextRun;
	private static Integer SDK;
	public static List<Integer> SystemUid;
	private static final String TAG = "ATK";
	private static boolean mCanGetUid = true;

	static{
		try{
			ActivityManager.RunningAppProcessInfo.class.getDeclaredField("uid");
			SDK = null;
		}
		catch (SecurityException localSecurityException){
			mCanGetUid = false;
		}
		catch (NoSuchFieldException localNoSuchFieldException){
			mCanGetUid = false;
		}
	}

	public static int GetAppUid(ActivityManager.RunningAppProcessInfo paramRunningAppProcessInfo){
//		android.util.Log.d("SUNMEM", "GetAppUid: mCanGetUid = "+mCanGetUid);
		int j, pid = 0;
		if (!mCanGetUid)
			j = pid;
		else{
			try{
				Field localField = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("uid");
				localField.setAccessible(true);
				j = localField.getInt(paramRunningAppProcessInfo);
			}catch (NoSuchFieldException localNoSuchFieldException){
				j = pid;
			}catch (IllegalAccessException localIllegalAccessException){
				j = pid;
			}
		}
		return j;
	}

	public static List<Integer> GetFrontAppUid(List<ActivityManager.RunningAppProcessInfo> paramList){
		ArrayList localArrayList = new ArrayList();
		Iterator localIterator = paramList.iterator();
		while (!localIterator.hasNext()){
			ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = (ActivityManager.RunningAppProcessInfo)localIterator.next();
			if (localRunningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
				localArrayList.add(Integer.valueOf(GetAppUid(localRunningAppProcessInfo)));
		}
		return localArrayList;
	}

	public static List<Integer> GetIgnoreOrUncheckUid(List<ActivityManager.RunningAppProcessInfo> paramList){
		ArrayList localArrayList = new ArrayList();
		if ((ProcessDetailInfo.mIgnoredAppSettings == null) && (ProcessDetailInfo.mSelectedAppSettings == null))
			return localArrayList;
		Iterator localIterator = paramList.iterator();
		while (localIterator.hasNext()){
			ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = (ActivityManager.RunningAppProcessInfo)localIterator.next();
			for (String str : localRunningAppProcessInfo.pkgList){
				if ((ProcessDetailInfo.mIgnoredAppSettings != null) && (ProcessDetailInfo.mIgnoredAppSettings.contains(str)))
					localArrayList.add(Integer.valueOf(GetAppUid(localRunningAppProcessInfo)));
				if ((ProcessDetailInfo.mSelectedAppSettings != null) && (ProcessDetailInfo.mSelectedAppSettings.contains(str)))
					localArrayList.add(Integer.valueOf(GetAppUid(localRunningAppProcessInfo)));
			}
		}
		return localArrayList;
	}

	public static ArrayList<ProcessDetailInfo> GetRunningProcess(Context paramContext, ActivityManager paramActivityManager){
		return GetRunningProcess(paramContext, paramActivityManager, Integer.MIN_VALUE);
	}

	public static ArrayList<ProcessDetailInfo> GetRunningProcess(Context paramContext, ActivityManager paramActivityManager, int paramInt){
		return GetRunningProcess(paramContext, paramActivityManager, paramInt, Setting.SECURITY_LEVEL, false);
	}

	public static ArrayList<ProcessDetailInfo> GetRunningProcess(Context paramContext, ActivityManager paramActivityManager,
			int paramInt1, int paramInt2, boolean paramBoolean){
		List localList1 = paramActivityManager.getRunningAppProcesses();
		ArrayList localArrayList = new ArrayList();
		List localList2 = null;
		if (paramBoolean)
			localList2 = GetFrontAppUid(localList1);
		SystemUid = GetSystemAppUid(localList1);
		Iterator localIterator3;
		if (paramInt2 == Setting.SECURITY_LEVEL_MEDIUM){
			localIterator3 = localList1.iterator();
			while (localIterator3.hasNext()){
				ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo3 = (ActivityManager.RunningAppProcessInfo)localIterator3.next();
				if ((localRunningAppProcessInfo3 == null) || (localRunningAppProcessInfo3.processName == null) ||
						(localRunningAppProcessInfo3.processName.startsWith("com.android.inputmethod")) ||
						(localRunningAppProcessInfo3.processName.equalsIgnoreCase("system")) ||
						(localRunningAppProcessInfo3.processName.equalsIgnoreCase("com.android.phone")) ||
						(localRunningAppProcessInfo3.processName.equalsIgnoreCase("android.process.acore")) ||
						(localRunningAppProcessInfo3.importance < paramInt1) ||
						((IsFroyoOrLater()) && (Setting.IGNORE_SERVICE_FRONT_APP) &&
								(localRunningAppProcessInfo3.importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) && 
								(!localRunningAppProcessInfo3.processName.equals(paramContext.getPackageName()))) || 
								((paramBoolean) && (IsFrontApp(localList2, localRunningAppProcessInfo3))))
					break;
				try{
					if ((localRunningAppProcessInfo3.pkgList == null) || (localRunningAppProcessInfo3.pkgList.length != 1) ||
							(!localRunningAppProcessInfo3.processName.equals(localRunningAppProcessInfo3.pkgList[0])))
						break;
					ProcessDetailInfo localProcessDetailInfo3 = new ProcessDetailInfo(paramContext, localRunningAppProcessInfo3.processName);
					localProcessDetailInfo3.Importance = localRunningAppProcessInfo3.importance;
					if ((!localProcessDetailInfo3.isApplication()) || (IsSystemApp(localRunningAppProcessInfo3)) ||
							(!localProcessDetailInfo3.isGoodProcess()) || (localProcessDetailInfo3.getIgnored()))
						break;
					localArrayList.add(localProcessDetailInfo3);
				}
				catch (Exception localException3){
					localException3.printStackTrace();
				}
			}
		}else if (paramInt2 == Setting.SECURITY_LEVEL_HIGH){
			Iterator localIterator2 = localList1.iterator();
			while (localIterator2.hasNext()){
				ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo2 = (ActivityManager.RunningAppProcessInfo)localIterator2.next();
				if ((localRunningAppProcessInfo2 != null) && (localRunningAppProcessInfo2.processName != null) && 
						(!localRunningAppProcessInfo2.processName.startsWith("com.android.inputmethod")) && 
						(!localRunningAppProcessInfo2.processName.equalsIgnoreCase("system")) && 
						(!localRunningAppProcessInfo2.processName.equalsIgnoreCase("com.android.phone")) && 
						(!localRunningAppProcessInfo2.processName.equalsIgnoreCase("android.process.acore")) && 
						(!localRunningAppProcessInfo2.processName.equalsIgnoreCase("com.htc.android.mail")) && 
						(!localRunningAppProcessInfo2.processName.equalsIgnoreCase("com.motorola.android.vvm")) && 
						(!localRunningAppProcessInfo2.processName.equalsIgnoreCase("com.android.alarmclock")) && 
						(localRunningAppProcessInfo2.importance >= paramInt1) && 
						((!IsFroyoOrLater()) || (!Setting.IGNORE_SERVICE_FRONT_APP) || 
								(localRunningAppProcessInfo2.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) || 
								(localRunningAppProcessInfo2.processName.equals(paramContext.getPackageName()))) && 
								((!paramBoolean) || (!IsFrontApp(localList2, localRunningAppProcessInfo2))))
					try{
						if ((localRunningAppProcessInfo2.pkgList != null) && (localRunningAppProcessInfo2.pkgList.length == 1) && 
								(localRunningAppProcessInfo2.processName.equals(localRunningAppProcessInfo2.pkgList[0]))){
							ProcessDetailInfo localProcessDetailInfo2 = new ProcessDetailInfo(paramContext, 
									localRunningAppProcessInfo2.processName);
							localProcessDetailInfo2.Importance = localRunningAppProcessInfo2.importance;
							if ((localProcessDetailInfo2.isApplication()) && (!localProcessDetailInfo2.isSystemApp()) && 
									(!IsSystemApp(localRunningAppProcessInfo2)) && (localProcessDetailInfo2.isGoodProcess()) && 
									(!localProcessDetailInfo2.getIgnored())){
								Log.d("SUNMEM", "GetRunningProcess: add processName = "+localRunningAppProcessInfo2.processName);
								localArrayList.add(localProcessDetailInfo2);
							}
						}
					}
				catch (Exception localException2){
					localException2.printStackTrace();
				}
			}
		}else if (paramInt2 == Setting.SECURITY_LEVEL_LOW){
			Iterator localIterator1 = localList1.iterator();
			while (localIterator1.hasNext()){
				ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo1 = (ActivityManager.RunningAppProcessInfo)localIterator1.next();
				if ((localRunningAppProcessInfo1 != null) && (localRunningAppProcessInfo1.processName != null) && 
						(!localRunningAppProcessInfo1.processName.equalsIgnoreCase("system")) && 
						(!localRunningAppProcessInfo1.processName.equalsIgnoreCase("com.android.phone")) && 
						(!localRunningAppProcessInfo1.processName.equalsIgnoreCase("android.process.acore")) && 
						(localRunningAppProcessInfo1.importance >= paramInt1) && 
						((!IsFroyoOrLater()) || (!Setting.IGNORE_SERVICE_FRONT_APP) || 
								(localRunningAppProcessInfo1.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) || 
								(localRunningAppProcessInfo1.processName.equals(paramContext.getPackageName()))) && 
								((!paramBoolean) || (!IsFrontApp(localList2, localRunningAppProcessInfo1))) && 
								(!IsSystemApp(localRunningAppProcessInfo1)))
					try{
						ProcessDetailInfo localProcessDetailInfo1 = new ProcessDetailInfo(paramContext, localRunningAppProcessInfo1.pkgList[0]);
						localProcessDetailInfo1.Importance = localRunningAppProcessInfo1.importance;
						if (!localProcessDetailInfo1.getIgnored())
							localArrayList.add(localProcessDetailInfo1);
					}
				catch (Exception localException1){
					localException1.printStackTrace();
				}
			}
		}
		return localArrayList;
	}

	public static ArrayList<ProcessDetailInfo> GetRunningProcess(Context paramContext, 
			ActivityManager paramActivityManager, int paramInt, boolean paramBoolean){
		return GetRunningProcess(paramContext, paramActivityManager, paramInt, Setting.SECURITY_LEVEL, paramBoolean);
	}

	public static ArrayList<ProcessDetailInfo> GetRunningProcess(Context paramContext, 
			ActivityManager paramActivityManager, boolean paramBoolean){
		return GetRunningProcess(paramContext, paramActivityManager, Integer.MIN_VALUE, paramBoolean);
	}

	public static List<Integer> GetSystemAppUid(List<ActivityManager.RunningAppProcessInfo> paramList){
		ArrayList localArrayList = new ArrayList();
		Iterator localIterator = paramList.iterator();
		while (localIterator.hasNext()){
			ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = (ActivityManager.RunningAppProcessInfo)localIterator.next();
			if (IsSystemProcessName(localRunningAppProcessInfo.processName))
				localArrayList.add(Integer.valueOf(GetAppUid(localRunningAppProcessInfo)));
		}
		Log.d("SUNMEM", "GetSystemAppUid size = "+localArrayList.size());
		return localArrayList;
	}

	public static void InvokeActivity(Activity paramActivity, Class<?> paramClass){
		Intent localIntent = new Intent();
		localIntent.setClass(paramActivity, paramClass);
		localIntent.addCategory("android.intent.category.DEFAULT");
		paramActivity.startActivity(localIntent);
	}

	public static boolean IsFrontApp(List<Integer> paramList, ActivityManager.RunningAppProcessInfo paramRunningAppProcessInfo){
		int i = GetAppUid(paramRunningAppProcessInfo);
		Iterator localIterator = paramList.iterator();
		if (!localIterator.hasNext());
		if (((Integer)localIterator.next()).intValue() != i)
			return true;
		return false;
	}

	public static boolean IsFroyo(){
		if (SDK == null)
			SDK = Integer.valueOf(Integer.parseInt(Build.VERSION.SDK));
		if (SDK.intValue() == 8)
			return true;
		return false;
	}

	public static boolean IsFroyoOrLater(){
		if (SDK == null)
			SDK = Integer.valueOf(Integer.parseInt(Build.VERSION.SDK));
		if (SDK.intValue() >= 8)
			return true;
		return false;
	}

	public static boolean IsGingerbreadOrlater(){
		if (SDK == null)
			SDK = Integer.valueOf(Integer.parseInt(Build.VERSION.SDK));
		if (SDK.intValue() >= 9)
			return true;
		return false;
	}

	public static boolean IsHoneyOrlater(){
		if (SDK == null)
			SDK = Integer.valueOf(Integer.parseInt(Build.VERSION.SDK));
		if (SDK.intValue() >= 11)
			return true;
		return  false;
	}

	public static boolean IsSameUidWithIgnoreOrUncheck(ActivityManager.RunningAppProcessInfo paramRunningAppProcessInfo){
		int i = GetAppUid(paramRunningAppProcessInfo);
		Iterator localIterator = IgnoreOrUncheckUid.iterator();
		if (!localIterator.hasNext());
		if ((((Integer)localIterator.next()).intValue() != i) || 
				(ProcessDetailInfo.mIgnoredAppSettings.contains(paramRunningAppProcessInfo.processName)) || 
				(ProcessDetailInfo.mSelectedAppSettings.contains(paramRunningAppProcessInfo.processName)))
			return true;
		return false;
	}

	public static boolean IsSystemApp(ActivityManager.RunningAppProcessInfo paramRunningAppProcessInfo){
		int i = GetAppUid(paramRunningAppProcessInfo);
		Iterator localIterator = SystemUid.iterator();
		while (localIterator.hasNext()){
			if (((Integer)localIterator.next()).intValue() == i)
				return true;
		}
		return false;
	}

	public static boolean IsSystemProcessName(String paramString){
		if ((paramString.equalsIgnoreCase("system")) || (paramString.equalsIgnoreCase("com.android.phone")) || 
				(paramString.equalsIgnoreCase("android.process.acore")) || (paramString.equalsIgnoreCase("android.process.media")) ||
				(paramString.equalsIgnoreCase("com.android.bluetooth")))
			return true;
		return false;
	}

	public static void KillATK(ActivityManager paramActivityManager, Context paramContext){
		if (IsFroyoOrLater()){
			AutoStartReceiver.ClearNotification(paramContext);
			Process.killProcess(Process.myUid());
			System.exit(0);
		}else{
			paramActivityManager.restartPackage(paramContext.getPackageName());
		}
	}


	@SuppressLint("NewApi")
	public static void KillApp(Context paramContext, ActivityManager paramActivityManager, String paramString){
		if(IsFroyoOrLater()){
			paramActivityManager.killBackgroundProcesses(paramString);
		}else{
			paramActivityManager.restartPackage(paramString);
		}
	}

	public static int KillProcess(Context paramContext, List<ProcessDetailInfo> paramList, ActivityManager paramActivityManager){
		return KillProcess(paramContext, paramList, paramActivityManager, true);
	}

	public static int KillProcess(Context paramContext, List<ProcessDetailInfo> paramList,
			ActivityManager paramActivityManager, boolean paramBoolean){
		boolean bool = false;
		int i = 0;
		//		Log.BeginTrasaction(paramContext);
		Iterator localIterator = paramList.iterator();
		while (localIterator.hasNext()){
			ProcessDetailInfo localProcessDetailInfo = (ProcessDetailInfo)localIterator.next();
			String str = localProcessDetailInfo.getPackageName();
			if (str.equals(paramContext.getPackageName())){
				bool = localProcessDetailInfo.getSelected();
			}else{
				if (localProcessDetailInfo.getSelected()){
					KillApp(paramContext, paramActivityManager, str);
					i++;
//					Log.I(paramContext, str);
				}
			}

			if (bool){
				if (!paramBoolean)
					AutoStartReceiver.ClearNotification(paramContext);
				KillATK(paramActivityManager, paramContext);
				i++;
			}
			//				Log.EndTrasaction(paramContext);
		}
		return i;
	}

	public static String MemoryToString(long paramLong){
		return String.valueOf(paramLong / 1024L / 1024L) + "M";
	}

	public static void ScheduleAutoKill(Context paramContext, boolean paramBoolean, long paramLong){
		PendingIntent localPendingIntent = PendingIntent.getBroadcast(paramContext, 0, new Intent(paramContext, AlarmReceiver.class), 0);
		AlarmManager localAlarmManager = (AlarmManager)paramContext.getSystemService("alarm");
		if (paramBoolean){
			localAlarmManager.cancel(localPendingIntent);
			NextRun = null;
		}else{
			if (paramLong == 0L){
				paramContext.startService(new Intent(paramContext, BackService.class));
			}
			else{
				localAlarmManager.setRepeating(1, paramLong + System.currentTimeMillis(), paramLong, localPendingIntent);
				NextRun = new Date(paramLong + System.currentTimeMillis());
			}
		}
	}

	public static NotificationManager buildNotification(Context paramContext, CharSequence paramCharSequence1,
			CharSequence paramCharSequence2, int paramInt1, int paramInt2){
		return buildNotification(paramContext, paramContext.getClass(), paramCharSequence1, paramCharSequence2, paramInt1, paramInt2);
	}

	public static NotificationManager buildNotification(Context paramContext, Class paramClass, 
			CharSequence paramCharSequence1, CharSequence paramCharSequence2, int paramInt1, int paramInt2){
		NotificationManager localNotificationManager = (NotificationManager)paramContext.getSystemService("notification");
		Notification localNotification = new Notification();
		localNotification.icon = paramInt1;
		localNotification.flags = 2;
		localNotification.setLatestEventInfo(paramContext, paramCharSequence1, paramCharSequence2, makeMoodIntent(paramContext, paramClass));
		localNotificationManager.notify(paramInt2, localNotification);
		return localNotificationManager;
	}

	public static long getAvaliableMemory(ActivityManager paramActivityManager){
		long l = 0L;
		try{
			ActivityManager.MemoryInfo localMemoryInfo = new ActivityManager.MemoryInfo();
			paramActivityManager.getMemoryInfo(localMemoryInfo);
			l = localMemoryInfo.availMem;
		}
		catch (Exception localException){
			localException.printStackTrace();
		}
		return l;
	}

	public static String getStringFromAsset(Context paramContext, String paramString){
		new String();
		try{
			InputStream localInputStream = paramContext.getAssets().open(paramString);
			byte[] arrayOfByte = new byte[localInputStream.available()];
			localInputStream.read(arrayOfByte);
			localInputStream.close();
			String str = new String(arrayOfByte);
			return str;
		}
		catch (IOException localIOException){
			throw new RuntimeException(localIOException);
		}
	}

	private static PendingIntent makeMoodIntent(Context paramContext, Class paramClass){
		Intent localIntent = new Intent(paramContext, paramClass);
		localIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		localIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return PendingIntent.getActivity(paramContext, 1, localIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
	}
}