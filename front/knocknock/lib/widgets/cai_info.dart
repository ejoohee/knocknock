import 'package:flutter/material.dart';

class CAI extends StatelessWidget {
  const CAI({super.key});

  @override
  Widget build(BuildContext context) {
    return SimpleDialog(
      title: const Text('통합대기환경지수(CAI) 란?'),
      children: <Widget>[
        const Padding(
          padding: EdgeInsets.symmetric(horizontal: 25.0, vertical: 10.0),
          child: Text(
              '대기오염도 측정치를 국민이 알기 쉽게 수치와 색상으로 나타낸 것으로, 대기오염도에 따른 인체 위해성과 체감오염도를 고려하여 개발된 대기오염도 표현방식입니다.'),
        ),
        SimpleDialogOption(
          onPressed: () {},
          child: Row(
            children: [
              Image.asset(
                'assets/images/good.png',
                width: 30,
              ),
              const Text('  :  좋음(0 ~ 50)'),
            ],
          ),
        ),
        SimpleDialogOption(
          onPressed: () {},
          child: Row(
            children: [
              Image.asset(
                'assets/images/soso.png',
                width: 30,
              ),
              const Text('  :  보통(51~100)'),
            ],
          ),
        ),
        SimpleDialogOption(
          onPressed: () {},
          child: Row(
            children: [
              Image.asset(
                'assets/images/bad.png',
                width: 30,
              ),
              const Text('  :  나쁨(101~250)'),
            ],
          ),
        ),
        SimpleDialogOption(
          onPressed: () {},
          child: Row(
            children: [
              Image.asset(
                'assets/images/verybad.png',
                width: 30,
              ),
              const Text('  :  매우나쁨(251~500)'),
            ],
          ),
        ),
      ],
    );
  }
}
