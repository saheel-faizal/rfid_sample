package com.zebra.demo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private var methodChannel: MethodChannel? = null
    private var mNfcAdapter: NfcAdapter? = null
    private var scanResultBroadcast: BroadcastReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Initialize Flutter MethodChannel
        methodChannel =
            MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), CHANNEL)


        // Initialize NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // Register BroadcastReceiver for RFID Scanning
        val filter: IntentFilter = IntentFilter()
        filter.addAction(getResources().getString(R.string.dw_action))
        filter.addCategory(getResources().getString(R.string.dw_category))

        scanResultBroadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val action: String = intent.getAction().toString()
                if (action != null && action == getResources().getString(R.string.dw_action)) {
                    val decodedData: String =
                        intent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data))
                            .toString()
                    if (decodedData != null) {
                        sendDataToFlutter("scanResult", decodedData)
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(scanResultBroadcast, filter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(scanResultBroadcast, filter)
        }

        // Handle method calls from Flutter
        methodChannel.setMethodCallHandler { call, result ->
            when (call.method) {
                "pairDevice" -> {
                    pairDevice(call.arguments.toString())
                    result.success("Pairing initiated for " + call.arguments.toString())
                }

                "unpairDevice" -> {
                    unpairDevice(call.arguments.toString())
                    result.success("Unpairing initiated for " + call.arguments.toString())
                }

                else -> result.notImplemented()
            }
        }
    }

    /**
     * Sends data to Flutter using MethodChannel
     */
    private fun sendDataToFlutter(event: String, data: String) {
        if (methodChannel != null) {
            methodChannel.invokeMethod(event, data)
        }
    }

    /**
     * Handles new RFID reader connections.
     */
    fun RFIDReaderAppeared(device: ReaderDevice) {
        if (!RFIDController.readersList.contains(device)) {
            RFIDController.readersList.add(device)
            sendDataToFlutter("readerConnected", device.getName())
        }
    }

    /**
     * Handles NFC scanning and sends the data to Flutter.
     */
    fun processNFCScan(nfcContent: String?) {
        if (nfcContent != null) {
            sendDataToFlutter("nfcScanResult", nfcContent)
        }
    }

    /**
     * Pair an RFID device.
     */
    private fun pairDevice(deviceName: String) {
        Application.scanPair.barcodeDeviceNameConnect(deviceName)
        sendDataToFlutter("pairingStatus", "Device $deviceName paired successfully")
    }

    /**
     * Unpair a connected RFID device.
     */
    private fun unpairDevice(deviceName: String) {
        Application.scanPair.unpair(deviceName)
        sendDataToFlutter("unpairStatus", "Device $deviceName unpaired successfully")
    }

    /**
     * Unregister the receiver when the activity is destroyed.
     */
    protected fun onDestroy() {
        super.onDestroy()
        if (scanResultBroadcast != null) {
            unregisterReceiver(scanResultBroadcast)
        }
    }

    companion object {
        private const val CHANNEL = "flutter_rfid_channel" // MethodChannel for Flutter
    }
}