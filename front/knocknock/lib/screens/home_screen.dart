import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:knocknock/providers/page_index.dart';
import 'package:knocknock/screens/main_page.dart';
import 'package:knocknock/screens/my_appliance_list.dart';
import 'package:knocknock/screens/my_info_modify.dart';
import 'package:knocknock/screens/my_lists.dart';
import 'package:knocknock/screens/my_page.dart';
import 'package:knocknock/screens/new_appliance_categories.dart';
import 'package:knocknock/screens/take_picture_screen.dart';
import 'package:provider/provider.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int currentPageIndex = 0;
  late Future<void> _initCameraFuture;

  @override
  void initState() {
    super.initState();

    _initCameraFuture = loadCam(); // initState에서 카메라 로딩 시작
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    currentPageIndex =
        context.watch<CurrentPageIndex>().currentPageIndexProvider;

    print(currentPageIndex);
  }

  NavigationDestinationLabelBehavior labelBehavior =
      NavigationDestinationLabelBehavior.alwaysShow;
  late CameraDescription firstCamera;
  Future<void> loadCam() async {
    final cameras = await availableCameras();
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
            context.read<CurrentPageIndex>().move(index);
            currentPageIndex = index;
          });
          if (index == 2) {
            await loadCam();
          }
          if (index == 3) {
            setState(() {});
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
      // body: <Widget>[
      //   const MainPage(),
      //   const NewApplianceCategories(),
      //   FutureBuilder(
      //       future: loadCam(),
      //       builder: (context, snapshot) {
      //         return TakePictureScreen(camera: firstCamera);
      //       }),
      //   const MyApplianceList(),
      //   const MyPage(),
      //   const MyInfoModify(),
      // ][currentPageIndex]);
      body: IndexedStack(
        index: currentPageIndex,
        children: <Widget>[
          const MainPage(),
          const NewApplianceCategories(),
          FutureBuilder<void>(
            future: _initCameraFuture,
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.done) {
                return TakePictureScreen(camera: firstCamera);
              } else {
                return const CircularProgressIndicator();
              }
            },
          ),
          const MyLists(),
          const MyPage(),
        ],
      ),
    );
  }
}
