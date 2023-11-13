import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:knocknock/screens/home_screen.dart';
import 'package:knocknock/screens/my_info_modify.dart';
import 'package:knocknock/widgets/app_bar_back.dart';

class AverageElectronic extends StatefulWidget {
  const AverageElectronic({super.key});

  @override
  State<AverageElectronic> createState() => _AverageElectronicState();
}

class _AverageElectronicState extends State<AverageElectronic> {
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const AppBarBack(
        title: '전력 소비량',
        page: HomeScreen(),
      ),
      body: Column(
        children: [
          Flexible(
            flex: 1,
            child: Container(
              alignment: Alignment.center,
              child: Text(
                "$address1 $address2의\n가구 평균 전력 소비량",
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
