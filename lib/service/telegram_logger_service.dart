// Replace with your Telegram bot token and chat ID
import 'package:dio/dio.dart';

const String telegramBotToken = "7691552261:AAHoAR22XME33YEPJjN1faTDCyo2MVK-ljQ";
// const String telegramChatId = "457782794";
const String telegramChatId = "-4637676989";

class TelegramLogger {
  static final Dio dio = Dio();

  static Future<void> sendLog(String message) async {
    try {
      final String url = "https://api.telegram.org/bot$telegramBotToken/sendMessage";
      final response = await dio.post(url, data: {
        "chat_id": telegramChatId,
        "text": message,
      },
          options: Options(
              contentType: Headers.formUrlEncodedContentType
          ));

      if (response.statusCode == 200) {
        print("Log sent to Telegram successfully!");
      } else {
        print("Failed to send log. Status code: ${response.statusCode}");
      }
    } catch (e) {
      print("Error sending log to Telegram: $e");
    }
  }
}
