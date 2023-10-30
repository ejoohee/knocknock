import 'package:flutter/material.dart';
import 'package:knocknock/components/tile.dart';
import 'package:knocknock/models/appliance_model.dart';

class NewApplianceCategoryEach extends StatefulWidget {
  const NewApplianceCategoryEach({super.key});

  @override
  State<NewApplianceCategoryEach> createState() =>
      _NewApplianceCategoryEachState();
}

class _NewApplianceCategoryEachState extends State<NewApplianceCategoryEach> {
  List<NewModelTile> models = [];

  @override
  void initState() {
    super.initState();
    loadModelData(); // 데이터 로딩
  }

  void loadModelData() {}

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        padding: const EdgeInsets.symmetric(
          horizontal: 30,
        ),
        child: Column(
          children: [
            const Row(),
            ListView.builder(itemBuilder: (context, index) {
              final model = models[index];
              // heading(등급)넣어주기 위한 조건
              if (index == 0 ||
                  models[index - 1].modelGrade != model.modelGrade) {
                return Column(
                  children: [
                    const SizedBox(
                      height: 10,
                    ),
                    Text('${model.modelGrade}등급 - 헤딩이다 헤헤'),
                    Tile(
                      isBookmarked: true,
                      bookmarkIcon: IconButton(
                          onPressed: () {},
                          icon: model.isLiked!
                              ? const Icon(Icons.favorite_rounded)
                              : const Icon(Icons.favorite_border_rounded)),
                      onTap: () {},
                      child: ListTile(
                        leading: const Icon(Icons.access_time_sharp),
                        title: Text(model.modelName!),
                        subtitle: Text(model.modelBrand!),
                      ),
                    ),
                  ],
                );
              }
              // divide할 필요없으면 그냥 카드
              return Tile(
                isBookmarked: true,
                bookmarkIcon: IconButton(
                    onPressed: () {},
                    icon: model.isLiked!
                        ? const Icon(Icons.favorite_rounded)
                        : const Icon(Icons.favorite_border_rounded)),
                onTap: () {},
                child: ListTile(
                  leading: const Icon(Icons.access_time_sharp),
                  title: Text(model.modelName!),
                  subtitle: Text(model.modelBrand!),
                ),
              );
            })
          ],
        ),
      ),
    );
  }
}
