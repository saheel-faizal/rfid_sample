import 'package:flutter/material.dart';
import 'package:lottie/lottie.dart';
import 'package:provider/provider.dart';
import 'package:qr_flutter/qr_flutter.dart';
import 'package:rfid_sample/provider/base_provider.dart';
import 'package:rfid_sample/utils/app_colors.dart';

class PdaRfidSampleScreen extends StatefulWidget {
  const PdaRfidSampleScreen({super.key});

  @override
  State<PdaRfidSampleScreen> createState() => _PdaRfidSampleScreenState();
}

class _PdaRfidSampleScreenState extends State<PdaRfidSampleScreen> {
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
      body: Consumer<BaseProvider>(
        builder: (context, provider, child) {
          return Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Visibility(
                visible: provider.isScannerActive,
                replacement: Center(
                  child: InkWell(
                    onTap: () => provider.startRfidScanning(context),
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
                                    color: AppColors.whiteColor,
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
                visible: provider.isScannerActive,
                child: Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: ElevatedButton(
                    onPressed: () => provider.stopRfidScanning(),
                    style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
                    child: const Text(
                      "Stop Scanner",
                      style: TextStyle(color: Colors.white),
                    ),
                  ),
                ),
              ),
            ],
          );
        },
      ),
    );
  }
}
