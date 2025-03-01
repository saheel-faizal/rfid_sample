import 'package:flutter/material.dart';
import 'package:rfid_sample/service/telegram_logger_service.dart';
import 'package:rfid_sample/utils/app_alerts.dart';

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

  Set<String> scannedEpcSet = {}; // Store unique scanned EPCs
  void initRFIDReader(BuildContext context) {}

  /// **Start Scanning for RFID Tags**
  Future<void> startRfidScanning(BuildContext context) async {
    if (_isScannerActive) return;
    initRFIDReader(context);
    TelegramLogger.sendLog("RFID Scanning Started");
    scannedTag = ""; // Reset previous scan
    _isScannerActive = true;
    notifyListeners();
  }

  /// **Stop Scanning**
  Future<void> stopRfidScanning() async {
    _isScannerActive = false;
    notifyListeners();
    TelegramLogger.sendLog("RFID Scanning Stopped");
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
