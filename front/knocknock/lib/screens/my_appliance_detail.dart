import 'package:flutter/material.dart';
import 'package:flutter_inner_shadow/flutter_inner_shadow.dart';
import 'package:knocknock/components/buttons.dart';
import 'package:knocknock/constants/color_chart.dart';

class MyApplianceDetail extends StatefulWidget {
  const MyApplianceDetail({super.key});

  @override
  State<MyApplianceDetail> createState() => _MyApplianceDetailState();
}

class _MyApplianceDetailState extends State<MyApplianceDetail> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(
            vertical: 30,
            horizontal: 30,
          ),
          child: Column(
            children: [
              Stack(
                children: [
                  Center(
                    child: Column(
                      children: [
                        const Text(
                          "닉네임",
                          style: TextStyle(
                            fontSize: 25, // 아이콘은 약 1.5배하자
                            fontWeight: FontWeight.w700,
                          ),
                        ),
                        Divider(
                          indent: 120,
                          endIndent: 120,
                          color: Theme.of(context).colorScheme.outlineVariant,
                          thickness: 2,
                        ),
                      ],
                    ),
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      IconButton(
                        onPressed: () {},
                        icon: const Icon(
                          Icons.bookmark_outline_rounded,
                          size: 36,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
              InnerShadow(
                shadows: [
                  Shadow(
                      color: Colors.black.withOpacity(0.4),
                      blurRadius: 8,
                      offset: const Offset(0, 0))
                ],
                child: Container(
                  margin: const EdgeInsets.symmetric(
                    vertical: 10,
                  ),
                  height: 210,
                  decoration: const BoxDecoration(
                    borderRadius: BorderRadius.all(Radius.circular(10)),
                    color: ColorChart.first,
                  ),
                ),
              ),
              Expanded(
                child: Padding(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 20.0,
                    vertical: 10,
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
                    child: ListView(
                      children: const [
                        ListTile(
                          leading: Text(
                            "분류",
                          ),
                          title: Text(
                            "value",
                            style: TextStyle(
                              fontWeight: FontWeight.w700,
                            ),
                            textAlign: TextAlign.end,
                          ),
                          trailing: Text(
                            "unit",
                          ),
                        ),
                        Divider(),
                        ListTile(
                          leading: Text(
                            "분류",
                          ),
                          title: Text(
                            "value",
                            style: TextStyle(
                              fontWeight: FontWeight.w700,
                            ),
                            textAlign: TextAlign.end,
                          ),
                          trailing: Text(
                            "unit",
                          ),
                        ),
                        Divider(),
                        ListTile(
                          leading: Text(
                            "분류",
                          ),
                          title: Text(
                            "value",
                            style: TextStyle(
                              fontWeight: FontWeight.w700,
                            ),
                            textAlign: TextAlign.end,
                          ),
                          trailing: Text(
                            "unit",
                          ),
                        ),
                        Divider(),
                        ListTile(
                          leading: Text(
                            "분류",
                          ),
                          title: Text(
                            "value",
                            style: TextStyle(
                              fontWeight: FontWeight.w700,
                            ),
                            textAlign: TextAlign.end,
                          ),
                          trailing: Text(
                            "unit",
                          ),
                        ),
                        Divider(),
                        ListTile(
                          leading: Text(
                            "분류",
                          ),
                          title: Text(
                            "value",
                            style: TextStyle(
                              fontWeight: FontWeight.w700,
                            ),
                            textAlign: TextAlign.end,
                          ),
                          trailing: Text(
                            "unit",
                          ),
                        ),
                        Divider(),
                        ListTile(
                          leading: Text(
                            "분류",
                          ),
                          title: Text(
                            "value",
                            style: TextStyle(
                              fontWeight: FontWeight.w700,
                            ),
                            textAlign: TextAlign.end,
                          ),
                          trailing: Text(
                            "unit",
                          ),
                        ),
                        Divider(),
                        ListTile(
                          leading: Text(
                            "분류",
                          ),
                          title: Text(
                            "value",
                            style: TextStyle(
                              fontWeight: FontWeight.w700,
                            ),
                            textAlign: TextAlign.end,
                          ),
                          trailing: Text(
                            "unit",
                          ),
                        ),
                        Divider(),
                        ListTile(
                          leading: Text(
                            "분류",
                          ),
                          title: Text(
                            "value",
                            style: TextStyle(
                              fontWeight: FontWeight.w700,
                            ),
                            textAlign: TextAlign.end,
                          ),
                          trailing: Text(
                            "unit",
                          ),
                        ),
                        Divider(),
                      ],
                    ),
                  ),
                ),
              ),
              KnockButton(
                onPressed: () {
                  // 버튼 클릭 시 실행할 동작
                },
                bColor: Theme.of(context).colorScheme.secondaryContainer,
                fColor: Theme.of(context).colorScheme.onSecondaryContainer,
                width: MediaQuery.of(context).size.width * 0.8, // 버튼의 너비
                height: MediaQuery.of(context).size.width * 0.16, // 버튼의 높이
                label: "새로운 가전 구경하러 가기", // 버튼에 표시할 텍스트
              ),
              const SizedBox(
                height: 10,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
