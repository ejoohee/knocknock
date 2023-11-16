import 'package:flutter/material.dart';
import 'package:knocknock/common/custom_icon_icons.dart';
import 'package:knocknock/components/buttons.dart';
import 'package:knocknock/interceptors/interceptor.dart';
import 'package:knocknock/screens/manage_appliances.dart';
import 'package:knocknock/screens/view_greenproducts.dart';
import 'package:knocknock/services/outer_service.dart' hide storage;
import 'package:knocknock/services/user_service.dart' hide storage;
// import 'package:knocknock/widgets/cai_info.dart';
import 'package:provider/provider.dart';
import 'package:video_player/video_player.dart';

class MainPage extends StatefulWidget {
  const MainPage({super.key});

  @override
  State<MainPage> createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  final UserService userService = UserService();
  final OuterService outerService = OuterService();
  final keywordController = TextEditingController();
  String address1 = '';
  String address2 = '';
  Map<String, dynamic> airInfo = {};
  VideoPlayerController? _controller;
  String imagePath = 'assets/images/good.png';
  String videoPath = 'assets/videos/goodBackground.mp4';

  @override
  void initState() {
    super.initState();
    initializeAddresses();
    initialAirInfo();
    _controller = VideoPlayerController.asset(videoPath)
      ..initialize().then((_) {
        setState(() {
          _controller!.play();
          _controller!.setLooping(true);
        });
      });
  }

  void updateVideo(String newVideoPath) {
    _controller?.dispose(); // 기존 컨트롤러 해제
    _controller = VideoPlayerController.asset(newVideoPath)
      ..initialize().then((_) {
        setState(() {
          _controller!.play();
          _controller!.setLooping(true);
        });
      });
  }

  void initialAirInfo() async {
    airInfo = await outerService.airInfo();
    print(airInfo);
    // airInfo['통합대기환경지수'] = '1';
    setState(() {
      airInfo = airInfo;
      print(airInfo['통합대기환경지수']);
      switch (airInfo['통합대기환경지수']) {
        case '1':
          setState(() {
            imagePath = 'assets/images/good.png';
            updateVideo('assets/videos/goodBackground.mp4');
          });
          break;
        case '2':
          setState(() {
            imagePath = 'assets/images/soso.png';
            updateVideo('assets/videos/sosoBackground.mp4');
          });

          break;
        case '3':
          setState(() {
            imagePath = 'assets/images/bad.png';
            updateVideo('assets/videos/badBackground.mp4');
          });
          break;
        case '4':
          setState(() {
            imagePath = 'assets/images/verybad.png';
            updateVideo('assets/videos/verybadBackground.mp4');
          });
          break;
      }
    });
  }

  void initializeAddresses() async {
    final fullAddress = await storage.read(key: "address");
    final addressParts = fullAddress!.split(' ');
    if (addressParts.length >= 2) {
      setState(() {
        address1 = addressParts[0];
        address2 = addressParts[1];
      });
    }
  }

  void onManagementButtonTap() {
    if (!mounted) return;
    Navigator.pushReplacement(context,
        MaterialPageRoute(builder: (context) => const ManageAppliances()));
  }

