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
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
@SuppressLint("NewApi")
public class DeviceScanActivity extends Activity {

	public static final byte BLE_GAP_AD_TYPE_FLAGS = 0x01;
	/** < Flags for discoverability. */
	public static final byte BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_MORE_AVAILABLE = 0x02;
	/** < Partial list of 16 bit service UUIDs. */
	public static final byte BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_COMPLETE = 0x03;
	/** < Complete list of 16 bit service UUIDs. */
	public static final byte BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_MORE_AVAILABLE = 0x04;
	/** < Partial list of 32 bit service UUIDs. */
	public static final byte BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_COMPLETE = 0x05;
	/** < Complete list of 32 bit service UUIDs. */
	public static final byte BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_MORE_AVAILABLE = 0x06;
	/** < Partial list of 128 bit service UUIDs. */
	public static final byte BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_COMPLETE = 0x07;
	/** < Complete list of 128 bit service UUIDs. */
	public static final byte BLE_GAP_AD_TYPE_SHORT_LOCAL_NAME = 0x08;
	/** < Short local device name. */
	public static final byte BLE_GAP_AD_TYPE_COMPLETE_LOCAL_NAME = 0x09;
	/** < Complete local device name. */
	public static final byte BLE_GAP_AD_TYPE_TX_POWER_LEVEL = 0x0A;
	/** < Transmit power level. */
	public static final byte BLE_GAP_AD_TYPE_CLASS_OF_DEVICE = 0x0D;
	/** < Class of device. */
	public static final byte BLE_GAP_AD_TYPE_SIMPLE_PAIRING_HASH_C = 0x0E;
	/** < Simple Pairing Hash C. */
	public static final byte BLE_GAP_AD_TYPE_SIMPLE_PAIRING_RANDOMIZER_R = 0x0F;
	/** < Simple Pairing Randomizer R. */
	public static final byte BLE_GAP_AD_TYPE_SECURITY_MANAGER_TK_VALUE = 0x10;
	/** < Security Manager TK Value. */
	public static final byte BLE_GAP_AD_TYPE_SECURITY_MANAGER_OOB_FLAGS = 0x11;
	/** < Security Manager Out Of Band Flags. */
	public static final byte BLE_GAP_AD_TYPE_SLAVE_CONNECTION_INTERVAL_RANGE = 0x12;
	/** < Slave Connection Interval Range. */
	public static final byte BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_16BIT = 0x14;
	/** < List of 16-bit Service Solicitation UUIDs. */
	public static final byte BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_128BIT = 0x15;
	/** < List of 128-bit Service Solicitation UUIDs. */
	public static final byte BLE_GAP_AD_TYPE_SERVICE_DATA = 0x16;
	/** < Service Data. */
	public static final byte BLE_GAP_AD_TYPE_PUBLIC_TARGET_ADDRESS = 0x17;
	/** < Public Target Address. */
	public static final byte BLE_GAP_AD_TYPE_RANDOM_TARGET_ADDRESS = 0x18;
	/** < Random Target Address. */
	public static final byte BLE_GAP_AD_TYPE_APPEARANCE = 0x19;
	/** < Appearance. */
	public static final byte BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA = (byte) 0xFF;
	/** < Manufacturer Specific Data. */

	private LeDeviceListAdapter mLeDeviceListAdapter;
	ListView mDeviceListView;
	ArrayList<Map<String, Object>> mDeviceInfo = new ArrayList<Map<String, Object>>();
	Map<String, Object> mDeviceRssi = new HashMap<String, Object>();
	Map<String, Object> mDeviceScanResData = new HashMap<String, Object>();
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private Handler mHandler;

	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_FILTER_PARA = 2;
	private static final int REQUEST_INFO_PARA = 3;
	// 10秒后停止查找搜索.
	private static final long SCAN_PERIOD = 30000;
	private static ActionBar mActionBar = null;
	private final static int DIALOG = 1;

	private BluetoothDevice mSelectDevice;

	private Button mDeviceFilter;
	private Button mInfoBtn;
	private LeConfigOper mLeConfigOper;
	private String mBleRssiSWStr;
	private boolean mBleRssiSW = false;
	private int mRssiValue = 0;
	private String mRssiValueStr;
	private String mDeviceType = "PXP";

