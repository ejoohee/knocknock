import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:knocknock/providers/page_index.dart';
import 'package:knocknock/screens/average_electronic.dart';
import 'package:knocknock/screens/log_in.dart';
import 'package:knocknock/screens/my_info_modify.dart';
import 'package:knocknock/services/user_service.dart';
import 'package:knocknock/widgets/app_bar_back.dart';
import 'package:provider/provider.dart';

class MyPage extends StatefulWidget {
  const MyPage({super.key});

  @override
  State<MyPage> createState() => _MyPageState();
}

class _MyPageState extends State<MyPage> {
  UserService userService = UserService();
  TextEditingController passwordController = TextEditingController();

  onCheckTap() async {
    String password = passwordController.text;
    final response = await userService.checkPassword(password);
    if (response == 200) {
      if (!mounted) return;
      Navigator.pushReplacement(context,
          MaterialPageRoute(builder: (context) => const MyInfoModify()));
    } else if (response == 404) {
      if (!mounted) return;
      showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
            title: const Text('알림'),
            content: const Text('비밀번호가 일치하지 않습니다'),
            actions: <Widget>[
              TextButton(
                onPressed: () {
                  Navigator.of(context).pop();
                },
                child: const Text('확인'),
              ),
            ],
          );
        },
      );
    } else {
      if (!mounted) return;
      showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
            title: const Text('알림'),
            content: const Text('서버 연결 오류입니다'),
            actions: <Widget>[
              TextButton(
                onPressed: () {
                  Navigator.of(context).pop();
                },
                child: const Text('확인'),
              ),
            ],
          );
        },
      );
    }
  }

  showPasswordCheckDialog() async {
    if (!mounted) return;
    await showDialog(
      context: context,
      builder: (BuildContext dialogContext) {
        return AlertDialog(
          title: const Text('비밀번호 확인'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextFormField(
                autovalidateMode: AutovalidateMode.onUserInteraction,
                controller: passwordController,
                obscureText: true,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return '비밀번호를 입력하세요.';
                  } else if (value.length < 8 || value.length > 16) {
                    return '비밀번호는 8~16자리 사이여야 합니다.';
                  }
                  return null;
                },
                decoration: const InputDecoration(
                  labelText: '비밀번호',
                ),
              ),
            ],
          ),
          actions: <Widget>[
            TextButton(
              onPressed: onCheckTap,
              child: const Text('확인'),
            ),
            TextButton(
              onPressed: () {
                Navigator.of(dialogContext).pop(); // 대화상자 닫기
              },
              child: const Text('취소'),
            ),
          ],
        );
      },
    );
  }

  onMyInfoModifyTextPressed() async {
    showPasswordCheckDialog();
  }

  onLogoutTextPressed() async {
    final response = await userService.logout();
    if (response == 200) {
      if (!mounted) return;
      Navigator.pushReplacement(
          context, MaterialPageRoute(builder: (context) => const Login()));
    } else if (response == 500) {
      if (!mounted) return;
      showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
            title: const Text('알림'),
            content: const Text('서버 연결 오류입니다'),
            actions: <Widget>[
              TextButton(
                onPressed: () {
                  Navigator.of(context).pop();
                },
                child: const Text('확인'),
              ),
            ],
          );
        },
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SingleChildScrollView(
        scrollDirection: Axis.vertical,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const SizedBox(
              height: 50,
            ),
            Container(
              height: 80,
              alignment: Alignment.center,
              child: const Text(
                '서비스',
                style: TextStyle(
                  fontSize: 40,
                ),
              ),
            ),
            const SizedBox(
              height: 50,
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
                            fit: BoxFit.contain,
                          ),
                        ),
                      ),
                      const Text(
                        '가전',
                        style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.w600,
                        ),
                      )
                    ],
                  ),
                ),
                GestureDetector(
                  onTap: () {
                    if (!mounted) return;
                    Navigator.pushReplacement(
                        context,
                        MaterialPageRoute(
                            builder: (context) => const AverageElectronic()));
                  },
                  child: Container(
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
                              fit: BoxFit.contain,
                            ),
                          ),
                        ),
                        const Text(
                          '전력 소비량',
                          style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.w600,
                          ),
                        )
                      ],
                    ),
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
                            fit: BoxFit.contain,
                          ),
                        ),
                      ),
                      const Text(
                        '우리 동네 정보',
                        style: TextStyle(
                          fontSize: 18,
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
                            fit: BoxFit.contain,
                          ),
                        ),
                      ),
                      const Text(
                        '폐 가전 수거 정보',
                        style: TextStyle(
                          fontSize: 18,
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
              onPressed: () {
                onMyInfoModifyTextPressed();
              },
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
              onPressed: () {
                onLogoutTextPressed();
              },
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
