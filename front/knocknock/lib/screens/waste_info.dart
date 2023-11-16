import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:knocknock/interceptors/interceptor.dart';
import 'package:knocknock/screens/home_screen.dart';
import 'package:knocknock/services/outer_service.dart' hide storage;
import 'package:knocknock/widgets/app_bar_back.dart';
import 'package:url_launcher/url_launcher.dart';

class WasteInfo extends StatefulWidget {
  const WasteInfo({Key? key}) : super(key: key);

  @override
  State<WasteInfo> createState() => _WasteInfoState();
}

class _WasteInfoState extends State<WasteInfo> {
  GoogleMapController? mapController;
  LatLng location = const LatLng(126.88945179341, 37.4635157852279);
  double locationX = 0;
  double locationY = 0;
  bool isLoading = true;
  OuterService outerService = OuterService();
  String address1 = '';
  String address2 = '';
  List<Map<String, dynamic>> wasteCompanyList = [];

  // ExpansionTile이 열려있는지 여부를 추적
  Map<int, bool> expansionTileOpenState = {};
  Set<Marker> markers = <Marker>{};
  @override
  void initState() {
    super.initState();
    initializeAddresses();
  }

  getLocationInfo(String address, int index) async {
    final result = await outerService.kakaoLocation(address);
    print("여기는 페이지 여기는 페이지 여기는 페이지 여기는 페이지$result");
    if (result['documents'].isNotEmpty) {
      setState(() {
        locationX = double.parse(result['documents'][0]['address']['x']);
        locationY = double.parse(result['documents'][0]['address']['y']);
      });
    } else {
      setState(() {
        locationX = 131.8652101810755;
        locationY = 37.246239419054476;
      });
    }

    if (mapController != null) {
      mapController!.animateCamera(
        CameraUpdate.newLatLng(
          LatLng(locationY, locationX),
        ),
      );
    }

    // ExpansionTile이 열린 상태를 업데이트
    setState(() {
      expansionTileOpenState[index] = true;
    });
  }

  addMarker(double latitude, double longitude, String markerId,
      String markerSnippet) {
    final MarkerId id = MarkerId(markerId);
    final Marker marker = Marker(
      markerId: id,
      position: LatLng(latitude, longitude),
      icon: BitmapDescriptor.defaultMarker, // 마커 아이콘 설정 (기본 마커 아이콘 사용)
      infoWindow:
          InfoWindow(title: markerId, snippet: markerSnippet), // 마커 정보창 설정
    );

    setState(() {
      markers.add(marker); // 마커 추가
    });
  }

  void onMapCreated(GoogleMapController controller) {
    mapController = controller;
  }

  void onCallTap(String callNumber) {
    launchUrl(
      Uri.parse(
        "tel:$callNumber",
      ),
    );
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
    await searchWasteCompany();
  }

  Future searchWasteCompany() async {
    var response = await outerService.getWasteCompanyList(address1, address2);
    setState(() {
      wasteCompanyList = response;
      isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const AppBarBack(
        title: '폐기물 수거 업체',
      ),
      body: Column(
        children: [
          Container(
            height: MediaQuery.of(context).size.height * 0.2,
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
          Expanded(
            child: isLoading
                ? const Center(child: CircularProgressIndicator())
                : ListView.builder(
                    itemCount: wasteCompanyList.length,
                    itemBuilder: (BuildContext context, int index) {
                      return Column(
                        children: [
                          const Divider(),
                          ExpansionTile(
                            tilePadding: const EdgeInsets.symmetric(
                              horizontal: 20,
                              vertical: 5,
                            ),
                            title: Text(
                              "${wasteCompanyList[index]['ENTRPS_NM']}",
                              style: const TextStyle(
                                fontSize: 20,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            subtitle: Text(
                              "${wasteCompanyList[index]['ADRES']}",
                              style: const TextStyle(
                                fontSize: 15,
                              ),
                            ),
                            onExpansionChanged: (expand) async {
                              // ExpansionTile이 처음 열릴 때만 위치 정보를 불러옴
                              if (expand &&
                                  !expansionTileOpenState.containsKey(index)) {
                                print(wasteCompanyList[index]['ADRES']);
                                await getLocationInfo(
                                    wasteCompanyList[index]['ADRES'], index);
                                await addMarker(
                                    locationY,
                                    locationX,
                                    wasteCompanyList[index]['ENTRPS_NM'],
                                    wasteCompanyList[index]['WSTE']);
                              }
                            },
                            children: <Widget>[
                              Padding(
                                padding: const EdgeInsets.symmetric(
                                  vertical: 10,
                                ),
                                child: GestureDetector(
                                  onTap: () {
                                    onCallTap(
                                      wasteCompanyList[index]['TELNO'],
                                    );
                                  },
                                  child: Row(
                                    mainAxisSize: MainAxisSize.min,
                                    children: [
                                      const Icon(Icons.phone),
                                      const SizedBox(width: 5),
                                      Text(
                                        wasteCompanyList[index]['TELNO'],
                                        style: const TextStyle(
                                          fontSize: 15,
                                          fontWeight: FontWeight.bold,
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                              SizedBox(
                                height: 400,
                                child: GoogleMap(
                                  onMapCreated: (controller) {
                                    if (mounted) {
                                      setState(() {
                                        mapController = controller;
                                      });
                                    }
                                  },
                                  initialCameraPosition: CameraPosition(
                                    target: LatLng(locationY, locationX),
                                    zoom: 15.0,
                                  ),
                                  markers: markers,
                                ),
                              ),
                            ],
                          ),
                        ],
                      );
                    },
                  ),
          ),
          const Divider(),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Text(
              '거주지에 폐기물 수거 업체가 없다면 가장 가까운 업체를 추천합니다.',
              style: TextStyle(
                color: Colors.green[900],
              ),
            ),
          ),
          const SizedBox(
            height: 20,
          ),
        ],
      ),
    );
  }
}
