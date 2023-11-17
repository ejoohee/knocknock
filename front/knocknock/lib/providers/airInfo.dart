import 'package:flutter/material.dart';

class AirInfoProvider with ChangeNotifier {
  int _airInfo = 0;
  int get airInfoProvider => _airInfo;

  void setAirInfo(int airInfo) {
    _airInfo = airInfo;
    notifyListeners();
  }
}
