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

  // 카테고리 불러오기
  Future<List<String>> findCategories() async {
    List<String> categories = [];
    final url = Uri.parse('$baseUrl/category');

    final token = await storage.read(key: "accessToken");
    final headers = {
      'Authorization': 'Bearer $token', // accessToken을 헤더에 추가
    };

    final response = await client.get(
      url,
      headers: headers,
    );

    if (response.statusCode == 200) {
      final List<dynamic> categoryData =
          jsonDecode(utf8.decode(response.bodyBytes));
      for (var categoryDatum in categoryData) {
        final String categoryName = categoryDatum['categoryName'];
        categories.add(categoryName);
      }
    } else if (response.statusCode == 401) {
      findCategories();
    }

//
    return categories;
  }

  // 새 가전 목록 조회
  Future<List<NewModelTile>> findNewModelList({
    required String type,
    required String keyword,
    required String category,
  }) async {
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

    print('응답 받았다 ${response.statusCode}');

    if (response.statusCode == 200) {
      final List<dynamic> models = jsonDecode(utf8.decode(response.bodyBytes));
      for (var model in models) {
        modelTiles.add(NewModelTile.fromJson(model));
      }
    } else if (response.statusCode == 401) {
      findNewModelList(type: type, keyword: keyword, category: category);
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
      final dynamic model = jsonDecode(utf8.decode(response.bodyBytes));
      newModel = NewModelDetail.fromJson(model);
    } else if (response.statusCode == 401) {
      findNewModelDetail(modelId);
    }
    // 나머지 에러 핸들링 필요
    return newModel;
  }

  // 가전 등록 과정에서 모델명으로 우선 조회
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

  // 내 가전 등록
  Future<String> registerMyAppliance(
    String modelName,
    String modelNickname,
  ) async {
    String message = '';

    final url = Uri.parse('$baseUrl/model/my');
    final token = await storage.read(key: "accessToken");
    final headers = {
      'Authorization': 'Bearer $token',
      "Content-Type": "application/json",
    };
    final response = await client.post(
      url,
      headers: headers,
      body: jsonEncode({
        "modelName": modelName,
        "modelNickname": modelNickname,
      }),
    );

    if (response.statusCode == 201) {
      Map<String, dynamic> responseBody = jsonDecode(response.body);
      message = responseBody['message'];
    } else if (response.statusCode == 404) {
      Map<String, dynamic> responseBody = jsonDecode(response.body);
      message = responseBody['message'];
    } else if (response.statusCode == 401) {
      registerMyAppliance(modelName, modelNickname);
    }
    // 500 일 때는 빈 메세지.
    // return type string으로 둬도 되는지 추후 확인
    return message;
  }

  // 내 가전 삭제
  Future<String> deleteMyAppliance(int myModelId) async {
    String message = '';

    final url = Uri.parse('$baseUrl/model/my/$myModelId');
    final token = await storage.read(key: "accessToken");
    final headers = {
      'Authorization': 'Bearer $token',
    };
    final response = await client.delete(
      url,
      headers: headers,
    );

    if (response.statusCode == 200) {
      Map<String, dynamic> responseBody = jsonDecode(response.body);
      message = responseBody['message'];
    } else if (response.statusCode == 404) {
      Map<String, dynamic> responseBody = jsonDecode(response.body);
      message = responseBody['message'];
    } else if (response.statusCode == 401) {
      deleteMyAppliance(myModelId);
    }
    // 500 일 때는 빈 메세지.
    return message;
  }

  // 내가 보유한 가전 목록 조회
  Future<List<MyModelTile>> findMyApplianceList(String category) async {
    List<MyModelTile> myApplianceList = [];

    final url = Uri.parse('$baseUrl/model?category=$category');
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
        myApplianceList.add(MyModelTile.fromJson(model));
      }
    } else if (response.statusCode == 401) {
      findMyApplianceList(category);
    }

    return myApplianceList;
  }

  // 내 가전 상세 조회
  Future<MyModelDetail> findMyApplianceDetail(int myModelId) async {
    late MyModelDetail myApplianceDetail;

    final url = Uri.parse('$baseUrl/model/my/one/$myModelId');
    final token = await storage.read(key: "accessToken");
    final headers = {
      'Authorization': 'Bearer $token', // accessToken을 헤더에 추가
    };
    final response = await client.get(
      url,
      headers: headers,
    );

    if (response.statusCode == 200) {
      final dynamic appliance = jsonDecode(response.body);
      myApplianceDetail = MyModelDetail.fromJson(appliance);
    } else if (response.statusCode == 401) {
      findMyApplianceDetail(myModelId);
    }
    // 404 조회 불가(db에 없다)
    // 500 서버에러
    return myApplianceDetail;
  }

  // 내 가전 핀 등록 & 해제(patch)
  Future<int> pinMyAppliance(int myModelId) async {
    final url = Uri.parse('$baseUrl/model/my/pin/$myModelId');
    final token = await storage.read(key: "accessToken");
    final headers = {
      'Authorization': 'Bearer $token', // accessToken을 헤더에 추가
    };
    final response = await client.patch(
      url,
      headers: headers,
    );

    // 에러 핸들링 더 추가 필요?
    // 200 or 다시 시도해주세요?
    return response.statusCode;
  }

  // 찜한 가전 목록 조회
  Future<List<LikedModel>> findLikedModel(String category) async {
    List<LikedModel> likedModelList = [];
    final url = Uri.parse('$baseUrl/model/like?category=$category');
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
        likedModelList.add(LikedModel.fromJson(model));
      }
    } else if (response.statusCode == 401) {
      findLikedModel(category);
    }

    //에러코드  403 > 빈 거? 혹은 아직 등록 ㄴ
    return likedModelList;
  }

  // 찜하기
  Future<String> addLike(int modelId) async {
    String message = '';
    print('모델아이디 들어왔다 : $modelId');
    final url = Uri.parse('$baseUrl/model/like/$modelId');
    final token = await storage.read(key: "accessToken");
    final headers = {
      'Authorization': 'Bearer $token', // accessToken을 헤더에 추가
    };

    final response = await client.post(
      url,
      headers: headers,
    );
    if (response.statusCode == 201) {
      message = '${response.statusCode}';
    } else if (response.statusCode == 401) {
      addLike(modelId);
    } else if (response.statusCode == 404) {
      Map<String, dynamic> responseBody = jsonDecode(response.body);

      message = responseBody['message'];
    }

    //에러코드  403
    return message;
  }

  // 찜 취소
  Future<String> cancelLike(int modelId) async {
    String message = '';

    final url = Uri.parse('$baseUrl/model/like/$modelId');
    final token = await storage.read(key: "accessToken");
    final headers = {
      'Authorization': 'Bearer $token', // accessToken을 헤더에 추가
    };

    final response = await client.delete(
      url,
      headers: headers,
    );

    if (response.statusCode == 200) {
      message = '${response.statusCode}';
    } else if (response.statusCode == 401) {
      cancelLike(modelId);
    } else if (response.statusCode == 404) {
      Map<String, dynamic> responseBody = jsonDecode(response.body);

      message = responseBody['message'];
    }

    //에러코드  403,, 처리..
    return message;
  }
}
