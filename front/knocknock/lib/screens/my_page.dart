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
    return Scaffold(
      appBar: const AppBarBack(title: '내 정보'),
      body: SingleChildScrollView(
        scrollDirection: Axis.vertical,
        child: Column(
          children: [
            const SizedBox(
              height: 40,
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 20,
                    vertical: 35,
                  ),
                  decoration: BoxDecoration(
                    border: BorderDirectional(
                      end: BorderSide(
                        color: Colors.grey.shade300,
                        width: 0.8,
                      ),
                      bottom: BorderSide(
                        color: Colors.grey.shade300,
                        width: 0.8,
                      ),
                    ),
                  ),
                  child: Column(
                    children: [
                      Container(
                        width: 150,
                        height: 150,
                        decoration: const BoxDecoration(
                          image: DecorationImage(
                            image: AssetImage('assets/images/appliance.png'),
                          ),
                        ),
                      ),
                      const Text(
                        '가전',
                        style: TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.w600,
                        ),
                      )
                    ],
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 20,
                    vertical: 35,
                  ),
                  decoration: BoxDecoration(
                    border: BorderDirectional(
                      start: BorderSide(
                        color: Colors.grey.shade300,
                        width: 0.8,
                      ),
                      bottom: BorderSide(
                        color: Colors.grey.shade300,
                        width: 0.8,
                      ),
                    ),
                  ),
                  child: Column(
                    children: [
                      Container(
                        width: 150,
                        height: 150,
                        decoration: const BoxDecoration(
                          image: DecorationImage(
                            image: AssetImage('assets/images/electric.png'),
                          ),
                        ),
                      ),
                      const Text(
                        '전력 소비',
                        style: TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.w600,
                        ),
                      )
                    ],
                  ),
                ),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 20,
                    vertical: 35,
                  ),
                  decoration: BoxDecoration(
                    border: BorderDirectional(
                      end: BorderSide(
                        color: Colors.grey.shade300,
                        width: 0.8,
                      ),
                      top: BorderSide(
                        color: Colors.grey.shade300,
                        width: 0.8,
                      ),
                    ),
                  ),
                  child: Column(
                    children: [
                      Container(
                        width: 150,
                        height: 150,
                        decoration: const BoxDecoration(
                          image: DecorationImage(
                            image: AssetImage('assets/images/neighborhood.png'),
                          ),
                        ),
                      ),
                      const Text(
                        '우리 동네 정보',
                        style: TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.w600,
                        ),
                      )
                    ],
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 20,
                    vertical: 35,
                  ),
                  decoration: BoxDecoration(
                    border: BorderDirectional(
                      start: BorderSide(
                        color: Colors.grey.shade300,
                        width: 0.8,
                      ),
                      top: BorderSide(
                        color: Colors.grey.shade300,
                        width: 0.8,
                      ),
                    ),
                  ),
                  child: Column(
                    children: [
                      Container(
                        width: 150,
                        height: 150,
                        decoration: const BoxDecoration(
                          image: DecorationImage(
                            image: AssetImage('assets/images/recycle.png'),
                          ),
                        ),
                      ),
                      const Text(
                        '폐 가전 수거 정보',
                        style: TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.w600,
                        ),
                      )
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(
              height: 50,
            ),
            TextButton(
              onPressed: () {},
              child: const Text(
                '내 정보 수정',
                style: TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.w600,
                  color: Colors.grey,
                ),
              ),
            ),
            const Divider(
              indent: 70,
              endIndent: 70,
            ),
            TextButton(
              onPressed: () {},
              child: const Text(
                '로그아웃',
                style: TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.w600,
                  color: Colors.grey,
                ),
              ),
            ),
            const Divider(
              indent: 70,
              endIndent: 70,
            ),
          ],
        ),
      ),
    );
  }
}
