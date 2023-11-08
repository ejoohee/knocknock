import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:knocknock/screens/home_screen.dart';
import 'package:knocknock/screens/sign_up.dart';
import 'package:knocknock/screens/sign_up_google.dart';
import 'package:knocknock/services/user_service.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:uni_links/uni_links.dart';
import 'package:flutter_web_auth/flutter_web_auth.dart';

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

  // 구글 로그인 누르면 실행되는 함수
  Future<UserCredential> signInWithGoogle() async {
    FirebaseAuth.instance.authStateChanges().listen((user) {
      if (user == null) {
      } else {}
    });
    // Trigger the authentication flow
    final GoogleSignInAccount? googleUser = await GoogleSignIn().signIn();

    // Obtain the auth details from the request
    final GoogleSignInAuthentication? googleAuth =
        await googleUser?.authentication;

    // Create a new credential
    final credential = GoogleAuthProvider.credential(
      accessToken: googleAuth?.accessToken,
      idToken: googleAuth?.idToken,
    );
    final result = await FirebaseAuth.instance.signInWithCredential(credential);
    print(result.user!.email);
    if (result.user != null) {
      final email = result.user!.email;
      final displayName = result.user!.displayName;
      // 여기선 checkgoogle을 해서 회원가입이 된 이메일인지 판단
      googleLogin(email!, displayName!);
    }
    return await FirebaseAuth.instance.signInWithCredential(credential);
  }

  googleLogin(String email, String nickname) async {
    final response = await userService.googleCheckEmail(email);
    if (response == 200) {
      final response2 =
          await userService.googleLogin(email, email, email, email);
      if (response == 200) {
        if (!mounted) return;
        Navigator.pushReplacement(context,
            MaterialPageRoute(builder: (context) => const HomeScreen()));
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
    } else if (response == 404) {
      if (!mounted) return;
      Navigator.pushReplacement(
          context,
          MaterialPageRoute(
              builder: (context) =>
                  SingUpGoogle(email: email, nickname: nickname)));
    }
  }
  // // 구글 로그인 누르면 실행되는 함수
  // onGoogleLoginTap() async {
  //   final url = Uri.parse(
  //       "https://accounts.google.com/o/oauth2/auth/oauthchooseaccount?client_id=36630242297-dlq71h1k3vjocp2ai6i902uuu0lg4273.apps.googleusercontent.com&redirect_uri=https%3A%2F%2Fa508.co.kr%2Fapi%2Fuser%2Flogin%2Fgoogle&response_type=code&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile&service=lso&o2v=1&theme=glif&flowName=GeneralOAuthFlow");
  //   final response = await launchUrl(url);
  // }

  // 비밀번호 찾기 누르면 나오는 modal
  void showFindPasswordDialog(BuildContext context) async {
    bool localIsLoading = false;
    setState(() {
      emailController2 = emailController;
    });
    showDialog(
      context: context,
      builder: (BuildContext dialogContext) {
        return StatefulBuilder(
            builder: (BuildContext context, StateSetter setState) {
          return Stack(
            children: [
              AlertDialog(
                title: const Text('비밀번호 재발급'),
                content: Column(
                  mainAxisSize: MainAxisSize.min,
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    TextFormField(
                      controller: nicknameController,
                      decoration: InputDecoration(
                        focusedBorder: OutlineInputBorder(
                          borderSide: const BorderSide(color: Colors.green),
                          borderRadius: BorderRadius.circular(10.0),
                        ),
                        border: OutlineInputBorder(
                          borderSide: BorderSide.none,
                          borderRadius: BorderRadius.circular(10.0),
                        ),
                        contentPadding: const EdgeInsets.symmetric(
                            vertical: 10, horizontal: 15),
                        labelText: '닉네임',
                        fillColor: Colors.grey[200],
                        filled: true,
                      ),
                    ),
                    const SizedBox(height: 10),
                    TextFormField(
                      controller: emailController2,
                      decoration: InputDecoration(
                        focusedBorder: OutlineInputBorder(
                          borderSide: const BorderSide(color: Colors.green),
                          borderRadius: BorderRadius.circular(10.0),
                        ),
                        border: OutlineInputBorder(
                          borderSide: BorderSide.none,
                          borderRadius: BorderRadius.circular(10.0),
                        ),
                        contentPadding: const EdgeInsets.symmetric(
                            vertical: 10, horizontal: 15),
                        labelText: '이메일',
                        fillColor: Colors.grey[200],
                        filled: true,
                      ),
                    ),
                    const SizedBox(height: 10),
                    GestureDetector(
                      onTap: () async {
                        setState(() {
                          localIsLoading = true;
                        });
                        final response = await userService.findPassword(
                            nicknameController.text, emailController2.text);
                        setState(() {
                          localIsLoading = false;
                        });

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
                                      Navigator.of(context).pop();
                                    },
                                    child: const Text('확인'),
                                  ),
                                ],
                              );
                            },
                          );
                        } else if (response == 404) {
                          if (!mounted) return;
                          showDialog(
                            context: context,
                            builder: (BuildContext context) {
                              return AlertDialog(
                                title: const Text('알림'),
                                content: const Text('닉네임 및 이메일을 다시 확인해주세요'),
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
                        alignment: Alignment.center,
                        height: 50,
                        decoration: BoxDecoration(
                          color: Colors.green[900],
                          borderRadius: BorderRadius.circular(10),
                        ),
                        child: const Text(
                          '재발급',
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
              if (localIsLoading)
                Positioned.fill(
                  child: Container(
                    color: Colors.black.withOpacity(0.5),
                    child: Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          CircularProgressIndicator(
                            color: Colors.green[900],
                            backgroundColor: Colors.black.withOpacity(0.5),
                            strokeWidth: 5.0,
                          ),
                          const SizedBox(height: 20),
                          const Text(
                            '재발급 중입니다...',
                            style: TextStyle(
                              color: Colors.white,
                              fontSize: 16.0,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
            ],
          );
        });
      },
    );
  }

  // 로그인 버튼 누르면 실행되는 함수
  onLoginTap() async {
    String email = emailController.text;
    String password = passwordController.text;

    if (_formKey.currentState!.validate()) {
      setState(() {
        isLoading = true;
      });
      final loginSuccess = await userService.login(email, password);
      setState(() {
        isLoading = false;
      });
      if (loginSuccess == 200) {
        final token = await storage.read(key: "accessToken");
        if (!mounted) return;
        Navigator.pushReplacement(context,
            MaterialPageRoute(builder: (context) => const HomeScreen()));
      } else if (loginSuccess == 400 || loginSuccess == 404) {
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
      body: Stack(
        children: [
          SingleChildScrollView(
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
                  height: MediaQuery.of(context).size.height *
                      0.7, // 대략적인 비율로 크기 조절
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
                            // fillColor: Colors.grey[200],
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
                            // fillColor: Colors.grey[200],
                            filled: true,
                          ),
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.end,
                          children: [
                            TextButton(
                                onPressed: () =>
                                    showFindPasswordDialog(context),
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
                          // onTap: onGoogleLoginTap,
                          onTap: signInWithGoogle,
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
                                      image: AssetImage(
                                          'assets/images/google.png'),
                                    ),
                                  ),
                                ),
                                const Text(
                                  'Google 이메일로 로그인하기',
                                  style: TextStyle(
                                      fontSize: 14,
                                      fontWeight: FontWeight.w400),
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
                                  color: Colors.grey,
                                  fontWeight: FontWeight.w600),
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
          if (isLoading)
            Positioned.fill(
              child: Container(
                color: Colors.black.withOpacity(0.5),
                child: Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      CircularProgressIndicator(
                        color: Colors.green[900],
                        backgroundColor: Colors.black.withOpacity(0.5),
                        strokeWidth: 5.0,
                      ),
                      const SizedBox(height: 20),
                      const Text(
                        '1분 정도 소요될 수 있습니다.',
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 16.0,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
        ],
      ),
    );
  }
}
