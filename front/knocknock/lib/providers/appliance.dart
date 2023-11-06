import 'package:flutter/material.dart';

class SelectedAppliance with ChangeNotifier {
  String _category = '';
  String get category => _category;

  String _modelName = '';
  String get modelName => _modelName;

  void select(String category) {
    _category = category;
    notifyListeners();
  }

  void reset() {
    _category = '';
    notifyListeners();
  }

  void selectModel(String model) {
    _modelName = model;
    notifyListeners();
  }
}
