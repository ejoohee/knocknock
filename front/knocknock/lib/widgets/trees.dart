import 'package:animated_text_kit/animated_text_kit.dart';
import 'package:flutter/material.dart';
import 'dart:math';

List<Offset> existingTrees = [];

class Trees extends StatelessWidget {
  final int treeCnt;
  const Trees({
    super.key,
    required this.treeCnt,
  });

  @override
  Widget build(BuildContext context) {
    existingTrees.clear(); // 다이얼로그가 열릴 때마다 초기화
    double dialogWidth = MediaQuery.of(context).size.width / 2;
    double dialogHeight = MediaQuery.of(context).size.height / 2;
    return Dialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(8),
      ),
      child: SizedBox(
        width: dialogWidth,
        height: dialogHeight,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Flexible(
              flex: 1,
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Column(
                      mainAxisAlignment: MainAxisAlignment.end,
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        AnimatedTextKit(
                          totalRepeatCount: 3,
                          pause: const Duration(milliseconds: 100),
                          animatedTexts: [
                            FlickerAnimatedText(
                              '1년이면',
                              textStyle: const TextStyle(
                                fontSize: 20,
                                fontWeight: FontWeight.w700,
                              ),
                            ),
                            ...List.generate(treeCnt <= 10 ? treeCnt : 10,
                                (index) {
                              return RotateAnimatedText(
                                '${index + 1}',
                                textStyle: TextStyle(
                                  fontSize: 40,
                                  fontWeight: FontWeight.w700,
                                  color: Theme.of(context).colorScheme.primary,
                                ),
                                duration: const Duration(milliseconds: 100),
                                transitionHeight: 47,
                                rotateOut: false,
                              );
                            }),
                            FlickerAnimatedText(
                              '$treeCnt',
                              textStyle: const TextStyle(
                                fontSize: 40,
                                fontWeight: FontWeight.w500,
                              ),
                            ),
                            FlickerAnimatedText(
                              "$treeCnt 그루의 나무를\n심을 수 있어요!",
                              textStyle: const TextStyle(
                                fontSize: 20,
                                fontWeight: FontWeight.w500,
                              ),
                              speed: const Duration(milliseconds: 2000),
                            ),
                          ],
                          onTap: () {
                            print("Tap Event");
                          },
                          onFinished: () async {
                            await Future.delayed(const Duration(seconds: 1));
                            Navigator.of(context).pop(); // 다이얼로그 닫기
                          },
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
            Flexible(
              flex: treeCnt > 5 ? 3 : 2,
              fit: FlexFit.tight,
              child: Stack(
                alignment: Alignment.center,
                children: List.generate(
                  treeCnt <= 10 ? treeCnt : 10,
                  (index) {
                    double x, y;
                    double wholeX = MediaQuery.sizeOf(context).width;
                    double wholeY = MediaQuery.sizeOf(context).height;
                    double xInit = wholeX / 13;
                    double yInit = wholeY / 13;

                    x = xInit +
                        wholeX / 11 * (index % 5) +
                        (index ~/ 5 * wholeX / 21);
                    y = yInit + (index ~/ 5) * (wholeX * 0.18);

                    existingTrees.add(Offset(x, y));

                    return Positioned(
                      left: x,
                      top: y,
                      child: Image.asset(
                        'assets/images/waving_tree.gif',
                        width: wholeX / 7,
                      ),
                    );
                  },
                ),
              ),
            ),
            treeCnt > 10
                ? Padding(
                    padding: const EdgeInsets.only(bottom: 15.0),
                    child: AnimatedTextKit(
                      repeatForever: true,
                      animatedTexts: [
                        TyperAnimatedText(
                          '· · ·',
                          textStyle: const TextStyle(
                            fontSize: 30,
                            fontWeight: FontWeight.w700,
                          ),
                          speed: const Duration(milliseconds: 300),
                        )
                      ],
                    ),
                  )
                : Container(),
          ],
        ),
      ),
    );
  }
}
