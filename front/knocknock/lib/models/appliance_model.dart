// 새 가전 목록
class NewModelTile {
  int? modelId;
  String? modelName;
  String? modelBrand;
  int? modelGrade;
  bool? isLiked;

  NewModelTile(
      {this.modelId,
      this.modelName,
      this.modelBrand,
      this.modelGrade,
      this.isLiked});

  NewModelTile.fromJson(Map<String, dynamic> json) {
    modelId = json['modelId'];
    modelName = json['modelName'];
    modelBrand = json['modelBrand'];
    modelGrade = json['modelGrade'];
    isLiked = json['isLiked'];
  }

  // Map<String, dynamic> toJson() {
  //   final Map<String, dynamic> data = new Map<String, dynamic>();
  //   data['modelId'] = this.modelId;
  //   data['modelName'] = this.modelName;
  //   data['modelBrand'] = this.modelBrand;
  //   data['modelGrade'] = this.modelGrade;
  //   data['isLiked'] = this.isLiked;
  //   return data;
  // }
}

// 새 가전 상세
class NewModelDetail {
  int? modelId;
  String? category;
  String? modelName;
  String? modelBrand;
  int? modelGrade;
  String? modelImg;
  String? modelURL;
  String? usage1;
  int? usageValue1;
  String? usageUnit1;
  String? usage2;
  int? usageValue2;
  String? usageUnit2;
  String? usage3;
  int? usageValue3;
  String? usageUnit3;
  int? modelCo2;
  String? co2Unit;
  int? modelCost;
  String? costUnit;
  String? isLiked;

  NewModelDetail(
      {this.modelId,
      this.category,
      this.modelName,
      this.modelBrand,
      this.modelGrade,
      this.modelImg,
      this.modelURL,
      this.usage1,
      this.usageValue1,
      this.usageUnit1,
      this.usage2,
      this.usageValue2,
      this.usageUnit2,
      this.usage3,
      this.usageValue3,
      this.usageUnit3,
      this.modelCo2,
      this.co2Unit,
      this.modelCost,
      this.costUnit,
      this.isLiked});

  NewModelDetail.fromJson(Map<String, dynamic> json) {
    modelId = json['modelId'];
    category = json['category'];
    modelName = json['modelName'];
    modelBrand = json['modelBrand'];
    modelGrade = json['modelGrade'];
    modelImg = json['modelImg'];
    modelURL = json['modelURL'];
    usage1 = json['usage1'];
    usageValue1 = json['usageValue1'];
    usageUnit1 = json['usageUnit1'];
    usage2 = json['usage2'];
    usageValue2 = json['usageValue2'];
    usageUnit2 = json['usageUnit2'];
    usage3 = json['usage3'];
    usageValue3 = json['usageValue3'];
    usageUnit3 = json['usageUnit3'];
    modelCo2 = json['modelCo2'];
    co2Unit = json['co2Unit'];
    modelCost = json['modelCost'];
    costUnit = json['costUnit'];
    isLiked = json['isLiked'];
  }

  // Map<String, dynamic> toJson() {
  //   final Map<String, dynamic> data = new Map<String, dynamic>();
  //   data['modelId'] = this.modelId;
  //   data['category'] = this.category;
  //   data['modelName'] = this.modelName;
  //   data['modelBrand'] = this.modelBrand;
  //   data['modelGrade'] = this.modelGrade;
  //   data['modelImg'] = this.modelImg;
  //   data['modelURL'] = this.modelURL;
  //   data['usage1'] = this.usage1;
  //   data['usageValue1'] = this.usageValue1;
  //   data['usageUnit1'] = this.usageUnit1;
  //   data['usage2'] = this.usage2;
  //   data['usageValue2'] = this.usageValue2;
  //   data['usageUnit2'] = this.usageUnit2;
  //   data['usage3'] = this.usage3;
  //   data['usageValue3'] = this.usageValue3;
  //   data['usageUnit3'] = this.usageUnit3;
  //   data['modelCo2'] = this.modelCo2;
  //   data['co2Unit'] = this.co2Unit;
  //   data['modelCost'] = this.modelCost;
  //   data['costUnit'] = this.costUnit;
  //   data['isLiked'] = this.isLiked;
  //   return data;
  // }
}

// 찜한 가전 목록
class LikedModel {
  int? modelId;
  String? modelName;
  String? modelBrand;

  LikedModel({this.modelId, this.modelName, this.modelBrand});

  LikedModel.fromJson(Map<String, dynamic> json) {
    modelId = json['modelId'];
    modelName = json['modelName'];
    modelBrand = json['modelBrand'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['modelId'] = modelId;
    data['modelName'] = modelName;
    data['modelBrand'] = modelBrand;
    return data;
  }
}
