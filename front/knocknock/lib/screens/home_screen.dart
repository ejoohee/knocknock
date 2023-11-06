import 'package:flutter/material.dart';
import 'package:knocknock/screens/main_page.dart';
import 'package:knocknock/screens/my_page.dart';
import 'package:knocknock/screens/new_appliance_categories.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int currentPageIndex = 0;
  NavigationDestinationLabelBehavior labelBehavior =
      NavigationDestinationLabelBehavior.alwaysShow;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        bottomNavigationBar: NavigationBar(
          labelBehavior: labelBehavior,
          selectedIndex: currentPageIndex,
          onDestinationSelected: (int index) {
            setState(() {
              currentPageIndex = index;
            });
          },
          destinations: const <Widget>[
            NavigationDestination(icon: Icon(Icons.home), label: '홈'),
            NavigationDestination(
                icon: Icon(Icons.search_rounded), label: '가전 찾기'),
            NavigationDestination(icon: Icon(Icons.add), label: '가전 등록'),
            NavigationDestination(
                icon: Icon(Icons.local_laundry_service), label: '내 가전'),
            NavigationDestination(icon: Icon(Icons.menu), label: '서비스'),
          ],
        ),
        body: <Widget>[
          const MainPage(),
          const NewApplianceCategories(),
          const Center(child: Text('가전 등록')),
          const Center(child: Text('내 가전')),
          const MyPage(),
        ][currentPageIndex]);
  }
}
