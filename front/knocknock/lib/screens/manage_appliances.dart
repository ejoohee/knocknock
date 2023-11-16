import 'package:flutter/material.dart';
import 'package:camera/camera.dart';
import 'package:flutter_inner_shadow/flutter_inner_shadow.dart';
import 'package:knocknock/providers/page_index.dart';
import 'package:knocknock/screens/home_screen.dart';
import 'package:knocknock/screens/new_appliance_categories.dart';
import 'package:knocknock/screens/take_picture_screen.dart';
import 'package:provider/provider.dart';

class ManageAppliances extends StatefulWidget {
  const ManageAppliances({super.key});

  @override
  State<ManageAppliances> createState() => _ManageAppliancesState();
}

class _ManageAppliancesState extends State<ManageAppliances> {
  Future<void> toRegisterMine() async {
    context.read<CurrentPageIndex>().move(2);
    Navigator.push(
      context,
      MaterialPageRoute(
          builder: (context) =>
              const HomeScreen()), // SignUpPage는 회원가입 페이지 위젯입니다.
    );
  }

  void toNewApplianceCategories() {
    context.read<CurrentPageIndex>().move(2);
    Navigator.push(
      context,
      MaterialPageRoute(
          builder: (context) =>
              const HomeScreen()), // SignUpPage는 회원가입 페이지 위젯입니다.
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            GestureDetector(
              onTap: toNewApplianceCategories,
              child: InnerShadow(
                shadows: [
                  Shadow(
                      color: Colors.black.withOpacity(0.4),
                      blurRadius: 15,
                      offset: const Offset(0, 0))
                ],
                child: Container(
                  width: MediaQuery.of(context).size.width * 0.6,
                  height: MediaQuery.of(context).size.height * 0.25,
                  decoration: BoxDecoration(
                    color: Theme.of(context).colorScheme.secondaryContainer,
                    borderRadius: BorderRadius.circular(20.0),
                  ),
                  child: Center(
                    child: Text(
                      '새로운\n가전 찾기',
                      style: TextStyle(
                        fontWeight: FontWeight.w700,
                        fontSize: 30,
                        color:
                            Theme.of(context).colorScheme.onSecondaryContainer,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ),
              ),
            ),
            const SizedBox(
              height: 80,
            ),
            GestureDetector(
              onTap: toRegisterMine,
              child: InnerShadow(
                shadows: [
                  Shadow(
                      color: Colors.black.withOpacity(0.4),
                      blurRadius: 15,
                      offset: const Offset(0, 0))
                ],
                child: Container(
                  width: MediaQuery.of(context).size.width * 0.6,
                  height: MediaQuery.of(context).size.height * 0.25,
                  decoration: BoxDecoration(
                    color: Theme.of(context).colorScheme.secondaryContainer,
                    borderRadius: BorderRadius.circular(20.0),
                  ),
                  child: Center(
                    child: Text(
                      '내 가전\n등록하기',
                      style: TextStyle(
                        fontWeight: FontWeight.w700,
                        fontSize: 30,
                        color:
                            Theme.of(context).colorScheme.onSecondaryContainer,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
