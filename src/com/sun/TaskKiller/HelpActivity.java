package com.sun.TaskKiller;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.rechild.advancedtaskkillerpro.R;

public class HelpActivity extends Activity{
	public void onCreate(Bundle paramBundle){
		super.onCreate(paramBundle);
		setContentView(R.layout.help);
		TextView localTextView = (TextView)findViewById(R.id.tvHelp);
		String str = CommonLibrary.getStringFromAsset(this, "help.txt");
		if (str == null)
			return;
		localTextView.setText(str);
	}
}
