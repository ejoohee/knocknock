import 'package:flutter/material.dart';

class NewApplianceCategories extends StatefulWidget {
  const NewApplianceCategories({super.key});

  @override
  State<NewApplianceCategories> createState() => _NewApplianceCategoriesState();
}

class _NewApplianceCategoriesState extends State<NewApplianceCategories> {
  List<String> categories = [
    'TV',
    '공기청정기',
    '냉장고',
    '김치냉장고',
    '세탁기(드럼)',
    '세탁기(일반)',
    '에어컨',
    '의류건조기',
    '전기레인지',
    '전기밥솥',
    '전기스토브',
    '전기온풍기',
    '정수기',
    '제습기',
    '진공청소기',
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          const SizedBox(
            height: 10,
          ),
          const Flexible(
            fit: FlexFit.tight,
            flex: 1,
            child: SafeArea(
              child: Center(
                child: Text(
                  '구매를 고려 중인\n가전의 종류를 골라주세요.',
                  style: TextStyle(
                    fontSize: 26,
                    fontWeight: FontWeight.w700,
                  ),
                  textAlign: TextAlign.center,
                ),
              ),
            ),
          ),
          Flexible(
            fit: FlexFit.tight,
            flex: 3,
            child: Container(
              padding: const EdgeInsets.symmetric(
                // vertical: 10,
                horizontal: 40,
              ),
              child: ShaderMask(
                shaderCallback: (Rect bounds) {
                  return LinearGradient(
                    //아래 속성들을 조절하여 원하는 값을 얻을 수 있다.
                    begin: Alignment.center,
                    end: Alignment.topCenter,
                    colors: [Colors.white, Colors.white.withOpacity(0.02)],
                    stops: const [0.9, 1],
                    tileMode: TileMode.mirror,
                  ).createShader(bounds);
                },
                child: GridView.count(
                  childAspectRatio: 0.75,
                  crossAxisCount: 3,
                  children: List.generate(15, (index) {
                    return Column(
                      children: [
                        InkWell(
                          child: Container(
                            width: MediaQuery.of(context).size.width * 0.23,
                            height: MediaQuery.of(context).size.width * 0.23,
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(20),
                              color: Theme.of(context)
                                  .colorScheme
                                  .surfaceVariant
                                  .withOpacity(0.4),
                            ),
                            child: Center(
                              child: Image.asset(
                                'assets/icons/${categories[index]}.png',
                                scale: 1.1,
                              ),
                            ),
                          ),
                        ),
                        Text(
                          categories[index],
                        ),
                        // const SizedBox(
                        //   height: 10,
                        // ),
                      ],
                    );
                  }),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
