/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhangyy.bluetooth.le;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * For a given BLE device, this Activity provides the user interface to connect,
 * display data, and display GATT services and characteristics supported by the
 * device. The Activity communicates with {@code BluetoothLeService}, which in
 * turn interacts with the Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
	private final static String TAG = DeviceControlActivity.class
			.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	public static final String EXTRAS_DEVICE_TYPE = "DEVICE_TYPE";

	private Button mMtuChangeBtn;
	private TextView mConnectionState;
	private String mDeviceName;
	private String mDeviceType;
	private String mDeviceAddress;
	private BluetoothLeService mBluetoothLeService;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private Map<String, BluetoothGattCharacteristic> mGattCharacteristicMap = new HashMap<String, BluetoothGattCharacteristic>();
	private boolean mConnected = false;
	private BluetoothGattCharacteristic mNotifyCharacteristic;

	private ListView mGattListView = null;
	private GroupListAdapter adapter = null;
	private List<String> list = new ArrayList<String>();
	private List<String> listTag = new ArrayList<String>();
	private List<String> mPropertylist = new ArrayList<String>();
	private TextView mListDeviceAddress;

	private LeConfigOper mLeConfigOper;
	private int mVoiceHidIndex;
	private int mVoiceCmdIndex;
	private int mVoiceDataIndex;
	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	// Handles various events fired by the Service.
	// ACTION_GATT_CONNECTED: connected to a GATT server.
	// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
	// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
	// ACTION_DATA_AVAILABLE: received data from the device. This can be a
	// result of read
	// or notification operations.
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				mConnected = true;
				updateConnectionState(R.string.connected);
				invalidateOptionsMenu();

				if (mDeviceType.equals("RCU"))
					if (mBluetoothLeService.bondDevice(mDeviceAddress))
						Log.i(TAG, "bind the device success!");
					else {
						Log.i(TAG, "bind the device failed, reconnect again!");
						mBluetoothLeService.connect(mDeviceAddress);
					}

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				mConnected = false;
				updateConnectionState(R.string.disconnected);
				invalidateOptionsMenu();
				clearUI();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				// Show all the supported services and characteristics on the
				// user interface.
				displayGattServices(mBluetoothLeService
						.getSupportedGattServices());

			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				displayData(intent
						.getStringExtra(BluetoothLeService.EXTRA_DATA));
			} else if (BluetoothLeService.ACTION_MTU_CHANGED.equals(action)) {
				System.out.println("--->Mtu Size is :"
						+ intent.getIntExtra(BluetoothLeService.EXTRA_DATA, 0));
				Intent intent0 = new Intent();
				intent0.setClass(DeviceControlActivity.this,
						RcuVoiceActivity.class);
				startActivity(intent0);
			}
		}
	};

	// If a given GATT characteristic is selected, check for supported features.
	// This sample
	// demonstrates 'Read' and 'Notify' features. See
	// http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for
	// the complete
	// list of supported characteristic features.
	private final ExpandableListView.OnChildClickListener servicesListClickListner = new ExpandableListView.OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			if (mGattCharacteristics != null) {

				final BluetoothGattCharacteristic characteristic = mGattCharacteristics
						.get(groupPosition).get(childPosition);
				final int charaProp = characteristic.getProperties();
				if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
					// If there is an active notification on a characteristic,
					// clear
					// it first so it doesn't update the data field on the user
					// interface.
					if (mNotifyCharacteristic != null) {
						mBluetoothLeService.setCharacteristicNotification(
								mNotifyCharacteristic, false);
						mNotifyCharacteristic = null;
					}
					mBluetoothLeService.readCharacteristic(characteristic);
				}
				if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
					mNotifyCharacteristic = characteristic;
					mBluetoothLeService.setCharacteristicNotification(
							characteristic, true);
				}
				return true;
			}
			return false;
		}
	};

	/* ListView单击事件响应 */
	private OnItemClickListener listviewclick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub

			if (mGattCharacteristicMap != null) {
				if (arg2 != 0) {

					BluetoothGattCharacteristic chara = mGattCharacteristicMap
							.get(arg2 + "");
					if (chara != null) {
						String str = (String) adapter.getItem(arg2 - 1)
								+ "\nuuid:" + chara.getUuid().toString();
						if (null != str)
							Toast.makeText(getApplicationContext(), str,
									Toast.LENGTH_SHORT).show();

						/* 启动下一个activity做准备 */
						mBluetoothLeService.mCtrlCharacteristic = chara;
						Intent CharaCtrlActivity = new Intent();
						CharaCtrlActivity.putExtra("NAME", mDeviceName);
						CharaCtrlActivity.putExtra("UUID", chara.getUuid()
								.toString().substring(4, 8));

						// 转到下一个页面之前读一次属性值
						mBluetoothLeService
								.readCharacteristic(mBluetoothLeService.mCtrlCharacteristic);
						CharaCtrlActivity.setClass(DeviceControlActivity.this,
								CharacteristicCtrl.class);
						startActivity(CharaCtrlActivity);
					}
				}
			}
		}
	};

	private void clearUI() {
		// mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
		// mDataField.setText(R.string.no_data);
		adapter.clear();
		mGattListView.setAdapter((ListAdapter) null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gatt_services_characteristics);

		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
		mDeviceType = intent.getStringExtra(EXTRAS_DEVICE_TYPE);

		// Sets up UI references.
		getActionBar().setTitle("Gatt Service");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.actionbarbackground));
		getActionBar().setIcon(R.drawable.return_array);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

		// 加载list header
		mGattListView = (ListView) findViewById(R.id.gattlistview);
		View header = View.inflate(this, R.layout.listviewheader, null);// 头部内容
		mGattListView.addHeaderView(header, null, false);// 添加头部,不可点击

		// ListView Header内容设置
		TextView mTitle = (TextView) findViewById(R.id.listviewheader);
		mTitle.setText(mDeviceName);
		mListDeviceAddress = (TextView) findViewById(R.id.deviceaddress);
		mListDeviceAddress.setText(mDeviceAddress);
		mConnectionState = (TextView) findViewById(R.id.connectstate);
		mMtuChangeBtn = (Button) findViewById(R.id.mtu_change_btn);
		mMtuChangeBtn.setOnClickListener(mMtuChangeBtnListener);

		// ListView适配器
		adapter = new GroupListAdapter(this, list, listTag, mPropertylist);
		mGattListView.setOnItemClickListener(listviewclick);
		mGattListView.setDividerHeight(-4);

		/* 读取darkblue配置 */
		mLeConfigOper = LeConfigOper.getSingleLeObject(this);
		/* get voice index */
		if (mLeConfigOper.getConfig(LeConfigOper.BLE_VOICE_HID) != null)
			mVoiceHidIndex = Integer.valueOf(mLeConfigOper
					.getConfig(LeConfigOper.BLE_VOICE_HID));
		else
			mVoiceCmdIndex = 0;
		if (mLeConfigOper.getConfig(LeConfigOper.BLE_VOICE_CMD) != null)
			mVoiceCmdIndex = Integer.valueOf(mLeConfigOper
					.getConfig(LeConfigOper.BLE_VOICE_CMD));
		else
			mVoiceCmdIndex = 0;
		if (mLeConfigOper.getConfig(LeConfigOper.BLE_VOICE_DATA) != null)
			mVoiceDataIndex = Integer.valueOf(mLeConfigOper
					.getConfig(LeConfigOper.BLE_VOICE_DATA));
		else
			mVoiceDataIndex = 0;

	}

	private OnClickListener mMtuChangeBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mBluetoothLeService.exchangeMtuSize(163);

		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (mBluetoothLeService != null) {
			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
			Log.d(TAG, "Connect request result=" + result);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.gatt_services, menu);
		if (mConnected) {
			menu.findItem(R.id.menu_connect).setVisible(false);
			menu.findItem(R.id.menu_disconnect).setVisible(true);
		} else {
			menu.findItem(R.id.menu_connect).setVisible(true);
			menu.findItem(R.id.menu_disconnect).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_connect:
			mBluetoothLeService.connect(mDeviceAddress);
			return true;
		case R.id.menu_disconnect:
			mBluetoothLeService.disconnect();
			return true;
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateConnectionState(final int resourceId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mConnectionState.setText(resourceId);
			}
		});
	}

	private void displayData(String data) {
		if (data != null) {
			// mDataField.setText(data);
		}
	}

	// Demonstrates how to iterate through the supported GATT
	// Services/Characteristics.
	// In this sample, we populate the data structure that is bound to the
	// ExpandableListView
	// on the UI.
	private void displayGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null)
			return;
		int position = 1;
		int count = 0;
		boolean isVoiceFlag = true;
		String uuid = null;
		String unknownServiceString = getResources().getString(
				R.string.unknown_service);
		String unknownCharaString = getResources().getString(
				R.string.unknown_characteristic);

		for (BluetoothGattService gattService : gattServices) {
			uuid = gattService.getUuid().toString();
			count = 0;
			unknownServiceString = "UUID: " + uuid.substring(4, 8);
			listTag.add(SampleGattAttributes.lookupService(uuid,
					unknownServiceString));
			list.add(SampleGattAttributes.lookupService(uuid,
					unknownServiceString));
			mPropertylist.add(SampleGattAttributes.lookup(uuid,
					unknownServiceString));

			position++;

			List<BluetoothGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();
			ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

			// Loops through available Characteristics.
			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				charas.add(gattCharacteristic);

				mGattCharacteristicMap.put("" + position, gattCharacteristic);
				uuid = gattCharacteristic.getUuid().toString();

				// uuid的获取
				unknownCharaString = uuid;
				list.add(SampleGattAttributes.lookup(uuid, unknownCharaString));

				/* voice characteristic search */
				isVoiceFlag = false;
				if (gattService.getUuid().toString().substring(4, 8)
						.equals("1812")) {
					if ((mVoiceCmdIndex > gattCharacteristics.size())
							|| (mVoiceDataIndex > gattCharacteristics.size())
							|| (mVoiceHidIndex > gattCharacteristics.size())) {
						Toast.makeText(getApplicationContext(),
								"Voice Index Error!", Toast.LENGTH_SHORT)
								.show();
						continue;
					}
					// get voice hid key characteristic
					if (mVoiceHidIndex == count) {
						mBluetoothLeService.mVoiceCharacteristicMap.put(
								BluetoothLeService.VOICE_KEY_CHARA,
								gattCharacteristics.get(mVoiceHidIndex));
						System.out.println("mVoiceHidIndex:" + mVoiceHidIndex);
						isVoiceFlag = true;
					}
					// get voice data characteristic
					if (mVoiceDataIndex == count) {
						mBluetoothLeService.mVoiceCharacteristicMap.put(
								BluetoothLeService.VOICE_VALUE_CHARA,
								gattCharacteristics.get(mVoiceDataIndex));
						System.out
								.println("mVoiceDataIndex:" + mVoiceDataIndex);
						isVoiceFlag = true;
					}
					// get voice cmd characteristic
					if (mVoiceCmdIndex == count) {
						mBluetoothLeService.mVoiceCharacteristicMap.put(
								BluetoothLeService.VOICE_CMD_CHARA,
								gattCharacteristics.get(mVoiceCmdIndex));
						System.out.println("mVoiceCmdIndex:" + mVoiceCmdIndex);
						isVoiceFlag = true;
					}

				}
				if (isVoiceFlag) {
					int property = gattCharacteristic.getProperties();
					mPropertylist.add("[" + uuid.substring(4, 8) + "]["
							+ "voice" + "]" + getProperty(property));

					position++;
				} else {

					// property的获取
					int property = gattCharacteristic.getProperties();
					mPropertylist.add("[" + uuid.substring(4, 8) + "]"
							+ getProperty(property));

					position++;
				}
				count++;
			}
			mGattCharacteristics.add(charas);
		}

		mGattListView.setAdapter(adapter);
	}

	private String getProperty(int property) {
		String str = "";
		if (0 != (property & BluetoothGattCharacteristic.PROPERTY_WRITE)) {
			str = str + " write";

		}
		if (0 != (property & BluetoothGattCharacteristic.PROPERTY_READ)) {
			str = str + " read";

		}
		if (0 != (property & BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
			str = str + " notify";
		}
		if (0 != (property & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) {
			str = str + " write no resp";
		}
		if (0 != (property & BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
			str = str + " indecate";
		}
		return str;
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_MTU_CHANGED);
		return intentFilter;
	}

	@Override
	/**
	 * 返回按键按下时，如果将已经配对的设备清除
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		final BluetoothDevice device = mBluetoothLeService
				.getRemoteConnectDevice(mDeviceAddress);
		try {
			if ((device != null) && (mConnected))
				ClsUtils.removeBond(device.getClass(), device);
			Toast.makeText(getApplicationContext(), "unbond success!",
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return super.onKeyDown(keyCode, event);
	}

}
