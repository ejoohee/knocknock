import 'package:flutter/material.dart';

class SelectedAppliance with ChangeNotifier {
  String _category = '';
  String get category => _category;

  int _modelId = 0;
  int get modelId => _modelId;

  void select(String category) {
    _category = category;
    notifyListeners();
  }

  void reset() {
    _category = '';
    notifyListeners();
  }

  void selectModel(int modelId) {
    _modelId = modelId;
    notifyListeners();
  }
}
