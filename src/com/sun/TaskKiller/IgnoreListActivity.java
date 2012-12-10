package com.sun.TaskKiller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.rechild.advancedtaskkillerpro.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class IgnoreListActivity extends Activity
implements AdapterView.OnItemClickListener{
	private static SharedPreferences.Editor mIgnoredAppEditor;
	private static SharedPreferences mIgnoredAppSettings;
	private TaskListAdapters.ProcessListAdapter mAdapter;
	private ArrayList<ProcessDetailInfo> mDetailList;
	private PackageManager mPackageManager;

	private void SetIgnore(String paramString){
		try{
			mPackageManager.getPackageInfo(paramString, PackageManager.GET_UNINSTALLED_PACKAGES);
			if (!mIgnoredAppSettings.contains(paramString))
				mIgnoredAppEditor.putBoolean(paramString, true);
			return;
		}
		catch (PackageManager.NameNotFoundException localNameNotFoundException){
		}
	}

	private void buildRecommendedIgnoreList(){
		SetIgnore("com.android.inputmethod.latin");
		SetIgnore("com.android.inputmethod.pinyin");
		SetIgnore("com.android.alarmclock");
		SetIgnore("com.android.providers.media");
		SetIgnore("com.android.mms");
		SetIgnore("com.android.deskclock");
		SetIgnore("com.android.calendar");
		SetIgnore("com.android.voicedialer");
		SetIgnore("android.process.media");
		SetIgnore("com.android.providers.calendar");
		SetIgnore("com.android.clock");
		SetIgnore("com.android.providers.telephony");
		SetIgnore("com.android.heroled");
		SetIgnore("com.android.music");
		SetIgnore("com.android.vending");
		SetIgnore("com.android.wallpaper");
		SetIgnore("com.android.bluetooth");
		SetIgnore("com.google.android.inputmethod.pinyin");
		SetIgnore("com.google.android.providers.gmail");
		SetIgnore("com.google.android.apps.gtalkservice");
		SetIgnore("com.google.android.googleapps");
		SetIgnore("com.google.process.gapps");
		SetIgnore("com.google.android.talk");
		SetIgnore("com.google.android.gm");
		SetIgnore("com.google.android.apps.uploader");
		SetIgnore("com.google.android.apps.maps:FriendService");
		SetIgnore("com.htc.AddProgramWidget");
		SetIgnore("com.htc.android.worldclock");
		SetIgnore("com.htc.photo.widgets");
		SetIgnore("com.htc.music");
		SetIgnore("com.htc.android.mail");
		SetIgnore("com.htc.elroy.Weather");
		SetIgnore("com.htc.calendar");
		SetIgnore("com.htc.htctwitter");
		SetIgnore("com.htc.socialnetwork.accountmanager");
		SetIgnore("com.motorola.widgetapp.weather");
		SetIgnore("com.motorola.android.audioeffect");
		SetIgnore("com.motorola.widget.apncontrol");
		SetIgnore("com.motorola.thumbnailservice");
		SetIgnore("com.motorola.usb");
		SetIgnore("com.motorola.atcmd");
		SetIgnore("com.motorola.android.motophoneportal.androidui");
		SetIgnore("com.motorola.android.vvm");
		SetIgnore("com.timsu.astrid");
		SetIgnore("com.weather.Weather");
		SetIgnore("jp.aplix.midp");
		SetIgnore("jp.aplix.midp.factory");
		SetIgnore("com.svox.pico");
		SetIgnore("com.tmobile.myfaves");
		SetIgnore("com.mclaughlin.HeroLED");
		SetIgnore("com.motorola.blur.contacts");
		mIgnoredAppEditor.commit();
	}

	private void clearIgnoreList(){
		SharedPreferences.Editor localEditor = getSharedPreferences("IgnoredPackage", 0).edit();
		localEditor.clear();
		localEditor.commit();
		Toast.makeText(this, R.string.ClearIgnoreMessage, 0).show();
	}

	private void refresh(){
		this.mDetailList = new ArrayList();
		Iterator localIterator = mIgnoredAppSettings.getAll().entrySet().iterator();
		while (true){
			if (!localIterator.hasNext()){
				mAdapter = new TaskListAdapters.ProcessListAdapter(this, mDetailList);
				mAdapter.setCheckBoxEnable(false);
				ListView localListView = (ListView)findViewById(R.id.lvIgnoreList);
				localListView.setOnItemClickListener(this);
				localListView.setAdapter(mAdapter);
				return;
			}
			Map.Entry localEntry = (Map.Entry)localIterator.next();
			try{
				ProcessDetailInfo localProcessDetailInfo = new ProcessDetailInfo(this, (String)localEntry.getKey());
				mDetailList.add(localProcessDetailInfo);
			}
			catch (Exception localException){
				localException.printStackTrace();
			}
		}
	}

	private void validateIgnoreList(){
		Iterator localIterator = mIgnoredAppSettings.getAll().entrySet().iterator();
		while (true){
			if (!localIterator.hasNext()){
				mIgnoredAppEditor.commit();
				return;
			}
			String str = (String)((Map.Entry)localIterator.next()).getKey();
			try{
				this.mPackageManager.getPackageInfo(str, PackageManager.GET_UNINSTALLED_PACKAGES);
			}
			catch (PackageManager.NameNotFoundException localNameNotFoundException){
				mIgnoredAppEditor.remove(str);
			}
		}
	}

	public void onCreate(Bundle paramBundle){
		super.onCreate(paramBundle);
		mIgnoredAppSettings = getSharedPreferences("IgnoredPackage", 0);
		mIgnoredAppEditor = mIgnoredAppSettings.edit();
		setContentView(R.layout.ignorelist);
		((Button)findViewById(R.id.btnClearAll)).setOnClickListener(new View.OnClickListener(){
			public void onClick(View paramView){
				IgnoreListActivity.this.clearIgnoreList();
				IgnoreListActivity.this.refresh();
			}
		});
		((Button)findViewById(R.id.btnBuildIgnoreList)).setOnClickListener(new View.OnClickListener(){
			public void onClick(View paramView){
				IgnoreListActivity.this.buildRecommendedIgnoreList();
				IgnoreListActivity.this.refresh();
			}
		});
		mPackageManager = getPackageManager();
		refresh();
	}

	public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong){
		TaskListAdapters.ListViewItem localListViewItem = (TaskListAdapters.ListViewItem)paramView.getTag();
		mIgnoredAppEditor.remove(localListViewItem.detailProcess.getProcessName());
		mIgnoredAppEditor.commit();
		refresh();
	}
}
