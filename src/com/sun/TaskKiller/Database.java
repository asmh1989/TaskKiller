package com.sun.TaskKiller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database{
	private static final String DB_CREATE = "CREATE TABLE Log (_id INTEGER PRIMARY KEY,time TIMESTAMP default CURRENT_TIMESTAMP,data TEXT)";
	public static final String DB_NAME = "ATKLogData.db";
	private static final String DB_TABLE = "Log";
	private static final int DB_VERSION = 1;
	public static final String KEY_DATA = "data";
	public static final String KEY_ID = "_id";
	public static final String KEY_TIME = "time";
	private Context mContext = null;
	private DatabaseHelper mDatabaseHelper = null;
	private SQLiteDatabase mSQLiteDatabase = null;

	public Database(Context paramContext){
		this.mContext = paramContext;
	}

	public void beginTransaction(){
		this.mSQLiteDatabase.beginTransaction();
	}

	public void clearTable(){
		this.mSQLiteDatabase.execSQL("DELETE FROM Log");
	}

	public void close(){
		this.mDatabaseHelper.close();
	}

	public void endTransaction(){
		this.mSQLiteDatabase.endTransaction();
	}

	public Cursor fetchAllData(){
		return this.mSQLiteDatabase.query("Log", new String[] { "_id", "time", "data" }, null, null, null, null, null);
	}

	public long insertData(String paramString){
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("data", paramString);
		return this.mSQLiteDatabase.insert("Log", "_id", localContentValues);
	}

	public void open()throws SQLException{
		mDatabaseHelper = new DatabaseHelper(mContext);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}

	private static class DatabaseHelper extends SQLiteOpenHelper{
		DatabaseHelper(Context paramContext){
			super(paramContext, "ATKLogData.db", null, 1);
		}

		public void onCreate(SQLiteDatabase paramSQLiteDatabase){
			paramSQLiteDatabase.execSQL("CREATE TABLE Log (_id INTEGER PRIMARY KEY,time TIMESTAMP default CURRENT_TIMESTAMP,data TEXT)");
		}

		public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2){
			paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS Log");
			onCreate(paramSQLiteDatabase);
		}
	}
}
