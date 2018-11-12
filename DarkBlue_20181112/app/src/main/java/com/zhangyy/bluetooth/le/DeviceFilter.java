package com.zhangyy.bluetooth.le;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.KeyEvent;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.zhangyy.bluetooth.le.R;

public class DeviceFilter extends Activity {

	private Switch mSwitchBtn;
	private SeekBar mRssiSeekBar;
	private TextView mRssiValueTextView;

	private RadioGroup mRGPeripheralType;
	private RadioButton mPxpRadioBtn;
	private RadioButton mRcuRadioBtn;
	private ImageView mDeviceTypeImageView;

	private int mRssiValue = -100;
	private boolean mIsRssiEnable = false;
	private String mDeviceType = "PXP";

	/* voice setting */
	private Spinner mVoiceHidIndexSpinner;
	private Spinner mVoiceCmdIndexSpinner;
	private Spinner mVoiceDataIndexSpinner;

	private LeConfigOper mLeConfigOper;

	/* voice setting */
	// Spinner voice

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_filter);

		/* 读取darkblue配置 */
		mLeConfigOper = LeConfigOper.getSingleLeObject(this);

		Intent intent = getIntent();
		mRssiValue = intent.getExtras().getInt("RSSI_VALUE");
		mIsRssiEnable = intent.getExtras().getBoolean("IS_RSSI_FILTER_ENABLE");
		mDeviceType = intent.getExtras().getString("DEVICE_TYPE");

		mSwitchBtn = (Switch) findViewById(R.id.filter_switch);
		mSwitchBtn.setOnCheckedChangeListener(mSwitchBtnListener);
		mSwitchBtn.setChecked(mIsRssiEnable);

		mRssiSeekBar = (SeekBar) findViewById(R.id.rssi_seek);
		mRssiSeekBar.setMax(70);
		mRssiSeekBar.setOnSeekBarChangeListener(mOnseekBarChangeListener);

		mRssiValueTextView = (TextView) findViewById(R.id.rssi_value);
		mRssiValueTextView.setText(mRssiValue - 100 + " dB");

		mRGPeripheralType = (RadioGroup) findViewById(R.id.devie_type);
		mRGPeripheralType.setOnCheckedChangeListener(mGroupRadioListener);
		mPxpRadioBtn = (RadioButton) findViewById(R.id.no_bond_device);
		mRcuRadioBtn = (RadioButton) findViewById(R.id.bond_device);
		mDeviceTypeImageView = (ImageView) findViewById(R.id.device_type_image);
		if (mIsRssiEnable) {
			mRssiSeekBar.setProgress(mRssiValue);
			mRssiValueTextView.setText((mRssiValue - 100) + " dB");

		} else {
			mRssiSeekBar.setProgress(mRssiValue);
			mRssiValueTextView.setText("---");
		}
		if (mDeviceType.equals("RCU")) {
			mPxpRadioBtn.setChecked(false);
			mRcuRadioBtn.setChecked(true);
			mDeviceTypeImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.device_bond));
		} else {
			mPxpRadioBtn.setChecked(true);
			mRcuRadioBtn.setChecked(false);
			mDeviceTypeImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.device_nobond));
		}
		/* voice setting */
		mVoiceHidIndexSpinner = (Spinner) findViewById(R.id.voice_hid_index);
		mVoiceCmdIndexSpinner = (Spinner) findViewById(R.id.voice_cmd_index);
		mVoiceDataIndexSpinner = (Spinner) findViewById(R.id.voice_data_index);

		/* recover the voice config */
		/* get voice index */
		if (mLeConfigOper.getConfig(LeConfigOper.BLE_VOICE_HID) != null)
			mVoiceHidIndexSpinner.setSelection(Integer.parseInt(mLeConfigOper
					.getConfig(LeConfigOper.BLE_VOICE_HID)));
		else
			mVoiceHidIndexSpinner.setSelection(0);
		if (mLeConfigOper.getConfig(LeConfigOper.BLE_VOICE_CMD) != null)
			mVoiceCmdIndexSpinner.setSelection(Integer.parseInt(mLeConfigOper
					.getConfig(LeConfigOper.BLE_VOICE_CMD)));
		else
			mVoiceCmdIndexSpinner.setSelection(0);
		if (mLeConfigOper.getConfig(LeConfigOper.BLE_VOICE_DATA) != null)
			mVoiceDataIndexSpinner.setSelection(Integer.parseInt(mLeConfigOper
					.getConfig(LeConfigOper.BLE_VOICE_DATA)));
		else
			mVoiceDataIndexSpinner.setSelection(0);

		getActionBar().setTitle("DeviceFilter");
		getActionBar().setIcon(R.drawable.filtersetting);
		// 设置Actonbar的背景
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.actionbarbackground));

	}

	private Switch.OnCheckedChangeListener mSwitchBtnListener = new Switch.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			if (buttonView == null)
				return;
			if (isChecked) {
				if ((mRssiSeekBar != null) && (mRssiValueTextView != null)) {
					mRssiSeekBar.setEnabled(true);
					mRssiValue = mRssiSeekBar.getProgress();
					mRssiValueTextView.setText((mRssiValue - 100) + " dB");
					mIsRssiEnable = true;

					mRssiValueTextView.setTextColor(Color.rgb(255, 255, 255));
				}

			} else {
				mRssiValue = mRssiSeekBar.getProgress();
				mRssiValueTextView.setText("--");
				mRssiValueTextView.setTextColor(Color.rgb(208, 208, 208));
				mRssiSeekBar.setEnabled(false);
				mIsRssiEnable = false;
			}
		}
	};
	private OnSeekBarChangeListener mOnseekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			mRssiValue = mRssiSeekBar.getProgress();
			mRssiValueTextView.setText((mRssiValue - 100) + " dB");
			// Toast.makeText(getApplicationContext(), "-" + mRssiValue + " dB",
			// Toast.LENGTH_SHORT).show();
		}
	};

	private OnCheckedChangeListener mGroupRadioListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if (checkedId == mPxpRadioBtn.getId()) {
				mDeviceType = "PXP";
				mDeviceTypeImageView.setImageDrawable(getResources()
						.getDrawable(R.drawable.device_nobond));
			} else if (checkedId == mRcuRadioBtn.getId()) {
				mDeviceType = "RCU";
				mDeviceTypeImageView.setImageDrawable(getResources()
						.getDrawable(R.drawable.device_bond));
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (mLeConfigOper.getConfig(LeConfigOper.BLE_DEVICE_TYPE) == null) {
			mLeConfigOper.addItem(LeConfigOper.BLE_DEVICE_TYPE, mDeviceType);
		} else {
			mLeConfigOper.updateItem(LeConfigOper.BLE_DEVICE_TYPE, mDeviceType);
		}

		if (mLeConfigOper.getConfig(LeConfigOper.BLE_RSSI_SWITCH) == null) {
			if (mIsRssiEnable)
				mLeConfigOper.addItem(LeConfigOper.BLE_RSSI_SWITCH,
						LeConfigOper.BLE_RSSI_SWITCH_ON);
			else
				mLeConfigOper.addItem(LeConfigOper.BLE_RSSI_SWITCH,
						LeConfigOper.BLE_RSSI_SWITCH_OFF);
		} else {
			if (mIsRssiEnable)
				mLeConfigOper.updateItem(LeConfigOper.BLE_RSSI_SWITCH,
						LeConfigOper.BLE_RSSI_SWITCH_ON);
			else
				mLeConfigOper.updateItem(LeConfigOper.BLE_RSSI_SWITCH,
						LeConfigOper.BLE_RSSI_SWITCH_OFF);
		}

		if (mLeConfigOper.getConfig(LeConfigOper.BLE_RSSI) == null) {
			mLeConfigOper.addItem(LeConfigOper.BLE_RSSI, mRssiValue + "");
		} else {
			mLeConfigOper.updateItem(LeConfigOper.BLE_RSSI, mRssiValue + "");
		}

		/* voice stetting save */
		if (mLeConfigOper.getConfig(LeConfigOper.BLE_VOICE_CMD) == null) {
			mLeConfigOper.addItem(LeConfigOper.BLE_VOICE_CMD,
					mVoiceCmdIndexSpinner.getSelectedItemId() + "");
		} else {
			mLeConfigOper.updateItem(LeConfigOper.BLE_VOICE_CMD,
					mVoiceCmdIndexSpinner.getSelectedItemId() + "");
		}
		if (mLeConfigOper.getConfig(LeConfigOper.BLE_VOICE_HID) == null) {
			mLeConfigOper.addItem(LeConfigOper.BLE_VOICE_HID,
					mVoiceHidIndexSpinner.getSelectedItemId() + "");
		} else {
			mLeConfigOper.updateItem(LeConfigOper.BLE_VOICE_HID,
					mVoiceHidIndexSpinner.getSelectedItemId() + "");
		}
		if (mLeConfigOper.getConfig(LeConfigOper.BLE_VOICE_DATA) == null) {
			mLeConfigOper.addItem(LeConfigOper.BLE_VOICE_DATA,
					mVoiceDataIndexSpinner.getSelectedItemId() + "");
		} else {
			mLeConfigOper.updateItem(LeConfigOper.BLE_VOICE_DATA,
					mVoiceDataIndexSpinner.getSelectedItemId() + "");
		}

		// mLeConfigOper.addItem(LeConfigOper.BLE_VOICE_HID, mRssiValue + "");
		// mLeConfigOper.addItem(LeConfigOper.BLE_VOICE_DATA, mRssiValue + "");

		mLeConfigOper.exportItems();/* 导出darkblue的配置 */

		Intent intent = new Intent();
		intent.putExtra("IS_RSSI_FILTER_ENABLE", mIsRssiEnable);
		intent.putExtra("RSSI_VALUE", mRssiValue);
		intent.putExtra("DEVICE_TYPE", mDeviceType);
		setResult(RESULT_OK, intent);
		finish();

		return super.onKeyDown(keyCode, event);
	}

}