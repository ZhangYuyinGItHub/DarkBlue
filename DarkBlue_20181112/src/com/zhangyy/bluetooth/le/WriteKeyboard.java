package com.zhangyy.bluetooth.le;

//import android.app.Activity;
//import android.os.Bundle;
//
//public class WriteKeyboard extends Activity {
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//	}
//
//}

import android.app.Activity;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class WriteKeyboard extends Activity {

	EditText edit;
	KeyboardView keyboardView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keyboardview);

		edit = (EditText) findViewById(R.id.edit);
		edit.setInputType(InputType.TYPE_NULL);
		edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showKeyboard();
			}
		});

		keyboardView = (KeyboardView) findViewById(R.id.keyboard_view);
		keyboardView.setKeyboard(new Keyboard(this, R.layout.keycontent));
		keyboardView.setEnabled(true);
		keyboardView.setPreviewEnabled(true);
		keyboardView
				.setOnKeyboardActionListener(new OnKeyboardActionListener() {
					@Override
					public void onKey(int primaryCode, int[] keyCodes) {
						Editable editable = edit.getText();
						int start = edit.getSelectionStart();
						if (primaryCode == Keyboard.KEYCODE_CANCEL) {
							hideKeyboard();
						} else if (primaryCode == Keyboard.KEYCODE_DELETE) {
							if (editable != null && editable.length() > 0) {
								editable.delete(start - 1, start);
							}
						} else if (primaryCode == 57419) { // go left
							if (start > 0) {
								edit.setSelection(start - 1);
							}
						} else if (primaryCode == 57421) { // go right
							if (start < edit.length()) {
								edit.setSelection(start + 1);
							}
						} else if (primaryCode == 58000) { // done
							String str = edit.getText().toString();
							// 数据是使用Intent返回
							Intent intent = new Intent();
							// 把返回数据存入Intent
							intent.putExtra("WRITEVALUE", str);
							setResult(RESULT_OK, intent);
							finish();
						} else {
							editable.insert(start,
									Character.toString((char) primaryCode));
						}
					}

					@Override
					public void onPress(int primaryCode) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onRelease(int primaryCode) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onText(CharSequence text) {
						// TODO Auto-generated method stub

					}

					@Override
					public void swipeDown() {
						// TODO Auto-generated method stub

					}

					@Override
					public void swipeLeft() {
						// TODO Auto-generated method stub

					}

					@Override
					public void swipeRight() {
						// TODO Auto-generated method stub

					}

					@Override
					public void swipeUp() {
						// TODO Auto-generated method stub

					}
				});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		String str = edit.getText().toString();
		// 数据是使用Intent返回
		Intent intent = new Intent();
		// 把返回数据存入Intent
		intent.putExtra("WRITEVALUE", str);
		setResult(RESULT_OK, intent);
		
	}

	private void showKeyboard() {
		// int visibility = keyboardView.getVisibility();
		// if (visibility == View.GONE || visibility == View.INVISIBLE) {
		// keyboardView.setVisibility(View.VISIBLE);
		// }
	}

	private void hideKeyboard() {
		// int visibility = keyboardView.getVisibility();
		// if (visibility == View.VISIBLE) {
		// keyboardView.setVisibility(View.INVISIBLE);
		// }
	}

}
