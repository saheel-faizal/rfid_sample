// import 'package:flutter/material.dart';
// import 'package:lottie/lottie.dart';
// import 'package:qr_flutter/qr_flutter.dart';
// import 'package:pda_rfid_scanner/pda_rfid_scanner.dart';
// import 'dart:async';
//
// import 'package:rfid_sample/service/telegram_logger_service.dart';
// import 'package:rfid_sample/utils/app_alerts.dart';
//
// class PdaRfidSampleScreen extends StatefulWidget {
//   const PdaRfidSampleScreen({super.key});
//
//   @override
//   State<PdaRfidSampleScreen> createState() => _PdaRfidSampleScreenState();
// }
//
// class _PdaRfidSampleScreenState extends State<PdaRfidSampleScreen> {
//   bool _isScannerActive = false;
//   String _lastScan = 'No scan yet';
//   StreamSubscription? _scanSubscription;
//
//   @override
//   void initState() {
//     super.initState();
//     _initializeScanner();
//   }
//
//   Future<void> _initializeScanner() async {
//     TelegramLogger.sendLog("_initializeScanner : initialize");
//     await PdaRfidScanner.enableRfid();
//     await PdaRfidScanner.setAutoRestartScan(true);
//     setState(() => _isScannerActive = true);
//
//     _scanSubscription = PdaRfidScanner.scanStream.listen(
//       (ScanResult result) {
//         TelegramLogger.sendLog(
//             "Scan detected : ${result.type.name} | ${result.data}");
//
//         setState(() {
//           _lastScan = result.data;
//         });
//
//         AppAlerts.appToast(
//             message: "Scan detected : ${result.type.name} | ${result.data}");
//       },
//       onError: (error) {
//         TelegramLogger.sendLog("Scan Error : ${error.toString()}");
//
//         print('Error receiving scan: $error');
//       },
//     );
//   }
//
//   Future<void> _stopScanner() async {
//     await PdaRfidScanner.disableRfid();
//     _scanSubscription?.cancel();
//     setState(() => _isScannerActive = false);
//   }
//
//   @override
//   void dispose() {
//     _stopScanner();
//     super.dispose();
//   }
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       backgroundColor: Colors.grey[900],
//       appBar: AppBar(
//         title: const Text(
//           'Gate User',
//           style: TextStyle(color: Colors.red),
//         ),
//         backgroundColor: Colors.black,
//       ),
//       body: Column(
//         mainAxisAlignment: MainAxisAlignment.center,
//         children: [
//           Visibility(
//             visible: _isScannerActive,
//             replacement: Center(
//               child: InkWell(
//                 onTap: _initializeScanner,
//                 child: Card(
//                   elevation: 10,
//                   shape: RoundedRectangleBorder(
//                     borderRadius: BorderRadius.circular(20),
//                   ),
//                   color: Colors.white,
//                   child: Padding(
//                     padding: const EdgeInsets.all(16.0),
//                     child: Stack(
//                       alignment: Alignment.center,
//                       children: [
//                         QrImageView(
//                           data: "just sample data",
//                           version: QrVersions.auto,
//                           size: 200.0,
//                         ),
//                         Container(
//                           height: 50,
//                           width: 200,
//                           color: Colors.red,
//                           child: Center(
//                             child: Text(
//                               "Tap to Scan",
//                               style: TextStyle(
//                                 color: Colors.white,
//                                 fontSize: 18,
//                                 fontWeight: FontWeight.bold,
//                               ),
//                             ),
//                           ),
//                         )
//                       ],
//                     ),
//                   ),
//                 ),
//               ),
//             ),
//             child: Center(
//               child: Container(
//                 color: Colors.white,
//                 child: Lottie.asset(
//                   'assets/scanner.json',
//                   width: 250,
//                   height: 250,
//                   repeat: true,
//                 ),
//               ),
//             ),
//           ),
//           Visibility(
//             visible: _isScannerActive,
//             child: Padding(
//               padding: const EdgeInsets.all(8.0),
//               child: ElevatedButton(
//                 onPressed: _stopScanner,
//                 style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
//                 child: const Text(
//                   "Stop Scanner",
//                   style: TextStyle(color: Colors.white),
//                 ),
//               ),
//             ),
//           Padding(
//             padding: const EdgeInsets.all(16.0),
//             child: Text(
//               "Last Scan: $_lastScan",
//               style: TextStyle(color: Colors.white, fontSize: 16),
//             ),
//           ),
//         ],
//       ),
//     );
//   }
// }
