import 'package:flutter/material.dart';

class CurrentPageIndex with ChangeNotifier {
  int _currentPageIndex = 0;
  int get currentPageIndexProvider => _currentPageIndex;

  void move(int currentPageIndex) {
    _currentPageIndex = currentPageIndex;
    notifyListeners();
  }

  void goHome() {
    _currentPageIndex = 0;
    notifyListeners();
  }
}
