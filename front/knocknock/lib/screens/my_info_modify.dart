import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:knocknock/screens/home_screen.dart';
import 'package:knocknock/screens/log_in.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:knocknock/screens/my_page.dart';
import 'package:knocknock/services/user_service.dart';
import 'package:knocknock/widgets/app_bar_back.dart';
import 'package:remedi_kopo/remedi_kopo.dart';

class MyInfoModify extends StatefulWidget {
  const MyInfoModify({super.key});

  @override
  State<MyInfoModify> createState() => _MyInfoModifyState();
}

const storage = FlutterSecureStorage();

class _MyInfoModifyState extends State<MyInfoModify> {
  final _formKey = GlobalKey<FormState>();
  Map<String, String> formData = {};
  TextEditingController emailController = TextEditingController();
  TextEditingController oldPasswordController = TextEditingController();
  TextEditingController newPasswordController = TextEditingController();
  TextEditingController checkPasswordController = TextEditingController();
  TextEditingController nicknameController = TextEditingController();
  TextEditingController addressController = TextEditingController();
  UserService userService = UserService();

  @override
  void initState() {
    super.initState();
    _loadInfo();
  }

  void searchAddress(BuildContext context) async {
    KopoModel? model = await Navigator.push(
      context,
      CupertinoPageRoute(
        builder: (context) => RemediKopo(),
      ),
    );

    if (model != null) {
      final address = model.address ?? '';
      addressController.value = TextEditingValue(
        text: address,
      );
      formData['address'] = address;
    }
  }

  Future<void> _loadInfo() async {
    String? email = await storage.read(key: "email");
    String? nickname = await storage.read(key: "nickname");
    String? address = await storage.read(key: "address");
    setState(
      () {
        emailController = TextEditingController(text: email);
        nicknameController = TextEditingController(text: nickname);
        addressController = TextEditingController(text: address);
      },
    );
  }

