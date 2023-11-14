import 'package:animated_text_kit/animated_text_kit.dart';
import 'package:flutter/material.dart';
import 'package:knocknock/components/buttons.dart';
import 'package:knocknock/models/my_appliance_model.dart';
import 'package:knocknock/providers/my_appliance.dart';
import 'package:knocknock/providers/page_index.dart';
import 'package:knocknock/screens/home_screen.dart';
import 'package:knocknock/screens/my_appliance_list.dart';
import 'package:knocknock/services/model_service.dart';
import 'package:provider/provider.dart';

class NicknameAssign extends StatefulWidget {
  const NicknameAssign({super.key});

  @override
  State<NicknameAssign> createState() => _NicknameAssignState();
}

class _NicknameAssignState extends State<NicknameAssign> {
  ModelService modelService = ModelService();
  late MyModelRegistering model;
  final myController = TextEditingController();

  @override
  void didChangeDependencies() {
    // TODO: implement didChangeDependencies
    super.didChangeDependencies();
    model = context.watch<RegisterAppliance>().myModel!;
    myController.text = '나의 ${model.category}'; // 초기값 설정
  }

  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();

  finalRegister(String nickname) async {
    String modelName = model.modelName!;

    final response =
        await modelService.registerMyAppliance(modelName, nickname);
    showPositive(response);
  }

  showPositive(String msg) {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          // Retrieve the text the that user has entered by using the
          // TextEditingController.
          content: Text(msg),
          actions: <Widget>[
            TextButton(
              onPressed: () {
                context.read<CurrentPageIndex>().move(3);
                Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (context) =>
                          const HomeScreen()), // SignUpPage는 회원가입 페이지 위젯입니다.
                );
              },
              child: const Text('확인'),
            ),
          ],
        );
      },
    );
  }

  showNegative() {
    showDialog(
      context: context,
      builder: (context) {
        return const AlertDialog(
          // Retrieve the text the that user has entered by using the
          // TextEditingController.
          content: Text('등록 실패'),
        );
      },
    );
  }

  String textContent = "";
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.all(
          30,
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                AnimatedTextKit(
                  animatedTexts: [
                    WavyAnimatedText(
                      '별명',
                      textStyle: const TextStyle(
                        fontSize: 24.0,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ],
                  isRepeatingAnimation: true,
                  // onTap: () {
                  //   print("Tap Event");
                  // },
                ),
                const Text(
                  '을 지정할 수 있어요!',
                  style: TextStyle(
                    fontSize: 24,
                  ),
                ),
              ],
            ),
            const SizedBox(
              height: 10,
            ),
            Image.network(model.modelImg!),
            const SizedBox(
              height: 10,
            ),
            Form(
              key: _formKey,
              child: Column(
                children: [
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 20.0),
                    child: TextFormField(
                      // controller: myController,
                      // initialValue: '나의 ${model.category}',
                      initialValue: '나의 ${model.category}',
                      onChanged: (value) {
                        setState(() => textContent = value);
                      },
                      maxLength: 8,
                      decoration: InputDecoration(
                        contentPadding: const EdgeInsets.symmetric(
                            vertical: 10, horizontal: 20),
                        prefixIcon: const Icon(Icons.edit_outlined),
                        // hintText: textContent,
                        filled: true,
                        fillColor:
                            Theme.of(context).colorScheme.secondaryContainer,

                        focusedBorder: OutlineInputBorder(
                          borderSide: BorderSide(
                              color: Theme.of(context).colorScheme.primary),
                          borderRadius: BorderRadius.circular(30),
                        ),
                        enabledBorder: OutlineInputBorder(
                          borderSide:
                              const BorderSide(color: Colors.transparent),
                          borderRadius: BorderRadius.circular(30),
                        ),
                        errorBorder: OutlineInputBorder(
                          borderSide: BorderSide(
                              color: Theme.of(context).colorScheme.error),
                          borderRadius: BorderRadius.circular(30),
                        ),
                        focusedErrorBorder: OutlineInputBorder(
                          borderSide: BorderSide(
                              color: Theme.of(context).colorScheme.onError),
                          borderRadius: BorderRadius.circular(30),
                        ),
                      ),

                      validator: (String? value) {
                        if (value == null || value.isEmpty) {
                          return '값을 입력해주세요.';
                        }
                        return null;
                      },
                    ),
                  ),
                  const SizedBox(
                    height: 30,
                  ),
                  KnockButton(
                    onPressed: () {
                      // 버튼 클릭 시 실행할 동작
                      if (_formKey.currentState!.validate()) {
                        // Process data.

                        // Process data.
                        finalRegister(textContent); // 컨트롤러의 값 출력
                      }
                    },
                    bColor: Theme.of(context).colorScheme.primary,
                    fColor: Theme.of(context).colorScheme.onPrimary,
                    width: MediaQuery.of(context).size.width * 0.8, // 버튼의 너비
                    height: MediaQuery.of(context).size.width * 0.16, // 버튼의 높이
                    label: "완료", // 버튼에 표시할 텍스트
                  ),
                ],
              ),
            )
          ],
        ),
      ),
    );
  }
}
