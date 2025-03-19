import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:rfid_sample/service/telegram_logger_service.dart';
import 'package:rfid_sample/utils/app_alerts.dart';
import 'package:zebra_rfid_reader_sdk/zebra_rfid_reader_sdk.dart';

import '../utils/config.dart';

class BaseProvider extends ChangeNotifier {
  bool isDataLoading = false;

  bool _isScannerActive = false; // Track scanner state

  String scannedTag = "";

  bool get isScannerActive => _isScannerActive;

  void processEpcData(
      BuildContext context, String scannedHexEpc, String gateUserType) {
    // Convert the scanned hex string to ASCII.
    TelegramLogger.sendLog(
        "processEpcData: Scanned EPC HEX Before parsing: $scannedHexEpc");

    String scannedEpc = hexToAscii(scannedHexEpc);

    TelegramLogger.sendLog("processEpcData: Scanned EPC: $scannedEpc");

    // Ensure the scanned EPC is **exactly 24 characters**
    if (scannedEpc.length != 24) {
      TelegramLogger.sendLog("Invalid EPC length: ${scannedEpc.length}");
      _showModernDialog(
          context, false, "Invalid EPC", "This EPC is not 24 characters long.");
      return;
    }

    // Expected EPC format: **24 characters** e.g., "F12025B00007C23500000000"
    RegExp epcPattern = RegExp(r'F12025B(\d{5})C(\d{1,10})');

    Match? match = epcPattern.firstMatch(scannedEpc);

    if (match != null) {
      String ticketId =
          "T-${match.group(1)}"; // Extract and format ticket number
      String valuesString = match.group(2) ?? ""; // Extract values

      // Convert the extracted values into a list of allowed values
      List<String> allowedValues =
          valuesString.split(''); // Split into single digits

      // Check if the gate user type (assumed to be a single digit) is allowed
      bool isAllowed = allowedValues.contains(gateUserType);

      TelegramLogger.sendLog(
          "EPC valid: $scannedEpc  | Ticket Number: $ticketId | Allowed Values: $valuesString");

      _showModernDialog(
        context,
        isAllowed,
        isAllowed ? "Access Granted." : "Access Denied.",
        isAllowed
            ? "User is allowed through this gate."
            : "User is NOT allowed through this gate.",
      );
    } else {
      TelegramLogger.sendLog("Invalid EPC format: $scannedHexEpc");
      _showModernDialog(context, false, "Invalid EPC",
          "This EPC does not match the expected format.");
    }
  }

  String hexToAscii(String hex) {
    TelegramLogger.sendLog("hexToAscii: $hex");
    final buffer = StringBuffer();
    for (int i = 0; i < hex.length; i += 2) {
      final part = hex.substring(i, i + 2);
      buffer.write(String.fromCharCode(int.parse(part, radix: 16)));
    }
    return buffer.toString();
  }

  final _zebraRfidReaderSdkPlugin = ZebraRfidReaderSdk();

  /// **Initialize RFID Reader**
  void initRFIDReader(BuildContext context) async {
    await requestAccess(); // Request Bluetooth permissions
    List<ReaderDevice> availableDevices =
        await _zebraRfidReaderSdkPlugin.getAvailableReaderList();

    if (availableDevices.isNotEmpty) {
      String tagName = availableDevices.first.name ??
          ""; // Select the first available device
      TelegramLogger.sendLog(
          "Available device found : ${availableDevices.first.name}");

      await _zebraRfidReaderSdkPlugin.connect(
        tagName,
        readerConfig: ReaderConfig(
          antennaPower: 200, // Set max antenna power
          beeperVolume: BeeperVolume.medium,
          isDynamicPowerEnable: true,
        ),
      );

      _zebraRfidReaderSdkPlugin.connectedReaderDevice.listen((event) {
        final result = jsonDecode(event.toString());
        TelegramLogger.sendLog("Connected to RFID Reader: ${result.toString()}");

      });
    } else {
      TelegramLogger.sendLog("No available RFID readers found.");
    }
  }

  /// **Request Bluetooth Scan and Connection Permissions**
  Future<void> requestAccess() async {
    await Permission.bluetoothScan.request();
    await Permission.bluetoothConnect.request();
  }


  /// **Start Scanning for RFID Tags**
  Future<void> startRfidScanning(BuildContext context) async {
    if (_isScannerActive) return;
    TelegramLogger.sendLog("RFID Scanning Started");
    _zebraRfidReaderSdkPlugin.setAntennaPower(120);
    _zebraRfidReaderSdkPlugin.readTags.listen((onData){
      final result = jsonDecode(onData.toString());
      final readTag = TagDataModel.fromJson(result);
      TelegramLogger.sendLog("ONDATA FOUND : $onData");
      processEpcData(context, readTag.tagId, gateUserType);
    });
    scannedTag = ""; // Reset previous scan
    _isScannerActive = true;
    notifyListeners();
  }

  /// **Stop Scanning**
  Future<void> stopRfidScanning() async {
    _isScannerActive = false;
    _zebraRfidReaderSdkPlugin.stopFindingTheTag();
    TelegramLogger.sendLog("Stopped Finding Tag");
    _zebraRfidReaderSdkPlugin.disconnect(); // Disconnect from RFID reader
    TelegramLogger.sendLog("Disconnected Scanner");
    notifyListeners();
    TelegramLogger.sendLog("RFID Scanning Stopped");
  }

  /// **Set Antenna Power**
  void setAntennaPower(int value) {
    if (value >= 120 && value <= 300) {
      _zebraRfidReaderSdkPlugin.setAntennaPower(value);
      debugPrint("Antenna Power set to $value");
    } else {
      debugPrint("Invalid Antenna Power value. Must be between 120 and 300.");
    }
  }

  /// **Set Beeper Volume**
  void setBeeperVolume(int volume) {
    _zebraRfidReaderSdkPlugin.setBeeperVolume(volume);
    debugPrint("Beeper Volume set to $volume");
  }

  /// **Enable/Disable Dynamic Power**
  void setDynamicPower(bool enable) {
    _zebraRfidReaderSdkPlugin.setDynamicPower(enable);
    debugPrint("Dynamic Power set to $enable");
  }

  /// **Find Specific RFID Tag**
  void findTag(String tagPattern) {
    _zebraRfidReaderSdkPlugin.findTheTag(tagPattern);
    debugPrint("Searching for tag: $tagPattern");
  }

  void _showModernDialog(
      BuildContext context, bool result, String message, String subMessage) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return Dialog(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(20.0),
          ),
          child: Container(
            padding: const EdgeInsets.all(20.0),
            decoration: BoxDecoration(
              color: result
                  ? Colors.green
                  : Colors.red, // Green for success, Red for failure
              borderRadius: BorderRadius.circular(20.0),
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Icon(
                  result ? Icons.check_circle : Icons.cancel,
                  size: 60,
                  color: Colors.white,
                ),
                const SizedBox(height: 10),
                Text(
                  message,
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 22,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 5),
                Text(
                  subMessage,
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 16,
                  ),
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  onPressed: () {
                    Navigator.pop(context); // Close dialog
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.white,
                    foregroundColor: result ? Colors.green : Colors.red,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(10),
                    ),
                  ),
                  child: const Text("OK"),
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}
