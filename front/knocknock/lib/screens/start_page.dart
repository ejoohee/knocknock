import 'package:flutter/material.dart';
import 'package:knocknock/screens/log_in.dart';
import 'package:video_player/video_player.dart';

class StartPage extends StatefulWidget {
  const StartPage({super.key});

  @override
  State<StartPage> createState() => _StartPageState();
}

class _StartPageState extends State<StartPage> {
  late VideoPlayerController _controller;

  @override
  void initState() {
    super.initState();
    // VideoPlayerController를 사용하여 동영상을 로드하고 초기화합니다.
    _controller = VideoPlayerController.asset('assets/videos/main.mp4')
      ..initialize().then((_) {
        // 동영상 재생 및 반복 설정
        _controller.play();
        _controller.setLooping(true);
        setState(() {});
      });
  }

  @override
  void dispose() {
    // 컨트롤러 해제
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          // 동영상이 초기화되면 AspectRatio 위젯을 사용하여 배경으로 설정
          _controller.value.isInitialized
              ? SizedBox.expand(
                  child: FittedBox(
                    fit: BoxFit.cover, // 동영상이 전체 화면을 채우도록 조정
                    child: SizedBox(
                      width: _controller.value.size.width ?? 0,
                      height: _controller.value.size.height ?? 0,
                      child: VideoPlayer(_controller), // 동영상 플레이어
                    ),
                  ),
                )
              : Container(),
          // 기존의 UI 요소들을 여기에 배치
          Column(
            children: [
              Flexible(
                flex: 2,
                child: Container(),
              ),
              Flexible(
                flex: 1,
                child: Container(
                  alignment: Alignment.bottomCenter,
                  child: const Text(
                    'KnocKnocK',
                    style: TextStyle(
                      fontSize: 40,
                      fontWeight: FontWeight.w600,
                      color: Colors.white,
                    ),
                  ),
                ),
              ),
              Container(
                alignment: Alignment.topCenter,
                child: const Text(
                  '지구를 지키는 가전선택 솔루션',
                  style: TextStyle(fontSize: 12, color: Colors.white),
                ),
              ),
              Flexible(
                flex: 5,
                child: Container(
                  alignment: Alignment.bottomCenter,
                  child: GestureDetector(
                    onTap: () {
                      Navigator.push(
                          context,
                          MaterialPageRoute(
                              builder: (context) => const Login()));
                    },
                    child: Container(
                      width: 250,
                      height: 60,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(10),
                        color: Colors.grey.withOpacity(0.5),
                      ),
                      alignment: Alignment.center,
                      child: const Text(
                        '시작하기',
                        style: TextStyle(fontSize: 20, color: Colors.white),
                      ),
                    ),
                  ),
                ),
              ),
              Flexible(
                flex: 2,
                child: Container(),
              )
            ],
          ),
        ],
      ),
    );
  }
}
