import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:knocknock/screens/home_screen.dart';
import 'package:knocknock/screens/my_info_modify.dart';
import 'package:knocknock/services/outer_service.dart' hide storage;
import 'package:knocknock/widgets/app_bar_back.dart';

class AverageElectronic extends StatefulWidget {
  const AverageElectronic({Key? key}) : super(key: key);

  @override
  State<AverageElectronic> createState() => _AverageElectronicState();
}

class _AverageElectronicState extends State<AverageElectronic> {
  OuterService outerService = OuterService();
  String address1 = '';
  String address2 = '';
  List<Map<String, dynamic>> averageElectronic = [];
  List<bool> isSelected = [true, false]; // 토글 상태를 저장하는 리스트

  @override
  void initState() {
    super.initState();
    initializeAddresses();
  }

  initializeAverageElectronic() async {
    await storage.read(key: "address");
    final now = DateTime.now();
    final year = now.year;
    final month = now.month;

    print(month);
    print(month);
    print(month);
    print(month);
    print(month);
    print(month);

    final response = await outerService.averageElectronic(
        address1, address2, year, month - 1);
    setState(() {
      averageElectronic = response;
    });
  }

  initializeAddresses() async {
    final fullAddress = await storage.read(key: "address");
    final addressParts = fullAddress!.split(' ');
    if (addressParts.length >= 2) {
      setState(() {
        address1 = addressParts[0];
        address2 = addressParts[1];
      });
    }
    initializeAverageElectronic();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const AppBarBack(
        title: "전력 소비량",
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
          ToggleButtons(
            isSelected: isSelected,
            borderRadius: const BorderRadius.all(Radius.circular(4)),
            selectedBorderColor: Theme.of(context).colorScheme.primaryContainer,
            selectedColor: Theme.of(context).colorScheme.onPrimaryContainer,
            fillColor: Theme.of(context).colorScheme.primaryContainer,
            color: Theme.of(context).colorScheme.secondary,
            constraints: const BoxConstraints(
              minHeight: 40.0,
              minWidth: 120.0,
            ),
            onPressed: (int index) {
              setState(() {
                for (int buttonIndex = 0;
                    buttonIndex < isSelected.length;
                    buttonIndex++) {
                  if (buttonIndex == index) {
                    isSelected[buttonIndex] = true;
                  } else {
                    isSelected[buttonIndex] = false;
                  }
                }
              });
            },
            children: const [
              Text(
                '사용 전력',
              ),
              Text(
                '비용',
              ),
            ],
          ),
          const SizedBox(
            height: 50,
          ),
          Flexible(
            flex: 4,
            child: averageElectronic.isNotEmpty
                ? Container(
                    decoration: BoxDecoration(
                      color: Theme.of(context).colorScheme.surfaceVariant,
                      borderRadius: BorderRadius.circular(20),
                    ),
                    padding: const EdgeInsets.fromLTRB(20, 40, 10, 20),
                    height: 500,
                    width: MediaQuery.sizeOf(context).width * 0.85,
                    child: BarChart(
                      swapAnimationDuration: const Duration(milliseconds: 500),
                      swapAnimationCurve: Curves.fastOutSlowIn,
                      BarChartData(
                        minY: 0,
                        maxY: isSelected[0] ? 300 : 50000, // 선택된 버튼에 따라 최대 값 조정
                        gridData: const FlGridData(show: true),
                        borderData: FlBorderData(
                          show: true,
                          border: Border(
                            left: BorderSide.none, // 좌측 선을 숨김
                            top: BorderSide.none, // 상단 선을 숨김
                            right: BorderSide(
                                width: 0.8,
                                color: Theme.of(context)
                                    .colorScheme
                                    .onSurfaceVariant), // 우측 선을 설정
                            bottom: BorderSide(
                                width: 0.8,
                                color: Theme.of(context)
                                    .colorScheme
                                    .onSurfaceVariant), //  // 하단 선을 설정
                          ),
                        ),
                        // backgroundColor:
                        //     Theme.of(context).colorScheme.surfaceVariant,
                        titlesData: FlTitlesData(
                          leftTitles: const AxisTitles(
                            sideTitles: SideTitles(
                              showTitles: false,
                              reservedSize: 40,
                            ),
                          ),
                          rightTitles: AxisTitles(
                            sideTitles: SideTitles(
                              showTitles: true, // 선택된 버튼에 따라 레이블 표시 여부 설정
                              reservedSize: 40,
                              getTitlesWidget: isSelected[1]
                                  ? (value, context) {
                                      return Padding(
                                        padding:
                                            const EdgeInsets.only(left: 10),
                                        child: Text("${value / 10000}"),
                                      );
                                    }
                                  : (value, context) {
                                      return Padding(
                                        padding:
                                            const EdgeInsets.only(left: 10),
                                        child: Text("${value.toInt()}"),
                                      );
                                    },
                            ),
                          ),
                          bottomTitles: AxisTitles(
                            sideTitles: SideTitles(
                                showTitles: true,
                                getTitlesWidget: (value, context) {
                                  return Text("${value.toInt()}월");
                                }),
                          ),
                          topTitles: const AxisTitles(
                            sideTitles: SideTitles(
                              showTitles: false,
                            ),
                          ),
                        ),
                        barGroups: [
                          BarChartGroupData(
                            x: DateTime.now().month - 5,
                            barRods: [
                              BarChartRodData(
                                fromY: 0,
                                toY: isSelected[0]
                                    ? averageElectronic[0]['powerUsage']
                                    : averageElectronic[0]['bill'].toDouble(),
                                color: Colors.blue,
                                width: 20,
                                borderRadius: const BorderRadius.only(
                                  topLeft: Radius.circular(6),
                                  topRight: Radius.circular(6),
                                ),
                              ),
                            ],
                          ),
                          BarChartGroupData(
                            x: DateTime.now().month - 4,
                            barRods: [
                              BarChartRodData(
                                toY: isSelected[0]
                                    ? averageElectronic[1]['powerUsage']
                                    : averageElectronic[1]['bill'].toDouble(),
                                color: Colors.green,
                                width: 20,
                                borderRadius: const BorderRadius.only(
                                  topLeft: Radius.circular(6),
                                  topRight: Radius.circular(6),
                                ),
                              ),
                            ],
                          ),
                          BarChartGroupData(
                            x: DateTime.now().month - 3,
                            barRods: [
                              BarChartRodData(
                                toY: isSelected[0]
                                    ? averageElectronic[2]['powerUsage']
                                    : averageElectronic[2]['bill'].toDouble(),
                                color: Colors.orange,
                                width: 20,
                                borderRadius: const BorderRadius.only(
                                  topLeft: Radius.circular(6),
                                  topRight: Radius.circular(6),
                                ),
                              ),
                            ],
                          ),
                          BarChartGroupData(
                            x: DateTime.now().month - 2,
                            barRods: [
                              BarChartRodData(
                                toY: isSelected[0]
                                    ? averageElectronic[3]['powerUsage']
                                    : averageElectronic[3]['bill'].toDouble(),
                                color: Colors.red,
                                width: 20,
                                borderRadius: const BorderRadius.only(
                                  topLeft: Radius.circular(6),
                                  topRight: Radius.circular(6),
                                ),
                              ),
                            ],
                          ),
                          BarChartGroupData(
                            x: DateTime.now().month - 1,
                            barRods: [
                              BarChartRodData(
                                toY: isSelected[0]
                                    ? averageElectronic[4]['powerUsage']
                                    : averageElectronic[4]['bill'].toDouble(),
                                color: Colors.yellow,
                                width: 20,
                                borderRadius: const BorderRadius.only(
                                  topLeft: Radius.circular(6),
                                  topRight: Radius.circular(6),
                                ),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                  )
                : const Center(
                    child: CircularProgressIndicator(),
                  ),
          ),
          const SizedBox(
            height: 20,
          ),
          isSelected[0] ? const Text("단위 : [kWh]") : const Text("단위 : [원]"),
        ],
      ),
    );
  }
}
