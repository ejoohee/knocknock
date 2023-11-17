import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:knocknock/interceptors/interceptor.dart';
import 'package:knocknock/screens/manage_appliances.dart';
import 'package:knocknock/screens/view_greenproducts.dart';
import 'package:knocknock/services/outer_service.dart' hide storage;
import 'package:knocknock/services/user_service.dart' hide storage;
import 'package:provider/provider.dart';
import 'package:video_player/video_player.dart';

class TMP extends StatefulWidget {
  const TMP({super.key});

  @override
  State<TMP> createState() => _TMPState();
}

class _TMPState extends State<TMP> {
  final UserService userService = UserService();
  final OuterService outerService = OuterService();
  final keywordController = TextEditingController();
  String address1 = '';
  String address2 = '';
  Map<String, dynamic> airInfo = {};
  VideoPlayerController? _controller;
  late String imagePath;
  // late String videoPath;

  @override
  void initState() {
    super.initState();
    initializeAddresses();
    initialAirInfo();
    // waitVideo();
  }

  // void waitVideo() {
  //   _controller = VideoPlayerController.asset(videoPath)
  //     ..initialize().then((_) {
  //       setState(() {
  //         _controller!.play();
  //         _controller!.setLooping(true);
  //       });
  //     });
  // }

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

  Future initialAirInfo() async {
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
      // updateVideo(videoPath);
    });
    // _controller = VideoPlayerController.asset(videoPath)
    //   ..initialize().then((_) {
    //     setState(() {
    //       _controller!.play();
    //       _controller!.setLooping(true);
    //     });
    //   });
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
    Navigator.pushReplacement(
        context,
        MaterialPageRoute(
            builder: (context) => ViewGreenProducts(
                info: response, keyword: keywordController.text)));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          FutureBuilder(
              future: initialAirInfo(),
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  // 데이터가 아직 준비되지 않은 경우에 대한 UI
                  return Container(
                    decoration: BoxDecoration(
                      color: Theme.of(context).colorScheme.background,
                    ),
                    child: const Expanded(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          CircularProgressIndicator(),
                        ],
                      ),
                    ),
                  );
                } else if (snapshot.hasError) {
                  // 에러 발생 시에 대한 UI
                  return Text('Error: ${snapshot.error}');
                } else if (!snapshot.hasData) {
                  // 데이터가 비어 있는 경우에 대한 UI
                  return const Expanded(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Center(
                          child: Text('설마... 데이터가 없나요?'),
                        ),
                      ],
                    ),
                  );
                }
                return Stack(
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
                    SingleChildScrollView(
                      scrollDirection: Axis.vertical,
                      child: Column(
                        children: [
                          Column(
                            children: [
                              Container(
                                height:
                                    MediaQuery.of(context).size.height * 0.15,
                                alignment: Alignment.bottomCenter,
                                child: Text(
                                  '$address2의 공기질',
                                  style: const TextStyle(
                                      fontSize: 35,
                                      fontWeight: FontWeight.w600),
                                ),
                              ),
                              Container(
                                // height: MediaQuery.of(context).size.height * 0.2,
                                alignment: Alignment.topCenter,
                                child: const Text(
                                  '통합대기환경지수(CAI, Comprehensive air-quality index)',
                                  style: TextStyle(
                                    fontSize: 13,
                                    color: Colors.grey,
                                  ),
                                ),
                              ),
                              Container(
                                margin:
                                    const EdgeInsets.symmetric(vertical: 20),
                                width: 150,
                                height: 150,
                                decoration: BoxDecoration(
                                    image: DecorationImage(
                                        image: AssetImage(imagePath))),
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
                            child: const Text(
                              '녹색 제품 검색',
                              style: TextStyle(
                                  fontSize: 25, fontWeight: FontWeight.w600),
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
                                focusedBorder: OutlineInputBorder(
                                  borderSide: BorderSide(
                                      color: Theme.of(context)
                                          .colorScheme
                                          .primary),
                                  borderRadius: BorderRadius.circular(30),
                                ),
                                enabledBorder: OutlineInputBorder(
                                  borderSide: const BorderSide(
                                      color: Colors.transparent),
                                  borderRadius: BorderRadius.circular(30),
                                ),
                              ),
                              validator: (String? value) {
                                return (value == null) ? '값을 입력하세요' : null;
                              },
                            ),
                          ),
                          const Divider(
                            height: 200,
                            indent: 50,
                            endIndent: 50,
                          ),
                          GestureDetector(
                            onTap: onManagementButtonTap,
                            child: Container(
                              padding: const EdgeInsets.all(20),
                              width: 250,
                              alignment: Alignment.center,
                              decoration: BoxDecoration(
                                color: Colors.green[900],
                                borderRadius: BorderRadius.circular(15),
                                boxShadow: const [
                                  BoxShadow(
                                    color: Colors.black26,
                                    offset: Offset(0, 4),
                                    blurRadius: 5,
                                  ),
                                ],
                              ),
                              child: const Text(
                                '내 가전 관리하기',
                                style: TextStyle(
                                  color: Colors.white,
                                  fontSize: 20,
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                );
              }),
        ],
      ),
    );
  }

  @override
  void dispose() {
    super.dispose();
    _controller?.dispose();
  }
}
