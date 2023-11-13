import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:knocknock/interceptors/interceptor.dart';
import 'package:knocknock/screens/home_screen.dart';
import 'package:knocknock/services/outer_service.dart' hide storage;
import 'package:knocknock/widgets/app_bar_back.dart';

class WasteInfo extends StatefulWidget {
  const WasteInfo({super.key});

  @override
  State<WasteInfo> createState() => _WasteInfoState();
}

class _WasteInfoState extends State<WasteInfo> {
  OuterService outerService = OuterService();
  String address1 = '';
  String address2 = '';
  List<Map<String, dynamic>> wasteCompanyList = [];
  @override
  void initState() {
    super.initState();
    initializeAddresses();
  }

  void initializeAddresses() async {
    final fullAddress = await storage.read(key: "address");
    final addressParts = fullAddress!.split(' ');
    if (addressParts.length >= 2) {
      setState(
        () {
          address1 = addressParts[0];
          address2 = addressParts[1];
        },
      );
    }
    searchWasteCompany();
  }

  Future searchWasteCompany() async {
    var response = await outerService.getWasteCompanyList(address1, address2);
    setState(() {
      wasteCompanyList = response;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const AppBarBack(
        title: '폐기물 수거 업체',
        page: HomeScreen(),
      ),
      body: Column(
        children: [
          Flexible(
            flex: 1,
            child: Container(
              alignment: Alignment.center,
              child: Text(
                "$address1 $address2의\n 폐기물 수거 업체",
                style: const TextStyle(
                  fontSize: 25,
                  fontWeight: FontWeight.bold,
                ),
                textAlign: TextAlign.center,
              ),
            ),
          ),
          Flexible(flex: 4, child: Container()),
        ],
      ),
    );
  }
}
