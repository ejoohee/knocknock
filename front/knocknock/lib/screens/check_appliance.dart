import 'package:flutter/material.dart';
import 'package:knocknock/components/buttons.dart';

class CheckAppliance extends StatefulWidget {
  const CheckAppliance({super.key});

  @override
  State<CheckAppliance> createState() => _CheckApplianceState();
}

class _CheckApplianceState extends State<CheckAppliance> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.all(
          30,
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text(
              '등록된 정보가 맞는지 확인해주세요',
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.w700,
              ),
            ),
            // Image(image: ),
            Expanded(
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 30.0),
                child: ListView(
                  children: const [
                    ListTile(
                      leading: Text(
                        "제품군",
                      ),
                      title: Text(
                        "세탁기",
                        style: TextStyle(
                          fontWeight: FontWeight.w700,
                        ),
                        textAlign: TextAlign.end,
                      ),
                    ),
                    Divider(),
                    ListTile(
                      leading: Text(
                        "모델명",
                      ),
                      title: Text(
                        "AB-1234",
                        style: TextStyle(
                          fontWeight: FontWeight.w700,
                        ),
                        textAlign: TextAlign.end,
                      ),
                    ),
                    Divider(),
                    ListTile(
                      leading: Text(
                        "업체명",
                      ),
                      title: Text(
                        "(주)삼성전자",
                        style: TextStyle(
                          fontWeight: FontWeight.w700,
                        ),
                        textAlign: TextAlign.end,
                      ),
                    ),
                    Divider(),
                  ],
                ),
              ),
            ),
            KnockButton(
              onPressed: () {
                // 버튼 클릭 시 실행할 동작
              },
              bColor: Theme.of(context).colorScheme.primary,
              fColor: Theme.of(context).colorScheme.onPrimary,
              width: MediaQuery.of(context).size.width * 0.8, // 버튼의 너비
              height: MediaQuery.of(context).size.width * 0.16, // 버튼의 높이
              label: "내 가전으로 등록하기", // 버튼에 표시할 텍스트
            ),
            const SizedBox(
              height: 10,
            ),
            KnockButton(
              onPressed: () {
                // 버튼 클릭 시 실행할 동작
              },
              bColor: Theme.of(context).colorScheme.secondaryContainer,
              fColor: Theme.of(context).colorScheme.onSecondaryContainer,
              width: MediaQuery.of(context).size.width * 0.8, // 버튼의 너비
              height: MediaQuery.of(context).size.width * 0.16, // 버튼의 높이
              label: "다시 찍기 📸", // 버튼에 표시할 텍스트
            )
          ],
        ),
      ),
    );
  }
}
