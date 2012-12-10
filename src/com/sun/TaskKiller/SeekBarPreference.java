package com.sun.TaskKiller;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarPreference extends DialogPreference
implements SeekBar.OnSeekBarChangeListener{
	private static final String androidns = "http://schemas.android.com/apk/res/android";
	private Context mContext;
	private int mDefault;
	private String mDialogMessage;
	private int mMax;
	private int mMin = 36;
	private int mOriginalValue = 0;
	private SeekBar mSeekBar;
	private TextView mSplashText;
	private String mSuffix;
	private int mValue;
	private TextView mValueText;

	public SeekBarPreference(Context paramContext, AttributeSet paramAttributeSet){
		super(paramContext, paramAttributeSet);
		mContext = paramContext;
		mDialogMessage = paramAttributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "dialogMessage");
		mSuffix = paramAttributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
		mDefault = paramAttributeSet.getAttributeIntValue("http://schemas.android.com/apk/res/android", "defaultValue", 0);
		mMax = paramAttributeSet.getAttributeIntValue("http://schemas.android.com/apk/res/android", "max", 100);
	}

	private void setValueText(int paramInt){
		String str1 = String.valueOf(paramInt);
		String str2;
		TextView localTextView = mValueText;
		if (mSuffix == null)
			str2 = str1;
		else 
			str2 = str1.concat(mSuffix);
		localTextView.setText(str2);
	}

	public int getMax(){
		return mMax;
	}

	public int getMin(){
		return mMin;
	}

	public int getProgress(){
		return mValue - mMin;
	}

	protected void onBindDialogView(View paramView){
		super.onBindDialogView(paramView);
		mSeekBar.setMax(mMax - mMin);
		mSeekBar.setProgress(mValue - mMin);
	}

	protected View onCreateDialogView(){
		LinearLayout localLinearLayout = new LinearLayout(mContext);
		localLinearLayout.setOrientation(1);
		localLinearLayout.setPadding(6, 6, 6, 6);
		mSplashText = new TextView(mContext);
		if (mDialogMessage != null)
			mSplashText.setText(mDialogMessage);
		localLinearLayout.addView(mSplashText);
		mValueText = new TextView(mContext);
		mValueText.setGravity(1);
		mValueText.setTextSize(32.0F);
		LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-1, -2);
		localLinearLayout.addView(mValueText, localLayoutParams);
		mSeekBar = new SeekBar(mContext);
		mSeekBar.setOnSeekBarChangeListener(this);
		localLinearLayout.addView(mSeekBar, new LinearLayout.LayoutParams(-1, -2));
		if (shouldPersist())
			mValue = getPersistedInt(mDefault);
		mSeekBar.setMax(mMax - mMin);
		mSeekBar.setProgress(mValue - mMin);
		setValueText(mValue);
		mOriginalValue = mValue;
		return localLinearLayout;
	}

	protected void onDialogClosed(boolean paramBoolean){
		if (paramBoolean)
			Setting.ITEM_HEIGHT = mValue;
		else
			if (shouldPersist())
				persistInt(mOriginalValue);
	}

	public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean){
		setValueText(paramInt + mMin);
		if (shouldPersist()){
			mValue = (paramInt + mMin);
			persistInt(mValue);
		}
		callChangeListener(new Integer(paramInt));
	}

	protected void onSetInitialValue(boolean paramBoolean, Object paramObject){
		super.onSetInitialValue(paramBoolean, paramObject);
		int i = 0;
		if (paramBoolean){
			if (shouldPersist())
				i = getPersistedInt(mDefault);
			mValue = i;
		}
		else{
			 mValue = ((Integer)paramObject).intValue();
		}
	}

	public void onStartTrackingTouch(SeekBar paramSeekBar){
	}

	public void onStopTrackingTouch(SeekBar paramSeekBar){
	}

	public void setMax(int paramInt){
		mMax = paramInt;
	}

	public void setMin(int paramInt){
		mMin = paramInt;
	}

	public void setProgress(int paramInt){
		mValue = (paramInt + mMin);
		if (mSeekBar != null)
			mSeekBar.setProgress(paramInt);
	}
}