import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class MainPage extends StatefulWidget {
  const MainPage({super.key});

  @override
  State<MainPage> createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  int currentPageIndex = 1;
  NavigationDestinationLabelBehavior labelBehavior =
      NavigationDestinationLabelBehavior.onlyShowSelected;

  final List<Widget> _widgetOptions = <Widget>[];

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
        body: Column(children: [
      // Expanded(child: _widgetOptions.elementAt(currentPageIndex)),
    ]));
  }
}
