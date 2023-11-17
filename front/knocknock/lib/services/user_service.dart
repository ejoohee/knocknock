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

    if (response.statusCode == 200) {
      final Map<String, dynamic> responseData =
          jsonDecode(utf8.decode(response.bodyBytes));
      final String? token = responseData['accessToken'];
      final String? refreshToken = responseData['refreshToken'];
      final String? eamil = responseData['email'];
      final String? nickname = responseData['nickname'];
      final String? address = responseData['address'];

      if (token != null) {
        await storage.write(key: "accessToken", value: token);
        await storage.write(key: "refreshToken", value: refreshToken);
        await storage.write(key: "email", value: eamil);
        await storage.write(key: "nickname", value: nickname);
        await storage.write(key: "address", value: address);
        return 200; // 로그인 성공 및 토큰 생성 성공
      }
    } else if (response.statusCode == 400 || response.statusCode == 404) {
      return 400; // 로그인 실패 (유저없음, 패스워드 불일치)
    }
    return 500; // 서버 연결 오류
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
      return 200; // 로그아웃 성공
    } else if (response.statusCode == 403) {
      // accessToken 만료
      logout();
    } else if (response.statusCode == 500) {
      // 서버 연결 오류
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
    dynamic responseBody = jsonDecode(response.body);

    if (response.statusCode == 200) {
      if (responseBody) {
        // 일치 검사 성공
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
          return 200; // 이메일 발신 성공
        } else if (response2.statusCode == 400) {
          return 400; // 이메일 발신 실패(보통 시간 초과)
        } else if (response2.statusCode == 500) {
          return 500;
        }
      } else {
        return 404;
      }
    } else if (response.statusCode == 404) {
      return 404; // 이메일이 없음
    }
    return 500; // 서버 연결 오류
  }

  // 4. 이메일 중복검사
  Future<int> checkEmail(String email) async {
    final url = Uri.parse("$baseUrl/email/check?email=$email");

    final response = await http.get(
      url,
      headers: {"Content-Type": "application/x-www-form-urlencoded"},
    );

    print(response.body);
    if (response.statusCode == 200) {
      return 200;
    } else if (response.statusCode == 400) {
      return 400; // 이메일 중복
    } else {
      return 500; // 서버 연결 오류
    }
  }

  // 5. 이메일 인증코드 발신
  Future<int> sendCheckEmailCode(String email) async {
    print("이메일을 보냅니다.");
    final url = Uri.parse("$baseUrl/email/sign-up");
    final response = await http.post(
      url,
      headers: {"Content-Type": "application/json"},
      body: jsonEncode({
        "email": email,
      }),
    );

    if (response.statusCode == 200) {
      return 200; // 이메일 발신 성공
    } else if (response.statusCode == 400) {
      return 400; // 이메일 중복(checkEmail에서 거르긴함) or 이메일 발신 실패
    } else {
      return 500; // 서버 연결 오류
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

    dynamic responseBody = jsonDecode(response.body);

    if (response.statusCode == 200) {
      if (responseBody == true) {
        return 200; // 이메일 인증 코드 일치
      } else {
        return 201; // 이메일 인증 코드 불일치
      }
    } else if (response.statusCode == 400) {
      return 400; // 해당 이메일로 유효한 인증 코드 미존재
    } else {
      return 500; // 서버 연결 오류
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
      return 201; // 회원 가입 성공
    } else if (response.statusCode == 400) {
      return 400; // 회원 가입에 필요한 조건 미충족 or 이메일 인증 미완료
    } else {
      return 500; // 서버 연결 오류
    }
  }

  // 8. 정보 수정 전 비밀번호 체크
  Future<int> checkPassword(String password) async {
    final url = Uri.parse("$baseUrl/user/password-check");
    final token = await storage.read(key: "accessToken");

    final response = await client.post(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        "Content-Type": "application/json",
      },
      body: jsonEncode(
        {
          "password": password,
        },
      ),
    );

    dynamic responseBody = jsonDecode(response.body);
    if (response.statusCode == 200) {
      if (responseBody == true) {
        return 200;
      } else {
        return 404;
      }
    } else {
      return 500;
    }
  }

  // 9. 회원정보 수정
  Future<int> modifyInfo(String nickname, String address) async {
    final url = Uri.parse("$baseUrl/user");
    final token = await storage.read(key: "accessToken");

    final response = await client.put(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        "Content-Type": "application/json",
      },
      body: jsonEncode(
        {
          "nickname": nickname,
          "address": address,
        },
      ),
    );

    if (response.statusCode == 200) {
      final Map<String, dynamic> responseData =
          jsonDecode(utf8.decode(response.bodyBytes));
      print(responseData);
      final String? nickname = responseData['nickname'];
      final String? address = responseData['address'];

      await storage.write(key: "nickname", value: nickname);
      await storage.write(key: "address", value: address);
      return 200; // 로그인 성공 및 토큰 생성 성공
    } else if (response.statusCode == 403 || response.statusCode == 404) {
      return 400; // 로그인 실패 (유저없음, 패스워드 불일치)
    }
    return 500; // 서버 연결 오류
  }

  // 10. 비밀번호 수정
  Future<int> modifyPassword(
      String oldPassword, String newPassword, String checkPassword) async {
    final url = Uri.parse("$baseUrl/user/password");
    final token = await storage.read(key: "accessToken");

    final response = await client.put(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        "Content-Type": "application/json",
      },
      body: jsonEncode(
        {
          "password": oldPassword,
          "newPassword": newPassword,
          "newPasswordCheck": checkPassword,
        },
      ),
    );

    if (response.statusCode == 200) {
      return 200;
    } else if (response.statusCode == 400) {
      return 400;
    } else if (response.statusCode == 403 || response.statusCode == 404) {
      return 404;
    }
    return 500;
  }

  // 11. 구글 로그인
  Future<int> googleLogin(
      String email, String password, String nickname, String address) async {
    final url = Uri.parse('$baseUrl/user/login/google');

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

    if (response.statusCode == 200) {
      final Map<String, dynamic> responseData =
          jsonDecode(utf8.decode(response.bodyBytes));
      final String? token = responseData['accessToken'];
      final String? refreshToken = responseData['refreshToken'];
      final String? eamil = responseData['email'];
      final String? userType = responseData['userType'];
      final String? nickname = responseData['nickname'];
      final String? address = responseData['address'];

      if (token != null) {
        await storage.write(key: "accessToken", value: token);
        await storage.write(key: "refreshToken", value: refreshToken);
        await storage.write(key: "email", value: eamil);
        await storage.write(key: "userType", value: userType);
        await storage.write(key: "nickname", value: nickname);
        await storage.write(key: "address", value: address);
        return 200; // 로그인 성공 및 토큰 생성 성공
      }
    } else if (response.statusCode == 400 || response.statusCode == 404) {
      return 400; // 로그인 실패 (유저없음, 패스워드 불일치)
    }
    return 500; // 서버 연결 오류
  }

  // 12. 구글 이메일 체크
  Future<int> googleCheckEmail(String email) async {
    final url = Uri.parse("$baseUrl/user/check/google");

    final response = await http.post(
      url,
      headers: {"Content-Type": "application/json"},
      body: jsonEncode(
        {
          "email": email,
        },
      ),
    );
    print(response.body);
    print(response.statusCode);
    if (response.statusCode == 200) {
      final response2 = await googleLogin(email, email, email, email);
      return response2;
    } else if (response.statusCode == 400 || response.statusCode == 404) {
      return 404; // 로그인 실패 (유저없음, 패스워드 불일치)
    }
    return 500; // 서버 연결 오류
  }

  // 14. 녹색 인증 제품 확인
  Future<List<Map<String, dynamic>>> checkGreenLabel(String keyword) async {
    final url = Uri.parse("$baseUrl/greenproduct/search?keyword=$keyword");
    final token = await storage.read(key: "accessToken");
    final headers = {
      'Authorization': 'Bearer $token', // accessToken을 헤더에 추가
    };
    final response = await client.get(
      url,
      headers: headers,
    );

    if (response.statusCode == 200) {
      List<dynamic> rawData = jsonDecode(utf8.decode(response.bodyBytes));
      print(rawData);
      return rawData.map((e) => e as Map<String, dynamic>).toList();
    } else {
      throw Exception('Failed to load data');
    }
  }
}
