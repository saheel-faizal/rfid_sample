// import 'package:flutter/material.dart';
// import 'package:rfid_sample/service/rfid_service.dart';
//
//
// class Rfid123Screen extends StatefulWidget {
//
//   const Rfid123Screen({super.key});
//
//   @override
//   State<Rfid123Screen> createState() => _Rfid123ScreenState();
// }
//
// class _Rfid123ScreenState extends State<Rfid123Screen> {
//   final RFIDNfcService _rfidNfcService = RFIDNfcService();
//
//   @override
//   Widget build(BuildContext context) {
//     return MaterialApp(
//       home: Scaffold(
//         appBar: AppBar(title: Text('RFID/NFC Service')),
//         body: Center(
//           child: Column(
//             mainAxisAlignment: MainAxisAlignment.center,
//             children: [
//               ElevatedButton(
//                 onPressed: () async {
//                   await _rfidNfcService.initRFIDReader();
//                 },
//                 child: Text('Initialize RFID Reader'),
//               ),
//               ElevatedButton(
//                 onPressed: () async {
//                   await _rfidNfcService.startRFIDScanning();
//                 },
//                 child: Text('Start RFID Scanning'),
//               ),
//               ElevatedButton(
//                 onPressed: () async {
//                   await _rfidNfcService.stopRFIDScanning();
//                 },
//                 child: Text('Stop RFID Scanning'),
//               ),
//             ],
//           ),
//         ),
//       ),
//     );
//   }
// }