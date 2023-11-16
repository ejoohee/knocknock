import 'package:flutter/material.dart';
import 'package:knocknock/common/custom_icon_icons.dart';
import 'package:knocknock/components/buttons.dart';
import 'package:knocknock/components/tile.dart';
import 'package:knocknock/constants/color_chart.dart';
import 'package:knocknock/models/my_appliance_model.dart';
import 'package:knocknock/providers/appliance.dart';
import 'package:knocknock/providers/page_index.dart';
import 'package:knocknock/screens/home_screen.dart';
import 'package:knocknock/screens/main_page.dart';
import 'package:knocknock/screens/my_appliance_detail.dart';
import 'package:knocknock/screens/new_appliance_categories.dart';
import 'package:knocknock/services/model_service.dart';
import 'package:knocknock/widgets/app_bar_back.dart';
import 'package:provider/provider.dart';

// const List<Widget> options = <Widget>[
//   Text('나의 가전'),
//   Text('찜한 가전'),
// ];

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
  // @override
  // void initState() {
  //   // TODO: implement initState
  //   super.initState();
  //   print('내 가전 목록 페이지???');
  //   modelListFuture = loadCompareModelData();
  // }

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
    // updateCategoryEntries();
  }

  final Set<String> uniqueCategories = <String>{};

  // void updateCategoryEntries() {
  //   // 모델 데이터를 가져와서 각 모델의 카테고리를 uniqueCategories에 추가
  //   modelListFuture.then((modelList) {
  //     for (MyModelTile model in modelList) {
  //       uniqueCategories.add(model.category!);
  //     }
  //   });
  // }

  deleteMyAppliance(int myModelId) async {
    final msg = await modelService.deleteMyAppliance(myModelId);
    if (!mounted) return;
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
                // 확인 버튼을 누르면 다시 빌드
                context.read<CurrentPageIndex>().move(3);
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const HomeScreen()),
                );
              },
              child: const Text('확인'),
            ),
          ],
        );
      },
    );
    // modelListFuture = loadCompareModelData();
  }

  // final List<bool> selectedOption = <bool>[true, false];

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.symmetric(
          horizontal: 30,
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch, // 자식이 가로폭을 채우도록 함
          children: [
            const SizedBox(
              height: 10,
            ),
            // DropdownMenu<String>(
            //   dropdownMenuEntries: categoryEntries,
            //   enableFilter: true,
            //   inputDecorationTheme: const InputDecorationTheme(
            //     border: UnderlineInputBorder(),
            //   ),
            // ),
            // ToggleButtons(
            //   direction: Axis.horizontal,
            //   onPressed: (int index) {
            //     setState(() {
            //       // The button that is tapped is set to true, and the others to false.
            //       for (int i = 0; i < selectedOption.length; i++) {
            //         selectedOption[i] = i == index;
            //       }
            //     });
            //   },
            //   borderRadius: const BorderRadius.all(Radius.circular(4)),
            //   selectedBorderColor: Colors.green[700],
            //   selectedColor: Colors.white,
            //   fillColor: Colors.green[200],
            //   color: Colors.green[400],
            //   constraints: const BoxConstraints(
            //     minHeight: 40.0,
            //     minWidth: 100.0,
            //   ),
            //   isSelected: selectedOption,
            //   children: options,
            // ),
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
                            // // 버튼 클릭 시 실행할 동작
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
                    child: ShaderMask(
                      shaderCallback: (Rect bounds) {
                        return LinearGradient(
                          //아래 속성들을 조절하여 원하는 값을 얻을 수 있다.
                          begin: Alignment.center,
                          end: Alignment.topCenter,
                          colors: [
                            Colors.white,
                            Colors.white.withOpacity(0.01)
                          ],
                          stops: const [0.95, 1],
                          tileMode: TileMode.mirror,
                        ).createShader(bounds);
                      },
                      child: ListView.builder(
                        key: const PageStorageKey<String>('value'),
                        itemCount: modelList.length,
                        itemBuilder: (context, index) {
                          final model = modelList[index];
                          // heading(등급)넣어주기 위한 조건

                          return Padding(
                            padding: const EdgeInsets.symmetric(
                              vertical: 6.0,
                            ),
                            child: Column(
                              children: [
                                InkWell(
                                  borderRadius: BorderRadius.circular(15),
                                  onLongPress: () {
                                    setState(() {
                                      isEditing = !isEditing;
                                    });
                                  },
                                  child: Tile(
                                    onTap: () {
                                      isEditing = false;
                                      Navigator.push(
                                        context,
                                        MaterialPageRoute(
                                            builder: (context) =>
                                                MyApplianceDetail(
                                                    myModelId:
                                                        model.myModelId!)),
                                      );
                                    },
                                    onLongPress: () {
                                      setState(() {
                                        isEditing = !isEditing;
                                      });
                                    },
                                    isBookmarked: isEditing,
                                    bookmarkIcon: Padding(
                                      padding: const EdgeInsets.only(left: 5),
                                      child: Column(
                                        children: [
                                          const SizedBox(
                                            height: 18,
                                          ),
                                          IconButton(
                                              onPressed: () {
                                                // 내 가전 삭제 버튼
                                                deleteMyAppliance(
                                                    model.myModelId!);
                                              },
                                              icon: Icon(
                                                // CustomIcon.trashcan,
                                                Icons.delete_forever_rounded,
                                                size: 40,
                                                color: Colors.red[600],
                                                fill: 0.2,
                                                shadows: [
                                                  Shadow(
                                                    color: Colors.black
                                                        .withOpacity(
                                                            0.4), // 그림자 색상과 투명도
                                                    offset: const Offset(-1,
                                                        1), // 그림자 offset (가로, 세로)
                                                    blurRadius: 2, // 그림자의 흐림 정도
                                                  ),
                                                  const Shadow(
                                                    color: Colors.white30,
                                                    offset: Offset(1, -1),
                                                    blurRadius: 2,
                                                  ),
                                                ],
                                              )),
                                        ],
                                      ),
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
                                      title: Row(
                                        children: [
                                          Text(
                                            model.modelNickname!,
                                            style: TextStyle(
                                              fontWeight: FontWeight.w700,
                                              color: Theme.of(context)
                                                  .colorScheme
                                                  .scrim,
                                            ),
                                          ),
                                          const SizedBox(
                                            width: 10,
                                          ),
                                          if (model.addAtPin != null)
                                            const Icon(
                                              CustomIcon.pin_1,
                                              size: 18,
                                            ),
                                        ],
                                      ),
                                      subtitle: Text(
                                        '제품군 : ${model.category!}\n업체명 : ${model.modelBrand!}',
                                        style: TextStyle(
                                          color: Theme.of(context)
                                              .colorScheme
                                              .scrim,
                                        ),
                                      ),
                                      isThreeLine: true,
                                      titleAlignment:
                                          ListTileTitleAlignment.center,
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          );
                        },
                      ),
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
