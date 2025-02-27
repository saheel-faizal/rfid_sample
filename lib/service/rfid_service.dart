import 'package:flutter/services.dart';

class RFIDNfcService {
  static const platform = MethodChannel('com.example.rfid_nfc_service');

  // Method to initialize RFID reader
  Future<void> initRFIDReader() async {
    try {
      await platform.invokeMethod('initRFIDReader');
    } on PlatformException catch (e) {
      print("Failed to initialize RFID reader: ${e.message}");
    }
  }

  // Method to start RFID scanning
  Future<void> startRFIDScanning() async {
    try {
      await platform.invokeMethod('startRFIDScanning');

    } on PlatformException catch (e) {
      print("Failed to start RFID scanning: ${e.message}");
    }
  }

  // Method to stop RFID scanning
  Future<void> stopRFIDScanning() async {
    try {
      await platform.invokeMethod('stopRFIDScanning');
    } on PlatformException catch (e) {
      print("Failed to stop RFID scanning: ${e.message}");
    }
  }

  // Method to handle NFC data
  Future<void> handleNfcData(String nfcData) async {
    try {
      await platform.invokeMethod('handleNfcData', {'nfcData': nfcData});
    } on PlatformException catch (e) {
      print("Failed to handle NFC data: ${e.message}");
    }
  }
}