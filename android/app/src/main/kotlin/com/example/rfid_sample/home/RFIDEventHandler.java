package com.example.rfid_sample.home;

import android.util.Log;

import com.zebra.rfid.api3.RfidEventsListener;
import com.zebra.rfid.api3.RfidReadEvents;
import com.zebra.rfid.api3.RfidStatusEvents;


public class RFIDEventHandler implements RfidEventsListener {
        private static String TAG = "RFIDEventHandler";

        @Override
        public void eventReadNotify(RfidReadEvents e) {

            Log.d(TAG, "RFIDEventHandler eventReadNotify");
        }

        @Override
        public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {
            Log.d(TAG, "RFIDEventHandler eventStatusNotify");
        }

    }

