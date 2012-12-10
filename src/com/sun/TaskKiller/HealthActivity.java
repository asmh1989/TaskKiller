package com.sun.TaskKiller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.rechild.advancedtaskkillerpro.R;

public class HealthActivity extends Activity{
	public void onCreate(Bundle paramBundle){
		super.onCreate(paramBundle);
		setContentView(R.layout.health);
		findViewById(R.id.btnBatteryUsageSummary).setOnClickListener(new View.OnClickListener(){
			public void onClick(View paramView){
				try{
					Intent localIntent = new Intent("android.intent.action.MAIN");
					localIntent.setClassName("com.android.settings", "com.android.settings.battery_history.BatteryHistory");
					HealthActivity.this.startActivity(localIntent);
					return;
				}
				catch (Exception localException){
					Toast.makeText(HealthActivity.this, R.string.do_not_support, 0).show();
				}
			}
		});
	}
}
