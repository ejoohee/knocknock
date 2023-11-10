import 'dart:io';

import 'package:animated_text_kit/animated_text_kit.dart';
import 'package:flutter/material.dart';
import 'package:knocknock/components/buttons.dart';
import 'package:knocknock/models/my_appliance_model.dart';
import 'package:knocknock/providers/my_appliance.dart';
import 'package:knocknock/screens/nickname_assign.dart';
import 'package:knocknock/services/model_service.dart';
import 'package:provider/provider.dart';

class DisplayInfoScreen extends StatefulWidget {
  const DisplayInfoScreen({
    super.key,
  });

  @override
  State<DisplayInfoScreen> createState() => _DisplayInfoScreenState();
}

class _DisplayInfoScreenState extends State<DisplayInfoScreen> {
  late MyModelRegistering modelInfo;

  @override
  void didChangeDependencies() {
    // TODO: implement didChangeDependencies
    super.didChangeDependencies();
    modelInfo = context.watch<RegisterAppliance>().myModel!;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Display the Info')),
      // The image is stored as a file on the device. Use the `Image.file`
      // constructor with the given path to display the image.
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(
            vertical: 10.0,
            horizontal: 30.0,
          ),
          child: Column(
            children: [
              SizedBox(
                height: 35,
                child: AnimatedTextKit(
                  animatedTexts: [
                    FadeAnimatedText(
                      '이 제품이 맞나요 ?',
                      textStyle: const TextStyle(
                        fontSize: 24.0,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    FadeAnimatedText(
                      '모델명을 정확히 확인해주세요.',
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
              ),
              const SizedBox(
                height: 30,
              ),
              // FutureBuilder(
              //   future: info,
              //   builder: (context, snapshot) {
              //     if (snapshot.connectionState == ConnectionState.waiting) {
              //       // 데이터가 아직 준비되지 않은 경우에 대한 UI
              //       return const Expanded(
              //         child: Column(
              //           mainAxisAlignment: MainAxisAlignment.center,
              //           children: [
              //             CircularProgressIndicator(),
              //           ],
              //         ),
              //       );
              //     } else if (snapshot.hasError) {
              //       // 에러 발생 시에 대한 UI
              //       return Text('Error: ${snapshot.error}');
              //     } else if (!snapshot.hasData) {
              //       // 데이터가 비어 있는 경우에 대한 UI
              //       return const Expanded(
              //         child: Column(
              //           mainAxisAlignment: MainAxisAlignment.center,
              //           children: [
              //             Center(
              //               child: Text('원하는 조건의 제품을 찾을 수 없어요:('),
              //             ),
              //           ],
              //         ),
              //       );
              //     }
              //     MyModelRegistering model = snapshot.data!;
              //     return Expanded(
              //       child: Column(
              //         children: [
              //           Image.network(model.modelImg!),
              //           Expanded(
              //             child: ListView(
              //               children: [
              //                 ListTile(
              //                   leading: const Text(
              //                     "제품군",
              //                   ),
              //                   title: Text(
              //                     "${model.category}",
              //                     style: const TextStyle(
              //                       fontWeight: FontWeight.w700,
              //                     ),
              //                     textAlign: TextAlign.end,
              //                   ),
              //                   // trailing: Text(
              //                   //   "unit",
              //                   // ),
              //                 ),
              //                 const Divider(),
              //                 ListTile(
              //                   leading: const Text(
              //                     "모델명",
              //                   ),
              //                   title: Text(
              //                     "${model.modelName}",
              //                     style: const TextStyle(
              //                       fontWeight: FontWeight.w700,
              //                     ),
              //                     textAlign: TextAlign.end,
              //                   ),
              //                   // trailing: Text(
              //                   //   "unit",
              //                   // ),
              //                 ),
              //                 const Divider(),
              //                 ListTile(
              //                   leading: const Text(
              //                     "업체명",
              //                   ),
              //                   title: Text(
              //                     "${model.modelBrand}",
              //                     style: const TextStyle(
              //                       fontWeight: FontWeight.w700,
              //                     ),
              //                     textAlign: TextAlign.end,
              //                   ),
              //                   // trailing: const Text(
              //                   //   "unit",
              //                   // ),
              //                 ),
              //                 const Divider(),
              //               ],
              //             ),
              //           ),
              //         ],
              //       ),
              //     );
              //   },
              // )
              Expanded(
                child: Column(
                  children: [
                    Image.network(modelInfo.modelImg!),
                    const SizedBox(
                      height: 10,
                    ),
                    Expanded(
                      child: Padding(
                        padding: const EdgeInsets.symmetric(
                          vertical: 20,
                          horizontal: 30.0,
                        ),
                        child: ListView(
                          children: [
                            const Divider(),
                            ListTile(
                              visualDensity: VisualDensity.compact,
                              leading: const Text(
                                "제품군",
                              ),
                              title: Text(
                                "${modelInfo.category}",
                                style: const TextStyle(
                                  fontWeight: FontWeight.w700,
                                ),
                                textAlign: TextAlign.end,
                              ),
                              // trailing: Text(
                              //   "unit",
                              // ),
                            ),
                            const Divider(),
                            ListTile(
                              visualDensity: VisualDensity.compact,

                              leading: const Text(
                                "모델명",
                              ),
                              title: Text(
                                "${modelInfo.modelName}",
                                style: const TextStyle(
                                  fontWeight: FontWeight.w700,
                                ),
                                textAlign: TextAlign.end,
                              ),
                              // trailing: Text(
                              //   "unit",
                              // ),
                            ),
                            const Divider(),
                            ListTile(
                              visualDensity: VisualDensity.compact,

                              leading: const Text(
                                "업체명",
                              ),
                              title: Text(
                                "${modelInfo.modelBrand}",
                                style: const TextStyle(
                                  fontWeight: FontWeight.w700,
                                ),
                                textAlign: TextAlign.end,
                              ),
                              // trailing: const Text(
                              //   "unit",
                              // ),
                            ),
                            const Divider(),
                          ],
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              KnockButton(
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) =>
                            const NicknameAssign()), // SignUpPage는 회원가입 페이지 위젯입니다.
                  );
                },
                bColor: Theme.of(context).colorScheme.primary,
                fColor: Theme.of(context).colorScheme.onPrimary,
                width: MediaQuery.of(context).size.width * 0.8, // 버튼의 너비
                height: MediaQuery.of(context).size.width * 0.16, // 버튼의 높이
                label: "내 가전으로 등록하기", // 버튼에 표시할 텍스트
              ),
              const SizedBox(
                height: 10,
              ),
              KnockButton(
                onPressed: () {
                  // 버튼 클릭 시 실행할 동작
                },
                bColor: Theme.of(context).colorScheme.secondaryContainer,
                fColor: Theme.of(context).colorScheme.onSecondaryContainer,
                width: MediaQuery.of(context).size.width * 0.8, // 버튼의 너비
                height: MediaQuery.of(context).size.width * 0.15, // 버튼의 높이
                label: "다시 찍기", // 버튼에 표시할 텍스트
              ),
              const SizedBox(
                height: 10,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
