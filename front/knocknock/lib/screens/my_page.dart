import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:knocknock/widgets/app_bar_back.dart';

class MyPage extends StatefulWidget {
  const MyPage({super.key});

  @override
  State<MyPage> createState() => _MyPageState();
}

class _MyPageState extends State<MyPage> {
  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      appBar: AppBarBack(title: '내 정보'),
    );
  }
}
