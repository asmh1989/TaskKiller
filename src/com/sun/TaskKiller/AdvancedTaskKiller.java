package com.sun.TaskKiller;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rechild.advancedtaskkillerpro.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AdvancedTaskKiller extends Activity
implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener{
	private static final int DIALOG_BUG = 4;
	private static final int DIALOG_HELP = 3;
	private static final int DIALOG_IGNORE = 5;
	private static final int DIALOG_IGNORE_SERVICE_ALERT = 6;
	private static final int DIALOG_INFO = 2;
	private static final int DIALOG_LOADING = 1;
	private static final int DIALOG_NEW_VERSION_ALERT = 7;
	private static final String LR = "\r\n";
	public static final String NOTIFY_MESSAGE = "Menu->Settings to disable this.";
	public static final String NOTIFY_TITLE = "Open Advanced Task Killer Pro";
	private static final String TAG = "ATK";
	ActivityManager mActivityManager = null;
	private TaskListAdapters.ProcessListAdapter mAdapter;
	private ArrayList<ProcessDetailInfo> mDetailList;
	private String mLogResult;
	private AlertDialog mMenuDialog;
	private Handler mHandler;

	private void IgnoreSystemApp(){
		Iterator localIterator = getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES).iterator();
		while (true){
			if (!localIterator.hasNext())
				return;
			PackageInfo localPackageInfo = (PackageInfo)localIterator.next();
			if (((ApplicationInfo.FLAG_SYSTEM & localPackageInfo.applicationInfo.flags) != 1) ||
					(!ProcessDetailInfo.IsPersistentApp(localPackageInfo)) || (localPackageInfo.applicationInfo == null) ||
					(CommonLibrary.IsSystemProcessName(localPackageInfo.applicationInfo.processName)))
				continue;
			ProcessDetailInfo.SetIgnored(true, this, localPackageInfo.applicationInfo.processName);
		}
	}

	private void bindEvent(){
		findViewById(R.id.btn_task).setOnClickListener(new View.OnClickListener(){
			public void onClick(View paramView){
				android.util.Log.e("ATK", "Start manully kill!");
				AdvancedTaskKiller.this.killAllTasks();
			}
		});
//		findViewById(R.id.btnCheck).setOnClickListener(new View.OnClickListener(){
//			public void onClick(View paramView){
//				Intent localIntent = new Intent();
//				localIntent.setClass(AdvancedTaskKiller.this, HealthActivity.class);
//				AdvancedTaskKiller.this.startActivity(localIntent);
//			}
//		});
		ListView localListView = (ListView)findViewById(R.id.listbody);
		localListView.setOnItemLongClickListener(this);
		localListView.setOnItemClickListener(this);
	}

	private void detail(TaskListAdapters.ListViewItem paramListViewItem){
		Intent localIntent;
		try{
			localIntent = new Intent("android.intent.action.VIEW");
			if (CommonLibrary.IsGingerbreadOrlater()){
				localIntent.setClassName("com.android.settings", "com.android.settings.applications.InstalledAppDetails");
				localIntent.setData(Uri.fromParts("package", paramListViewItem.detailProcess.getPackageName(), null));
				startActivity(localIntent);
				return;
			}
		}
		catch (Exception localException){
			android.util.Log.e("ATK", localException.toString());
			Toast.makeText(this, paramListViewItem.detailProcess.getPackageName(), 0).show();
		}
		return;
	}

	private void doAction(TaskListAdapters.ListViewItem paramListViewItem, int paramInt){
		switch (paramInt){
		case Setting.ACTION_KILL:
			kill(paramListViewItem);
			break;
		case Setting.ACTION_SWITCH:
			switchTo(paramListViewItem);
			break;
		case Setting.ACTION_SELECT:
			selectOrUnselect(paramListViewItem);
			break;
		case Setting.ACTION_IGNORE:
			ignore(paramListViewItem);
			break;
		case Setting.ACTION_DETAIL:
			detail(paramListViewItem);
			break;
		case Setting.ACTION_MENU:
			menu(paramListViewItem);
			break;
		default:
			return;
		}
	}

	private ListView getListView(){
		return (ListView)findViewById(R.id.listbody);
	}

	private String getValuesFromPreference(SharedPreferences paramSharedPreferences){
		String str1 = "";
		String str2 = null;
		try{
			Iterator localIterator = paramSharedPreferences.getAll().entrySet().iterator();
			if (!localIterator.hasNext()){
				str2 = str1;
				return str2;
			}
			String str = str1 + ((Map.Entry)localIterator.next()).getKey() + "\r\n";
			str1 = str;
		}
		catch (Exception localException){
			str2 = localException.toString();
		}
		return str2;
	}

	private int getVersionCode(){
		PackageManager localPackageManager = getPackageManager();
		int i;
		try{
			i = localPackageManager.getPackageInfo(getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES).versionCode;
			return i;
		}
		catch (PackageManager.NameNotFoundException localNameNotFoundException){
			i = Setting.VERSION_CODE;
		}
		return i;
	}

	private void ignore(TaskListAdapters.ListViewItem paramListViewItem){
		paramListViewItem.detailProcess.setIgnored(true);
		refresh();
	}

	private void kill(TaskListAdapters.ListViewItem paramListViewItem){
		try{
			if (paramListViewItem.detailProcess.getPackageName().equals(getPackageName())){
				CommonLibrary.KillATK(mActivityManager, this);
			}else{
				CommonLibrary.KillApp(this, mActivityManager, paramListViewItem.detailProcess.getPackageName());
				mDetailList.remove(paramListViewItem.detailProcess);
				mAdapter.notifyDataSetChanged();
				refreshMem();
			}
		}
		catch (Exception localException){
			mAdapter.notifyDataSetChanged();
		}
	}

	private void killAllTasks(){
		CommonLibrary.KillProcess(this, mDetailList, mActivityManager, true);
		getRunningProcess();
		mAdapter = new TaskListAdapters.ProcessListAdapter(this, mDetailList);
		getListView().setAdapter(mAdapter);
		refreshMem();
		android.util.Log.e("ATK", "Manually kill ends");
	}

	private void menu(TaskListAdapters.ListViewItem paramListViewItem){
		final TaskListAdapters.ListViewItem p = paramListViewItem;
		mMenuDialog = new AlertDialog.Builder(this)
		.setTitle(paramListViewItem.detailProcess.getLabel())
		.setIcon(paramListViewItem.detailProcess.getIcon())
		.setItems(R.array.select_dialog_items, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface paramDialogInterface, int paramInt){
				switch (paramInt){
				case 0:
					AdvancedTaskKiller.this.kill(p);
					break;
				case 1:
					AdvancedTaskKiller.this.selectOrUnselect(p);
					break;
				case 2:
					AdvancedTaskKiller.this.switchTo(p);
					break;
				case 3:
					AdvancedTaskKiller.this.ignore(p);
					break;
				case 4:
					AdvancedTaskKiller.this.detail(p);
					break;
				default:
					return;
				}
			}
		}).create();
		mMenuDialog.show();
	}

	private void refreshMem(){
		if (mHandler == null)
			mHandler = new Handler(){
			public void handleMessage(Message paramMessage){
				try{
					String str = "Available Memory: " + CommonLibrary.MemoryToString(
							CommonLibrary.getAvaliableMemory(AdvancedTaskKiller.this.mActivityManager));
					AdvancedTaskKiller.this.setTitle(str + "  " + Setting.getAutoKillInfo());
					return;
				}
				catch (Exception localException){
					localException.printStackTrace();
				}
			}
		};
		mHandler.sendEmptyMessageDelayed(0, 700L);
	}

	private void selectOrUnselect(TaskListAdapters.ListViewItem paramListViewItem){
		if (paramListViewItem.detailProcess.getSelected()){
			paramListViewItem.iconCheck.setImageResource(R.drawable.btn_check_off);
			paramListViewItem.detailProcess.setSelected(false);
		}
		else{
			paramListViewItem.iconCheck.setImageResource(R.drawable.btn_check_on);
			paramListViewItem.detailProcess.setSelected(true);
		}
	}

	//	private void sendBugReport(){
	//		StringBuilder localStringBuilder = new StringBuilder();
	//		localStringBuilder.append(mLogResult + "\r\n");
	//		PackageManager localPackageManager = getApplicationContext().getPackageManager();
	//		Iterator localIterator1;
	//		Iterator localIterator2;
	//		try{
	//			localStringBuilder.append("ATK build " + String.valueOf(localPackageManager.getPackageInfo(getPackageName(), 8192).versionCode) + "\r\n");
	//			localStringBuilder.append("Device " + Build.DEVICE + "\r\n");
	//			localStringBuilder.append("SDK " + Build.VERSION.SDK + "\r\n");
	//			localStringBuilder.append("Release " + Build.VERSION.RELEASE + "\r\n");
	//			localStringBuilder.append(Setting.GetAllValues());
	//			localStringBuilder.append("Unselected apps\r\n");
	//			localStringBuilder.append(getValuesFromPreference(ProcessDetailInfo._SelectedAppSettings));
	//			localStringBuilder.append("Ignored apps\r\n");
	//			localStringBuilder.append(getValuesFromPreference(ProcessDetailInfo._IgnoredAppSettings));
	//			localStringBuilder.append("Current running apps\r\n");
	//			localIterator1 = mActivityManager.getRunningAppProcesses().iterator();
	//			label243: if (!localIterator1.hasNext()){
	//				localIterator2 = CommonLibrary.GetRunningProcess(this, mActivityManager).iterator();
	//				if (localIterator2.hasNext())
	//					break label504;
	//				if (Setting.IS_LOG_ENABLE)
	//					localStringBuilder.append(Log.GetAllLog(this));
	//			}
	//		}
	//		catch (PackageManager.NameNotFoundException localNameNotFoundException){
	//			try{
	//
	//				label266: ArrayList localArrayList = new ArrayList();
	//			localArrayList.add("logcat");
	//			localArrayList.add("-d");
	//			localArrayList.add("-v");
	//			localArrayList.add("long");
	//			localArrayList.add("*:E");
	//			BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(
	//					Runtime.getRuntime().exec((String[])localArrayList.toArray(new String[localArrayList.size()])).getInputStream()), 4024);
	//			while (true)
	//			{
	//				String str = localBufferedReader.readLine();
	//				if (str == null)
	//				{
	//					this._LogResult = localStringBuilder.toString();
	//					return;
	//					localNameNotFoundException = localNameNotFoundException;
	//					localStringBuilder.append("Error: name not found");
	//					break;
	//					ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = (ActivityManager.RunningAppProcessInfo)localIterator1.next();
	//					if ((localRunningAppProcessInfo == null) || (localRunningAppProcessInfo.processName == null))
	//						break label243;
	//					localStringBuilder.append(localRunningAppProcessInfo.processName);
	//					localStringBuilder.append(" ");
	//					localStringBuilder.append(CommonLibrary.GetAppUid(localRunningAppProcessInfo));
	//					localStringBuilder.append(" ");
	//					localStringBuilder.append(localRunningAppProcessInfo.pid);
	//					localStringBuilder.append("\r\n");
	//					break label243;
	//					label504: ProcessDetailInfo localProcessDetailInfo = (ProcessDetailInfo)localIterator2.next();
	//					localStringBuilder.append(localProcessDetailInfo.getLabel());
	//					localStringBuilder.append(" ");
	//					localStringBuilder.append(localProcessDetailInfo.getPackageName());
	//					localStringBuilder.append("\r\n");
	//					break label266;
	//				}
	//				localStringBuilder.append(str);
	//				localStringBuilder.append("\r\n");
	//			}
	//			}
	//			catch (IOException localIOException){
	//				while (true)
	//					localIOException.printStackTrace();
	//			}
	//		}
	//	}

	private void sendEmail(String paramString){
		Intent localIntent = new Intent("android.intent.action.SEND");
		localIntent.putExtra("android.intent.extra.EMAIL", new String[] { "rechild.dev@gmail.com" });
		localIntent.putExtra("android.intent.extra.TEXT", paramString);
		Object localObject = "";
		try{
			ApplicationInfo localApplicationInfo = getPackageManager().getApplicationInfo(
					getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
			String str = localApplicationInfo.loadLabel(getPackageManager()) + " Bug Report";
			localObject = str;
			localIntent.putExtra("android.intent.extra.SUBJECT", (String)localObject);
			localIntent.setType("message/rfc882");
			Intent.createChooser(localIntent, "Choose Email Client");
			startActivity(localIntent);
			return;
		}
		catch (PackageManager.NameNotFoundException localNameNotFoundException){
			localNameNotFoundException.printStackTrace();
		}
	}

	private void showRunningServices(){
		try{
			Intent localIntent = new Intent();
			localIntent.setClassName("com.android.settings", "com.android.settings.RunningServices");
			startActivity(localIntent);
			return;
		}
		catch (Exception localException){
			Toast.makeText(this, R.string.do_not_support, 0).show();
		}
	}

	private void switchTo(TaskListAdapters.ListViewItem paramListViewItem){
		try{
			Intent localIntent = new Intent("android.intent.action.MAIN");
			localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			localIntent.setClassName(paramListViewItem.detailProcess.getPackageName(), paramListViewItem.detailProcess.getBaseActivity());
			startActivity(localIntent);
			return;
		}
		catch (Exception localException){
		}
	}

	private void uninstall(TaskListAdapters.ListViewItem paramListViewItem){
		startActivity(new Intent("android.intent.action.DELETE", Uri.parse("package:" + paramListViewItem.detailProcess.getPackageName())));
	}

	public void getRunningProcess(){
		mDetailList = CommonLibrary.GetRunningProcess(this, mActivityManager);
		mAdapter = new TaskListAdapters.ProcessListAdapter(this, mDetailList);
	}

	public void onConfigurationChanged(Configuration paramConfiguration){
		super.onConfigurationChanged(paramConfiguration);
	}

	public void onCreate(Bundle paramBundle){
		super.onCreate(paramBundle);
		new Setting(getSharedPreferences("AdvTaskKillerSettings", 0), this);
		if ((Setting.AUTO_KILL_LEVEL > 0) && (CommonLibrary.NextRun == null))
			CommonLibrary.ScheduleAutoKill(this, false, Setting.AUTO_KILL_FREQUENCY);
		if (Setting.IS_BUTTON_AT_TOP)
			setContentView(R.layout.main);
		else
			setContentView(R.layout.main_button_at_bottom);
		mActivityManager = ((ActivityManager)getSystemService("activity"));
		int i = getVersionCode();
		if (i != Setting.VERSION_CODE){
			Setting.setVersionCode(i);
			Setting.setIgnoreServiceFrontApp(false);
			showDialog(DIALOG_NEW_VERSION_ALERT);
		}
	}

	protected Dialog onCreateDialog(int paramInt){
		super.onCreateDialog(paramInt);
		Dialog dialog;
		switch (paramInt){
		case DIALOG_LOADING:
			View localView2 = LayoutInflater.from(this).inflate(R.layout.loading_dialog, null);
			((TextView)localView2.findViewById(R.id.loadingdialogmsg)).setText("Building system ignore list for first running!");
			dialog = new AlertDialog.Builder(this).setView(localView2).create();
			break;
		case DIALOG_IGNORE:
			dialog = new AlertDialog.Builder(this).setTitle(R.string.ignore_service_alert_dialog_title)
			.setIcon(android.R.drawable.ic_menu_info_details)//17301569)
			.setMessage(R.string.ignore_service_alert_dialog_message)
			.setCancelable(false)
			.setNegativeButton("No, I want to force stop them", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt){
				}
			}).setPositiveButton("Yes, Ignore service and front app", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
				{
					Setting.setIgnoreServiceFrontApp(true);
					AdvancedTaskKiller.this.killAllTasks();
				}
			}).create();
			break;
		case DIALOG_IGNORE_SERVICE_ALERT:
			dialog = new AlertDialog.Builder(this).setTitle(R.string.ignore_service_alert_dialog_title)
			.setIcon(android.R.drawable.ic_menu_info_details)
			.setMessage(R.string.ignore_service_alert_dialog_message)
			.setCancelable(false)
			.setNegativeButton("No, I want to force stop them", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt){
				}
			}).setPositiveButton("Yes, Ignore service and front app", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
				{
					Setting.setIgnoreServiceFrontApp(true);
					AdvancedTaskKiller.this.killAllTasks();
				}
			}).create();
			break;
		case DIALOG_NEW_VERSION_ALERT:
			dialog = new AlertDialog.Builder(this).setTitle(R.string.new_version_alter_title)
			.setIcon(android.R.drawable.ic_menu_info_details)
			.setMessage(R.string.new_version_alert_message)
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt){
					AdvancedTaskKiller.this.showDialog(DIALOG_IGNORE);
					new IgnoreTask().execute(new String[] { "" });
				}
			}).create();
			break;
		case DIALOG_INFO:
			dialog = new AlertDialog.Builder(this).setTitle(R.string.dialog_about_title)
			.setIcon(android.R.drawable.ic_menu_info_details)
			.setMessage(CommonLibrary.getStringFromAsset(this, "about.txt"))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt){
				}
			}).create();
			break;
		case DIALOG_HELP:
			dialog = new AlertDialog.Builder(this).setTitle(R.string.dialog_help_title)
			.setIcon(android.R.drawable.ic_menu_info_details)
			.setMessage(CommonLibrary.getStringFromAsset(this, "help.txt"))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt){
				}
			}).create();

			break;
		case DIALOG_BUG:
			final View localView1 = LayoutInflater.from(this).inflate(R.layout.bug_report_diag, null);
			dialog = new AlertDialog.Builder(this).setView(localView1)
					.setPositiveButton("Send via email", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt){
							AdvancedTaskKiller.this.showDialog(1);
							EditText localEditText = (EditText)localView1.findViewById(R.id.editErrorMessage);
							AdvancedTaskKiller.this.mLogResult = localEditText.getText().toString();
							new SendBugReportTask().execute(new String[] { "" });
						}
					}).create();
		default:
			View localView3 = LayoutInflater.from(this).inflate(R.layout.loading_dialog, null);
			dialog = new AlertDialog.Builder(this).setView(localView3).create();
		}
		return dialog;

	}

	public boolean onCreateOptionsMenu(Menu paramMenu){
		paramMenu.add(0, 0, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);//17301577);
//		paramMenu.add(0, 1, 1, "Report Bug").setIcon(android.R.drawable.ic_menu_edit);//17301566);
		paramMenu.add(0, 2, 2, "Help").setIcon(android.R.drawable.ic_menu_help);//17301568);
		paramMenu.add(0, 3, 3, "Info").setIcon(android.R.drawable.ic_menu_info_details);
		paramMenu.add(0, 4, 4, "Service").setIcon(android.R.drawable.ic_menu_view);//17301591);
		paramMenu.add(0, 5, 5, "Exit").setIcon(android.R.drawable.ic_menu_close_clear_cancel);//17301560);
		if (Setting.IS_LOG_ENABLE)
			paramMenu.add(0, 6, 6, "Log").setIcon(android.R.drawable.ic_menu_agenda);//17301556);
		return true;
	}

	protected void onDestroy(){
		super.onDestroy();
	}

	public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong){
		doAction((TaskListAdapters.ListViewItem)paramView.getTag(), Setting.CLICK_ACTION);
	}

	public boolean onItemLongClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong){
		TaskListAdapters.ListViewItem localListViewItem = (TaskListAdapters.ListViewItem)paramView.getTag();
		if ((Setting.LONG_PRESS_ACTION == 0) && (getPackageName().equals(localListViewItem.detailProcess.getPackageName()))){
			AutoStartReceiver.ClearNotification(this);
			finish();
		}else{
			doAction(localListViewItem, Setting.LONG_PRESS_ACTION);
		}
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem paramMenuItem){
		switch (paramMenuItem.getItemId()){
		default:
			showDialog(DIALOG_INFO);
			return true;
		case 0:
			CommonLibrary.InvokeActivity(this, NewSettings.class);
			break;
		case 1:
			showDialog(DIALOG_BUG);
			break;
		case 2:
			CommonLibrary.InvokeActivity(this, HelpActivity.class);
			break;
		case 3:
			showDialog(DIALOG_INFO);
			break;
		case 4:
			showRunningServices();
			break;
		case 5:
			AutoStartReceiver.ClearNotification(this);
			System.exit(0);
			break;
		case 6:
			CommonLibrary.InvokeActivity(this, LogActivity.class);
			break;
		}
		return true;
	}

	protected void onPause(){
		super.onPause();
	}

	protected void onResume(){
		super.onResume();
		refresh();
	}

	public void refresh(){
		getRunningProcess();
		getListView().setAdapter(this.mAdapter);
		bindEvent();
		refreshMem();
		AutoStartReceiver.RefreshNotification(this);
	}

	private class IgnoreTask extends AsyncTask<String, String, String>{
		private IgnoreTask(){
		}

		protected String doInBackground(String[] paramArrayOfString){
			AdvancedTaskKiller.this.IgnoreSystemApp();
			return "OK";
		}

		protected void onPostExecute(String paramString){
			AdvancedTaskKiller.this.removeDialog(5);
			AdvancedTaskKiller.this.refresh();
		}
	}

	private class KillAllTask extends AsyncTask<String, String, String>{
		private KillAllTask(){
		}

		protected String doInBackground(String[] paramArrayOfString){
			try{
				CommonLibrary.KillProcess(AdvancedTaskKiller.this, AdvancedTaskKiller.this.mDetailList, 
						AdvancedTaskKiller.this.mActivityManager, true);
				AdvancedTaskKiller.this.getRunningProcess();
				return "OK";
			}
			catch (Exception localException){
				android.util.Log.e("ATK", localException.toString());
			}
			return "OK";
		}

		protected void onPostExecute(String paramString){
			AdvancedTaskKiller.this.mAdapter = new TaskListAdapters.ProcessListAdapter(AdvancedTaskKiller.this, 
					AdvancedTaskKiller.this.mDetailList);
			AdvancedTaskKiller.this.getListView().setAdapter(AdvancedTaskKiller.this.mAdapter);
			AdvancedTaskKiller.this.refreshMem();
			android.util.Log.e("ATK", "Manually kill ends");
		}
	}

	private class SendBugReportTask extends AsyncTask<String, String, String>{
		private SendBugReportTask(){
		}

		protected String doInBackground(String[] paramArrayOfString){
			String str;
			try{
//				AdvancedTaskKiller.this.sendBugReport();
				str = "OK";
				return str;
			}
			catch (Exception localException){
				str = null;
			}
			return str;
		}

		protected void onPostExecute(String paramString){
			AdvancedTaskKiller.this.removeDialog(1);
			if (paramString == null)
				return;
			AdvancedTaskKiller.this.sendEmail(AdvancedTaskKiller.this.mLogResult);
		}
	}
}