	private byte[] mScanResData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_scan);

		getActionBar().setTitle(R.string.title_devices);
		getActionBar().setIcon(R.drawable.scan);
		mHandler = new Handler();

		// getListView().setDivider(null);
		// getListView().setDividerHeight(3);

		mDeviceListView = (ListView) findViewById(R.id.device_listview);
		mDeviceFilter = (Button) findViewById(R.id.filterBtn);
		mDeviceFilter.setOnClickListener(filterBtnListener);

		mInfoBtn = (Button) findViewById(R.id.infoBtn);
		mInfoBtn.setOnClickListener(InfoBtnListener);

		// 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
					.show();
			finish();
		}

		// 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// 检查设备上是否支持蓝牙
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported,
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		// 设置Actonbar的背景
		mActionBar = getActionBar();
		mActionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbarbackground));

		/* 从数据库读取darkblue配置 */
		mLeConfigOper = LeConfigOper.getSingleLeObject(this);
		// mBleRssi = mLeConfigOper.getConfig(LeConfigOper.BLE_RSSI);
		mRssiValueStr = mLeConfigOper.getConfig(LeConfigOper.BLE_RSSI);
		if (mRssiValueStr == null) {
			mRssiValue = 0;
		} else {
			mRssiValue = Integer.parseInt(mRssiValueStr, 10);
		}
		mDeviceType = mLeConfigOper.getConfig(LeConfigOper.BLE_DEVICE_TYPE);
		mBleRssiSWStr = mLeConfigOper.getConfig(LeConfigOper.BLE_RSSI_SWITCH);
		if (null == mBleRssiSWStr) {
			mBleRssiSW = false;
		} else {
			if (mBleRssiSWStr.equals(LeConfigOper.BLE_RSSI_SWITCH_ON))
				mBleRssiSW = true;
			else
				mBleRssiSW = false;
		}

		mDeviceListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				new Bundle();
				//
				mSelectDevice = mLeDeviceListAdapter.getDevice(position);
				// showDialog(DIALOG, bundle);

				final Intent intent = new Intent();
				intent.setClass(getApplicationContext(),
						DeviceControlActivity.class);

				intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME,
						mSelectDevice.getName());
				intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS,
						mSelectDevice.getAddress());
				intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_TYPE,
						mDeviceType);
				if (mScanning) {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					mScanning = false;
				}
				startActivity(intent);

			}
		});

	}

	private Button.OnClickListener filterBtnListener = new Button.OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent filterIntent = new Intent();

			filterIntent.putExtra("RSSI_VALUE", mRssiValue);

			filterIntent.putExtra("IS_RSSI_FILTER_ENABLE", mBleRssiSW);
			if (mDeviceType == null)
				mDeviceType = "PXP";
			filterIntent.putExtra("DEVICE_TYPE", mDeviceType);
			filterIntent.setClass(DeviceScanActivity.this, DeviceFilter.class);
			startActivityForResult(filterIntent, REQUEST_FILTER_PARA);
		}

	};

	/**
	 * 功能：版本信息监听器
	 */
	private Button.OnClickListener InfoBtnListener = new Button.OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent infoIntent = new Intent();

			infoIntent.setClass(DeviceScanActivity.this, InfoActivity.class);
			startActivityForResult(infoIntent, REQUEST_INFO_PARA);
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		if (!mScanning) {
			menu.findItem(R.id.menu_stop).setVisible(false);
			menu.findItem(R.id.menu_scan).setVisible(true);
			menu.findItem(R.id.menu_refresh).setActionView(null);
		} else {
			menu.findItem(R.id.menu_stop).setVisible(true);
			menu.findItem(R.id.menu_scan).setVisible(false);
			menu.findItem(R.id.menu_refresh).setActionView(
					R.layout.actionbar_indeterminate_progress);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_scan:
			mLeDeviceListAdapter.clear();
			scanLeDevice(true);
			break;
		case R.id.menu_stop:
			scanLeDevice(false);
			break;
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}

		// Initializes list view adapter.
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		if (mDeviceListView != null)
			mDeviceListView.setAdapter(mLeDeviceListAdapter);
		scanLeDevice(true);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		} else if (requestCode == REQUEST_FILTER_PARA
				&& resultCode == Activity.RESULT_OK) {
			mRssiValue = data.getExtras().getInt("RSSI_VALUE");
			mBleRssiSW = data.getExtras().getBoolean("IS_RSSI_FILTER_ENABLE");
			mDeviceType = data.getExtras().getString("DEVICE_TYPE");

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
		mLeDeviceListAdapter.clear();
	}

	// @Override
	// protected void onListItemClick(ListView l, View v, int position, long id)
	// {
	// System.out.println("==position==" + position);
	//
	// Bundle bundle = new Bundle();
	//
	// mSelectDevice = mLeDeviceListAdapter.getDevice(position);
	//
	// showDialog(DIALOG, bundle);
	//
	// // Map<String, Object> map;
	// // map = mDeviceInfo.get(position);
	// // String str = null;
	// // if (null != map)
	// // str = (String) map.get("DEVICENAME");
	// //
	// // Toast.makeText(getApplicationContext(), position + str,
	// // Toast.LENGTH_SHORT).show();
	// }

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					invalidateOptionsMenu();
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
		invalidateOptionsMenu();
	}

	// Adapter for holding devices found through scanning.
	private class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = DeviceScanActivity.this.getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device) {
			if (!mLeDevices.contains(device)) {
				mLeDevices.add(device);
			}
		}

		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		public void clear() {
			mLeDevices.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder;
			// General ListView optimization code.
			if (view == null) {
				view = mInflator.inflate(R.layout.listitem_device, null);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView) view
						.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView) view
						.findViewById(R.id.device_name);
				viewHolder.devicerssi = (TextView) view.findViewById(R.id.rssi);
				viewHolder.deviceImage = (ImageView) view
						.findViewById(R.id.rssi_image);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			BluetoothDevice device = mLeDevices.get(i);
			/* item device name set */
			final String deviceName = device.getName();
			if (deviceName != null && deviceName.length() > 0)
				viewHolder.deviceName.setText(deviceName);
			else
				viewHolder.deviceName.setText(R.string.unknown_device);

			/* item device address set */
			viewHolder.deviceAddress.setText(device.getAddress());

			/* item device rssi set */
			if (mDeviceRssi.get(device.getAddress()) != null) {
				viewHolder.devicerssi.setText(mDeviceRssi.get(
						device.getAddress()).toString());

				int rssi = Integer.valueOf(
						mDeviceRssi.get(device.getAddress()).toString())
						.intValue();
				if (rssi <= -90)
					viewHolder.deviceImage.setImageResource(R.drawable.rssi0);
				else if ((rssi >= -89) && (rssi <= -76))
					viewHolder.deviceImage.setImageResource(R.drawable.rssi1);
				else if ((rssi >= -77) && (rssi <= -64))
					viewHolder.deviceImage.setImageResource(R.drawable.rssi2);
				else if ((rssi >= -65) && (rssi <= -52))
					viewHolder.deviceImage.setImageResource(R.drawable.rssi3);
				else if ((rssi >= -53) && (rssi <= -42))
					viewHolder.deviceImage.setImageResource(R.drawable.rssi4);
				else if (rssi >= -30)
					viewHolder.deviceImage.setImageResource(R.drawable.rssi5);
			}

			/* item device scan res data set */
			byte[] deviceScanData = (byte[]) mDeviceScanResData.get(device
					.getAddress());
			if (deviceScanData != null) {
				
			}

			return view;
		}
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				final byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {

					if (mBleRssiSW)
						if ((mRssiValue - 100) > rssi) {

							return;
						}

					mLeDeviceListAdapter.addDevice(device);
					mLeDeviceListAdapter.notifyDataSetChanged();

					// Map<String, Object> map = new HashMap<String, Object>();
					mDeviceRssi.put(device.getAddress(), rssi + "");
					// String str = advDataParse(BLE_GAP_AD_TYPE_APPEARANCE,
					// scanRecord);
					// map.put("LOCALNAME", str);
					// map.put("DEVICENAME", device.getName());
					// mDeviceInfo.add(map);
					mDeviceScanResData.put(device.getAddress(), scanRecord);
				}
			});
		}
	};

	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
		TextView devicerssi;
		ImageView deviceImage;
	}

	/*
	 * 函数功能：解析广播数据将解析的数据放到容器中
	 */
	private String advDataParse(byte type, byte[] inByte) {
		String str = null;
		int index = 0;
		int length;

		length = inByte.length;
		while (index < (length - 1)) {
			int field_length = inByte[index];
			byte field_type = inByte[index + 1];

			if (field_type == type) {
				int i;
				for (i = 0; i < field_length - 1; i++) {
					str = str + inByte[index + 2 + i];
				}
				return str;
			}
			index += field_length + 1;
		}
		return null;
	}
}