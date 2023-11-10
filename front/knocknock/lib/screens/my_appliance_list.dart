import 'package:flutter/material.dart';
import 'package:knocknock/common/custom_icon_icons.dart';
import 'package:knocknock/components/buttons.dart';
import 'package:knocknock/components/tile.dart';
import 'package:knocknock/constants/color_chart.dart';
import 'package:knocknock/models/my_appliance_model.dart';
import 'package:knocknock/providers/appliance.dart';
import 'package:knocknock/providers/page_index.dart';
import 'package:knocknock/screens/my_appliance_detail.dart';
import 'package:knocknock/screens/new_appliance_categories.dart';
import 'package:knocknock/services/model_service.dart';
import 'package:provider/provider.dart';

class MyApplianceList extends StatefulWidget {
  const MyApplianceList({super.key});

  @override
  State<MyApplianceList> createState() => _MyApplianceListState();
}

class _MyApplianceListState extends State<MyApplianceList> {
  List<Color> colors = [
    ColorChart.first,
    ColorChart.second,
    ColorChart.third,
    ColorChart.forth,
    ColorChart.fifth,
  ];
  final ModelService modelService = ModelService();
  late Future<List<MyModelTile>> modelListFuture; // Future to load modelList

  bool isEditing = false;

  Future<List<MyModelTile>> loadCompareModelData() async {
    try {
      final loadedModelList = await modelService.findMyApplianceList('');
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
      body: Padding(
        padding: const EdgeInsets.all(30.0),
        child: Column(
          children: [
            const Row(),
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
                            if (!mounted) return;
                            context.read<CurrentPageIndex>().move(2);
                            // 버튼 클릭 시 실행할 동작
                            // Navigator.push(
                            //   context,
                            //   MaterialPageRoute(
                            //       builder: (context) =>
                            //           // 등록 페이지로 수정하기
                            //           const NewApplianceCategories()),
                            // );
                          },
                          bColor: Theme.of(context).colorScheme.primary,
                          fColor: Theme.of(context).colorScheme.onPrimary,
                          width:
                              MediaQuery.of(context).size.width * 0.5, // 버튼의 너비
                          height: MediaQuery.of(context).size.width *
                              0.16, // 버튼의 높이
                          label: "내 가전 등록하기 ⇀", // 버튼에 표시할 텍스트)
                        ),
                      ],
                    ),
                  );
                } else {
                  List<MyModelTile> modelList = snapshot.data!;
                  return Expanded(
                    child: ListView.builder(
                      itemCount: modelList.length,
                      itemBuilder: (context, index) {
                        final model = modelList[index];
                        // heading(등급)넣어주기 위한 조건

                        return Column(
                          children: [
                            Tile(
                              onTap: () {
                                Navigator.push(
                                  context,
                                  MaterialPageRoute(
                                      builder: (context) => MyApplianceDetail(
                                          myModelId: model.myModelId!)),
                                );
                              },
                              onLongPress: () {
                                setState(() {
                                  isEditing = true;
                                });
                              },
                              isBookmarked: true,
                              bookmarkIcon: IconButton(
                                onPressed: () {},
                                icon: model.addAtPin != null
                                    ? const Icon(
                                        CustomIcon.pin_1,
                                        color: Colors.red,
                                      )
                                    : Container(),
                              ),
                              child: ListTile(
                                leading: ExcludeSemantics(
                                  child: CircleAvatar(
                                    radius: 35,
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
                                title: Text(
                                  model.modelNickname!,
                                  style: const TextStyle(
                                    fontWeight: FontWeight.w700,
                                  ),
                                ),
                                subtitle: Text(
                                    '제품군 : ${model.category!}\n업체명 : ${model.modelBrand!}'),
                                isThreeLine: true,
                                titleAlignment: ListTileTitleAlignment.center,
                              ),
                            ),
                            const SizedBox(
                              height: 15,
                            )
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
    );
  }
}
