import 'package:flutter/material.dart';
import 'package:knocknock/components/buttons.dart';
import 'package:knocknock/components/circle_clipper.dart';
import 'package:knocknock/components/tile.dart';
import 'package:knocknock/constants/color_chart.dart';
import 'package:knocknock/models/my_appliance_model.dart';
import 'package:knocknock/providers/appliance.dart';
import 'package:knocknock/screens/compare.dart';
import 'package:knocknock/services/model_service.dart';
import 'package:provider/provider.dart';

class CompareList extends StatefulWidget {
  const CompareList({super.key});

  @override
  State<CompareList> createState() => _CompareListState();
}

class _CompareListState extends State<CompareList> {
  List<Color> colors = [
    ColorChart.first,
    ColorChart.second,
    ColorChart.third,
    ColorChart.forth,
    ColorChart.fifth,
  ];
  final ModelService modelService = ModelService();
  late Future<List<MyModelTile>> modelListFuture; // Future to load modelList

  Future<List<MyModelTile>> loadCompareModelData() async {
    final selectedCategory = context.watch<SelectedAppliance>().category;
    try {
      final loadedModelList =
          await modelService.findMyApplianceList(selectedCategory);
      return loadedModelList;
    } catch (e) {
      // 에러 처리
      print("Error loading clothing data: $e");
      return [];
    }
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    modelListFuture = loadCompareModelData();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(
            30,
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              const Text(
                "어떤 가전과 비교하시겠어요?",
                style: TextStyle(
                  fontSize: 26,
                  fontWeight: FontWeight.w700,
                ),
                textAlign: TextAlign.center,
              ),
              FutureBuilder(
                future: modelListFuture,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    // 데이터가 아직 준비되지 않은 경우에 대한 UI
                    return const Expanded(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          CircularProgressIndicator(),
                        ],
                      ),
                    );
                  } else if (snapshot.hasError) {
                    // 에러 발생 시에 대한 UI
                    return Text('Error: ${snapshot.error}');
                  } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
                    // 데이터가 비어 있는 경우에 대한 UI
                    return Expanded(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          const Center(
                            child: Text(
                              '아직 등록된 제품이 없네요:(\n제품을 등록하시겠어요?',
                              textAlign: TextAlign.center,
                            ),
                          ),
                          const SizedBox(
                            height: 50,
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
                            width: MediaQuery.of(context).size.width *
                                0.5, // 버튼의 너비
                            height: MediaQuery.of(context).size.width *
                                0.16, // 버튼의 높이
                            label: "내 가전 등록하기 ⇀", // 버튼에 표시할 텍스트)
                          ),
                        ],
                      ),
                    );
                  } else {
                    // 데이터를 사용하여 ListView.builder 생성
                    List<MyModelTile> modelList = snapshot.data!;
                    return Expanded(
                      child: ListView.builder(
                        padding: const EdgeInsets.all(8),
                        itemCount: modelList.length,
                        itemBuilder: (BuildContext context, int index) {
                          final model = modelList[index];
                          return Column(
                            children: [
                              Tile(
                                isBookmarked: false,
                                onTap: () {
                                  Navigator.push(
                                    context,
                                    MaterialPageRoute(
                                        builder: (context) => const Compare()),
                                  );
                                },
                                color: Colors.grey.shade300,
                                child: ListTile(
                                  titleAlignment: ListTileTitleAlignment.center,
                                  leading: ExcludeSemantics(
                                    child: CircleAvatar(
                                      radius: 40,
                                      backgroundColor:
                                          colors[model.modelGrade! - 1],
                                      child: Text(
                                        '${model.modelGrade!}',
                                        style: const TextStyle(
                                          color: Colors.white,
                                          fontSize: 40,
                                        ),
                                      ),
                                    ),
                                  ),
                                  title: Text(model.modelNickname!),
                                  subtitle: Text(
                                      '${model.modelBrand!}\n${model.category!}'),
                                ),
                              ),
                              const SizedBox(
                                height: 15,
                              ),
                            ],
                          );
                        },
                      ),
                    );
                  }
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}
