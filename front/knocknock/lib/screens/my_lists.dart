import 'package:flutter/material.dart';
import 'package:knocknock/screens/liked_appliance_list.dart';
import 'package:knocknock/screens/main_page.dart';
import 'package:knocknock/screens/my_appliance_list.dart';
import 'package:knocknock/widgets/app_bar_back.dart';

const List<Widget> options = <Widget>[
  Text('나의 가전'),
  Text('찜한 가전'),
];

class MyLists extends StatefulWidget {
  const MyLists({Key? key}) : super(key: key);

  @override
  State<MyLists> createState() => _MyListsState();
}

class _MyListsState extends State<MyLists> {
  final List<bool> selectedOption = <bool>[true, false];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBarBack(
        title: '내 가전 모아보기',
        isLeadingNeeded: false,
      ),
      body: SafeArea(
        child: Center(
          child: Column(
            children: [
              const SizedBox(
                height: 10,
              ),
              ToggleButtons(
                direction: Axis.horizontal,
                onPressed: (int index) {
                  setState(() {
                    // The button that is tapped is set to true, and the others to false.
                    for (int i = 0; i < selectedOption.length; i++) {
                      selectedOption[i] = i == index;
                    }
                  });
                },
                borderRadius: const BorderRadius.all(Radius.circular(4)),
                selectedBorderColor:
                    Theme.of(context).colorScheme.primaryContainer,
                selectedColor: Theme.of(context).colorScheme.onPrimaryContainer,
                fillColor: Theme.of(context).colorScheme.primaryContainer,
                color: Theme.of(context).colorScheme.secondary,
                constraints: const BoxConstraints(
                  minHeight: 40.0,
                  minWidth: 90.0,
                ),
                isSelected: selectedOption,
                children: options,
              ),
              selectedOption[0]
                  ? Expanded(child: MyApplianceList(key: UniqueKey()))
                  : const Expanded(child: LikedApplianceList()),
            ],
          ),
        ),
      ),
    );
  }
}