  Future onSerchButtonTap() async {
    final response = await userService.checkGreenLabel(keywordController.text);
    if (!mounted) return;
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) =>
            ViewGreenProducts(info: response, keyword: keywordController.text),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          _controller != null && _controller!.value.isInitialized
              ? SizedBox.expand(
                  child: FittedBox(
                    fit: BoxFit.cover,
                    child: SizedBox(
                      width: _controller!.value.size.width,
                      height: _controller!.value.size.height,
                      child: VideoPlayer(_controller!),
                    ),
                  ),
                )
              : Container(),
          Positioned.fill(
              child: Container(
            color: Theme.of(context).colorScheme.background.withOpacity(0.35),
          )),
          SingleChildScrollView(
            scrollDirection: Axis.vertical,
            child: Column(
              children: [
                Column(
                  children: [
                    Container(
                      height: MediaQuery.of(context).size.height * 0.15,
                      alignment: Alignment.bottomCenter,
                      child: Text(
                        '$address2의 공기질',
                        style: TextStyle(
                          fontSize: 27,
                          fontWeight: FontWeight.w600,
                          color: Theme.of(context)
                              .colorScheme
                              .onBackground
                              .withOpacity(0.95),
                        ),
                      ),
                    ),
                    Container(
                      // height: MediaQuery.of(context).size.height * 0.2,
                      alignment: Alignment.topCenter,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          Text(
                            '통합대기환경지수(CAI, Comprehensive air-quality index)',
                            style: TextStyle(
                              fontSize: 12,
                              color: Theme.of(context).colorScheme.outline,
                            ),
                          ),
                          IconButton(
                              onPressed: () {
                                // showDialog(
                                //   context: context,
                                //   builder: (BuildContext dialogContext) {
                                //     return StatefulBuilder(builder:
                                //         (BuildContext context,
                                //             StateSetter setState) {
                                //       return const CAI();
                                //     });
                                //   },
                                // );
                              },
                              icon: Icon(
                                CustomIcon.info,
                                color: Theme.of(context).colorScheme.outline,
                                size: 15,
                              )),
                        ],
                      ),
                    ),
                    Container(
                      margin: const EdgeInsets.symmetric(vertical: 10),
                      width: 150,
                      height: 150,
                      decoration: BoxDecoration(
                        image: DecorationImage(
                          image: AssetImage(imagePath),
                        ),
                      ),
                    )
                  ],
                ),
                const Divider(
                  indent: 50,
                  endIndent: 50,
                ),
                Container(
                  padding: const EdgeInsets.all(40),
                  alignment: Alignment.center,
                  child: Text(
                    '녹색 제품 검색',
                    style: TextStyle(
                      fontSize: 27,
                      fontWeight: FontWeight.w600,
                      color: Theme.of(context)
                          .colorScheme
                          .onBackground
                          .withOpacity(0.95),
                    ),
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 60,
                  ),
                  child: TextFormField(
                    controller: keywordController,
                    decoration: InputDecoration(
                      contentPadding: const EdgeInsets.symmetric(
                          vertical: 10, horizontal: 20),
                      isDense: true,
                      hintText: '검색어를 입력하세요',
                      hintStyle: const TextStyle(
                        fontWeight: FontWeight.w300,
                      ),
                      suffixIcon: IconButton(
                        onPressed: onSerchButtonTap,
                        icon: const Icon(Icons.search),
                      ),
                      filled: true,
                      fillColor: Theme.of(context).colorScheme.surfaceVariant,
                      focusedBorder: OutlineInputBorder(
                        borderSide: BorderSide(
                            color: Theme.of(context).colorScheme.primary),
                        borderRadius: BorderRadius.circular(30),
                      ),
                      enabledBorder: OutlineInputBorder(
                        borderSide: const BorderSide(color: Colors.transparent),
                        borderRadius: BorderRadius.circular(30),
                      ),
                    ),
                    validator: (String? value) {
                      return (value == null) ? '값을 입력하세요' : null;
                    },
                  ),
                ),
                const Divider(
                  height: 150,
                  indent: 50,
                  endIndent: 50,
                ),
                KnockButton(
                  onPressed: () {
                    // final Uri url = Uri.parse(model.modelURL!);
                    // await launchUrl(url);
                  },
                  bColor: Theme.of(context).colorScheme.secondaryContainer,
                  fColor: Theme.of(context).colorScheme.onSecondaryContainer,
                  width: MediaQuery.of(context).size.width * 0.8, // 버튼의 너비
                  height: MediaQuery.of(context).size.width * 0.16, // 버튼의 높이
                  label: "내 가전 관리하기", // 버튼에 표시할 텍스트
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  // @override
  // void dispose() {
  //   super.dispose();
  //   _controller?.dispose();
  // }
}
