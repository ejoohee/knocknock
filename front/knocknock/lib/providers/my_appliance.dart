import 'package:flutter/material.dart';
import 'package:knocknock/models/my_appliance_model.dart';

class RegisterAppliance with ChangeNotifier {
  MyModelRegistering? _myModel;
  MyModelRegistering? get myModel => _myModel;

  String _nickname = '';
  String get nickname => _nickname;

  void register(MyModelRegistering? myModel) {
    _myModel = myModel;
    notifyListeners();
  }

  void resetMyModel() {
    _myModel = null;
    notifyListeners();
  }

  void registerNickname(String nickname) {
    _nickname = nickname;
    notifyListeners();
  }
}
