package com.zhangyy.bluetooth.le;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RcuVoiceActivity extends Activity {

	private BluetoothLeService mBluetoothLeService;

	Button mVoiceClearBtn;
	Button mVoiceExportBtn;
	TextView mVoiceRevTextView;

	String mVoiceRevStr = "";

	VoiceDataProcessThread mVoiceProcessThread;

	// short[] mVoiceDecodeArr;

	int[] array = { 1, 2, 3, 4, 5 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rcu_voice_layout);
		getActionBar().hide();

		/* Service bind */
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

		mVoiceClearBtn = (Button) findViewById(R.id.mClearVoiceData);
		mVoiceClearBtn.setOnClickListener(mVoiceClearListener);
		mVoiceExportBtn = (Button) findViewById(R.id.mExportVoiceData);
		mVoiceExportBtn.setOnClickListener(mVoiceExportListener);
		mVoiceRevTextView = (TextView) findViewById(R.id.voice_rev);
		mVoiceRevTextView.setMovementMethod(ScrollingMovementMethod
				.getInstance());

		mVoiceProcessThread = new VoiceDataProcessThread();
		mVoiceProcessThread.start();
	}

	private OnClickListener mVoiceClearListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mBluetoothLeService.setCharacteristicNotification(
					mBluetoothLeService.mVoiceCharacteristicMap
							.get(BluetoothLeService.VOICE_VALUE_CHARA), true);

			mVoiceRevStr = "";
			mVoiceRevTextView.setText(mVoiceRevStr);
		}
	};
	private OnClickListener mVoiceExportListener = new OnClickListener() {

		@SuppressWarnings({ "unused" })
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// mVoiceRevTextView.setText(getStringFromC());
			int a[] = encodeArray(array);
			// 不需要返回值，实际操作的是同一块内存，内容已经发生了改变
			for (int i : a) {
				System.out.println(i);
			}
		}
	};

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				Toast.makeText(getApplicationContext(), "device is Connected!",
						Toast.LENGTH_SHORT).show();
				// mDeviceState.setText("Connected");

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				Toast.makeText(getApplicationContext(),
						"Service is Disconnected!", Toast.LENGTH_SHORT).show();
				// mDeviceState.setText("Disconnect");
			} else if (BluetoothLeService.ACTION_DATA_READ.equals(action)) {
				/* 属性数据的读操作操作 */
				// Toast.makeText(getApplicationContext(), "get data",
				// Toast.LENGTH_SHORT).show();

			} else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {
				/* 属性数据的写操作 */
				mBluetoothLeService.setCharacteristicNotification(
						mBluetoothLeService.mVoiceCharacteristicMap
								.get(BluetoothLeService.VOICE_VALUE_CHARA),
						true);
				System.out.println("--->write value");

			} else if (BluetoothLeService.ACTION_DATA_NOTIFY.equals(action)) {
				/* 属性通知操作 */
				System.out.println("--->receive characteristic changed:"
						+ intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

			} else if (BluetoothLeService.ACTION_VOICE_DATA_NOTIFY
					.equals(action)) {
				/* 属性语音操作 */
				/* send vocie data to new thread for process */
				Message msg = new Message();
				msg.what = 0x1400;
				Bundle bundle = new Bundle();
				bundle.putByteArray("VOICE_DATA",
						intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
				msg.setData(bundle);

				// 向新线程中的Handler发送消息
				mVoiceProcessThread.mHandler.sendMessage(msg);

			} else if (BluetoothLeService.ACTION_MTU_CHANGED.equals(action)) {
				System.out.println("--->Voice Mtu Size is :"
						+ intent.getIntExtra(BluetoothLeService.EXTRA_DATA, 0));
			}
		}
	};

	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {

			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			Toast.makeText(getApplicationContext(), "Service is Connected!",
					Toast.LENGTH_SHORT).show();
			/* set key map enable */
			mBluetoothLeService.setCharacteristicNotification(
					mBluetoothLeService.mVoiceCharacteristicMap
							.get(BluetoothLeService.VOICE_KEY_CHARA), true);

		}

		public void onServiceDisconnected(ComponentName componentName) {
			Toast.makeText(getApplicationContext(), "onServiceDisconnected",
					Toast.LENGTH_SHORT).show();
			mBluetoothLeService = null;

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
		intentFilter.addAction(BluetoothLeService.ACTION_VOICE_DATA_NOTIFY);
		return intentFilter;
	}

	/**
	 * @author zhangyuyin
	 * @category 导出文件到SD卡
	 * @param filename
	 *            文件名
	 * @param content
	 *            文件的内容
	 */
	public boolean FileToScard(String filename, byte[] content) {
		boolean flag = false;
		FileOutputStream mFileOutputStream = null;
		if ((content == null) || (content.length == 0))
			return false;
		// 获得SD卡所在的路径
		File mFile = new File(Environment.getExternalStorageDirectory(),
				filename);
		// 判断SD卡是否可用
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				/**
				 * FileOutputStream 构造函数中，有两个参数，第一个是文件名；第二个是文件的读写方式，Boolean类型，
				 * 默认是false即清除原来的数据重新写入，若设置为true则选择追加的方式。
				 */
				mFileOutputStream = new FileOutputStream(mFile, true);
				mFileOutputStream.write(content);
				// Toast.makeText(RcuVoiceActivity.this, mFile.getName(),
				// Toast.LENGTH_SHORT).show();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (mFileOutputStream != null) {
					try {
						mFileOutputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		} else {
			System.out.println("Sd card can not be used!!");
		}
		return flag;
	}

	/**
	 * define a new thread class
	 */
	class VoiceDataProcessThread extends Thread {
		public Handler mHandler;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			mHandler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					// super.handleMessage(msg);
					if (msg.what == 0x1400) {
						byte[] data = msg.getData().getByteArray("VOICE_DATA");
						// mVoiceDecodeArr = new short[mVoiceEnCodeArr.length *
						// 2];
						//
						// DeCode(mVoiceEnCodeArr, mVoiceEnCodeArr.length,
						// mVoiceDecodeArr);
						FileToScard("RcuVoice.dat", data);

						// FileToScard("RcuVoiceDecode.dat", outdata1);
						System.out.println("<---");
					}
				}

				@Override
				public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
					// TODO Auto-generated method stub
					return super.sendMessageAtTime(msg, uptimeMillis);
				}

			};
			Looper.loop();// 直到消息队列循环结束
			// super.run();
			mHandler.sendEmptyMessage(0);
		}

	}

	/* message hadle */
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 0) {
				mVoiceRevTextView.setText(mVoiceRevStr);
				System.out.println("---->rev data from subThread.");
			} else if (msg.what == 1) {
				String str = msg.getData().getString("VOICE_DATA");
				mVoiceRevStr += str;
				mVoiceRevTextView.setText(mVoiceRevStr);
				// FileToScard("RcuVoice.txt", str);
				System.out.println("--->");
			}

			super.handleMessage(msg);
		}
	};

	/****************************** JNI *************************************/
	private native void DeCode(byte[] arrin, int len, short[] arrout);

	private native String GetString(byte[] bytein);

	private native int[] encodeArray(int[] arr);


}
