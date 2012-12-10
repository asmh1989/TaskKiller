package com.sun.TaskKiller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import java.io.File;

public class Log{
	private static final String TAG = "ATK";
	private static Database mDatabase;

	public static void BeginTrasaction(Context paramContext){
		if (Setting.IS_LOG_ENABLE);{
			openDatabase(paramContext);
			mDatabase.beginTransaction();
		}
	}

	public static void EndTrasaction(Context paramContext){
		if (Setting.IS_LOG_ENABLE);{
			openDatabase(paramContext);
			mDatabase.endTransaction();
		}
	}

	public static String GetAllLog(Context paramContext){
		String str;
		if (!Setting.IS_LOG_ENABLE){
			str = "";

		}else{
			openDatabase(paramContext);
			Cursor localCursor = mDatabase.fetchAllData();
			StringBuilder localStringBuilder = new StringBuilder();
			localCursor.moveToFirst();
			while (true){
				if (localCursor.isAfterLast()){
					str = localStringBuilder.toString();
					break;
				}
				localStringBuilder.append(localCursor.getString(1) + " ");
				localStringBuilder.append(localCursor.getString(2) + "\r\n");
				localCursor.moveToNext();
			}
		}
		return str;
	}

	public static void I(Context paramContext, String paramString)
	{
		if (!Setting.IS_LOG_ENABLE)
			android.util.Log.e("ATK", paramString);
		else{
			openDatabase(paramContext);
			mDatabase.insertData(paramString);
		}
	}
	
	public static void d(String tag, String s){
		android.util.Log.d(tag, s);
	}

	private static void deleteDB(Context paramContext){
		File localFile = paramContext.getDatabasePath("ATKLogData.db");
		if ((localFile != null) && (localFile.exists()))
			localFile.delete();
	}

	private static void openDatabase(Context paramContext){
		if (mDatabase == null);
		try{
			mDatabase = new Database(paramContext);
			mDatabase.open();
			Cursor localCursor = mDatabase.fetchAllData();
			if ((localCursor.moveToLast()) && (localCursor.getPosition() > 700))
				mDatabase.clearTable();
			return;
		}catch (SQLiteDatabaseCorruptException localSQLiteDatabaseCorruptException){
			deleteDB(paramContext);
			mDatabase = new Database(paramContext);
			mDatabase.open();
		}
	}
}