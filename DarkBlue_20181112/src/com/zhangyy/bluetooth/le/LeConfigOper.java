package com.zhangyy.bluetooth.le;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class LeConfigOper {

	public final static String BLE_RSSI = "BLE_RSSI";
	public final static String BLE_RSSI_SWITCH = "BLE_RSSI_SWITCH";
	public final static String BLE_RSSI_SWITCH_ON = "BLE_RSSI_SWITCH_ON";
	public final static String BLE_RSSI_SWITCH_OFF = "BLE_RSSI_SWITCH_OFF";
	public final static String BLE_DEVICE_TYPE = "BLE_DEVICE_TYPE";
	
	public final static String BLE_VOICE_CMD = "BLE_VOICE_CMD";
	public final static String BLE_VOICE_HID = "BLE_VOICE_HID";
	public final static String BLE_VOICE_DATA = "BLE_VOICE_DATA";

	private LeConfigSQLiteOpenHelper mDarkBlueDb;
	private static volatile LeConfigOper singDarkBlueObject = null;

	private LeConfigOper(Context context) {
		mDarkBlueDb = new LeConfigSQLiteOpenHelper(context);
	}

	/**
	 * ��ȡ���ݿ��������
	 * 
	 */
	public static LeConfigOper getSingleLeObject(Context context) {
		if (singDarkBlueObject == null) {
			singDarkBlueObject = new LeConfigOper(context);

		}

		return singDarkBlueObject;
	}

	/**
	 * ���һ�����ݼ�¼
	 * 
	 * @param ray_value
	 * @param time
	 */
	public boolean addItem(String configName, String configValue) {

		boolean ret = false;
		SQLiteDatabase db = mDarkBlueDb.getWritableDatabase();
		db.execSQL("insert into iconfig (name, value) values (?, ?)",
				new String[] { configName, configValue });

		db.close();
		ret = findItem(configName, configValue);
		return ret;

	}

	/**
	 * ��ѯһ�����ݼ�¼
	 * 
	 * @param ray_value
	 * @param time
	 */
	public boolean findItem(String configName, String configValue) {
		boolean ret = false;
		SQLiteDatabase db = mDarkBlueDb.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from iconfig where name = ? and value = ?",
				new String[] { configName, configValue });
		if (cursor.getCount() != 0)
			ret = true;
		cursor.close();
		db.close();

		return ret;
	}

	/**
	 * ��ȡһ����������
	 * 
	 * @param ray_value
	 * @param time
	 */
	public String getConfig(String configName) {
		boolean ret = false;
		String configValue = null;
		SQLiteDatabase db = mDarkBlueDb.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from iconfig where name = ?",
				new String[] { configName });
		for (; cursor.moveToNext() == true;) {

			configValue = cursor.getString(cursor.getColumnIndex("value"));

		}
		cursor.close();
		db.close();

		return configValue;
	}

	/**
	 * �޸�һ�����ݼ�¼
	 * 
	 * @param ��������
	 * @param ����ֵ
	 */
	public boolean updateItem(String configName, String configValue) {
		boolean ret = false;
		SQLiteDatabase db = mDarkBlueDb.getReadableDatabase();
		db.execSQL("update iconfig set value=? where name=?", new String[] {
				configValue, configName });

		db.close();
		if (false == findItem(configName, configValue))
			System.out.println("update failed, please check!");
		else
			System.out.println("update success!");

		return ret;
	}

	/**
	 * 
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 */
	public void deleteItems(String month, String day, String hour,
			String minute, String second) {
		String time = "";
		if ((month == null) || (month.equals("")))
			time = "2016-";
		else if ((day == null) || (day.equals("")))
			time = "2016-" + month;
		else if ((hour == null) || (hour.equals("")))
			time = "2016-" + month + "-" + day;
		else if ((minute == null) || (minute.equals("")))
			time = "2016-" + month + "-" + day + " " + hour;
		else if ((second == null) || (second.equals("")))
			time = "2016-" + month + "-" + day + " " + hour + ":" + minute;
		else
			time = "2016-" + month + "-" + day + " " + hour + ":" + minute
					+ ":" + second;

		System.out.print("-----" + time + "-----");

		SQLiteDatabase db = mDarkBlueDb.getWritableDatabase();

		db.execSQL("delete from iconfig where time like '" + time + "%'");
		db.close();
	}

	/**
	 * ����darkblue������
	 * 
	 */
	public boolean exportItems() {

		SQLiteDatabase db = mDarkBlueDb.getReadableDatabase();

		Cursor cursor = db.rawQuery("select * from iconfig", null);

		for (; cursor.moveToNext() == true;) {

			int pos = cursor.getPosition();

			String name = cursor.getString(cursor.getColumnIndex("name"));
			String value = cursor.getString(cursor.getColumnIndex("value"));

			String item = pos + "  " + name + "  " + value + "\n";

			// String rayId = cursor.getString(cursor.getColumnIndex("id"));
			FileToScard("darkblue_Config.txt", item);

		}
		cursor.close();
		db.close();

		return true;
	}

	/**
	 * @author zhangyuyin
	 * @category �����ļ���SD��
	 * @param filename
	 *            �ļ���
	 * @param content
	 *            �ļ�������
	 */
	public boolean FileToScard(String filename, String content) {
		boolean flag = false;
		FileOutputStream mFileOutputStream = null;
		// ���SD�����ڵ�·��
		File mFile = new File(Environment.getExternalStorageDirectory(),
				filename);
		// �ж�SD���Ƿ����
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				/**
				 * FileOutputStream ���캯���У���������������һ�����ļ������ڶ������ļ��Ķ�д��ʽ��Boolean���ͣ�
				 * Ĭ����false�����ԭ������������д�룬������Ϊtrue��ѡ��׷�ӵķ�ʽ��
				 */
				mFileOutputStream = new FileOutputStream(mFile, true);
				mFileOutputStream.write(content.getBytes());
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
		}
		return flag;
	}

}
