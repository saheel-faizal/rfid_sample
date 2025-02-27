//
// import 'package:flutter/material.dart';
// import 'package:lottie/lottie.dart';
// import 'package:provider/provider.dart';
// import 'package:qr_flutter/qr_flutter.dart';
// import 'package:rfid_sample/provider/base_provider.dart';
// import 'package:rfid_sample/utils/app_colors.dart';
// import 'package:rfid_sample/utils/config.dart';
//
// class GateUserHomeScreen extends StatefulWidget {
//   const GateUserHomeScreen({super.key});
//
//   @override
//   State<GateUserHomeScreen> createState() => _GateUserHomeScreenState();
// }
//
// class _GateUserHomeScreenState extends State<GateUserHomeScreen> {
//   @override
//   void initState() {
//     WidgetsBinding.instance.addPostFrameCallback((callback) {
//       final baseProvider = Provider.of<BaseProvider>(context, listen: false);
//       // baseProvider.initRFIDReader(context);
//     });
//     super.initState();
//   }
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       backgroundColor: Colors.grey[900],
//       appBar: AppBar(
//         title: Text(
//           'Gate User: $gateUserType',
//           style: TextStyle(color: Colors.red),
//         ),
//         backgroundColor: Colors.black,
//       ),
//       body: Consumer<BaseProvider>(builder: (_, provider, child) {
//         return Column(
//           mainAxisAlignment: MainAxisAlignment.center,
//           children: [
//             // **Show scanning animation if scanner is ON**
//             Visibility(
//               visible: provider.isScannerActive,
//               replacement:
//               Consumer<BaseProvider>(builder: (_, provider, child) {
//                 return Center(
//                   child: InkWell(
//                     onTap: () async {
//                       provider.startRfidScanning(context);
//                     },
//                     child: Card(
//                       elevation: 10,
//                       shape: RoundedRectangleBorder(
//                         borderRadius: BorderRadius.circular(20),
//                       ),
//                       color: Colors.white,
//                       child: Padding(
//                         padding: const EdgeInsets.all(16.0),
//                         child: Stack(
//                           alignment: Alignment.center,
//                           children: [
//                             QrImageView(
//                               data: "just sample data",
//                               version: QrVersions.auto,
//                               size: 200.0,
//                             ),
//                             Container(
//                               height: 50,
//                               width: 200,
//                               color: Colors.red,
//                               child: Center(
//                                 child: Text(
//                                   provider.isScannerActive
//                                       ? "Scanner Active"
//                                       : "Tap to Scan",
//                                   style: TextStyle(
//                                       color: AppColors.whiteColor,
//                                       fontSize: 18,
//                                       fontWeight: FontWeight.bold),
//                                 ),
//                               ),
//                             )
//                           ],
//                         ),
//                       ),
//                     ),
//                   ),
//                 );
//               }),
//               child: Center(
//                 child: Container(
//                   color: Colors.white,
//                   child: Lottie.asset(
//                     'assets/scanner.json',
//                     width: 250,
//                     height: 250,
//                     repeat: true,
//                   ),
//                 ),
//               ),
//             ),
//
//             Visibility(
//               visible: provider.isScannerActive,
//               child: Padding(
//                 padding: const EdgeInsets.all(8.0),
//                 child: ElevatedButton(
//                   onPressed: () {
//                     provider.stopRfidScanning();
//                   },
//                   style: ElevatedButton.styleFrom(
//                       backgroundColor: AppColors.failureColor),
//                   child: Text(
//                     "Stop Scanner",
//                     style: TextStyle(color: Colors.white),
//                   ),
//                 ),
//               ),
//             )
//
//             // Text("Tap to scan",style: TextStyle(color: Colors.white,fontSize: 18,fontWeight: FontWeight.bold),),
//             // Center(
//             //   child: InkWell(
//             //     onTap: () async {
//             //       final result = await Navigator.push(
//             //         context,
//             //         MaterialPageRoute(builder: (context) => QRScannerScreen()),
//             //       );
//             //
//             //       // Show dialog based on the result
//             //       if (result) {
//             //         _showModernDialog(context, result);
//             //       } else {
//             //         _showModernDialog(context, result);
//             //       }
//             //     },
//             //     child: Card(
//             //       elevation: 10,
//             //       shape: RoundedRectangleBorder(
//             //         borderRadius: BorderRadius.circular(20),
//             //       ),
//             //       color: Colors.white,
//             //       child: Padding(
//             //         padding: const EdgeInsets.all(16.0),
//             //         child: Stack(
//             //           alignment: Alignment.center,
//             //           children: [
//             //             QrImageView(
//             //               data: "just sample data",
//             //               version: QrVersions.auto,
//             //               size: 200.0,
//             //             ),
//             //             Container(
//             //               height: 50,
//             //               width: 200,
//             //               color: Colors.red,
//             //               child: Center(
//             //                 child: Text(
//             //                   "Scan QR Code 🔎",
//             //                   style: TextStyle(
//             //                       color: AppColors.whiteColor,
//             //                       fontSize: 18,
//             //                       fontWeight: FontWeight.bold),
//             //                 ),
//             //               ),
//             //             )
//             //           ],
//             //         ),
//             //       ),
//             //     ),
//             //   ),
//             // ),
//           ],
//         );
//       }),
//     );
//   }
//
//   void _showModernDialog(BuildContext context, bool result) {
//     showDialog(
//       context: context,
//       builder: (BuildContext context) {
//         return Dialog(
//           shape: RoundedRectangleBorder(
//             borderRadius: BorderRadius.circular(20.0),
//           ),
//           child: Container(
//             padding: const EdgeInsets.all(20.0),
//             decoration: BoxDecoration(
//               color: result
//                   ? Colors.green
//                   : Colors.red, // Green for success, Red for failure
//               borderRadius: BorderRadius.circular(20.0),
//             ),
//             child: Column(
//               mainAxisSize: MainAxisSize.min,
//               children: [
//                 Icon(
//                   result ? Icons.check_circle : Icons.cancel,
//                   size: 60,
//                   color: Colors.white,
//                 ),
//                 const SizedBox(height: 10),
//                 Text(
//                   result ? "Access Granted" : "Access Denied",
//                   style: TextStyle(
//                     color: Colors.white,
//                     fontSize: 22,
//                     fontWeight: FontWeight.bold,
//                   ),
//                 ),
//                 const SizedBox(height: 5),
//                 Text(
//                   result
//                       ? "User is allowed through this gate."
//                       : "User is NOT allowed through this gate.",
//                   textAlign: TextAlign.center,
//                   style: TextStyle(
//                     color: Colors.white,
//                     fontSize: 16,
//                   ),
//                 ),
//                 const SizedBox(height: 20),
//                 ElevatedButton(
//                   onPressed: () {
//                     Navigator.pop(context); // Close dialog
//                   },
//                   style: ElevatedButton.styleFrom(
//                     backgroundColor: Colors.white,
//                     foregroundColor: result ? Colors.green : Colors.red,
//                     shape: RoundedRectangleBorder(
//                       borderRadius: BorderRadius.circular(10),
//                     ),
//                   ),
//                   child: const Text("OK"),
//                 ),
//               ],
//             ),
//           ),
//         );
//       },
//     );
//   }
// }
