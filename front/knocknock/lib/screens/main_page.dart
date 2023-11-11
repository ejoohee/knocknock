import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:knocknock/interceptors/interceptor.dart';
import 'package:knocknock/screens/manage_appliances.dart';
import 'package:knocknock/screens/view_greenproducts.dart';
import 'package:knocknock/services/user_service.dart' hide storage;
import 'package:provider/provider.dart';

class MainPage extends StatefulWidget {
  const MainPage({super.key});

  @override
  State<MainPage> createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  final UserService userService = UserService();
  final keywordController = TextEditingController();
  String address1 = '';
  String address2 = '';

  @override
  void initState() {
    super.initState();
    initializeAddresses();
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
      body: SingleChildScrollView(
        scrollDirection: Axis.vertical,
        child: Column(
          children: [
            Column(
              children: [
                Container(
                  height: MediaQuery.of(context).size.height * 0.15,
                  alignment: Alignment.bottomCenter,
                  child: Text(
                    '$address2의 공기질',
                    style: const TextStyle(
                        fontSize: 35, fontWeight: FontWeight.w600),
                  ),
                ),
                Container(
                  height: MediaQuery.of(context).size.height * 0.2,
                  alignment: Alignment.topCenter,
                  child: const Text(
                    '통합대기환경지수(CAI, Comprehensive air-quality index)',
                    style: TextStyle(
                      fontSize: 13,
                      color: Colors.grey,
                    ),
                  ),
                ),
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
                style: TextStyle(fontSize: 25, fontWeight: FontWeight.w600),
              ),
            ),
            Container(
              padding: const EdgeInsets.symmetric(
                horizontal: 60,
              ),
              child: TextFormField(
                controller: keywordController,
                decoration: InputDecoration(
                  contentPadding:
                      const EdgeInsets.symmetric(vertical: 10, horizontal: 20),
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
                        color: Theme.of(context).colorScheme.primary),
                    borderRadius: BorderRadius.circular(30),
                  ),
                  enabledBorder: OutlineInputBorder(
                    borderSide: const BorderSide(color: Colors.transparent),
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
    );
  }
}
