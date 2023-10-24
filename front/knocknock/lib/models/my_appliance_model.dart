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
