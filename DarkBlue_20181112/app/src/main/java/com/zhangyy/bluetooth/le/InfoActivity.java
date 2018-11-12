package com.zhangyy.bluetooth.le;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

public class InfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_activity);
		getActionBar().hide();
	}

}