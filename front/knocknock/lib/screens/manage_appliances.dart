import 'package:flutter/material.dart';
import 'package:camera/camera.dart';
import 'package:knocknock/screens/new_appliance_categories.dart';
import 'package:knocknock/screens/take_picture_screen.dart';

class ManageAppliances extends StatefulWidget {
  const ManageAppliances({super.key});

  @override
  State<ManageAppliances> createState() => _ManageAppliancesState();
}

class _ManageAppliancesState extends State<ManageAppliances> {
  Future<void> toRegisterMine() async {
    WidgetsFlutterBinding.ensureInitialized();
    // Obtain a list of the available cameras on the device.
    final cameras = await availableCameras();
    // Get a specific camera from the list of available cameras.
    final firstCamera = cameras.first;
    if (!mounted) return;
    Navigator.push(
      context,
      MaterialPageRoute(
          builder: (context) => TakePictureScreen(
              camera: firstCamera)), // SignUpPage는 회원가입 페이지 위젯입니다.
    );
  }

  void toNewApplianceCategories() {
    Navigator.push(
      context,
      MaterialPageRoute(
          builder: (context) =>
              const NewApplianceCategories()), // SignUpPage는 회원가입 페이지 위젯입니다.
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
              child: Container(
                width: MediaQuery.of(context).size.width * 0.6,
                height: MediaQuery.of(context).size.height * 0.25,
                decoration: BoxDecoration(
                  color: Theme.of(context).colorScheme.primary,
                  borderRadius: BorderRadius.circular(20.0),
                ),
                child: Center(
                  child: Text(
                    '새로운\n가전 찾기',
                    style: TextStyle(
                      fontWeight: FontWeight.w700,
                      fontSize: 30,
                      color: Theme.of(context).colorScheme.onPrimary,
                    ),
                    textAlign: TextAlign.center,
                  ),
                ),
              ),
            ),
            const SizedBox(
              height: 80,
            ),
            GestureDetector(
              onTap: toRegisterMine,
              child: Container(
                width: MediaQuery.of(context).size.width * 0.6,
                height: MediaQuery.of(context).size.height * 0.25,
                decoration: BoxDecoration(
                  color: Theme.of(context).colorScheme.primary,
                  borderRadius: BorderRadius.circular(20.0),
                ),
                child: Center(
                  child: Text(
                    '내 가전\n등록하기',
                    style: TextStyle(
                      fontWeight: FontWeight.w700,
                      fontSize: 30,
                      color: Theme.of(context).colorScheme.onPrimary,
                    ),
                    textAlign: TextAlign.center,
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
