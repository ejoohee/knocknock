import 'package:flutter/material.dart';

class SelectedAppliance with ChangeNotifier {
  String _category = '';
  String get category => _category;

  int _modelId = 0;
  int get modelId => _modelId;

  int _comparedMine = 0;
  int get comparedMine => _comparedMine;

  String _nickname = '';
  String get nickname => _nickname;

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

  void selectMyModel(int myModelId) {
    _comparedMine = myModelId;
    notifyListeners();
  }

  void selectNickname(String nickname) {
    _nickname = nickname;
    notifyListeners();
  }
}
