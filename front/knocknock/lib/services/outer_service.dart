import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:async';
import 'package:http_interceptor/http_interceptor.dart';
import 'package:knocknock/constants/api.dart';
import 'package:knocknock/interceptors/interceptor.dart';

const String baseUrl = Api.BASE_URL;
const storage = FlutterSecureStorage();

class OuterService {
  final client = InterceptedClient.build(interceptors: [HttpInterceptor()]);

  // 1. 폐기물 업체 리스트
  Future<List<Map<String, dynamic>>> getWasteCompanyList(
      String sido, String gugun) async {
    print(sido);
    print(gugun);
    final url = Uri.parse('$baseUrl/waste');
    final response = await http.post(
      url,
      headers: {"Content-Type": "application/json"},
      body: jsonEncode(
        {
          "sido": sido,
          "gugun": gugun,
        },
      ),
    );
    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(utf8.decode(response.bodyBytes));
      print(url);
      print(response.statusCode);
      print(data);
      return data.map((e) => e as Map<String, dynamic>).toList();
    } else {
      throw Exception('Failed to load data');
    }
  }

  // 2. 대기질 정보
  Future<Map<String, dynamic>> airInfo() async {
    final url = Uri.parse('$baseUrl/air-info');
    final token = await storage.read(key: "accessToken");
    final response = await client.get(
      url,
      headers: {
        'Authorization': 'Bearer $token',
      },
    );
    print(response.body);
    print(response.statusCode);
    if (response.statusCode == 200) {
      final Map<String, dynamic> data =
          jsonDecode(utf8.decode(response.bodyBytes));
      print(data);
      return data;
    } else {
      throw Exception('Failed to load data');
    }
  }
}
