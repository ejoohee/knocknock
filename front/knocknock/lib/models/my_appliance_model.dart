// 내 가전 등록하기 전 맞는 정보인지 확인용
class MyModelRegistering {
  String? category;
  String? modelName;
  String? modelBrand;
  String? modelImg;

  MyModelRegistering(
      {this.category, this.modelName, this.modelBrand, this.modelImg});

  MyModelRegistering.fromJson(Map<String, dynamic> json) {
    category = json['category'];
    modelName = json['modelName'];
    modelBrand = json['modelBrand'];
    modelImg = json['modelImg'];
  }

  // Map<String, dynamic> toJson() {
  //   final Map<String, dynamic> data = new Map<String, dynamic>();
  //   data['category'] = this.category;
  //   data['modelName'] = this.modelName;
  //   data['modelBrand'] = this.modelBrand;
  //   data['modelImg'] = this.modelImg;
  //   return data;
  // }
}

// 내 가전 목록
class MyModelTile {
  int? myModelId;
  int? modelId;
  String? category;
  String? modelBrand;
  int? modelGrade;
  String? modelNickname;
  String? addAtPin;

  MyModelTile(
      {this.myModelId,
      this.modelId,
      this.category,
      this.modelBrand,
      this.modelGrade,
      this.modelNickname,
      this.addAtPin});

  MyModelTile.fromJson(Map<String, dynamic> json) {
    myModelId = json['myModelId'];
    modelId = json['modelId'];
    category = json['category'];
    modelBrand = json['modelBrand'];
    modelGrade = json['modelGrade'];
    modelNickname = json['modelNickname'];
    addAtPin = json['addAtPin'];
  }

  // Map<String, dynamic> toJson() {
  //   final Map<String, dynamic> data = <String, dynamic>{};
  //   data['myModelId'] = myModelId;
  //   data['modelId'] = modelId;
  //   data['category'] = category;
  //   data['modelBrand'] = modelBrand;
  //   data['modelGrade'] = modelGrade;
  //   data['modelNickname'] = modelNickname;
  //   data['addAtPin'] = addAtPin;
  //   return data;
  // }
}

// 내 가전 상세
class MyModelDetail {
  int? myModelId;
  int? modelId;
  String? category;
  String? modelName;
  String? modelBrand;
  int? modelGrade;
  String? modelImg;
  String? modelNickname;
  String? usage1;
  double? usageValue1;
  String? usageUnit1;
  String? usage2;
  double? usageValue2;
  String? usageUnit2;
  String? usage3;
  double? usageValue3;
  String? usageUnit3;
  int? modelCo2;
  String? co2Unit;
  int? modelCost;
  String? costUnit;
  String? addAtPin;

  MyModelDetail(
      {this.myModelId,
      this.modelId,
      this.category,
      this.modelName,
      this.modelBrand,
      this.modelGrade,
      this.modelImg,
      this.modelNickname,
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
      this.addAtPin});

  MyModelDetail.fromJson(Map<String, dynamic> json) {
    myModelId = json['myModelId'];
    modelId = json['modelId'];
    category = json['category'];
    modelName = json['modelName'];
    modelBrand = json['modelBrand'];
    modelGrade = json['modelGrade'];
    modelImg = json['modelImg'];
    modelNickname = json['modelNickname'];
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
    addAtPin = json['addAtPin'];
  }

  // Map<String, dynamic> toJson() {
  //   final Map<String, dynamic> data = new Map<String, dynamic>();
  //   data['myModelId'] = this.myModelId;
  //   data['modelId'] = this.modelId;
  //   data['category'] = this.category;
  //   data['modelName'] = this.modelName;
  //   data['modelBrand'] = this.modelBrand;
  //   data['modelGrade'] = this.modelGrade;
  //   data['modelImg'] = this.modelImg;
  //   data['modelNickname'] = this.modelNickname;
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
  //   data['addAtPin'] = this.addAtPin;
  //   return data;
  // }
}
