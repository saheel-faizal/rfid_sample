import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:rfid_sample/provider/base_provider.dart';
import 'package:rfid_sample/utils/config.dart';
import 'package:rfid_sample/view/pda_rfid_pub.dart';

void main() {
  // configureGateUser("Gate_1");
  configureGateUser("Gate_2");
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => BaseProvider()),
      ],
      child: MaterialApp(
        debugShowCheckedModeBanner: false,
        home: PdaRfidSampleScreen(),
      ),
    );
  }
}
