package com.zebra.demo

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import com.example.rfid_sample.reader_connection.ScanPair
import com.example.rfid_sample.rfid.RFIDController
import com.zebra.rfid.api3.ReaderDevice
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel


class MainActivity : FlutterActivity() {
    private var methodChannel: MethodChannel? = null
    private var mNfcAdapter: NfcAdapter? = null
    private var scanResultBroadcast: BroadcastReceiver? = null
    private var scanPair: ScanPair? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Flutter MethodChannel
        methodChannel = MethodChannel(
            flutterEngine!!.dartExecutor.binaryMessenger,
            CHANNEL
        )

        // Initialize NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // Register BroadcastReceiver for RFID Scanning
        val filter = IntentFilter().apply {
            addAction(DW_ACTION)  // Directly using constant instead of R.string.dw_action
            addCategory(DW_CATEGORY) // Directly using constant instead of R.string.dw_category
        }

        scanResultBroadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val action: String? = intent.action
                if (action != null && action == DW_ACTION) {
                    val decodedData: String? = intent.getStringExtra(DATAWEDGE_INTENT_KEY_DATA)
                    if (!decodedData.isNullOrEmpty()) {
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
        methodChannel?.setMethodCallHandler { call, result ->
            when (call.method) {
                "pairDevice" -> {
                    pairDevice(call.arguments.toString())
                    result.success("Pairing initiated for ${call.arguments}")
                }
                "unpairDevice" -> {
                    unpairDevice(call.arguments.toString())
                    result.success("Unpairing initiated for ${call.arguments}")
                }
                else -> result.notImplemented()
            }
        }
    }

    /**
     * Sends data to Flutter using MethodChannel
     */
    private fun sendDataToFlutter(event: String, data: String) {
        methodChannel?.invokeMethod(event, data)
    }

    /**
     * Handles new RFID reader connections.
     */
    fun RFIDReaderAppeared(device: ReaderDevice) {
        if (!RFIDController.readersList.contains(device)) {
            RFIDController.readersList.add(device)
            sendDataToFlutter("readerConnected", device.name)
        }
    }

    /**
     * Handles NFC scanning and sends the data to Flutter.
     */
    fun processNFCScan(nfcContent: String?) {
        nfcContent?.let { sendDataToFlutter("nfcScanResult", it) }
    }

    /**
     * Pair an RFID device.
     */
    private fun pairDevice(deviceName: String) {
        scanPair?.barcodeDeviceNameConnect(deviceName)
        sendDataToFlutter("pairingStatus", "Device $deviceName paired successfully")
    }

    /**
     * Unpair a connected RFID device.
     */
    private fun unpairDevice(deviceName: String) {
        scanPair?.unpair(deviceName)
        sendDataToFlutter("unpairStatus", "Device $deviceName unpaired successfully")
    }

    /**
     * Unregister the receiver when the activity is destroyed.
     */
    override fun onDestroy() {
        super.onDestroy()
        scanResultBroadcast?.let { unregisterReceiver(it) }
    }

    companion object {
        private const val CHANNEL = "flutter_rfid_channel" // MethodChannel for Flutter

        // RFID & NFC Constants (from strings.xml)
        private const val DW_ACTION = "com.symbol.dwudiusertokens.udi"
        private const val DW_CATEGORY = "zebra.intent.dwudiusertokens.UDI"
        private const val DATAWEDGE_INTENT_KEY_DATA = "com.symbol.datawedge.data_string"
    }
}
