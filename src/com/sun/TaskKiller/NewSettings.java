package com.sun.TaskKiller;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import com.rechild.advancedtaskkillerpro.R;

public class NewSettings extends PreferenceActivity{
	private ListPreference mAutoKillLevel;
	private ListPreference mKillFrequency;
	private CheckBoxPreference mNotification;

	private void setAutoKill(){
		if (Setting.AUTO_KILL_LEVEL > Setting.SECURITY_LEVEL_HIGH)
			CommonLibrary.ScheduleAutoKill(this, false, Long.valueOf(Setting.AUTO_KILL_FREQUENCY).longValue());
		else
			CommonLibrary.ScheduleAutoKill(this, true, Long.valueOf(Setting.AUTO_KILL_FREQUENCY).longValue());
	}

	private void setKillFrequencyEnableOrDisable(String paramString){
		if (paramString.equals(String.valueOf(0)))
			mKillFrequency.setEnabled(false);
		else
			mKillFrequency.setEnabled(true);
	}

	protected void onCreate(Bundle paramBundle){
		super.onCreate(paramBundle);
		addPreferencesFromResource(R.xml.settings);
		mNotification = ((CheckBoxPreference)findPreference("IsNotificationEnabled"));
		mNotification.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
			public boolean onPreferenceClick(Preference paramPreference){
				Setting.IS_NOTIFICATION_ENABLE = NewSettings.this.mNotification.isChecked();
				AutoStartReceiver.RefreshNotification(NewSettings.this);
				return true;
			}
		});
		if (!Setting.INCLUDE_AUTOKILL_FEATURE)
			return;
		mAutoKillLevel = ((ListPreference)findPreference("AutoKillLevelValue"));
		mAutoKillLevel.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
			public boolean onPreferenceChange(Preference paramPreference, Object paramObject){
				SharedPreferences.Editor localEditor = paramPreference.getSharedPreferences().edit();
				localEditor.putString("AutoKillLevelValue", paramObject.toString());
				localEditor.commit();
				Setting.AUTO_KILL_LEVEL = Integer.parseInt(paramObject.toString());
				NewSettings.this.setAutoKill();
				NewSettings.this.setKillFrequencyEnableOrDisable(paramObject.toString());
				return true;
			}
		});
		mKillFrequency = ((ListPreference)findPreference("AutoKillFrequecyValue"));
		mKillFrequency.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
			public boolean onPreferenceChange(Preference paramPreference, Object paramObject){
				SharedPreferences.Editor localEditor = paramPreference.getSharedPreferences().edit();
				localEditor.putString("AutoKillFrequecyValue", paramObject.toString());
				localEditor.commit();
				Setting.AUTO_KILL_FREQUENCY = Long.parseLong(paramObject.toString());
				NewSettings.this.setAutoKill();
				return true;
			}
		});
		setKillFrequencyEnableOrDisable(this.mAutoKillLevel.getValue());
	}

	protected void onDestroy(){
		super.onDestroy();
		Setting.restoreSettings(this);
	}
}
