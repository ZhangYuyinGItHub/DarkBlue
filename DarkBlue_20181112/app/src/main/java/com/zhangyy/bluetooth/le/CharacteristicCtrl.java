package com.zhangyy.bluetooth.le;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ComponentName;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.zhangyy.bluetooth.le.R;

public class CharacteristicCtrl extends Activity {

	private final static String TAG = CharacteristicCtrl.class.getSimpleName();

	TextView mTitle = null;
	TextView mNotifyTextView = null;
	Button mCharaReadBtn = null;
	Button mCharaWriteBtn = null;
	Button mCharaNotifyBtn = null;
	// EditText mCharaWriteValueEdit = null;
	TextView mDeviceState = null;
	TextView mProperties = null;

	private BluetoothLeService mBluetoothLeService;
	private int mPorperty;

	// ListView 处理
	ListView mReadListView = null;
	List<Map<String, Object>> mReadList = new ArrayList<Map<String, Object>>();
	SimpleAdapter mReadAdapter;
	ListView mWriteListView = null;
	List<Map<String, Object>> mWriteList = new ArrayList<Map<String, Object>>();
	SimpleAdapter mWriteAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chara_ctrl_layout);

		mCharaWriteBtn = (Button) findViewById(R.id.charawrite);
		mCharaReadBtn = (Button) findViewById(R.id.chararead);
		mCharaNotifyBtn = (Button) findViewById(R.id.charanotify);
		// mCharaWriteValueEdit = (EditText) findViewById(R.id.writeedittext);

		mCharaWriteBtn.setOnClickListener(charaWriteClickListener);
		mCharaReadBtn.setOnClickListener(charaReadClickListener);
		mCharaNotifyBtn.setOnClickListener(charaNotifyClickListener);
		mCharaWriteBtn.setEnabled(false);
		mCharaReadBtn.setEnabled(false);
		mCharaNotifyBtn.setEnabled(false);
		mCharaNotifyBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		mCharaReadBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		mCharaWriteBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		mCharaWriteBtn.setTextColor(Color.rgb(78, 78, 78));
		mCharaReadBtn.setTextColor(Color.rgb(78, 78, 78));
		mCharaNotifyBtn.setTextColor(Color.rgb(78, 78, 78));

		mTitle = (TextView) findViewById(R.id.devicename);
		final Intent intent = getIntent();
		mTitle.setText("UUID: " + intent.getStringExtra("UUID"));
		mDeviceState = (TextView) findViewById(R.id.devicestate);
		mDeviceState.setText("Connected");
		mProperties = (TextView) findViewById(R.id.charaproperty);

		/* Service bind */
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

		/* read Listview 设置 */
		mReadAdapter = new SimpleAdapter(this, mReadList,
				R.layout.ctrl_read_listview, new String[] { "value", "time" },
				new int[] { R.id.readvalue, R.id.readtime });
		mReadListView = (ListView) findViewById(R.id.readlistview);
		mReadListView.setAdapter(mReadAdapter);

		/* Write Listview 设置 */
		mWriteAdapter = new SimpleAdapter(this, mWriteList,
				R.layout.ctrl_write_listview, new String[] { "value", "time" },
				new int[] { R.id.writevalue, R.id.writetime });
		mWriteListView = (ListView) findViewById(R.id.writelistview);
		mWriteListView.setAdapter(mWriteAdapter);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(R.drawable.return_array);
		getActionBar().setTitle(intent.getStringExtra("NAME"));
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * 函数功能：计算ListView的动态高度
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		SimpleAdapter listAdapter = (SimpleAdapter) listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {

			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();

			mPorperty = mBluetoothLeService.mCtrlCharacteristic.getProperties();
			String str = "";

			if (0 != (mPorperty & BluetoothGattCharacteristic.PROPERTY_WRITE)) {
				str = str + "write";
				mCharaWriteBtn.setEnabled(true);
				mCharaWriteBtn.setTextColor(Color.rgb(00, 0, 255));

			}
			if (0 != (mPorperty & BluetoothGattCharacteristic.PROPERTY_READ)) {
				str = str + "/read";
				mCharaReadBtn.setEnabled(true);
				mCharaReadBtn.setTextColor(Color.rgb(0, 0, 255));

			}
			if (0 != (mPorperty & BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
				str = str + "/Notify";
				mCharaNotifyBtn.setEnabled(true);
				if (!mBluetoothLeService.mCharaNotifyEnableList
						.contains(mBluetoothLeService.mCtrlCharacteristic
								.hashCode() + "")) {
					mCharaNotifyBtn.setText("Listen for Notification");
					mCharaNotifyBtn.setTextColor(Color.BLUE);
				} else {
					mCharaNotifyBtn.setText("Stop Notification");
					mCharaNotifyBtn.setTextColor(Color.RED);
				}
			}

			if (0 != (mPorperty & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) {
				str = str + "/Write no Resp";
				mCharaWriteBtn.setEnabled(true);
				mCharaWriteBtn.setTextColor(Color.rgb(00, 0, 255));
			}
			if (0 != (mPorperty & BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
				str = str + "/Indicate";
				// todo
			}
			mProperties.setText(str);
		}

		public void onServiceDisconnected(ComponentName componentName) {
			Toast.makeText(getApplicationContext(), "onServiceDisconnected",
					Toast.LENGTH_SHORT).show();
			mBluetoothLeService.setCharacteristicNotification(
					mBluetoothLeService.mCtrlCharacteristic, false);
			mBluetoothLeService = null;

		}
	};

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				Toast.makeText(getApplicationContext(),
						"Service is Connected!", Toast.LENGTH_SHORT).show();
				mDeviceState.setText("Connected");

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				Toast.makeText(getApplicationContext(),
						"Service is Disconnected!", Toast.LENGTH_SHORT).show();
				mDeviceState.setText("Disconnect");
			} else if ((BluetoothLeService.ACTION_DATA_READ.equals(action))
					|| (BluetoothLeService.ACTION_DATA_NOTIFY.equals(action))) {
				/* 属性数据的读操作和通知操作 */
				// Toast.makeText(getApplicationContext(), "get data",
				// Toast.LENGTH_SHORT).show();

				String extraCharaId = mBluetoothLeService.mCtrlCharacteristic
						.hashCode() + "";
				String notifyCharaId = intent
						.getStringExtra(BluetoothLeService.HASHCODE);
				
				// 防止通知信息打印到其他的页面上
				if (!extraCharaId.equals(notifyCharaId)) {
					System.out.println("the characteristic instance id is: "
							+ mBluetoothLeService.mCtrlCharacteristic
									.getInstanceId());
					return;
				}
				// 上报的通知需要和当前界面的属性一致
				// 获取数据
				String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
				// 获取系统时间
				SimpleDateFormat sDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd  hh:mm:ss");
				String time = sDateFormat.format(new java.util.Date());

				Map<String, Object> listem = new HashMap<String, Object>();

				listem.put("value", data);
				listem.put("time", time);
				mReadList.add(0, listem);
				if (mReadList.size() > 5)
					mReadList.remove(5);
				mReadAdapter.notifyDataSetChanged();
				setListViewHeightBasedOnChildren(mReadListView);

			} else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {
				/* 属性数据的写操作 */
				Toast.makeText(getApplicationContext(), "write value",
						Toast.LENGTH_SHORT).show();
				// 获取数据
				String data = "0x"
						+ intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
				// 获取系统时间
				SimpleDateFormat sDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd  hh:mm:ss");
				String time = sDateFormat.format(new java.util.Date());

				Map<String, Object> listem = new HashMap<String, Object>();
				listem.put("value", data);
				listem.put("time", time);
				mWriteList.add(0, listem);
				mWriteAdapter.notifyDataSetChanged();
				setListViewHeightBasedOnChildren(mWriteListView);
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (mBluetoothLeService != null) {
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mServiceConnection);
		mBluetoothLeService = null;

	}

	private Button.OnClickListener charaWriteClickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			Intent writeIntent = new Intent();
			writeIntent.setClass(CharacteristicCtrl.this, WriteKeyboard.class);
			startActivityForResult(writeIntent, 1);

		}
	};

	/*
	 * 思路：“String a=”B2” --> byte b=0xB2”字符的byte转换为byte数据类型 ,通过Integer作为转换的中间桥梁
	 * 函数功能：“123456”-->0x123456
	 */
	public static int stringToByte(String in, byte[] b) {
		if (in.length() % 2 != 0) {
			in = "0" + in;
		}

		int j = 0;
		StringBuffer buf = new StringBuffer(2);
		for (int i = 0; i < in.length(); i++, j++) {
			buf.insert(0, in.charAt(i));
			buf.insert(1, in.charAt(i + 1));
			int t = Integer.parseInt(buf.toString(), 16);
			System.out.println("byte hex value:" + t);
			b[j] = (byte) t;
			i++;
			buf.delete(0, 2);
		}

		return j;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		String str = data.getExtras().getString("WRITEVALUE");
		if (str.length() == 0)
			return;

		// 动态创建一个byte数组，防止奇数的场景
		byte[] value = new byte[(str.length() / 2) + (str.length() % 2)];

		stringToByte(str, value);
		mBluetoothLeService.mCtrlCharacteristic.setValue(value);
		mBluetoothLeService
				.writeCharacteristic(mBluetoothLeService.mCtrlCharacteristic);

		if (0 != (mBluetoothLeService.mCtrlCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) {

			Map<String, Object> listem = new HashMap<String, Object>();
			listem.put("value", "0x" + str);
			// 获取系统时间
			SimpleDateFormat sDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd  hh:mm:ss");
			String time = sDateFormat.format(new java.util.Date());
			listem.put("time", time);
			mWriteList.add(0, listem);
			mWriteAdapter.notifyDataSetChanged();
			setListViewHeightBasedOnChildren(mWriteListView);
		}
	};

	private Button.OnClickListener charaReadClickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// mRevTextView.setText("Write button is press!");
			mBluetoothLeService
					.readCharacteristic(mBluetoothLeService.mCtrlCharacteristic);
			Toast.makeText(getApplicationContext(), "Read clicked",
					Toast.LENGTH_SHORT).show();

		}
	};

	private Button.OnClickListener charaNotifyClickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (mCharaNotifyBtn.getText().equals("Listen for Notification")) {
				mBluetoothLeService.setCharacteristicNotification(
						mBluetoothLeService.mCtrlCharacteristic, true);
				mCharaNotifyBtn.setText("Stop Notification");
				mCharaNotifyBtn.setTextColor(Color.RED);

				if (false == mBluetoothLeService.mCharaNotifyEnableList
						.contains(mBluetoothLeService.mCtrlCharacteristic
								.hashCode() + "")) {
					Toast.makeText(getApplicationContext(), "Notify Enable",
							Toast.LENGTH_SHORT).show();
					mBluetoothLeService.mCharaNotifyEnableList
							.add(mBluetoothLeService.mCtrlCharacteristic
									.hashCode() + "");
				}

			} else {
				mBluetoothLeService.setCharacteristicNotification(
						mBluetoothLeService.mCtrlCharacteristic, false);
				mCharaNotifyBtn.setText("Listen for Notification");
				mCharaNotifyBtn.setTextColor(Color.BLUE);

				if (true == mBluetoothLeService.mCharaNotifyEnableList
						.contains(mBluetoothLeService.mCtrlCharacteristic
								.hashCode() + "")) {
					Toast.makeText(getApplicationContext(), "Notify Disable",
							Toast.LENGTH_SHORT).show();
					mBluetoothLeService.mCharaNotifyEnableList
							.remove(mBluetoothLeService.mCtrlCharacteristic
									.hashCode() + "");
				}

			}
		}
	};

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_READ);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_NOTIFY);
		return intentFilter;
	}
}
