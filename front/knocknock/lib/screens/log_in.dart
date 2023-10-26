import 'package:flutter/material.dart';
import 'package:knocknock/screens/main_page.dart';
import 'package:knocknock/screens/sign_up.dart';
import 'package:knocknock/services/user_service.dart';

class Login extends StatefulWidget {
  const Login({super.key});

  @override
  State<Login> createState() => _LoginState();
}

UserService userService = UserService();
TextEditingController emailController = TextEditingController();
TextEditingController passwordController = TextEditingController();
TextEditingController emailController2 =
    TextEditingController(text: emailController.text);

TextEditingController nicknameController = TextEditingController();
final _formKey = GlobalKey<FormState>();

final emailRegex = RegExp(
    r"^[a-zA-Z0-9.a-zA-Z0-9.!#$%&'*+-/=?^_`{|}~]+@[a-zA-Z0-9]+\.[a-zA-Z]+");

class _LoginState extends State<Login> {
  bool isLoading = false;

  // 비밀번호 찾기 누르면 나오는 modal
  showFindPasswordDialog() async {
    Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        const Text(
          '비밀번호 재발급',
        ),
        TextFormField(
          controller: nicknameController,
          decoration: const InputDecoration(
            labelText: '닉네임',
          ),
        ),
        TextFormField(
          controller: emailController2,
          decoration: const InputDecoration(
            labelText: '이메일',
          ),
        ),
        GestureDetector(
          onTap: () async {
            final response = await userService.findPassword(
                nicknameController.text, emailController2.text);
            if (response == 200) {
              if (!mounted) return;
              showDialog(
                context: context,
                builder: (BuildContext context) {
                  return AlertDialog(
                    title: const Text('알림'),
                    content: const Text('임시 비밀번호 발급 완료'),
                    actions: <Widget>[
                      TextButton(
                        onPressed: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (context) => const Login()),
                          );
                        },
                        child: const Text('확인'),
                      ),
                    ],
                  );
                },
              );
            } else if (response == 400) {
              if (!mounted) return;
              showDialog(
                context: context,
                builder: (BuildContext context) {
                  return AlertDialog(
                    title: const Text('알림'),
                    content: const Text('임시 비밀번호 발급 실패'),
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
          },
          child: Container(
            color: Colors.green[900],
            width: 100,
            child: const Text('재발급'),
          ),
        ),
      ],
    );

