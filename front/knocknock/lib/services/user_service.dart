import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:async';
import 'package:http_interceptor/http_interceptor.dart';
import 'package:knocknock/constants/api.dart';
import 'package:knocknock/interceptors/interceptor.dart';

const String baseUrl = Api.BASE_URL;
const storage = FlutterSecureStorage();

class UserService {
  final client = InterceptedClient.build(interceptors: [HttpInterceptor()]);

  // 1. 로그인
  Future<int> login(String email, String password) async {
    final url = Uri.parse('$baseUrl/user/login');
    final response = await http.post(
      url,
      headers: {"Content-Type": "application/json"},
      body: jsonEncode(
        {
          "email": email,
          "password": password,
        },
      ),
    );

    if (response.statusCode == 201) {
      final Map<String, dynamic> responseData = jsonDecode(response.body);
      final String? token = responseData['accessToken'];
      final String? refreshToken = responseData['refreshToken'];
      final String? nickname = responseData['nickname'];
      final String? address = responseData['address'];

      if (token != null) {
        await storage.write(key: "accessToken", value: token);
        await storage.write(key: "refreshToken", value: refreshToken);
        await storage.write(key: "nickname", value: nickname);
        await storage.write(key: "address", value: address);
        return 201;
      }
    } else if (response.statusCode == 400) {
      return 400;
    }
    return 500;
  }

  // 2. 로그아웃
  Future<int> logout() async {
    final url = Uri.parse('$baseUrl/user/logout');
    final accessToken = await storage.read(key: "accessToken");
    final response = await client.post(
      url,
      headers: {
        "Authorization": "Bearer $accessToken",
      },
    );

    if (response.statusCode == 200) {
      await storage.deleteAll();
      return 200;
    } else if (response.statusCode == 403) {
      logout();
    } else if (response.statusCode == 500) {
      return 500;
    }

    throw Error();
  }

  // 3. 비밀번호 찾기(이메일, 닉네임 일치 여부)
  Future<int> findPassword(String nickname, String email) async {
    final url = Uri.parse('$baseUrl/user/password');
    final response = await http.post(
      url,
      headers: {"Content-Type": "application/json"},
      body: jsonEncode(
        {
          "nickname": nickname,
          "email": email,
        },
      ),
    );

    if (response.statusCode == 200) {
      // 3-1. 임시 비밀번호 발급
      final url2 = Uri.parse('$baseUrl/email/password');
      final response2 = await http.post(
        url2,
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(
          {
            "email": email,
          },
        ),
      );
      if (response2.statusCode == 200) {
        return 200;
      } else if (response2.statusCode == 500) {
        return 500;
      }
    } else if (response.statusCode == 400) {
      return 400;
    }
    return 500;
  }

  // 4. 이메일 중복검사
  Future<int> checkEmail(String email) async {
    final url = Uri.parse("$baseUrl/user/check?email=$email");

    final response = await http.get(
      url,
      headers: {"Content-Type": "application/x-www-form-urlencoded"},
    );

    if (response.statusCode == 200) {
      return 200;
    } else if (response.statusCode == 400) {
      return 400;
    } else {
      return 500;
    }
  }

  // 5. 이메일 인증코드 발신
  Future<int> sendCheckEmailCode(String email) async {
    final url = Uri.parse("$baseUrl/email/sign-up?email=$email");
    final response = await http.post(
      url,
      headers: {"Content-Type": "application/x-www-form-urlencoded"},
    );

    if (response.statusCode == 200) {
      return 200;
    } else {
      return 500;
    }
  }

  // 6. 이메일 인증코드 유효 검사
  Future<int> checkCode(String email, String code) async {
    final url = Uri.parse("$baseUrl/email/check");
    final response = await http.post(
      url,
      headers: {"Content-Type": "application/json"},
      body: jsonEncode({
        "email": email,
        "code": code,
      }),
    );

    if (response.statusCode == 200) {
      return 200;
    } else {
      return 500;
    }
  }

  // 7. 회원가입
  Future<int> signUp(
      String email, String password, String nickname, String address) async {
    final url = Uri.parse("$baseUrl/user/sign-up");
    final response = await http.post(
      url,
      headers: {"Content-Type": "application/json"},
      body: jsonEncode(
        {
          "email": email,
          "password": password,
          "nickname": nickname,
          "address": address,
        },
      ),
    );

    if (response.statusCode == 201) {
      return 201;
    } else if (response.statusCode == 400) {
      return 400; // 하나라도 안 쓰면, email 인증 안 했으면
    } else {
      return 500;
    }
  }
}
