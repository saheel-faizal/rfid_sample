import 'package:flutter/material.dart';
import 'package:lottie/lottie.dart';
import 'package:qr_flutter/qr_flutter.dart';
import 'package:zebra123/zebra123.dart';

class Zebra123Screen extends StatefulWidget {
  const Zebra123Screen({super.key});

  @override
  State<Zebra123Screen> createState() => _Zebra123ScreenState();
}

class _Zebra123ScreenState extends State<Zebra123Screen> {
  Zebra123? zebra123;
  bool _isScannerActive = false;
  String _lastScan = 'No scan yet';

  @override
  void initState() {
    super.initState();
    _initializeScanner();
  }

  Future<void> _initializeScanner() async {
    zebra123 = Zebra123(callback: _callback);
    await zebra123?.connect();
    setState(() => _isScannerActive = true);
  }

  Future<void> _stopScanner() async {
    await zebra123?.disconnect();
    setState(() => _isScannerActive = false);
  }

  void _callback(Interfaces interface, Events event, dynamic data) {
    if (event == Events.readRfid && data is List<RfidTag>) {
      setState(() {
        _lastScan = data.isNotEmpty ? data.first.epc : 'No scan yet';
      });
    }
  }

  @override
  void dispose() {
    _stopScanner();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[900],
      appBar: AppBar(
        title: const Text(
          'Gate User',
          style: TextStyle(color: Colors.red),
        ),
        backgroundColor: Colors.black,
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Visibility(
            visible: _isScannerActive,
            replacement: Center(
              child: InkWell(
                onTap: _initializeScanner,
                child: Card(
                  elevation: 10,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(20),
                  ),
                  color: Colors.white,
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Stack(
                      alignment: Alignment.center,
                      children: [
                        QrImageView(
                          data: "just sample data",
                          version: QrVersions.auto,
                          size: 200.0,
                        ),
                        Container(
                          height: 50,
                          width: 200,
                          color: Colors.red,
                          child: Center(
                            child: Text(
                              "Tap to Scan",
                              style: TextStyle(
                                color: Colors.white,
                                fontSize: 18,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                        )
                      ],
                    ),
                  ),
                ),
              ),
            ),
            child: Center(
              child: Container(
                color: Colors.white,
                child: Lottie.asset(
                  'assets/scanner.json',
                  width: 250,
                  height: 250,
                  repeat: true,
                ),
              ),
            ),
          ),
          Visibility(
            visible: _isScannerActive,
            child: Padding(
              padding: const EdgeInsets.all(8.0),
              child: ElevatedButton(
                onPressed: _stopScanner,
                style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
                child: const Text(
                  "Stop Scanner",
                  style: TextStyle(color: Colors.white),
                ),
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Text(
              "Last Scan: $_lastScan",
              style: TextStyle(color: Colors.white, fontSize: 16),
            ),
          ),
        ],
      ),
    );
  }
}
