import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:knocknock/screens/home_screen.dart';
import 'package:knocknock/screens/view_greenproducts.dart';
import 'package:knocknock/widgets/app_bar_back.dart';

class GreenDetailInfo extends StatefulWidget {
  final Map<String, dynamic> product;
  final List<Map<String, dynamic>> info;
  final String keyword;
  const GreenDetailInfo(
      {super.key,
      required this.product,
      required this.info,
      required this.keyword});

  @override
  State<GreenDetailInfo> createState() => _GreenDetailInfoState();
}

class _GreenDetailInfoState extends State<GreenDetailInfo> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBarBack(
        title: '녹색 제품 상세 정보',
        page: ViewGreenProducts(info: widget.info, keyword: widget.keyword),
      ),
      body: Column(
        children: [
          const SizedBox(
            height: 20,
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Container(
                alignment: Alignment.center,
                width: 100,
                height: 100,
                decoration: const BoxDecoration(
                  image: DecorationImage(
                    image: AssetImage('assets/images/green.png'),
                    fit: BoxFit.cover,
                  ),
                ),
              ),
              const Text(''),
            ],
          ),
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 5, horizontal: 30),
            child: Text(
              '「환경기술 및 환경산업 지원법」 제 17조 제 1항에 따라 같은 용도의 다른 제품에 비해 제품의 환경성을 개선한 제품',
              textAlign: TextAlign.justify,
              style: TextStyle(
                color: Colors.green[900],
              ),
            ),
          ),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 10),
                child: Text(
                  '모델명: ${widget.product['prodMdel']}',
                  style: const TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                  vertical: 10,
                ),
                child: Text(
                  '업체명: ${widget.product['prodVcnm']}',
                  style: const TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                  vertical: 10,
                ),
                child: Text(
                  '인증 발급: ${widget.product['prodRsdt']}',
                  style: const TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                  vertical: 10,
                ),
                child: Text(
                  '인증 만료: ${widget.product['prodRedt']}',
                  style: const TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                  vertical: 10,
                ),
                child: Text(
                  '인증 사유: ${widget.product['prodInrs']}',
                  style: const TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ],
          )
        ],
      ),
    );
  }
}
