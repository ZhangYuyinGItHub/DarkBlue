package com.zhangyy.bluetooth.le;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LeConfigSQLiteOpenHelper extends SQLiteOpenHelper {

	public LeConfigSQLiteOpenHelper(Context context) {
		super(context, "darkblue.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table iconfig (id integer primary key autoincrement, name varchar(30), value varchar(30))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
