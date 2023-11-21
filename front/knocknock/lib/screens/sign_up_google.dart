import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:knocknock/screens/home_screen.dart';
import 'package:knocknock/screens/log_in.dart';
import 'package:remedi_kopo/remedi_kopo.dart';

class SingUpGoogle extends StatefulWidget {
  final String? email;
  final String? nickname;

  const SingUpGoogle({super.key, this.email, this.nickname});

  @override
  State<SingUpGoogle> createState() => _SingUpGoogleState();
}

TextEditingController emailController = TextEditingController();
TextEditingController passwordController = TextEditingController();
TextEditingController passwordConfirmController = TextEditingController();
TextEditingController nicknameController = TextEditingController();
TextEditingController addressController = TextEditingController();

final _formKey = GlobalKey<FormState>();
Map<String, String> formData = {};

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

class _SingUpGoogleState extends State<SingUpGoogle> {
  @override
  void initState() {
    super.initState();
    if (widget.email != null) {
      emailController.text = widget.email!;
    }
    if (widget.nickname != null) {
      nicknameController.text = widget.nickname!;
    }
  }

  bool isLoading = false;
  bool isEmailVerified = true;

  // 회원가입 버튼 누르면 실행되는 함수
  logInWithGoogle(
      String email, String password, String nickname, String address) async {
    final response =
        await userService.googleLogin(email, password, nickname, address);
    if (response == 200) {
      if (!mounted) return;
      Navigator.pushReplacement(
          context, MaterialPageRoute(builder: (context) => const HomeScreen()));
    } else {
      if (!mounted) return;
      showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
            title: const Text('알림'),
            content: const Text('서버 연결 오류'),
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
                  height: MediaQuery.of(context).size.height * 0.15,
                  alignment: Alignment.center,
                  child: const Text(
                    '간편 회원가입',
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
                          autovalidateMode: AutovalidateMode.onUserInteraction,
                          controller: emailController,
                          readOnly: isEmailVerified,
                          validator: (value) {
                            if (value == null || value.isEmpty) {
                              return '이메일을 입력하세요.';
                            } else if (!emailRegex.hasMatch(value)) {
                              return '유효한 이메일을 입력하세요.';
                            }
                            return null;
                          },
                          decoration: InputDecoration(
                            hintText: "유효한 이메일을 입력해주세요",
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

                        TextFormField(
                          autovalidateMode: AutovalidateMode.onUserInteraction,
                          controller: passwordController,
                          obscureText: true, // 비밀번호 숨기기 옵션
                          validator: (value) {
                            if (value == null || value.isEmpty) {
                              return '사용할 비밀번호를 입력하세요.';
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
                                vertical: 15, horizontal: 15),
                            labelText: '비밀번호',
                            fillColor: Colors.grey[200],
                            filled: true,
                          ),
                        ),

                        const SizedBox(height: 15),
                        TextFormField(
                          autovalidateMode: AutovalidateMode.onUserInteraction,
                          controller: passwordConfirmController,
                          obscureText: true,
                          validator: (value) {
                            if (value != passwordController.text) {
                              return '비밀번호가 일치하지 않습니다.';
                            }
                            return null;
                          },
                          decoration: InputDecoration(
                            hintText: "비밀번호를 확인해주세요",
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
                            labelText: '비밀번호 확인',
                            fillColor: Colors.grey[200],
                            filled: true,
                          ),
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
                            hintText: "2~5자리 사이로 입력해주세요",
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
                        const SizedBox(
                          height: 15,
                        ),
                        Stack(
                          alignment: Alignment.centerRight,
                          children: [
                            TextFormField(
                              autovalidateMode:
                                  AutovalidateMode.onUserInteraction,
                              controller: addressController,
                              validator: (value) {
                                if (value == null || value.isEmpty) {
                                  return '주소를 입력하세요';
                                }
                                return null;
                              },
                              decoration: InputDecoration(
                                hintText: "도로명 주소를 입력해주세요",
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
                          onTap: () {
                            logInWithGoogle(
                                emailController.text,
                                passwordController.text,
                                nicknameController.text,
                                addressController.text);
                          },
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
                              '회원가입',
                              style: TextStyle(
                                color: Colors.white,
                                fontSize: 20,
                              ),
                            ),
                          ),
                        ),
                        // const SizedBox(height: 20),
                        // const Row(
                        //   children: [
                        //     Expanded(
                        //       child: Divider(
                        //         thickness: 1,
                        //       ),
                        //     ),
                        //     Padding(
                        //       padding: EdgeInsets.symmetric(horizontal: 10),
                        //       child: Text('간편 회원가입'),
                        //     ),
                        //     Expanded(
                        //       child: Divider(
                        //         thickness: 1,
                        //       ),
                        //     ),
                        //   ],
                        // ),
                        // const SizedBox(height: 20),
                        // GestureDetector(
                        //   child: Container(
                        //     padding: const EdgeInsets.symmetric(
                        //         horizontal: 7, vertical: 12),
                        //     decoration: BoxDecoration(
                        //       color: Colors.grey[200],
                        //       borderRadius: BorderRadius.circular(15),
                        //       boxShadow: [
                        //         BoxShadow(
                        //           color: Colors.grey.withOpacity(0.6),
                        //           spreadRadius: 1,
                        //           blurRadius: 5,
                        //           offset: const Offset(0, 5),
                        //         ),
                        //       ],
                        //     ),
                        //     child: Row(
                        //       mainAxisAlignment: MainAxisAlignment.spaceAround,
                        //       children: [
                        //         Container(
                        //           width: 30,
                        //           height: 30,
                        //           decoration: const BoxDecoration(
                        //             image: DecorationImage(
                        //               image: AssetImage(
                        //                   'assets/images/google.png'),
                        //             ),
                        //           ),
                        //         ),
                        //         const Text(
                        //           'Google 이메일로 회원가입',
                        //           style: TextStyle(
                        //               fontSize: 14,
                        //               fontWeight: FontWeight.w400),
                        //         ),
                        //         const SizedBox(
                        //           width: 15,
                        //         ),
                        //       ],
                        //     ),
                        //   ),
                        // ),
                        // const SizedBox(
                        //   height: 10,
                        // ),
                        // Row(
                        //   mainAxisAlignment: MainAxisAlignment.center,
                        //   children: [
                        //     const Text(
                        //       '이미 계정이 있으신가요?',
                        //       style: TextStyle(
                        //           color: Colors.grey, fontWeight: FontWeight.w600),
                        //     ),
                        //     TextButton(
                        //       onPressed: () {
                        //         Navigator.push(
                        //           context,
                        //           MaterialPageRoute(
                        //               builder: (context) => const Login()),
                        //         );
                        //       },
                        //       child: Text(
                        //         '로그인',
                        //         style: TextStyle(
                        //           color: Colors.green[900],
                        //           fontWeight: FontWeight.w600,
                        //         ),
                        //       ),
                        //     ),
                        //   ],
                        // ),
                      ],
                    ),
                  ),
                )
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
