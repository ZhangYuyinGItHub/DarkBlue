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

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access");
        attributes.put("00001813-0000-1000-8000-00805f9b34fb", "Scan Parameters");
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put("0000180f-0000-1000-8000-00805f9b34fb", "Battery Service");
        attributes.put("00001802-0000-1000-8000-00805f9b34fb", "Immediate Alert");
        attributes.put("00001803-0000-1000-8000-00805f9b34fb", "Link Loss");
        attributes.put("00001804-0000-1000-8000-00805f9b34fb", "Tx Power");
        attributes.put("00001801-0000-1000-8000-00805f9b34fb", "Generic Attribute");
        attributes.put("00001812-0000-1000-8000-00805f9b34fb", "Human Interface Device");
        attributes.put("00002a06-0000-1000-8000-00805f9b34fb", "Alert Level");
        attributes.put("00002a07-0000-1000-8000-00805f9b34fb", "Tx Power Level");
        // Sample Characteristics.Device Name
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a00-0000-1000-8000-00805f9b34fb", "Device Name");
        attributes.put("00002a01-0000-1000-8000-00805f9b34fb", "Appearance");
        attributes.put("00002a04-0000-1000-8000-00805f9b34fb", "Preferred Connection Parameters");
        attributes.put("00002a4d-0000-1000-8000-00805f9b34fb", "Characteristic Report");
        attributes.put("00002a4b-0000-1000-8000-00805f9b34fb", "Characteristic Report Map");
        attributes.put("00002a4a-0000-1000-8000-00805f9b34fb", "HID Information");
        attributes.put("00002a4c-0000-1000-8000-00805f9b34fb", "HID Control Point");
        attributes.put("00002a4e-0000-1000-8000-00805f9b34fb", "Protocol Mode");
        attributes.put("00002a05-0000-1000-8000-00805f9b34fb", "Service Changed");
        attributes.put("00002a19-0000-1000-8000-00805f9b34fb", "Battery Level");
        attributes.put("00002a24-0000-1000-8000-00805f9b34fb", "Model Number String");
        attributes.put("00002a25-0000-1000-8000-00805f9b34fb", "Serial Number String");
        attributes.put("00002a26-0000-1000-8000-00805f9b34fb", "Firmware Revision String");
        attributes.put("00002a27-0000-1000-8000-00805f9b34fb", "Hardware Revision String");
        attributes.put("00002a28-0000-1000-8000-00805f9b34fb", "Software Revision String");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put("00002a23-0000-1000-8000-00805f9b34fb", "System ID");
        attributes.put("00002a2a-0000-1000-8000-00805f9b34fb", "IEEE 11073-20601 Regulatory Certification Data List");
        attributes.put("00002a50-0000-1000-8000-00805f9b34fb", "System PnP ID");
        attributes.put("00002a22-0000-1000-8000-00805f9b34fb", "Boot Keyboard Input Report");
        attributes.put("00002a32-0000-1000-8000-00805f9b34fb", "Boot Keyboard Output Report");
        attributes.put("00002a33-0000-1000-8000-00805f9b34fb", "Boot Mouse Input Report");
        attributes.put("00002a4f-0000-1000-8000-00805f9b34fb", "Scan Interval Window");
        attributes.put("00002a31-0000-1000-8000-00805f9b34fb", "Scan Refresh");

    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    public static String lookupService(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : (name + "[" + uuid.substring(4, 8) + "]");
    }

    /*ad-type flags*/
    private static HashMap<Integer, String> flags = new HashMap();

    static {
        flags.put(0x01, "LE Limited Discoverable Mode");
        flags.put(0x02, "LE General Discoverable Mode");
        flags.put(0x04, "BR/EDR Not Supported");
        flags.put(0x08, "Simultaneous LE and BR/EDR to Same Device Capable (Controller)");
        flags.put(0x10, "Simultaneous LE and BR/EDR to Same Device Capable (Host)");
    }

    public static String lookupflags(int flag) {
        String str = "";
        if (0x01 == (flag & 0x01))
            str += flags.get(0x01) + "\n";
        if (0x02 == (flag & 0x02))
            str += flags.get(0x02) + "\n";
        if (0x04 == (flag & 0x04))
            str += flags.get(0x04) + "\n";
        if (0x08 == (flag & 0x08))
            str += flags.get(0x08) + "\n";
        if (0x10 == (flag & 0x10))
            str += flags.get(0x10) + "\n";

        return str;
    }

    /*appearance*/
    private static HashMap<Integer, String> appearance = new HashMap();

    static {
        appearance.put(64, "Generic Phone");
        appearance.put(128, "Generic Computer");
        appearance.put(192, "Generic Watch");
        appearance.put(193, "Watch: Sports Watch");
        appearance.put(256, "Generic Clock");
        appearance.put(320, "Generic Display");
        appearance.put(384, "Generic Remote Control");
        appearance.put(448, "Generic Eye-glasses");
        appearance.put(512, "Generic Tag");
        appearance.put(576, "Generic Keyring");
        appearance.put(640, "Generic Media Player");
        appearance.put(704, "Generic Barcode Scanner");
        appearance.put(768, "Generic Thermometer");
        appearance.put(769, "Thermometer: Ear");
        appearance.put(832, "Generic Heart rate Sensor");
        appearance.put(833, "Heart Rate Sensor: Heart Rate Belt");
        appearance.put(896, "Generic Blood Pressure");
        appearance.put(897, "Blood Pressure: Arm");
        appearance.put(898, "Blood Pressure: Wrist");
        appearance.put(960, "Human Interface Device (HID)");
        appearance.put(961, "Keyboard");
        appearance.put(962, "Mouse");
        appearance.put(963, "Joystick");
        appearance.put(964, "Gamepad");
        appearance.put(965, "Digitizer Tablet");
        appearance.put(966, "Card Reader");
        appearance.put(967, "Digital Pen");
        appearance.put(968, "Barcode Scanner");
        appearance.put(1024, "Generic Glucose Meter");
        appearance.put(1088, "Generic: Running Walking Sensor");
        appearance.put(1089, "Running Walking Sensor: In-Shoe");
        appearance.put(1090, "Running Walking Sensor: On-Shoe");
        appearance.put(1091, "Running Walking Sensor: On-Hip");
        appearance.put(1152, "Generic: Cycling");
        appearance.put(1153, "Cycling: Cycling Computer");
        appearance.put(1154, "Cycling: Speed Sensor");
        appearance.put(1155, "Cycling: Cadence Sensor");
        appearance.put(1156, "Cycling: Power Sensor");
        appearance.put(1157, "Cycling: Speed and Cadence Sensor");
        appearance.put(3136, "Generic: Pulse Oximeter");
        appearance.put(3137, "Fingertip");
        appearance.put(3138, "Wrist Worn");
        appearance.put(3200, "Generic: Weight Scale");
        appearance.put(5184, "Generic: Outdoor Sports Activity");
        appearance.put(5185, "Location Display Device");
        appearance.put(5186, "Location and Navigation Display Device");
        appearance.put(5187, "Location Pod");
        appearance.put(5188, "Location and Navigation Pod");
    }

    public static String lookupappearance(byte[] value) {

        int index = (int) (((value[1] & 0xff) << 8) | (value[0] & 0xff));
        String str = appearance.get(index & 0xffff);

        if (str == null)
            str = "";
        else
            str += "\n";

        return str;
    }

    /*find up uuids*/
    public static String lookup16bitsuuid(byte[] value, int length) {

        String str = "";
        for (int index = 0; index <= length - 2; index = index + 2) {
            StringBuilder stringBuilder = new StringBuilder(length);
            stringBuilder.append(String.format("%02X", value[index + 1]));
            stringBuilder.append(String.format("%02X", value[index]));
            str += "0x" + stringBuilder.toString() + "  ";
        }
        str += "\n";
        return str;
    }

    public static String lookup32bitsuuid(byte[] value, int length) {
        String str = "";
        for (int index = 0; index <= length - 4; index = index + 4) {
            StringBuilder stringBuilder = new StringBuilder(length);
            stringBuilder.append(String.format("%02X", value[index + 3]));
            stringBuilder.append(String.format("%02X", value[index + 2]));
            stringBuilder.append(String.format("%02X", value[index + 1]));
            stringBuilder.append(String.format("%02X", value[index]));
            str += "0x" + stringBuilder.toString() + "  ";
        }
        str += "\n";
        return str;
    }

    public static String lookup128bitsuuid(byte[] value, int length) {
        String str = "";
        for (int index = 0; index <= length - 16; index = index + 16) {
            StringBuilder stringBuilder = new StringBuilder(length);
            stringBuilder.append(String.format("%02X", value[index + 15]));
            stringBuilder.append(String.format("%02X", value[index + 14]));
            stringBuilder.append(String.format("%02X", value[index + 13]));
            stringBuilder.append(String.format("%02X", value[index + 12]));
            stringBuilder.append(String.format("%02X", value[index + 11]));
            stringBuilder.append(String.format("%02X", value[index + 10]));
            stringBuilder.append(String.format("%02X", value[index + 9]));
            stringBuilder.append(String.format("%02X", value[index + 8]));
            stringBuilder.append(String.format("%02X", value[index + 7]));
            stringBuilder.append(String.format("%02X", value[index + 6]));
            stringBuilder.append(String.format("%02X", value[index + 5]));
            stringBuilder.append(String.format("%02X", value[index + 4]));
            stringBuilder.append(String.format("%02X", value[index + 3]));
            stringBuilder.append(String.format("%02X", value[index + 2]));
            stringBuilder.append(String.format("%02X", value[index + 1]));
            stringBuilder.append(String.format("%02X", value[index]));
            str += "0x" + stringBuilder.toString() + "  ";
        }
        str += "\n";
        return str;
    }

    /*device names*/
    public static String lookupdevicename(byte[] value, int length) {

        String strRead = new String(value);
        strRead = String.copyValueOf(strRead.toCharArray(), 0, value.length);

        return strRead;
    }
}