    String email = emailController.text;
    setState(() {
      isLoading = true; // 로딩 시작
    });
    final response = await userService.sendCheckEmailCode(email);
    setState(() {
      isLoading = false; // 로딩 완료
    });
    if (response == 200) {
      if (!mounted) return;
      await showDialog(
        context: context,
        builder: (BuildContext dialogContext) {
          return AlertDialog(
            title: const Text('이메일 인증'),
            content: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const Text('이메일로 발송된 인증번호를 입력하세요.'),
                TextFormField(
                  controller: verificationController,
                  decoration: const InputDecoration(
                    labelText: '인증번호',
                  ),
                ),
              ],
            ),
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
            content: const Text('다시 시도해주세요.'),
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

  // 로그인 버튼 누르면 실행되는 함수
  onLoginTap() async {
    String email = emailController.text;
    String password = passwordController.text;

    if (_formKey.currentState!.validate()) {
      setState(() {
        isLoading = true;
      });
      print('로그인 시도');
      final loginSuccess = await userService.login(email, password);
      print('로그인 리턴');
      setState(() {
        isLoading = false;
      });
      if (loginSuccess == 200) {
        if (!mounted) return;
        Navigator.pushReplacement(
            context, MaterialPageRoute(builder: (context) => const MainPage()));
      } else if (loginSuccess == 400) {
        if (!mounted) return;
        showDialog(
          context: context,
          builder: (BuildContext context) {
            return AlertDialog(
              title: const Text('알림'),
              content: const Text('이메일 혹은 비밀번호를 확인해주세요'),
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
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      resizeToAvoidBottomInset: true,
      body: SingleChildScrollView(
        scrollDirection: Axis.vertical,
        child: Column(
          children: [
            Container(
              height: MediaQuery.of(context).size.height * 0.1,
            ),
            Container(
              height: MediaQuery.of(context).size.height * 0.2,
              alignment: Alignment.center,
              child: const Text(
                '로그인',
                style: TextStyle(
                  fontSize: 40,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
            Container(
              height:
                  MediaQuery.of(context).size.height * 0.7, // 대략적인 비율로 크기 조절
              width: 300,
              alignment: Alignment.topCenter,
              child: Form(
                key: _formKey,
                child: Column(
                  children: [
                    TextFormField(
                      controller: emailController,
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return '이메일을 입력하세요.';
                        } else if (!emailRegex.hasMatch(value)) {
                          return '유효한 이메일을 입력하세요.';
                        }
                        return null;
                      },
                      decoration: InputDecoration(
                        hintText: "회원가입된 이메일을 입력해주세요",
                        focusedBorder: OutlineInputBorder(
                          borderSide: const BorderSide(color: Colors.green),
                          borderRadius: BorderRadius.circular(15.0),
                        ),
                        border: OutlineInputBorder(
                          borderSide: BorderSide.none,
                          borderRadius: BorderRadius.circular(15.0),
                        ),
                        contentPadding: const EdgeInsets.symmetric(
                            vertical: 20, horizontal: 15),
                        labelText: '이메일',
                        fillColor: Colors.grey[200],
                        filled: true,
                      ),
                    ),
                    const SizedBox(height: 20),
                    TextFormField(
                      controller: passwordController,
                      obscureText: true, // 비밀번호 숨기기 옵션
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return '비밀번호를 입력하세요.';
                        } else if (value.length < 8 || value.length > 16) {
                          return '비밀번호는 8~16자리 사이여야 합니다.';
                        }
                        return null;
                      },
                      decoration: InputDecoration(
                        hintText: "8~16자리 사이로 입력해주세요",
                        focusedBorder: OutlineInputBorder(
                          borderSide: const BorderSide(color: Colors.green),
                          borderRadius: BorderRadius.circular(15.0),
                        ),
                        border: OutlineInputBorder(
                          borderSide: BorderSide.none,
                          borderRadius: BorderRadius.circular(15.0),
                        ),
                        contentPadding: const EdgeInsets.symmetric(
                            vertical: 20, horizontal: 15),
                        labelText: '비밀번호',
                        fillColor: Colors.grey[200],
                        filled: true,
                      ),
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: [
                        TextButton(
                            onPressed: showFindPasswordDialog,
                            child: const Text('비밀번호를 잊으셨나요?')),
                      ],
                    ),
                    // const SizedBox(height: 30),
                    GestureDetector(
                      onTap: onLoginTap,
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
                          '로그인',
                          style: TextStyle(
                            color: Colors.white,
                            fontSize: 20,
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(height: 30),
                    const Row(
                      children: [
                        Expanded(
                          child: Divider(
                            thickness: 1,
                          ),
                        ),
                        Padding(
                          padding: EdgeInsets.symmetric(horizontal: 10),
                          child: Text('간편 로그인'),
                        ),
                        Expanded(
                          child: Divider(
                            thickness: 1,
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 30),
                    GestureDetector(
                      child: Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 7, vertical: 12),
                        decoration: BoxDecoration(
                          color: Colors.grey[200],
                          borderRadius: BorderRadius.circular(15),
                          boxShadow: [
                            BoxShadow(
                              color: Colors.grey.withOpacity(0.6),
                              spreadRadius: 1,
                              blurRadius: 5,
                              offset: const Offset(0, 5),
                            ),
                          ],
                        ),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceAround,
                          children: [
                            Container(
                              width: 30,
                              height: 30,
                              decoration: const BoxDecoration(
                                image: DecorationImage(
                                  image: AssetImage('assets/images/google.png'),
                                ),
                              ),
                            ),
                            const Text(
                              'Google 이메일로 로그인하기',
                              style: TextStyle(
                                  fontSize: 14, fontWeight: FontWeight.w400),
                            ),
                            const SizedBox(
                              width: 15,
                            ),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(
                      height: 10,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        const Text(
                          '계정이 없으신가요?',
                          style: TextStyle(
                              color: Colors.grey, fontWeight: FontWeight.w600),
                        ),
                        TextButton(
                          onPressed: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (context) =>
                                      const Signup()), // SignUpPage는 회원가입 페이지 위젯입니다.
                            );
                          },
                          child: Text(
                            '회원가입',
                            style: TextStyle(
                              color: Colors.green[900],
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
