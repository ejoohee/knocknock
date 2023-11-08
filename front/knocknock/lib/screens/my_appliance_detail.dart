import 'package:flutter/material.dart';
import 'package:flutter_inner_shadow/flutter_inner_shadow.dart';
import 'package:knocknock/common/custom_icon_icons.dart';
import 'package:knocknock/components/buttons.dart';
import 'package:knocknock/constants/color_chart.dart';
import 'package:knocknock/models/my_appliance_model.dart';
import 'package:knocknock/services/model_service.dart';

class MyApplianceDetail extends StatefulWidget {
  final int myModelId;
  const MyApplianceDetail({required this.myModelId, Key? key})
      : super(key: key);

  @override
  State<MyApplianceDetail> createState() => _MyApplianceDetailState();
}

class _MyApplianceDetailState extends State<MyApplianceDetail> {
  final ModelService modelService = ModelService();
  List<Color> colors = [
    ColorChart.first,
    ColorChart.second,
    ColorChart.third,
    ColorChart.forth,
    ColorChart.fifth,
  ];
  late Future<MyModelDetail>? myModelDetail;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    myModelDetail = loadMyModelDetail();
  }

  Future<MyModelDetail> loadMyModelDetail() async {
    print('내가전 상세 들어는 왔냐');
    final detail = await modelService.findMyApplianceDetail(widget.myModelId);
    return detail;
  }

  pin(int myModelId) async {
    int response = await modelService.pinMyAppliance(widget.myModelId);
    if (response == 200) {
      myModelDetail = loadMyModelDetail();
    }
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(
            vertical: 30,
            horizontal: 30,
          ),
          child: FutureBuilder<MyModelDetail>(
              future: myModelDetail,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  // 데이터가 아직 준비되지 않은 경우에 대한 UI
                  return const Expanded(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        CircularProgressIndicator(),
                      ],
                    ),
                  );
                } else if (snapshot.hasError) {
                  // 에러 발생 시에 대한 UI
                  return Text('Error: ${snapshot.error}');
                } else if (!snapshot.hasData) {
                  // 데이터가 비어 있는 경우에 대한 UI
                  return const Expanded(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Center(
                          child: Text('원하는 조건의 제품을 찾을 수 없어요:('),
                        ),
                      ],
                    ),
                  );
                }
                final model = snapshot.data;

                return Column(
                  children: [
                    Stack(
                      children: [
                        Center(
                          child: Column(
                            children: [
                              Text(
                                model!.modelNickname!,
                                style: const TextStyle(
                                  fontSize: 25, // 아이콘은 약 1.5배하자
                                  fontWeight: FontWeight.w700,
                                ),
                              ),
                              Divider(
                                indent: 120,
                                endIndent: 120,
                                color: Theme.of(context)
                                    .colorScheme
                                    .outlineVariant,
                                thickness: 2,
                              ),
                            ],
                          ),
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.end,
                          crossAxisAlignment: CrossAxisAlignment.center,
                          children: [
                            IconButton(
                                onPressed: () {
                                  pin(model.myModelId!);
                                },
                                icon: model.addAtPin == null
                                    ? const Icon(
                                        CustomIcon.pin_outline,
                                        size: 36,
                                        color: Colors.red,
                                      )
                                    : const Icon(
                                        CustomIcon.pin_1,
                                        size: 36,
                                        color: Colors.red,
                                      )),
                            const SizedBox(
                              width: 40,
                            )
                          ],
                        ),
                      ],
                    ),
                    InnerShadow(
                      shadows: [
                        Shadow(
                            color: Colors.black.withOpacity(0.4),
                            blurRadius: 8,
                            offset: const Offset(0, 0))
                      ],
                      child: TweenAnimationBuilder(
                        tween: Tween(begin: 0.0, end: 1.0),
                        curve: Curves.bounceInOut,
                        duration: const Duration(milliseconds: 3000),
                        builder: (context, value, child) {
                          return Container(
                            padding: const EdgeInsets.symmetric(
                              horizontal: 15,
                            ),
                            margin: const EdgeInsets.symmetric(
                              vertical: 10,
                            ),
                            height: 210,
                            width: MediaQuery.of(context).size.width * 0.83,
                            decoration: BoxDecoration(
                              borderRadius:
                                  const BorderRadius.all(Radius.circular(10)),
                              color: colors[model.modelGrade! - 1]
                                  .withOpacity(value),
                            ),
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: model.modelImg == null
                                      ? Image.asset(
                                          'assets/images/not_found.png')
                                      : Image.network(
                                          '${model.modelImg}',
                                        ),
                                ),
                                Flexible(
                                  flex: 1,
                                  child: Image.asset(
                                    'assets/images/grade${model.modelGrade}.png',
                                    width: MediaQuery.of(context).size.width *
                                        0.35,
                                  ),
                                ),
                              ],
                            ),
                          );
                        },
                      ),
                      // Container(
                      //   padding: const EdgeInsets.symmetric(
                      //     horizontal: 15,
                      //   ),
                      //   margin: const EdgeInsets.symmetric(
                      //     vertical: 10,
                      //   ),
                      //   height: 210,
                      //   width: MediaQuery.of(context).size.width * 0.83,
                      //   decoration: BoxDecoration(
                      //     borderRadius:
                      //         const BorderRadius.all(Radius.circular(10)),
                      //     color: colors[model.modelGrade! - 1],
                      //   ),
                      //   child: Row(
                      //     mainAxisAlignment: MainAxisAlignment.center,
                      //     children: [
                      //       Flexible(
                      //         flex: 1,
                      //         child: model.modelImg == null
                      //             ? Image.asset('assets/images/not_found.png')
                      //             : Image.network(
                      //                 '${model.modelImg}',
                      //               ),
                      //       ),
                      //       Flexible(
                      //         flex: 1,
                      //         child: Image.asset(
                      //           'assets/images/grade${model.modelGrade}.png',
                      //           width: MediaQuery.of(context).size.width * 0.35,
                      //         ),
                      //       ),
                      //     ],
                      //   ),
                      // ),
                    ),
                    Expanded(
                      child: Padding(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 20.0,
                          vertical: 10,
                        ),
                        child: ShaderMask(
                          shaderCallback: (Rect bounds) {
                            return LinearGradient(
                              //아래 속성들을 조절하여 원하는 값을 얻을 수 있다.
                              begin: Alignment.center,
                              end: Alignment.topCenter,
                              colors: [
                                Colors.white,
                                Colors.white.withOpacity(0.02)
                              ],
                              stops: const [0.9, 1],
                              tileMode: TileMode.mirror,
                            ).createShader(bounds);
                          },
                          child: ListView(
                            children: [
                              ListTile(
                                leading: const Text(
                                  "제품군",
                                ),
                                title: Text(
                                  "${model.category}",
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
                                leading: const Text(
                                  "모델명",
                                ),
                                title: Text(
                                  "${model.modelName}",
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
                                leading: const Text(
                                  "업체명",
                                ),
                                title: Text(
                                  "${model.modelBrand}",
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
                              ListTile(
                                leading: const Text(
                                  "CO2 배출량",
                                ),
                                title: Text(
                                  "${model.modelCo2}",
                                  style: const TextStyle(
                                    fontWeight: FontWeight.w700,
                                  ),
                                  textAlign: TextAlign.end,
                                ),
                                trailing: Text(
                                  "${model.co2Unit}",
                                ),
                              ),
                              const Divider(),
                              model.modelCost != null
                                  ? ListTile(
                                      leading: const Text(
                                        "에너지비용",
                                      ),
                                      title: Text(
                                        "${model.modelCost}",
                                        style: const TextStyle(
                                          fontWeight: FontWeight.w700,
                                        ),
                                        textAlign: TextAlign.end,
                                      ),
                                      trailing: Text(
                                        "${model.costUnit}",
                                      ),
                                    )
                                  : Container(),
                              model.modelCost != null
                                  ? const Divider()
                                  : Container(),
                              ListTile(
                                leading: Text(
                                  "${model.usage1}",
                                ),
                                title: Text(
                                  "${model.usageValue1}",
                                  style: const TextStyle(
                                    fontWeight: FontWeight.w700,
                                  ),
                                  textAlign: TextAlign.end,
                                ),
                                trailing: Text(
                                  "${model.usageUnit1}",
                                ),
                              ),
                              const Divider(),
                              model.usage2 != null
                                  ? ListTile(
                                      leading: Text(
                                        "${model.usage2}",
                                      ),
                                      title: Text(
                                        "${model.usageValue2}",
                                        style: const TextStyle(
                                          fontWeight: FontWeight.w700,
                                        ),
                                        textAlign: TextAlign.end,
                                      ),
                                      trailing: Text(
                                        "${model.usageUnit2}",
                                      ),
                                    )
                                  : Container(),
                              model.usage2 != null
                                  ? const Divider()
                                  : Container(),
                              model.usage3 != null
                                  ? ListTile(
                                      leading: Text(
                                        "${model.usage3}",
                                      ),
                                      title: Text(
                                        "${model.usageValue3}",
                                        style: const TextStyle(
                                          fontWeight: FontWeight.w700,
                                        ),
                                        textAlign: TextAlign.end,
                                      ),
                                      trailing: Text(
                                        "${model.usageUnit3}",
                                      ),
                                    )
                                  : Container(),
                              model.usage2 != null
                                  ? const Divider()
                                  : Container(),
                            ],
                          ),
                        ),
                      ),
                    ),
                    KnockButton(
                      onPressed: () {
                        // 버튼 클릭 시 실행할 동작
                      },
                      bColor: Theme.of(context).colorScheme.secondaryContainer,
                      fColor:
                          Theme.of(context).colorScheme.onSecondaryContainer,
                      width: MediaQuery.of(context).size.width * 0.8, // 버튼의 너비
                      height:
                          MediaQuery.of(context).size.width * 0.16, // 버튼의 높이
                      label: "새로운 가전 구경하러 가기", // 버튼에 표시할 텍스트
                    ),
                    const SizedBox(
                      height: 10,
                    ),
                  ],
                );
              }),
        ),
      ),
    );
  }
}
