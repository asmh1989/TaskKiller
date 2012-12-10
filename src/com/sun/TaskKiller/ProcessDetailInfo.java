package com.sun.TaskKiller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ProcessDetailInfo{
	private static HashMap<String, ResolveInfo> AppsTable;
	public static final String IGNORE_PREFS_NAME = "IgnoredPackage";
	public static final String SELECT_PREFS_NAME = "CleanoidUnselectedPackage";
	static SharedPreferences.Editor mIgnoredAppEditor;
	public static SharedPreferences mIgnoredAppSettings;
	private static SharedPreferences.Editor mSelectedAppEditor;
	public static SharedPreferences mSelectedAppSettings;
	public int Importance;
	private ApplicationInfo mAppInfo = null;
	private String mLabel;
	private String mPackageName;
	private PackageInfo mPkgInfo = null;
	private PackageManager mPkgManager;
	private ResolveInfo mResolveInfo;
	private boolean isApplication = true;

	public ProcessDetailInfo(Context paramContext, String paramString) throws Exception{
		loadApps(paramContext);
		mResolveInfo = ((ResolveInfo)AppsTable.get(paramString));
		if ((mResolveInfo != null) && (mResolveInfo.activityInfo != null) && 
				(mResolveInfo.activityInfo.applicationInfo != null))
			mAppInfo = mResolveInfo.activityInfo.applicationInfo;
		else
			isApplication = false;
		mPackageName = paramString;
		if (mPkgManager == null)
			mPkgManager = paramContext.getApplicationContext().getPackageManager();
		if (mSelectedAppSettings == null)
			mSelectedAppSettings = paramContext.getSharedPreferences("CleanoidUnselectedPackage", 0);
		if (mSelectedAppEditor == null)
			mSelectedAppEditor = mSelectedAppSettings.edit();
		if (mIgnoredAppSettings == null)
			mIgnoredAppSettings = paramContext.getSharedPreferences("IgnoredPackage", 0);
		if (mIgnoredAppEditor == null)
			mIgnoredAppEditor = mIgnoredAppSettings.edit();
	}

	public static boolean IsPersistentApp(PackageInfo paramPackageInfo){
		boolean bool = false;
		if (paramPackageInfo == null)
			bool = false;
		else{
			if ((paramPackageInfo.applicationInfo != null) && 
					((ApplicationInfo.FLAG_PERSISTENT & paramPackageInfo.applicationInfo.flags) == ApplicationInfo.FLAG_PERSISTENT)){
				bool = true;
			}
			else{
				if (paramPackageInfo.activities == null)
					bool = false;
			}
			return bool;
		}
		ActivityInfo[] arrayOfActivityInfo = paramPackageInfo.activities;
		int i = arrayOfActivityInfo.length;
		for (int j = 0; ; j++){
			if (j >= i){
				bool = false;
				break;
			}
			ActivityInfo localActivityInfo = arrayOfActivityInfo[j];
			if ((localActivityInfo != null) && (localActivityInfo.applicationInfo != null) &&
					((ApplicationInfo.FLAG_PERSISTENT & localActivityInfo.applicationInfo.flags) == ApplicationInfo.FLAG_PERSISTENT)){
				bool = true;
				break;
			}
		}
		return bool;
	}

	public static boolean IsUnselectOrIgnore(String paramString){
		boolean bool;
		if (mSelectedAppSettings.getBoolean(paramString, false))
			bool = true;
		else{
			if (mIgnoredAppSettings.getBoolean(paramString, false))
				bool = true;
			else
				bool = false;
		}
		return bool;
	}

	public static void SetIgnored(boolean paramBoolean, Context paramContext, String paramString){
		if (mIgnoredAppSettings == null)
			mIgnoredAppSettings = paramContext.getSharedPreferences("IgnoredPackage", 0);
		if (mIgnoredAppEditor == null)
			mIgnoredAppEditor = mIgnoredAppSettings.edit();
		if (paramBoolean)
			mIgnoredAppEditor.putBoolean(paramString, true);
		else
			mIgnoredAppEditor.remove(paramString);
		mIgnoredAppEditor.commit();
	}

	private PackageInfo getPackageInfo(){
		if (mPkgInfo != null){
			try{
				mPkgInfo = mPkgManager.getPackageInfo(mAppInfo.packageName, ApplicationInfo.FLAG_SYSTEM);
				return mPkgInfo;
			}
			catch (PackageManager.NameNotFoundException localNameNotFoundException){
				Log.e("NameNotFoundException", localNameNotFoundException.toString());
			}
		}
		return mPkgInfo;
	}

	private static void loadApps(Context paramContext){
		Iterator localIterator = null ;
		if (AppsTable == null){
			AppsTable = new HashMap();
			Intent localIntent = new Intent("android.intent.action.MAIN", null);
			localIntent.addCategory("android.intent.category.LAUNCHER");
			localIterator = paramContext.getPackageManager().queryIntentActivities(localIntent, 0).iterator();
			while(localIterator.hasNext()){
				ResolveInfo localResolveInfo = (ResolveInfo)localIterator.next();
				if ((localResolveInfo != null) && (localResolveInfo.activityInfo != null) && (localResolveInfo.activityInfo.packageName != null)){
					AppsTable.put(localResolveInfo.activityInfo.processName, localResolveInfo);
//					Log.d("SUNMEM", "loadApps: add progressName = "+localResolveInfo.activityInfo.processName);
				}
			}
		}
	}

	public static void setSelected(boolean paramBoolean, Context paramContext, String paramString){
		if (mSelectedAppSettings == null)
			mSelectedAppSettings = paramContext.getSharedPreferences("CleanoidUnselectedPackage", 0);
		if (mSelectedAppEditor == null)
			mSelectedAppEditor = mSelectedAppSettings.edit();
		if ((paramBoolean) && (mSelectedAppSettings.contains(paramString)))
			mSelectedAppEditor.remove(paramString);
		else{
			if (!paramBoolean){
				Log.d("SUNMEM", "setSelected: put paramString = "+paramString);
				mSelectedAppEditor.putBoolean(paramString, true);
			}
		}
		mSelectedAppEditor.commit();
	}

	public String getBaseActivity(){
		if (mResolveInfo != null)
			return mResolveInfo.activityInfo.name;
		return getPackageInfo().activities[0].name;
	}

	public Drawable getIcon(){
		if (mAppInfo != null)
			return mAppInfo.loadIcon(mPkgManager);
		return null;
	}

	public boolean getIgnored(){
		return mIgnoredAppSettings.getBoolean(mPackageName, false);
	}

	public String getLabel(){
		try{
			if ((mPackageName != null) && (mPackageName.equals(mAppInfo.processName)))
				mLabel = mAppInfo.loadLabel(mPkgManager).toString();
			else
				mLabel = mAppInfo.processName;
			return mLabel;
		}catch (Exception localException){
			mLabel = mPackageName;
		}
		return mLabel;
	}

	public String getPackageName(){
		return mPackageName;
	}

	public String getProcessName(){
		return mPackageName;
	}

	public boolean getSelected(){
		Log.d("SUNMEM", "Selected = "+mSelectedAppSettings.getBoolean(mPackageName, false));
		return !mSelectedAppSettings.getBoolean(mPackageName, false);
	}

	public boolean isApplication(){
		return isApplication;
	}

	public boolean isGoodProcess(){
		return mAppInfo != null;
	}

	public boolean isSystemApp(){
		return (mAppInfo != null) && ((ApplicationInfo.FLAG_SYSTEM ^ mAppInfo.flags) == ApplicationInfo.FLAG_SYSTEM);
	}

	public void setIgnored(boolean paramBoolean){
		if (paramBoolean)
			mIgnoredAppEditor.putBoolean(mPackageName, true);
		else
			mIgnoredAppEditor.remove(mPackageName);
		mIgnoredAppEditor.commit();
	}

	public void setLabel(String paramString){
		mLabel = paramString;
	}

	public void setSelected(boolean paramBoolean){
		if ((paramBoolean) && (mSelectedAppSettings.contains(mPackageName))){
			mSelectedAppEditor.remove(mPackageName);
		}else{
			if (!paramBoolean){
				mSelectedAppEditor.putBoolean(mPackageName, true);
			}
		}
		mSelectedAppEditor.commit();
	}
}