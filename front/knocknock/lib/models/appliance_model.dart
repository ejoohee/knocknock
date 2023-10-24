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

class NewModelDetail {
  int? modelId;
  String? category;
  String? modelName;
  String? modelBrand;
  int? modelGrade;
  String? modelImg;
  String? modelURL;
  String? usage;
  int? modelUsage;
  String? usageUnit;
  String? usage2;
  int? modelUsage2;
  String? usage3;
  int? modelUsage3;
  int? modelCo2;
  int? modelCost;
  String? releasedDate;
  bool? isLiked;

  NewModelDetail(
      {this.modelId,
      this.category,
      this.modelName,
      this.modelBrand,
      this.modelGrade,
      this.modelImg,
      this.modelURL,
      this.usage,
      this.modelUsage,
      this.usageUnit,
      this.usage2,
      this.modelUsage2,
      this.usage3,
      this.modelUsage3,
      this.modelCo2,
      this.modelCost,
      this.releasedDate,
      this.isLiked});

  NewModelDetail.fromJson(Map<String, dynamic> json) {
    modelId = json['modelId'];
    category = json['category'];
    modelName = json['modelName'];
    modelBrand = json['modelBrand'];
    modelGrade = json['modelGrade'];
    modelImg = json['modelImg'];
    modelURL = json['modelURL'];
    usage = json['usage'];
    modelUsage = json['modelUsage'];
    usageUnit = json['usageUnit'];
    usage2 = json['usage2'];
    modelUsage2 = json['modelUsage2'];
    usage3 = json['usage3'];
    modelUsage3 = json['modelUsage3'];
    modelCo2 = json['modelCo2'];
    modelCost = json['modelCost'];
    releasedDate = json['releasedDate'];
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
  //   data['usage'] = this.usage;
  //   data['modelUsage'] = this.modelUsage;
  //   data['usageUnit'] = this.usageUnit;
  //   data['usage2'] = this.usage2;
  //   data['modelUsage2'] = this.modelUsage2;
  //   data['usage3'] = this.usage3;
  //   data['modelUsage3'] = this.modelUsage3;
  //   data['modelCo2'] = this.modelCo2;
  //   data['modelCost'] = this.modelCost;
  //   data['releasedDate'] = this.releasedDate;
  //   data['isLiked'] = this.isLiked;
  //   return data;
  // }
}