  Future<void> onModifyButtonTap() async {
    if (_formKey.currentState!.validate()) {
      final nickname = nicknameController.text;
      final address = addressController.text;
      final response = await userService.modifyInfo(nickname, address);
      setState(() {});
      if (response == 200) {
        if (!mounted) return;
        showDialog(
          context: context,
          builder: (BuildContext context) {
            return AlertDialog(
              title: const Text('알림'),
              content: const Text('수정 완료'),
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
  }

  Future<void> onModifyPasswordTap() async {
    if (_formKey.currentState!.validate()) {
      final oldPassword = oldPasswordController.text;
      final newPassword = newPasswordController.text;
      final checkPassword = checkPasswordController.text;
      final response = await userService.modifyPassword(
          oldPassword, newPassword, checkPassword);
      if (response == 200) {
        if (!mounted) return;
        showDialog(
          context: context,
          builder: (BuildContext context) {
            return AlertDialog(
              title: const Text('알림'),
              content: const Text('변경 완료'),
              actions: <Widget>[
                TextButton(
                  onPressed: () {
                    Navigator.of(context).push(
                      MaterialPageRoute(
                        builder: (context) => const MyInfoModify(),
                      ),
                    );
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
              content: const Text('현재 비밀번호를 확인해주세요'),
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
  }

  showModifyPassword() async {
    if (!mounted) return;
    await showDialog(
      context: context,
      builder: (BuildContext dialogContext) {
        return AlertDialog(
          title: const Text('비밀번호 변경'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextFormField(
                autovalidateMode: AutovalidateMode.onUserInteraction,
                controller: oldPasswordController,
                obscureText: true,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return '사용할 비밀번호를 입력하세요.';
                  } else if (value.length < 8 || value.length > 16) {
                    return '비밀번호는 8~16자리 사이여야 합니다.';
                  }
                  return null;
                },
                decoration: InputDecoration(
                  focusedBorder: OutlineInputBorder(
                    borderSide: const BorderSide(color: Colors.green),
                    borderRadius: BorderRadius.circular(10.0),
                  ),
                  border: OutlineInputBorder(
                    borderSide: BorderSide.none,
                    borderRadius: BorderRadius.circular(10.0),
                  ),
                  contentPadding:
                      const EdgeInsets.symmetric(vertical: 10, horizontal: 15),
                  labelText: '현재 비밀번호',
                  fillColor: Colors.grey[200],
                  filled: true,
                ),
              ),
              const SizedBox(height: 10),
              TextFormField(
                autovalidateMode: AutovalidateMode.onUserInteraction,
                controller: newPasswordController,
                obscureText: true,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return '사용할 비밀번호를 입력하세요.';
                  } else if (value.length < 8 || value.length > 16) {
                    return '비밀번호는 8~16자리 사이여야 합니다.';
                  }
                  return null;
                },
                decoration: InputDecoration(
                  focusedBorder: OutlineInputBorder(
                    borderSide: const BorderSide(color: Colors.green),
                    borderRadius: BorderRadius.circular(10.0),
                  ),
                  border: OutlineInputBorder(
                    borderSide: BorderSide.none,
                    borderRadius: BorderRadius.circular(10.0),
                  ),
                  contentPadding:
                      const EdgeInsets.symmetric(vertical: 10, horizontal: 15),
                  labelText: '새 비밀번호',
                  fillColor: Colors.grey[200],
                  filled: true,
                ),
              ),
              const SizedBox(height: 10),
              TextFormField(
                autovalidateMode: AutovalidateMode.onUserInteraction,
                controller: checkPasswordController,
                obscureText: true,
                validator: (value) {
                  if (value != newPasswordController.text) {
                    return '비밀번호가 일치하지 않습니다.';
                  }
                  return null;
                },
                decoration: InputDecoration(
                  focusedBorder: OutlineInputBorder(
                    borderSide: const BorderSide(color: Colors.green),
                    borderRadius: BorderRadius.circular(10.0),
                  ),
                  border: OutlineInputBorder(
                    borderSide: BorderSide.none,
                    borderRadius: BorderRadius.circular(10.0),
                  ),
                  contentPadding:
                      const EdgeInsets.symmetric(vertical: 10, horizontal: 15),
                  labelText: '새 비밀번호 확인',
                  fillColor: Colors.grey[200],
                  filled: true,
                ),
              ),
              const SizedBox(
                height: 10,
              ),
              GestureDetector(
                onTap: onModifyPasswordTap,
                child: Container(
                  alignment: Alignment.center,
                  height: 50,
                  decoration: BoxDecoration(
                    color: Colors.green[900],
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: const Text(
                    '변경하기',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 20,
                    ),
                  ),
                ),
              )
            ],
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      resizeToAvoidBottomInset: true,
      appBar: const AppBarBack(title: "", page: HomeScreen()),
      body: Stack(
        children: [
          SingleChildScrollView(
            scrollDirection: Axis.vertical,
            child: Column(
              children: [
                Container(
                  height: MediaQuery.of(context).size.height * 0.15,
                  alignment: Alignment.center,
                  child: const Text(
                    '정보 수정',
                    style: TextStyle(
                      fontSize: 40,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
                Container(
                  height: MediaQuery.of(context).size.height * 0.75,
                  width: 300,
                  alignment: Alignment.topCenter,
                  child: Form(
                    key: _formKey,
                    child: Column(
                      children: [
                        TextFormField(
                          controller: emailController,
                          readOnly: true,
                          decoration: InputDecoration(
                            focusedBorder: OutlineInputBorder(
                              borderSide: const BorderSide(color: Colors.green),
                              borderRadius: BorderRadius.circular(15.0),
                            ),
                            border: OutlineInputBorder(
                              borderSide: BorderSide.none,
                              borderRadius: BorderRadius.circular(15.0),
                            ),
                            contentPadding: const EdgeInsets.symmetric(
                                vertical: 15, horizontal: 15),
                            labelText: '이메일',
                            fillColor: Colors.grey[200],
                            filled: true,
                          ),
                        ),
                        const SizedBox(height: 15),
                        Stack(
                          alignment: Alignment.center,
                          children: [
                            TextFormField(
                              controller: passwordController,
                              obscureText: true,
                              readOnly: true,
                              decoration: InputDecoration(
                                focusedBorder: OutlineInputBorder(
                                  borderSide:
                                      const BorderSide(color: Colors.green),
                                  borderRadius: BorderRadius.circular(15.0),
                                ),
                                border: OutlineInputBorder(
                                  borderSide: BorderSide.none,
                                  borderRadius: BorderRadius.circular(15.0),
                                ),
                                contentPadding: const EdgeInsets.symmetric(
                                    vertical: 15, horizontal: 15),
                                labelText: '비밀번호',
                                fillColor: Colors.grey[200],
                                filled: true,
                              ),
                            ),
                            Positioned(
                              top: 3,
                              right: 10,
                              child: TextButton(
                                onPressed: () {
                                  showModifyPassword();
                                },
                                child: const Text('변경하기'),
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 15),
                        TextFormField(
                          autovalidateMode: AutovalidateMode.onUserInteraction,
                          controller: nicknameController,
                          validator: (value) {
                            if (value == null || value.isEmpty) {
                              return '닉네임을 입력해주세요.';
                            } else if (value.length < 2 || value.length > 5) {
                              return '닉네임은 2~5자리 사이여야 합니다.';
                            }
                            return null;
                          },
                          decoration: InputDecoration(
                            focusedBorder: OutlineInputBorder(
                              borderSide: const BorderSide(color: Colors.green),
                              borderRadius: BorderRadius.circular(15.0),
                            ),
                            border: OutlineInputBorder(
                              borderSide: BorderSide.none,
                              borderRadius: BorderRadius.circular(15.0),
                            ),
                            contentPadding: const EdgeInsets.symmetric(
                                vertical: 15, horizontal: 15),
                            labelText: '닉네임',
                            fillColor: Colors.grey[200],
                            filled: true,
                          ),
                        ),
                        const SizedBox(height: 15),
                        Stack(
                          alignment: Alignment.centerRight,
                          children: [
                            TextFormField(
                              controller: addressController,
                              autovalidateMode:
                                  AutovalidateMode.onUserInteraction,
                              validator: (value) {
                                if (value == null || value.isEmpty) {
                                  return '주소를 입력하세요';
                                }
                                return null;
                              },
                              decoration: InputDecoration(
                                focusedBorder: OutlineInputBorder(
                                  borderSide:
                                      const BorderSide(color: Colors.green),
                                  borderRadius: BorderRadius.circular(15.0),
                                ),
                                border: OutlineInputBorder(
                                  borderSide: BorderSide.none,
                                  borderRadius: BorderRadius.circular(15.0),
                                ),
                                contentPadding: const EdgeInsets.symmetric(
                                    vertical: 15, horizontal: 15),
                                labelText: '주소',
                                fillColor: Colors.grey[200],
                                filled: true,
                              ),
                            ),
                            Positioned(
                              top: 3,
                              right: 0,
                              child: TextButton(
                                onPressed: () {
                                  searchAddress(context);
                                },
                                child: const Text('검색'),
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 20),
                        GestureDetector(
                          onTap: onModifyButtonTap,
                          child: Container(
                            alignment: Alignment.center,
                            width: 300,
                            height: 60,
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(15),
                              color: Colors.green[900],
                              boxShadow: [
                                BoxShadow(
                                  color: Colors.grey.withOpacity(0.6),
                                  spreadRadius: 1,
                                  blurRadius: 5,
                                  offset: const Offset(0, 5),
                                ),
                              ],
                            ),
                            child: const Text(
                              '수정하기',
                              style: TextStyle(
                                color: Colors.white,
                                fontSize: 20,
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
