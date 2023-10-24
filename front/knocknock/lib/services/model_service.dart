import 'dart:convert';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http_interceptor/http_interceptor.dart';
import 'package:knocknock/constants/api.dart';
import 'package:knocknock/interceptors/interceptor.dart';
import 'package:knocknock/models/appliance_model.dart';
import 'package:knocknock/models/my_appliance_model.dart';

const String baseUrl = Api.BASE_URL;
const storage = FlutterSecureStorage();

class ModelService {
  final client = InterceptedClient.build(interceptors: [HttpInterceptor()]);

  // 새 가전 목록 조회
  Future<List<NewModelTile>> findNewModelList(
    String type,
    String keyword,
    String category,
  ) async {
    List<NewModelTile> modelTiles = [];

    final url = Uri.parse(
        '$baseUrl/model?type=$type&keyword=$keyword&category=$category');
    final token = await storage.read(key: "accessToken");
    final headers = {
      'Authorization': 'Bearer $token', // accessToken을 헤더에 추가
    };
    final response = await client.get(
      url,
      headers: headers,
    );

    if (response.statusCode == 200) {
      final List<dynamic> models = jsonDecode(response.body);
      for (var model in models) {
        modelTiles.add(NewModelTile.fromJson(model));
      }
    } else if (response.statusCode == 401) {
      findNewModelList(type, keyword, category);
    }

    return modelTiles;
  }

  // 새 가전 상세조회
  Future<NewModelDetail> findNewModelDetail(int modelId) async {
    // late가 될까요???
    late NewModelDetail newModel;
    final url = Uri.parse('$baseUrl/model/$modelId');
    final token = await storage.read(key: "accessToken");
    final headers = {
      'Authorization': 'Bearer $token', // accessToken을 헤더에 추가
    };
    final response = await client.get(
      url,
      headers: headers,
    );

    if (response.statusCode == 200) {
      final dynamic model = jsonDecode(response.body);
      newModel = NewModelDetail.fromJson(model);
    } else if (response.statusCode == 401) {
      findNewModelDetail(modelId);
    }

    return newModel;
  }

  // 가전 등록 시 모델명으로 우선 조회
  Future<MyModelRegistering> findRegistering(String modelName) async {
    // late가 될까요???
    late MyModelRegistering registeringModel;

    final url = Uri.parse('$baseUrl/model/check?modelName=$modelName');
    final token = await storage.read(key: "accessToken");
    final headers = {
      'Authorization': 'Bearer $token', // accessToken을 헤더에 추가
    };
    final response = await client.get(
      url,
      headers: headers,
    );
    if (response.statusCode == 200) {
      final dynamic model = jsonDecode(response.body);
      registeringModel = MyModelRegistering.fromJson(model);
    } else if (response.statusCode == 401) {
      findRegistering(modelName);
    }

    return registeringModel;
  }
}
