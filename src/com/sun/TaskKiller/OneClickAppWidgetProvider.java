package com.sun.TaskKiller;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.rechild.advancedtaskkillerpro.R;

public class OneClickAppWidgetProvider extends AppWidgetProvider{
	static final ComponentName THIS_APPWIDGET = new ComponentName("com.rechild.taskkillerfull", 
			"com.rechild.taskkillerfull.OneClickAppWidgetProvider");
	private static OneClickAppWidgetProvider sInstance;

	private void defaultAppWidget(Context paramContext, int[] paramArrayOfInt){
		paramContext.getResources();
		RemoteViews localRemoteViews = new RemoteViews(paramContext.getPackageName(), R.layout.appwidget);
		linkButtons(paramContext, localRemoteViews);
		pushUpdate(paramContext, paramArrayOfInt, localRemoteViews);
	}

	static OneClickAppWidgetProvider getInstance(){
		if (sInstance == null)
			sInstance = new OneClickAppWidgetProvider();
		OneClickAppWidgetProvider localOneClickAppWidgetProvider = sInstance;
		return localOneClickAppWidgetProvider;
	}

	private void linkButtons(Context paramContext, RemoteViews paramRemoteViews){
		ComponentName localComponentName = new ComponentName(paramContext, BackService.class);
		Intent localIntent = new Intent("com.rechild.advancedtaskkillerpro.action.killall");
		localIntent.setComponent(localComponentName);
		paramRemoteViews.setOnClickPendingIntent(R.id.ivWidgetIcon, PendingIntent.getService(paramContext, 0, localIntent, 0));
	}

	private void pushUpdate(Context paramContext, int[] paramArrayOfInt, RemoteViews paramRemoteViews){
		AppWidgetManager localAppWidgetManager = AppWidgetManager.getInstance(paramContext);
		if (paramArrayOfInt != null)
			localAppWidgetManager.updateAppWidget(paramArrayOfInt, paramRemoteViews);
		else
			localAppWidgetManager.updateAppWidget(THIS_APPWIDGET, paramRemoteViews);
	}

	public void onUpdate(Context paramContext, AppWidgetManager paramAppWidgetManager, int[] paramArrayOfInt){
		defaultAppWidget(paramContext, paramArrayOfInt);
	}
}