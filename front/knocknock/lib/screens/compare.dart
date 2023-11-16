import 'dart:math';
import 'package:knocknock/providers/page_index.dart';
import 'package:knocknock/screens/home_screen.dart';
import 'package:knocknock/widgets/no_tree.dart';
import 'package:knocknock/widgets/trees.dart';
import 'package:knocknock/widgets/app_bar_back.dart';
import 'package:text_divider/text_divider.dart';
import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:knocknock/constants/color_chart.dart';
import 'package:knocknock/models/my_appliance_model.dart';
import 'package:knocknock/providers/appliance.dart';
import 'package:knocknock/services/model_service.dart';
import 'package:provider/provider.dart';
// import 'package:fl_chart/fl_chart.dart';
import 'package:vertical_barchart/extension/expandedSection.dart';
import 'package:vertical_barchart/vertical-barchart.dart';
import 'package:vertical_barchart/vertical-barchartmodel.dart';
import 'package:vertical_barchart/vertical-legend.dart';

class Compare extends StatefulWidget {
  const Compare({super.key});

  @override
  State<Compare> createState() => _CompareState();
}

class _CompareState extends State<Compare> {
  ModelService modelService = ModelService();
  late Future<ModelsCompared> twoModels;
  late int modelId, myModelId;
  late double gradeN, gradeM, co2n, co2m, costN, costM;
  late int treeCnt;
  late String newImg, oldImg, nickname;
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    // loadModels();
  }

  Future<ModelsCompared> loadModels() async {
    final models = await modelService.findComparingSubjects(modelId, myModelId);
    return models;
  }

  @override
  void didChangeDependencies() {
    // TODO: implement didChangeDependencies
    super.didChangeDependencies();
    modelId = context.watch<SelectedAppliance>().modelId;
    myModelId = context.watch<SelectedAppliance>().comparedMine;
    nickname = context.watch<SelectedAppliance>().nickname;
    twoModels = loadModels();
  }

  int selectedIndex = -1; // 초기화 outofindexbound

  double newBarSizeValue() {
    if (selectedIndex == -1) {
      return 18.0;
    } else if (selectedIndex % 2 == 0) {
      return 30.0;
    } else {
      return 5.0;
    }
  }

  double myBarSizeValue() {
    if (selectedIndex == -1) {
      return 18.0;
    } else if (selectedIndex % 2 == 1) {
      return 30.0;
    } else {
      return 5.0;
    }
  }

  int newImageFlex() {
    if (selectedIndex == -1) {
      return 4;
    } else if (selectedIndex % 2 == 0) {
      return 5;
    } else {
      return 4;
    }
  }

  int myImageFlex() {
    if (selectedIndex == -1) {
      return 4;
    } else if (selectedIndex % 2 == 1) {
      return 5;
    } else {
      return 4;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const AppBarBack(title: '비교하기'),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 15.0),
          child: Stack(
            alignment: Alignment.bottomLeft,
            children: [
              FutureBuilder<ModelsCompared>(
                  future: twoModels,
                  builder: (context, snapshot) {
                    if (snapshot.connectionState == ConnectionState.waiting) {
                      // 데이터가 아직 준비되지 않은 경우에 대한 UI
                      return const Expanded(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
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
                      return const Text('No data available.');
                    } else {
                      ModelsCompared models = snapshot.data!;
                      gradeN = models.modelAGrade!.toDouble();
                      gradeM = models.modelBGrade!.toDouble();
                      co2n = models.modelACo2!.toDouble();
                      co2m = models.modelBCo2!.toDouble();
                      costN = models.modelACost!.toDouble();
                      costM = models.modelBCost!.toDouble();
                      treeCnt = models.treeCnt!;
                      newImg = models.modelAImg ?? '';
                      oldImg = models.modelBImg ?? '';

                      List<VBarChartModel> gradeNew = [
                        VBarChartModel(
                          index: 0,
                          label: "NEW",
                          colors: [
                            ColorChart.first,
                            ColorChart.second,
                            ColorChart.third,
                            ColorChart.forth,
                            ColorChart.fifth,
                          ],
                          jumlah: gradeN,
                          tooltip: gradeM < gradeN ? '😥' : '😍',
                          description: Text(
                            " 에너지 소비 효율 등급 : ${gradeN.toInt()}",
                            style: const TextStyle(fontSize: 10),
                            textAlign: TextAlign.center,
                          ),
                        ),
                      ];
                      List<VBarChartModel> gradeMy = [
                        VBarChartModel(
                          index: 0,
                          label: "OLD",
                          colors: [
                            ColorChart.first,
                            ColorChart.second,
                            ColorChart.third,
                            ColorChart.forth,
                            ColorChart.fifth,
                          ],
                          jumlah: gradeM,
                          tooltip: gradeM > gradeN ? '😥' : '😍',
                          description: Text(
                            " 에너지 소비 효율 등급 : ${gradeM.toInt()}",
                            style: const TextStyle(fontSize: 10),
                            textAlign: TextAlign.center,
                          ),
                        ),
                      ];
                      List<VBarChartModel> co2New = [
                        VBarChartModel(
                          index: 0,
                          label: "NEW",
                          colors: [
                            Colors.teal.shade300,
                            Colors.indigo,
                          ],
                          jumlah: co2n,
                          tooltip: co2m < co2n ? '😥' : '😍',
                          description: Text(
                            " 연간 CO₂ 배출 : ${co2n.toInt()} g/년",
                            style: const TextStyle(fontSize: 10),
                            textAlign: TextAlign.center,
                          ),
                        ),
                      ];
                      List<VBarChartModel> co2My = [
                        VBarChartModel(
                          index: 0,
                          label: "OLD",
                          colors: [
                            // Colors.limeAccent,
                            // Colors.lightGreen,
                            // Colors.green.shade600
                            Colors.blueGrey.shade200,
                            Colors.blueGrey.shade600
                          ],
                          jumlah: co2m,
                          tooltip: co2m > co2n ? '😥' : '😍',
                          description: Text(
                            " 연간 CO₂ 배출 : ${co2m.toInt()} g/년",
                            style: const TextStyle(fontSize: 10),
                            textAlign: TextAlign.center,
                          ),
                        ),
                      ];

                      List<VBarChartModel> costNew = [
                        VBarChartModel(
                          index: 0,
                          label: "NEW",
                          colors: [
                            Colors.teal.shade300,
                            Colors.indigo,
                          ],
                          jumlah: costN,
                          tooltip: costM < costN ? '😥' : '😍',
                          description: Text(
                            "연간 에너지 비용 : ${costN.toInt()} 원",
                            style: const TextStyle(fontSize: 10),
                          ),
                        ),
                      ];
                      List<VBarChartModel> costMy = [
                        VBarChartModel(
                          index: 0,
                          label: "OLD",
                          colors: [
                            // Colors.limeAccent,
                            // Colors.lightGreen,
                            // Colors.green.shade600
                            Colors.blueGrey.shade200,
                            Colors.blueGrey.shade600
                          ],
                          jumlah: costM,
                          tooltip: costM > costN ? '😥' : '😍',
                          description: Text(
                            "연간 에너지 비용 : ${costM.toInt()} 원",
                            style: const TextStyle(fontSize: 10),
                          ),
                        ),
                      ];
                      return SingleChildScrollView(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            SizedBox(
                              height: 180,
                              child: Stack(
                                alignment: Alignment.center,
                                children: [
                                  Row(
                                    mainAxisAlignment:
                                        MainAxisAlignment.spaceAround,
                                    children: [
                                      Expanded(
                                        flex: newImageFlex(),
                                        child: GestureDetector(
                                          onTap: () {
                                            // 새 가전 탭
                                            setState(() {
                                              selectedIndex = 0;
                                            });
                                          },
                                          onDoubleTap: () {
                                            setState(() {
                                              selectedIndex = -1;
                                            });
                                          },
                                          child: Stack(
                                            alignment:
                                                AlignmentDirectional.center,
                                            children: [
                                              newImg == ''
                                                  ? Image.asset(
                                                      'assets/images/question_marks.png',
                                                      width: MediaQuery.sizeOf(
                                                                  context)
                                                              .width /
                                                          3,
                                                    )
                                                  : Image.network(
                                                      newImg,
                                                      scale: selectedIndex == 0
                                                          ? 0.9
                                                          : 1,
                                                      errorBuilder: (context,
                                                          error, stackTrace) {
                                                        // print(
                                                        //     'Error loading image: $error');
                                                        return Padding(
                                                          padding:
                                                              const EdgeInsets
                                                                  .only(
                                                                  right: 30.0),
                                                          child: Image.asset(
                                                            'assets/images/question_marks.png',
                                                            width: MediaQuery
                                                                        .sizeOf(
                                                                            context)
                                                                    .width /
                                                                3,
                                                          ),
                                                        );
                                                      },
                                                    ),
                                              const Center(
                                                child: Text(
                                                  "NEW",
                                                  style: TextStyle(
                                                    letterSpacing: 2,
                                                    color: Colors.white70,
                                                    fontSize: 30,
                                                    fontWeight: FontWeight.w700,
                                                    shadows: [
                                                      Shadow(
                                                        color: Colors.black,
                                                        offset: Offset(2, 2),
                                                        blurRadius: 4,
                                                      ),
                                                    ],
                                                  ),
                                                ),
                                              ),
                                            ],
                                          ),
                                        ),
                                      ),
                                      Expanded(
                                        flex: myImageFlex(),
                                        child: GestureDetector(
                                          onTap: () {
                                            // 내 가전 탭
                                            setState(() {
                                              selectedIndex = 1;
                                            });
                                          },
                                          onDoubleTap: () {
                                            setState(() {
                                              selectedIndex = -1;
                                            });
                                          },
                                          child: Stack(
                                            alignment:
                                                AlignmentDirectional.center,
                                            children: [
                                              oldImg == ''
                                                  ? Image.asset(
                                                      'assets/images/question_marks.png')
                                                  : Image.network(oldImg,
                                                      scale: selectedIndex == 1
                                                          ? 0.9
                                                          : 1),
                                              Center(
                                                child: Text(
                                                  nickname,
                                                  style: TextStyle(
                                                    letterSpacing: 2,
                                                    color: Colors.white70,
                                                    fontSize:
                                                        nickname.length > 5
                                                            ? 18
                                                            : 30,
                                                    fontWeight: FontWeight.w700,
                                                    shadows: const [
                                                      Shadow(
                                                        color: Colors.black,
                                                        offset: Offset(2, 2),
                                                        blurRadius: 4,
                                                      ),
                                                    ],
                                                  ),
                                                ),
                                              ),
                                            ],
                                          ),
                                        ),
                                      ),
                                    ],
                                  ),
                                  Center(
                                    child: Image.asset(
                                      'assets/images/versus.png',
                                      width:
                                          MediaQuery.sizeOf(context).width / 6,
                                    ),
                                  )
                                ],
                              ),
                            ),
                            const SizedBox(
                              height: 30,
                            ),
                            TextDivider.horizontal(
                              text: Text(
                                '에너지소비효율등급',
                                style: TextStyle(
                                  fontSize: 15,
                                  color: Theme.of(context).colorScheme.outline,
                                ),
                              ),
                              indent: 10,
                              endIndent: 10,
                            ),
                            Column(
                              children: [
                                VerticalBarchart(
                                  labelColor: Theme.of(context)
                                      .colorScheme
                                      .onBackground,
                                  barSize: newBarSizeValue(),
                                  labelSizeFactor: 0.25,
                                  background:
                                      Theme.of(context).colorScheme.background,
                                  maxX: 5,
                                  data: gradeNew,
                                  // showBackdrop: true,
                                  tooltipSize: 40,
                                  legendPosition: LegendPosition.TOP,
                                ),
                                VerticalBarchart(
                                  labelColor: Colors.grey,
                                  barSize: myBarSizeValue(),
                                  labelSizeFactor: 0.25,
                                  background:
                                      Theme.of(context).colorScheme.background,
                                  maxX: 5,
                                  data: gradeMy,
                                  // showBackdrop: true,
                                  tooltipSize: 40,
                                ),
                              ],
                            ),
                            const SizedBox(
                              height: 20,
                            ),
                            TextDivider.horizontal(
                              text: Text(
                                '연간 CO₂ 배출량',
                                style: TextStyle(
                                  fontSize: 15,
                                  color: Theme.of(context).colorScheme.outline,
                                ),
                              ),
                              indent: 10,
                              endIndent: 10,
                            ),
                            Column(
                              children: [
                                VerticalBarchart(
                                  labelColor: Theme.of(context)
                                      .colorScheme
                                      .onBackground,
                                  barSize: newBarSizeValue(),
                                  // 두께
                                  labelSizeFactor:
                                      0.25, // label width ( 0.0 - 0.5, default is 0.33)
                                  tooltipSize: 40,
                                  background:
                                      Theme.of(context).colorScheme.background,
                                  maxX: max(co2n, co2m),
                                  data: co2New,
                                ),
                                VerticalBarchart(
                                  labelColor: Colors.grey,
                                  barSize: myBarSizeValue(),
                                  labelSizeFactor: 0.25,
                                  tooltipSize: 40,
                                  background:
                                      Theme.of(context).colorScheme.background,
                                  maxX: max(co2n, co2m),
                                  data: co2My,
                                ),
                              ],
                            ),
                            const SizedBox(
                              height: 20,
                            ),
                            TextDivider.horizontal(
                              text: Text(
                                '에너지 비용',
                                style: TextStyle(
                                  fontSize: 15,
                                  color: Theme.of(context).colorScheme.outline,
                                ),
                              ),
                              indent: 10,
                              endIndent: 10,
                            ),
                            Column(
                              children: [
                                VerticalBarchart(
                                  labelColor: Theme.of(context)
                                      .colorScheme
                                      .onBackground,
                                  barSize: newBarSizeValue(),
                                  labelSizeFactor: 0.25,
                                  tooltipSize: 40,
                                  background:
                                      Theme.of(context).colorScheme.background,
                                  maxX: max(costM, co2n),
                                  data: costNew,
                                  showLegend: false,
                                ),
                                VerticalBarchart(
                                  labelColor: Colors.grey,
                                  barSize: myBarSizeValue(),
                                  labelSizeFactor: 0.25,
                                  tooltipSize: 40,
                                  background:
                                      Theme.of(context).colorScheme.background,
                                  maxX: max(costM, costN),
                                  data: costMy,
                                  showLegend: true,
                                  legend: [
                                    Vlegend(
                                      size: 11,
                                      isSquare: false,
                                      color: Colors.teal.shade200,
                                      text: "새가전",
                                    ),
                                    Vlegend(
                                      size: 11,
                                      isSquare: false,
                                      color: Colors.blueGrey.shade200,
                                      text: nickname,
                                    ),
                                  ],
                                ),
                                const Divider(
                                  indent: 20,
                                  endIndent: 20,
                                ),
                                const SizedBox(
                                  height: 30,
                                ),
                              ],
                            ),
                          ],
                        ),
                      );
                    }
                  }),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 30.0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    IconButton.filledTonal(
                      onPressed: () {
                        context.read<CurrentPageIndex>().move(0);
                        Navigator.pushReplacement(
                            context,
                            MaterialPageRoute(
                                builder: (context) => const HomeScreen()));
                      },
                      icon: Icon(
                        Icons.home,
                        color: Theme.of(context).colorScheme.onSurfaceVariant,
                        size: 40,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () {
          // Add your onPressed code here!
          showDialog(
            context: context,
            builder: (BuildContext dialogContext) {
              return StatefulBuilder(
                  builder: (BuildContext context, StateSetter setState) {
                return treeCnt > 0
                    ? Trees(
                        treeCnt: treeCnt,
                      )
                    : const NoTree();
              });
            },
          );
        },
        label: const Text('교체하면 어떤 효과가 있을까?'),
        icon: const Icon(Icons.add),
      ),
    );
  }
}
