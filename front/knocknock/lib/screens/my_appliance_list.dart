import 'package:flutter/material.dart';
import 'package:knocknock/components/circle_clipper.dart';
import 'package:knocknock/components/tile.dart';
import 'package:knocknock/models/my_appliance_model.dart';

class MyApplianceList extends StatefulWidget {
  const MyApplianceList({super.key});

  @override
  State<MyApplianceList> createState() => _MyApplianceListState();
}

class _MyApplianceListState extends State<MyApplianceList> {
  List<MyModelTile> models = [];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          const Row(),
          ListView.builder(itemBuilder: (context, index) {
            final model = models[index];
            // heading(등급)넣어주기 위한 조건

            return Tile(
              onTap: () {},
              isBookmarked: true,
              bookmarkIcon: IconButton(
                  onPressed: () {}, icon: const Icon(Icons.abc_sharp)),
              child: ListTile(
                leading: ClipPath(
                  clipper: CircleClipper(),
                  child: Container(
                    width: 100,
                    height: 100,
                    color: Colors.blue,
                  ),
                ),
                title: Text(model.modelNickname!),
                subtitle: Text('${model.category!}\n${model.modelBrand!}'),
                isThreeLine: true,
              ),
            );
          }),
        ],
      ),
    );
  }
}
