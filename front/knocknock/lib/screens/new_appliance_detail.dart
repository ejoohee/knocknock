import 'package:flutter/material.dart';
import 'package:flutter_inner_shadow/flutter_inner_shadow.dart';
import 'package:knocknock/components/buttons.dart';
import 'package:knocknock/constants/color_chart.dart';
import 'package:knocknock/models/appliance_model.dart';
import 'package:knocknock/providers/appliance.dart';
import 'package:knocknock/screens/compare_list.dart';
import 'package:knocknock/screens/new_appliance_category_each.dart';
import 'package:knocknock/services/model_service.dart';
import 'package:knocknock/widgets/app_bar_back.dart';
import 'package:provider/provider.dart';
import 'package:http/http.dart' as http;

class NewApplianceDetail extends StatefulWidget {
  const NewApplianceDetail({super.key});

  @override
  State<NewApplianceDetail> createState() => _NewApplianceDetailState();
}

class _NewApplianceDetailState extends State<NewApplianceDetail>
    with TickerProviderStateMixin {
  final ModelService modelService = ModelService();
  List<Color> colors = [
    ColorChart.first,
    ColorChart.second,
    ColorChart.third,
    ColorChart.forth,
    ColorChart.fifth,
  ];
  late Future<NewModelDetail> modelDetail;
  late int selectedModel;
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    // print('시작');
    // modelDetail = loadModelDetail();
  }

  Future<NewModelDetail> loadModelDetail() async {
    final detail = await modelService.findNewModelDetail(selectedModel);
    return detail;
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    selectedModel = context.watch<SelectedAppliance>().modelId;
    modelDetail = loadModelDetail();
  }

// 찜 추가
  Future<bool> addLike(int modelId) async {
    late String response;

    response = await modelService.addLike(modelId);
    if (response == '201') {
      return true;
    } else {
      //메세지 pop...
    }
    return false;
  }

  // 찜 삭제
  Future<bool> deleteLike(int modelId) async {
    late String response;

    response = await modelService.cancelLike(modelId);
    if (response == '200') {
      return true;
    } else {
      //메세지 pop...
    }
    return false;
  }

  // 네트워크 이미지 기다리기
  Future<String> loadImage(String imageUrl) async {
    final response = await http.get(Uri.parse(imageUrl));
    if (response.statusCode == 200) {
      return imageUrl;
    } else {
      throw Exception('Failed to load image');
    }
  }

  final double _imageSize = 0.0; // 이미지의 크기를 제어할 변수

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const AppBarBack(title: '상세조회', page: NewApplianceCategoryEach()),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 30),
          child: FutureBuilder<NewModelDetail>(
              future: modelDetail,
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
                        const Center(
                          child: Text(
                            "상세조회",
                            style: TextStyle(
                              fontSize: 25, // 아이콘은 약 1.5배하자
                              fontWeight: FontWeight.w700,
                            ),
                          ),
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.end,
                          crossAxisAlignment: CrossAxisAlignment.center,
                          children: [
                            IconButton(
                                onPressed: () async {
                                  // 찜
                                  if (!model.isLiked!) {
                                    if (await addLike(model.modelId!)) {
                                      setState(() {
                                        model.isLiked = true;
                                      });
                                    }
                                  } else {
                                    if (await deleteLike(model.modelId!)) {
                                      setState(() {
                                        model.isLiked = false;
                                      });
                                    }
                                  }
                                },
                                icon: !model!.isLiked!
                                    ? const Icon(
                                        Icons.favorite_border_rounded,
                                        size: 36,
                                        color: Colors.red,
                                      )
                                    : const Icon(
                                        Icons.favorite_rounded,
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
                        duration: const Duration(milliseconds: 1800),
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
                            child: FutureBuilder<String>(
                                future: loadImage(model.modelImg!),
                                builder: (context, snapshot) {
                                  if (snapshot.connectionState ==
                                      ConnectionState.waiting) {
                                    return Container();
                                  }

                                  return Row(
                                    mainAxisAlignment: MainAxisAlignment.center,
                                    children: [
                                      Expanded(
                                        flex: 1,
                                        child: model.modelImg == null
                                            ? Image.asset(
                                                'assets/images/not_found.png',
                                                width: _imageSize,
                                              )
                                            : Image.network(
                                                '${model.modelImg}',
                                                errorBuilder: (context, error,
                                                    stackTrace) {
                                                  print(
                                                      'Error loading image: $error');
                                                  return Image.asset(
                                                      'assets/images/not_found.png');
                                                },
                                              ),
                                      ),
                                      Expanded(
                                        flex: 1,
                                        child: Image.asset(
                                          'assets/images/grade${model.modelGrade}.png',
                                        ),
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
                          horizontal: 40.0,
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
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                              builder: (context) =>
                                  const CompareList()), // SignUpPage는 회원가입 페이지 위젯입니다.
                        );
                      },
                      bColor: Theme.of(context).colorScheme.primary,
                      fColor: Theme.of(context).colorScheme.onPrimary,
                      width: MediaQuery.of(context).size.width * 0.8, // 버튼의 너비
                      height:
                          MediaQuery.of(context).size.width * 0.16, // 버튼의 높이
                      label: "나의 현재 가전과 비교하기", // 버튼에 표시할 텍스트
                    ),
                    const SizedBox(
                      height: 10,
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
                      label: "구매하러 가기 🔗", // 버튼에 표시할 텍스트
                    )
                  ],
                );
              }),
        ),
      ),
    );
  }
}
