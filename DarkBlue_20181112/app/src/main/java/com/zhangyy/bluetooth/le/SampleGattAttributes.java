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
        String name = attributes.get(uuid) ;
        return name == null ? defaultName : name;
    }
    public static String lookupService(String uuid, String defaultName) {
        String name = attributes.get(uuid) ;
        return name == null ? defaultName : (name+"["+uuid.substring(4, 8)+"]");
    }
}
