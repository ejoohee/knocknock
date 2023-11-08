import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:knocknock/screens/main_page.dart';
import 'package:knocknock/screens/my_appliance_list.dart';
import 'package:knocknock/screens/my_page.dart';
import 'package:knocknock/screens/new_appliance_categories.dart';
import 'package:knocknock/screens/take_picture_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int currentPageIndex = 0;
  NavigationDestinationLabelBehavior labelBehavior =
      NavigationDestinationLabelBehavior.alwaysShow;
  late CameraDescription firstCamera;
  Future<void> loadCam() async {
    WidgetsFlutterBinding.ensureInitialized();
    // Obtain a list of the available cameras on the device.
    final cameras = await availableCameras();
    // Get a specific camera from the list of available cameras.
    firstCamera = cameras.first;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        bottomNavigationBar: NavigationBar(
          labelBehavior: labelBehavior,
          selectedIndex: currentPageIndex,
          onDestinationSelected: (int index) async {
            setState(() {
              currentPageIndex = index;
            });
            if (index == 2) {
              await loadCam();
            }
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
          FutureBuilder(
              future: loadCam(),
              builder: (context, snapshot) {
                return TakePictureScreen(camera: firstCamera);
              }),
          const MyApplianceList(),
          const MyPage(),
        ][currentPageIndex]);
  }
}
