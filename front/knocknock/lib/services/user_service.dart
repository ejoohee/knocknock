import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:async';
import 'package:http_interceptor/http_interceptor.dart';
import 'package:knocknock/constants/api.dart';

const String baseUrl = Api.BASE_URL;
const storage = FlutterSecureStorage();

class UserService {}
