import 'package:flutter/material.dart';
import 'package:knocknock/screens/green_detail_info.dart';
import 'package:knocknock/screens/home_screen.dart';
import 'package:knocknock/services/user_service.dart';
import 'package:knocknock/widgets/app_bar_back.dart';

class ViewGreenProducts extends StatefulWidget {
  final List<Map<String, dynamic>> info;
  final String keyword;
  const ViewGreenProducts(
      {super.key, required this.info, required this.keyword});

  @override
  State<ViewGreenProducts> createState() => _ViewGreenProductsState();
}

class _ViewGreenProductsState extends State<ViewGreenProducts> {
  UserService userService = UserService();
  late TextEditingController keywordController;
  List<Map<String, dynamic>> searchResult = [];

  @override
  void initState() {
    super.initState();
    keywordController = TextEditingController(text: widget.keyword);
    searchResult = widget.info;
  }

  Future onSerchButtonTap() async {
    var response = await userService.checkGreenLabel(keywordController.text);
    setState(() {
      searchResult = response;
    });
  }

  Future onTrailingTap(Map<String, dynamic> detail) async {
    if (!mounted) return;
    Navigator.pushReplacement(
      context,
      MaterialPageRoute(
        builder: (context) => GreenDetailInfo(
          product: detail,
          info: searchResult,
          keyword: keywordController.text,
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBarBack(
        title: '녹색 제품 정보',
      ),
      body: Column(
        children: [
          Container(
            padding: const EdgeInsets.symmetric(
              horizontal: 40,
              vertical: 10,
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
                  borderSide:
                      BorderSide(color: Theme.of(context).colorScheme.primary),
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
          Expanded(
            child: ListView.builder(
              itemCount: searchResult.length,
              itemBuilder: (BuildContext context, int index) {
                var product = searchResult[index];
                return ExpansionTile(
                  leading: Image.asset("assets/images/green.png"),
                  title: Text("제품명: ${product['prodMdel']}"),
                  subtitle: Text("업체명: ${product['prodVcnm']}"),
                  children: <Widget>[
                    Padding(
                      padding: const EdgeInsets.symmetric(
                          vertical: 5, horizontal: 30),
                      child: Text(
                        '「환경기술 및 환경산업 지원법」 제 17조 제 1항에 따라 같은 용도의 다른 제품에 비해 제품의 환경성을 개선한 제품',
                        textAlign: TextAlign.justify,
                        style: TextStyle(
                          color: Colors.green[900],
                        ),
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 20),
                      child: ListTile(
                          leading: const Text('ㆍ모델명',
                              style: TextStyle(
                                fontSize: 13,
                              )),
                          title: Text(
                            '${product['prodMdel']}',
                            textAlign: TextAlign.end,
                            style: const TextStyle(fontWeight: FontWeight.w700),
                          )),
                    ),
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 20),
                      child: ListTile(
                          leading: const Text(
                            'ㆍ업체명',
                            style: TextStyle(
                              fontSize: 13,
                            ),
                          ),
                          title: Text(
                            '${product['prodVcnm']}',
                            textAlign: TextAlign.end,
                            style: const TextStyle(fontWeight: FontWeight.w700),
                          )),
                    ),
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 20),
                      child: ListTile(
                          leading: const Text(
                            'ㆍ인증 발급',
                            style: TextStyle(
                              fontSize: 13,
                            ),
                          ),
                          title: Text(
                            '${product['prodRsdt']}',
                            textAlign: TextAlign.end,
                            style: const TextStyle(fontWeight: FontWeight.w700),
                          )),
                    ),
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 20),
                      child: ListTile(
                          leading: const Text(
                            'ㆍ인증 만료',
                            style: TextStyle(
                              fontSize: 13,
                            ),
                          ),
                          title: Text(
                            '${product['prodRedt']}',
                            textAlign: TextAlign.end,
                            style: const TextStyle(fontWeight: FontWeight.w700),
                          )),
                    ),
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 20),
                      child: ListTile(
                          leading: const Text(
                            'ㆍ인증 사유',
                            style: TextStyle(
                              fontSize: 13,
                            ),
                          ),
                          title: Text(
                            '${product['prodInrs']}',
                            textAlign: TextAlign.end,
                            style: const TextStyle(fontWeight: FontWeight.w700),
                          )),
                    ),
                  ],
                );
              },
            ),
          )
        ],
      ),
    );
  }
}
