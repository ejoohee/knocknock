import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:knocknock/common/custom_icon_icons.dart';
import 'package:knocknock/components/buttons.dart';
import 'package:knocknock/components/tile.dart';
import 'package:knocknock/models/appliance_model.dart';
import 'package:knocknock/providers/appliance.dart';
import 'package:knocknock/providers/page_index.dart';
import 'package:knocknock/screens/new_appliance_categories.dart';
import 'package:knocknock/screens/new_appliance_detail.dart';
import 'package:knocknock/services/model_service.dart';
import 'package:provider/provider.dart';

class LikedApplianceList extends StatefulWidget {
  const LikedApplianceList({super.key});

  @override
  State<LikedApplianceList> createState() => _LikedApplianceListState();
}

class _LikedApplianceListState extends State<LikedApplianceList> {
  final ModelService modelService = ModelService();
  late Future<List<LikedModel>> modelListFuture; // Future to load modelList
  String selectedCategory = '';
  bool isEditing = false;
  List<DropdownMenuItem<String>> categories = [
    const DropdownMenuItem(value: '전체 카테고리', child: Text('전체 카테고리'))
  ];
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    modelListFuture = loadLikedModels();
    loadCategories();
  }

  //리스트 불러오기
  Future<List<LikedModel>> loadLikedModels() async {
    print(selectedCategory);
    try {
      final loadedModelList =
          await modelService.findLikedModel(selectedCategory);
      return loadedModelList;
    } catch (e) {
      // 에러 처리
      print("Error loading clothing data: $e");
      return [];
    }
  }

  // 존재하는 카테고리만 생성하기
  void loadCategories() async {
    try {
      final loadedModelList =
          await modelService.findLikedModel(selectedCategory);
      Set<String> uniqueCategories =
          loadedModelList.map((model) => model.category!).toSet();
      List<String> categoryItems = uniqueCategories.toList();
      for (int i = 0; i < categoryItems.length; i++) {
        categories.add(DropdownMenuItem(
            value: categoryItems[i], child: Text(categoryItems[i])));
      }
    } catch (e) {
      // 에러 처리
      print("Error loading clothing data: $e");
    }
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

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(
        horizontal: 30,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch, // 자식이 가로폭을 채우도록 함

        children: [
          const SizedBox(
            height: 5,
          ),
          Padding(
            padding: const EdgeInsets.symmetric(
              horizontal: 20.0,
              vertical: 5,
            ),
            child: DropdownButtonFormField(
              autofocus: false,
              value: categories.first.value,
              items: categories,
              onChanged: (String? value) {
                setState(() {
                  if (value == '전체 카테고리') {
                    selectedCategory = '';
                  } else {
                    selectedCategory = value!;
                  }
                  modelListFuture = loadLikedModels();
                });
              },
              alignment: Alignment.center,
              decoration: const InputDecoration(
                floatingLabelBehavior: FloatingLabelBehavior.always,
                floatingLabelAlignment: FloatingLabelAlignment.center,
                // contentPadding:
                //     const EdgeInsets.symmetric(vertical: 12, horizontal: 10),
                // isDense: true,
                // enabledBorder: OutlineInputBorder(
                //   borderSide: BorderSide(
                //     width: 1,
                //     color: Theme.of(context).colorScheme.secondary,
                //   ),
                //   borderRadius: BorderRadius.circular(30),
                // ),
                // focusedBorder: OutlineInputBorder(
                //   borderSide: BorderSide(
                //     width: 1,
                //     color: Theme.of(context).colorScheme.primary,
                //   ),
                //   borderRadius: BorderRadius.circular(30),
                // ),
                // filled: true,
                // fillColor: Theme.of(context).colorScheme.primaryContainer,
              ),
            ),
          ),
          const SizedBox(
            height: 10,
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
                          '아직 찜한 제품이 없네요:(\n제품을 찾아보시겠어요?',
                          textAlign: TextAlign.center,
                        ),
                      ),
                      const SizedBox(
                        height: 50,
                      ),
                      KnockButton(
                        onPressed: () {
                          if (!mounted) return;
                          context.read<CurrentPageIndex>().move(1);
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
                        height:
                            MediaQuery.of(context).size.width * 0.16, // 버튼의 높이
                        label: "새 가전 구경하기 ⇀", // 버튼에 표시할 텍스트)
                      ),
                    ],
                  ),
                );
              } else {
                List<LikedModel> modelList = snapshot.data!;
                return Expanded(
                  child: ShaderMask(
                    shaderCallback: (Rect bounds) {
                      return LinearGradient(
                        //아래 속성들을 조절하여 원하는 값을 얻을 수 있다.
                        begin: Alignment.center,
                        end: Alignment.topCenter,
                        colors: [Colors.white, Colors.white.withOpacity(0.01)],
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
                          padding: const EdgeInsets.symmetric(vertical: 5.0),
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
                                    context
                                        .read<SelectedAppliance>()
                                        .selectModel(model.modelId!);
                                    context
                                        .read<SelectedAppliance>()
                                        .select(model.category!);
                                    Navigator.push(
                                      context,
                                      MaterialPageRoute(
                                          builder: (context) =>
                                              const NewApplianceDetail()),
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
                                            onPressed: () async {
                                              // 찜 삭제 버튼
                                              if (await deleteLike(
                                                  model.modelId!)) {
                                                setState(() {
                                                  isEditing = false;
                                                  modelListFuture =
                                                      loadLikedModels();
                                                });
                                              }
                                            },
                                            icon: Icon(
                                              // CustomIcon.trashcan,
                                              Icons.delete_forever_rounded,
                                              size: 38,
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
                                  color:
                                      Theme.of(context).colorScheme.secondary,
                                  child: ListTile(
                                    // 카테고리 이미지!!
                                    leading: SizedBox(
                                      width: isEditing ? 45 : 60,
                                      child: Image.asset(
                                          'assets/icons/${model.category}.png'),
                                    ),
                                    title: Text(
                                      model.modelName!,
                                      style: TextStyle(
                                        fontSize: isEditing ? 14 : 15,
                                        fontWeight: FontWeight.w700,
                                        color: Theme.of(context)
                                            .colorScheme
                                            .onSecondary,
                                      ),
                                    ),
                                    subtitle:
                                        // 카테고리 넣어주기...
                                        Text(
                                      '제품군 : ${model.category}\n업체명 : ${model.modelBrand!}',
                                      style: TextStyle(
                                        fontSize: 12,
                                        color: Theme.of(context)
                                            .colorScheme
                                            .onSecondary,
                                      ),
                                    ),
                                    isThreeLine: true,
                                    titleAlignment:
                                        ListTileTitleAlignment.center,
                                    trailing:
                                        const Icon(CustomIcon.chevron_right),
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
    );
  }
}
