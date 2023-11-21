import 'package:animated_text_kit/animated_text_kit.dart';
import 'package:flutter/material.dart';
import 'package:flutter_inner_shadow/flutter_inner_shadow.dart';

class NoTree extends StatelessWidget {
  const NoTree({super.key});

  @override
  Widget build(BuildContext context) {
    return Dialog(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(8),
        ),
        child: Container(
          padding: const EdgeInsets.symmetric(
            vertical: 15,
            horizontal: 10,
          ),
          height: MediaQuery.sizeOf(context).height * 0.4,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              Image.asset(
                'assets/images/emoji_thinking.png',
                width: 180,
              ),
              SizedBox(
                height: 30,
                child: AnimatedTextKit(
                  totalRepeatCount: 3,
                  pause: const Duration(milliseconds: 100),
                  animatedTexts: [
                    FlickerAnimatedText(
                      '지금 제품이 더 나을지도..',
                      textStyle: const TextStyle(
                        fontSize: 18,
                      ),
                    ),
                    FlickerAnimatedText(
                      '다른 제품을 고려해보세요!',
                      textStyle: const TextStyle(
                        fontSize: 18,
                      ),
                    ),
                  ],
                  onTap: () {
                    print("Tap Event");
                  },
                  onFinished: () {
                    Navigator.of(context).pop(); // 다이얼로그 닫기
                  },
                ),
              ),
            ],
          ),
        ));
  }
}
