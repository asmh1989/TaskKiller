package com.sun.TaskKiller;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.rechild.advancedtaskkillerpro.R;

public class LogActivity extends Activity{
	public void onCreate(Bundle paramBundle){
		super.onCreate(paramBundle);
		setContentView(R.layout.log);
		TextView localTextView = (TextView)findViewById(R.id.tvLog);
		String str = Log.GetAllLog(this);
		if (str == null)
			return;
		localTextView.setText(str);
	}
}
