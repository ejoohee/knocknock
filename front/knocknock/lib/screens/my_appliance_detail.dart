import 'package:flutter/material.dart';
import 'package:flutter_inner_shadow/flutter_inner_shadow.dart';
import 'package:knocknock/common/custom_icon_icons.dart';
import 'package:knocknock/components/buttons.dart';
import 'package:knocknock/constants/color_chart.dart';
import 'package:knocknock/models/my_appliance_model.dart';
import 'package:knocknock/providers/page_index.dart';
import 'package:knocknock/screens/home_screen.dart';
import 'package:knocknock/screens/my_appliance_list.dart';
import 'package:knocknock/services/model_service.dart';
import 'package:http/http.dart' as http;
import 'package:knocknock/widgets/app_bar_back.dart';
import 'package:provider/provider.dart';

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
  late String title;
  late bool isPinned;
  late int id;
  bool isLoading = true;
  Icon pinIcon = const Icon(CustomIcon.pin_outline);
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    myModelDetail = loadMyModelDetail();
  }

  Future<MyModelDetail> loadMyModelDetail() async {
    final detail = await modelService.findMyApplianceDetail(widget.myModelId);
    title = detail.modelNickname!;
    isPinned = detail.addAtPin != null;
    id = detail.myModelId!;
    isLoading = false;
    setState(() {});

    return detail;
  }

  pin(int myModelId) async {
    int response = await modelService.pinMyAppliance(widget.myModelId);
    if (response == 200) {
      // myModelDetail = loadMyModelDetail();
      isPinned = !isPinned;
    }
    setState(() {});
  }

  Future<String?> loadImage(String? imageUrl) async {
    if (imageUrl == null) {
      return null;
    }
    final response = await http.get(Uri.parse(imageUrl));
    if (response.statusCode == 200) {
      return imageUrl;
    } else {
      return '';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(isLoading ? '' : title),
            IconButton(
              onPressed: () {
                pin(id);
              },
              icon: isLoading || !isPinned
                  ? Icon(
                      CustomIcon.pin_outline,
                      size: 25,
                      color: Colors.blueGrey[200],
                      shadows: [
                        Shadow(
                          color: Colors.black.withOpacity(0.4), // 그림자 색상과 투명도
                          offset: const Offset(0, 1), // 그림자 offset (가로, 세로)
                          blurRadius: 4, // 그림자의 흐림 정도
                        ),
                        const Shadow(
                          color: Colors.white,
                          offset: Offset(0, -1),
                          blurRadius: 2,
                        ),
                      ],
                    )
                  : Icon(
                      CustomIcon.pin_1,
                      size: 25,
                      color: Colors.red[700],
                      shadows: [
                        Shadow(
                          color: Colors.black.withOpacity(0.4), // 그림자 색상과 투명도
                          offset: const Offset(0, 1), // 그림자 offset (가로, 세로)
                          blurRadius: 5, // 그림자의 흐림 정도
                        ),
                        const Shadow(
                          color: Colors.white,
                          offset: Offset(0, -1),
                          blurRadius: 2,
                        ),
                      ],
                    ),
            ),
          ],
        ),
        centerTitle: true,
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(
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
                        Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            CircularProgressIndicator(),
                          ],
                        ),
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
                        duration: const Duration(milliseconds: 1800),
                        builder: (context, value, child) {
                          return Container(
                            margin: const EdgeInsets.symmetric(
                              vertical: 10,
                            ),
                            height: 210,
                            width: MediaQuery.of(context).size.width * 0.83,
                            decoration: BoxDecoration(
                              borderRadius:
                                  const BorderRadius.all(Radius.circular(10)),
                              color: colors[model!.modelGrade! - 1]
                                  .withOpacity(value),
                            ),
                            child: FutureBuilder<String?>(
                                future: loadImage(model.modelImg),
                                builder: (context, snapshot) {
                                  if (snapshot.connectionState ==
                                      ConnectionState.waiting) {
                                    return Container();
                                  }

                                  return Stack(
                                    children: [
                                      Padding(
                                        padding: const EdgeInsets.fromLTRB(
                                            30, 15, 30, 25),
                                        child: Image.asset(
                                          'assets/images/grade${model.modelGrade}.png',
                                        ),
                                      ),
                                      Row(
                                        mainAxisAlignment:
                                            MainAxisAlignment.end,
                                        children: [
                                          model.modelImg == ''
                                              ? Padding(
                                                  padding: const EdgeInsets
                                                      .symmetric(
                                                    horizontal: 30,
                                                    vertical: 40,
                                                  ),
                                                  child: Image.asset(
                                                    'assets/images/not_found.png',
                                                  ),
                                                )
                                              : Padding(
                                                  padding: () {
                                                    if (model.category ==
                                                        'TV') {
                                                      return const EdgeInsets
                                                          .fromLTRB(
                                                          0, 50, 20, 40);
                                                    } else if (model.category ==
                                                        '진공청소기') {
                                                      return const EdgeInsets
                                                          .fromLTRB(
                                                          0, 50, 40, 40);
                                                    } else {
                                                      return const EdgeInsets
                                                          .symmetric(
                                                          vertical: 38);
                                                    }
                                                  }(),
                                                  child: Image.network(
                                                    '${model.modelImg}',
                                                    errorBuilder: (context,
                                                        error, stackTrace) {
                                                      print(
                                                          'Error loading image: $error');
                                                      return Padding(
                                                        padding:
                                                            const EdgeInsets
                                                                .only(
                                                                right: 30.0),
                                                        child: Image.asset(
                                                            'assets/images/not_found.png'),
                                                      );
                                                    },
                                                  ),
                                                ),
                                        ],
                                      ),
                                    ],
                                  );
                                }),
                          );
                        },
                      ),
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
                                  "${model!.category}",
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
                        context.read<CurrentPageIndex>().move(1);

                        Navigator.push(
                          context,
                          MaterialPageRoute(
                              builder: (context) =>
                                  const HomeScreen()), // SignUpPage는 회원가입 페이지 위젯입니다.
                        );
                      },
                      bColor: Theme.of(context).colorScheme.secondaryContainer,
                      fColor:
                          Theme.of(context).colorScheme.onSecondaryContainer,
                      width: MediaQuery.of(context).size.width * 0.8, // 버튼의 너비
                      height:
                          MediaQuery.of(context).size.width * 0.16, // 버튼의 높이
                      label: "새로운 가전 구경하러 가기 ▷▶", // 버튼에 표시할 텍스트
                    ),
                    const SizedBox(
                      height: 30,
                    ),
                  ],
                );
              }),
        ),
      ),
    );
  }
}
