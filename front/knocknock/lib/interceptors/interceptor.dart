import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:knocknock/constants/api.dart';
import 'package:http_interceptor/http_interceptor.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

const storage = FlutterSecureStorage();
const String baseUrl = Api.BASE_URL;

class HttpInterceptor implements InterceptorContract {
  @override
  Future<RequestData> interceptRequest({required RequestData data}) async {
    final token = await storage.read(key: "accessToken");
    if (token != null) {
      data.headers["Authorization"] = "Bearer $token";
    }
    return data;
  }

  @override
  Future<ResponseData> interceptResponse({required ResponseData data}) async {
    if (data.statusCode == 403) {
      String? accessToken = await storage.read(key: "accessToken");
      String? refreshToken = await storage.read(key: "refreshToken");

      final url = Uri.parse('$baseUrl/user/reissue-token');
      final response = await http.post(
        url,
        headers: {
          "Authorization": "Bearer $accessToken",
          "Refresh-Token": "$refreshToken",
        },
      );

      if (response.statusCode == 200) {
        Map<String, dynamic> responseBody = json.decode(response.body);
        String? newAccessToken = responseBody['accessToken'];
        String? newRefreshToken = responseBody['refreshToken'];

        await storage.write(key: "accessToken", value: newAccessToken);
        await storage.write(key: "refreshToken", value: newRefreshToken);

        final originalRequestData = data.request;
        originalRequestData?.headers["Authorization"] =
            "Bearer $newAccessToken";
      }
    }
    return data;
  }
}
