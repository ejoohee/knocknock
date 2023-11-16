import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:knocknock/providers/my_appliance.dart';
import 'package:knocknock/providers/page_index.dart';
import 'package:knocknock/screens/average_electronic.dart';
import 'package:knocknock/screens/log_in.dart';
import 'package:knocknock/screens/manage_appliances.dart';
import 'package:knocknock/screens/my_info_modify.dart';
import 'package:knocknock/screens/waste_info.dart';
import 'package:knocknock/services/user_service.dart';
import 'package:knocknock/widgets/app_bar_back.dart';
import 'package:provider/provider.dart';
import 'package:text_divider/text_divider.dart';
import 'package:intl/intl.dart';

class MyPage extends StatefulWidget {
  const MyPage({super.key});

  @override
  State<MyPage> createState() => _MyPageState();
}

class _MyPageState extends State<MyPage> {
  UserService userService = UserService();
  TextEditingController passwordController = TextEditingController();
  String today = '';
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    today = getToday();
  }

  String getToday() {
    DateTime now = DateTime.now();
    DateFormat formatter = DateFormat('yyyy-MM-dd');
    today = formatter.format(now);
    return today;
  }

  onWasteInfoTap() async {
    if (!mounted) return;
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => const WasteInfo(),
      ),
    );
  }

  onAverageElectronTap() async {
    if (!mounted) return;
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => const AverageElectronic(),
      ),
    );
  }

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
      body: Padding(
        padding: const EdgeInsets.symmetric(
          vertical: 20.0,
          horizontal: 30,
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          mainAxisSize: MainAxisSize.max,
          children: [
            const SizedBox(
              height: 40,
            ),
            Flexible(
              flex: 3,
              child: Row(
                children: [
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.fromLTRB(15, 15, 10, 15),
                      child: Container(
                        padding: const EdgeInsets.all(15),
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(15),
                          color: Theme.of(context).colorScheme.outlineVariant,
                          boxShadow: [
                            BoxShadow(
                              color: Theme.of(context)
                                  .colorScheme
                                  .shadow
                                  .withOpacity(0.5),
                              spreadRadius: 1,
                              blurRadius: 1,
                              offset: const Offset(-2, 2),
                            ),
                            BoxShadow(
                              color:
                                  Theme.of(context).colorScheme.surfaceVariant,
                              spreadRadius: 1,
                              blurRadius: 1,
                              offset: const Offset(2, -2),
                            ),
                          ],
                        ),
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text(
                              '등록한 가전 수',
                              style: TextStyle(
                                fontSize: 18,
                                fontWeight: FontWeight.w800,
                              ),
                              textAlign: TextAlign.left,
                            ),
                            Text(
                              '${context.watch<RegisterAppliance>().qtt} 개',
                              style: const TextStyle(
                                fontSize: 20,
                                fontWeight: FontWeight.w300,
                              ),
                              textAlign: TextAlign.left,
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.fromLTRB(10, 15, 15, 15),
                      child: Container(
                        padding: const EdgeInsets.all(15),
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(15),
                          color: Theme.of(context).colorScheme.outlineVariant,
                          boxShadow: [
                            BoxShadow(
                              color: Theme.of(context)
                                  .colorScheme
                                  .shadow
                                  .withOpacity(0.5),
                              spreadRadius: 1,
                              blurRadius: 1,
                              offset: const Offset(-2, 2),
                            ),
                            BoxShadow(
                              color:
                                  Theme.of(context).colorScheme.surfaceVariant,
                              spreadRadius: 1,
                              blurRadius: 1,
                              offset: const Offset(2, -2),
                            ),
                          ],
                        ),
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text(
                              'Today is...',
                              style: TextStyle(
                                fontSize: 18,
                                fontWeight: FontWeight.w800,
                              ),
                              textAlign: TextAlign.left,
                            ),
                            Text(
                              today,
                              style: const TextStyle(
                                fontSize: 20,
                                fontWeight: FontWeight.w300,
                              ),
                              textAlign: TextAlign.left,
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(
              height: 20,
            ),
            const TextDivider(
              text: Text('Services'),
              indent: 20,
              endIndent: 20,
            ),
            Flexible(
              flex: 2,
              child: Padding(
                padding:
                    const EdgeInsets.symmetric(horizontal: 15.0, vertical: 8),
                child: GestureDetector(
                  onTap: () {
                    Navigator.of(context).push(
                      MaterialPageRoute(
                        builder: (context) => const ManageAppliances(),
                      ),
                    );
                  },
                  child: Container(
                    // height: 100,
                    alignment: Alignment.center,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(15),
                      color: Theme.of(context).colorScheme.outlineVariant,
                      boxShadow: [
                        BoxShadow(
                          color: Theme.of(context)
                              .colorScheme
                              .shadow
                              .withOpacity(0.5),
                          spreadRadius: 1,
                          blurRadius: 1,
                          offset: const Offset(-2, 2),
                        ),
                        BoxShadow(
                          color: Theme.of(context).colorScheme.surfaceVariant,
                          spreadRadius: 1,
                          blurRadius: 1,
                          offset: const Offset(2, -2),
                        ),
                      ],
                    ),
                    child: ListTile(
                      leading: Image.asset(
                        'assets/images/appliance.png',
                        width: 40,
                      ),
                      title: const Text('가전 서비스'),
                      trailing: const Icon(Icons.chevron_right_rounded),
                      titleAlignment: ListTileTitleAlignment.center,
                    ),
                  ),
                ),
              ),
            ),
            Flexible(
              flex: 2,
              child: Padding(
                padding:
                    const EdgeInsets.symmetric(horizontal: 15.0, vertical: 8),
                child: GestureDetector(
                  onTap: onAverageElectronTap,
                  child: Container(
                    // height: 100,
                    alignment: Alignment.center,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(15),
                      color: Theme.of(context).colorScheme.outlineVariant,
                      boxShadow: [
                        BoxShadow(
                          color: Theme.of(context)
                              .colorScheme
                              .shadow
                              .withOpacity(0.5),
                          spreadRadius: 2,
                          blurRadius: 3,
                          offset: const Offset(-2, 2),
                        ),
                        BoxShadow(
                          color: Theme.of(context).colorScheme.surfaceVariant,
                          spreadRadius: 1,
                          blurRadius: 1,
                          offset: const Offset(2, -2),
                        ),
                      ],
                    ),
                    child: ListTile(
                      leading: Padding(
                        padding: const EdgeInsets.all(5.0),
                        child: Image.asset(
                          'assets/images/electric.png',
                          width: 30,
                        ),
                      ),
                      title: const Text('우리 동네 전력 소비'),
                      trailing: const Icon(Icons.chevron_right_rounded),
                      titleAlignment: ListTileTitleAlignment.center,
                    ),
                  ),
                ),
              ),
            ),
            Flexible(
              flex: 2,
              child: Padding(
                padding:
                    const EdgeInsets.symmetric(horizontal: 15.0, vertical: 8),
                child: GestureDetector(
                  onTap: onWasteInfoTap,
                  child: Container(
                    // height: 100,
                    alignment: Alignment.center,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(15),
                      color: Theme.of(context).colorScheme.outlineVariant,
                      boxShadow: [
                        BoxShadow(
                          color: Theme.of(context)
                              .colorScheme
                              .shadow
                              .withOpacity(0.5),
                          spreadRadius: 2,
                          blurRadius: 3,
                          offset: const Offset(-2, 2),
                        ),
                        BoxShadow(
                          color: Theme.of(context).colorScheme.surfaceVariant,
                          spreadRadius: 1,
                          blurRadius: 1,
                          offset: const Offset(2, -2),
                        ),
                      ],
                    ),
                    child: ListTile(
                      leading: Image.asset(
                        'assets/images/recycle.png',
                        width: 40,
                      ),
                      title: const Text('폐가전 수거 정보'),
                      trailing: const Icon(Icons.chevron_right_rounded),
                      titleAlignment: ListTileTitleAlignment.center,
                    ),
                  ),
                ),
              ),
            ),
            const SizedBox(
              height: 20,
            ),
            const TextDivider(
              text: Text('My'),
              indent: 20,
              endIndent: 20,
            ),
            Flexible(
              flex: 2,
              child: Padding(
                padding:
                    const EdgeInsets.symmetric(horizontal: 15.0, vertical: 8),
                child: GestureDetector(
                  onTap: onMyInfoModifyTextPressed,
                  child: Container(
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(15),
                      color: Theme.of(context).colorScheme.outlineVariant,
                      boxShadow: [
                        BoxShadow(
                          color: Theme.of(context)
                              .colorScheme
                              .shadow
                              .withOpacity(0.5),
                          spreadRadius: 2,
                          blurRadius: 3,
                          offset: const Offset(-2, 2),
                        ),
                        BoxShadow(
                          color: Theme.of(context).colorScheme.surfaceVariant,
                          spreadRadius: 1,
                          blurRadius: 1,
                          offset: const Offset(2, -2),
                        ),
                      ],
                    ),
                    child: const ListTile(
                      leading: Padding(
                        padding: EdgeInsets.symmetric(horizontal: 5.0),
                        child: Icon(Icons.person),
                      ),
                      title: Text('내 정보 수정'),
                      trailing: Icon(Icons.chevron_right_rounded),
                      titleAlignment: ListTileTitleAlignment.center,
                    ),
                  ),
                ),
              ),
            ),
            Flexible(
              flex: 2,
              child: Padding(
                padding:
                    const EdgeInsets.symmetric(horizontal: 15.0, vertical: 8),
                child: GestureDetector(
                  onTap: onLogoutTextPressed,
                  child: Container(
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(15),
                      color: Theme.of(context).colorScheme.outlineVariant,
                      boxShadow: [
                        BoxShadow(
                          color: Theme.of(context)
                              .colorScheme
                              .shadow
                              .withOpacity(0.5),
                          spreadRadius: 2,
                          blurRadius: 3,
                          offset: const Offset(-2, 2),
                        ),
                        BoxShadow(
                          color: Theme.of(context).colorScheme.surfaceVariant,
                          spreadRadius: 1,
                          blurRadius: 1,
                          offset: const Offset(2, -2),
                        ),
                      ],
                    ),
                    child: const ListTile(
                      leading: Padding(
                        padding: EdgeInsets.symmetric(horizontal: 5.0),
                        child: Icon(Icons.logout),
                      ),
                      title: Text('로그아웃'),
                      trailing: Icon(Icons.chevron_right_rounded),
                      titleAlignment: ListTileTitleAlignment.center,
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
