import 'package:flutter/material.dart';
import 'package:knocknock/providers/appliance.dart';
import 'package:knocknock/screens/new_appliance_category_each.dart';
import 'package:knocknock/services/model_service.dart';
import 'package:knocknock/widgets/app_bar_back.dart';
import 'package:provider/provider.dart';

class NewApplianceCategories extends StatefulWidget {
  const NewApplianceCategories({super.key});

  @override
  State<NewApplianceCategories> createState() => _NewApplianceCategoriesState();
}

class _NewApplianceCategoriesState extends State<NewApplianceCategories> {
  late Future<List<String>> categories;
  ModelService modelService = ModelService();

  @override
  void initState() {
    super.initState();
    categories = loadCategories();
  }

  Future<List<String>> loadCategories() async {
    try {
      final categoryList = await modelService.findCategories();
      return categoryList;
    } catch (e) {
      print("Error loading categories: $e");
      return <String>[]; // 빈 리스트를 반환하거나 다른 오류 처리를 수행
    }
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    categories = loadCategories();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBarBack(
        title: '',
        isLeadingNeeded: false,
      ),
      body: Column(
        children: [
          const SizedBox(
            height: 10,
          ),
          const Flexible(
            fit: FlexFit.tight,
            flex: 1,
            child: SafeArea(
              child: Center(
                child: Text(
                  '구매를 고려 중인\n가전의 종류를 골라주세요.',
                  style: TextStyle(
                    fontSize: 26,
                    fontWeight: FontWeight.w700,
                  ),
                  textAlign: TextAlign.center,
                ),
              ),
            ),
          ),
          Flexible(
            fit: FlexFit.tight,
            flex: 4,
            child: Container(
              padding: const EdgeInsets.symmetric(
                // vertical: 10,
                horizontal: 40,
              ),
              child: FutureBuilder(
                future: categories,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    // 데이터가 아직 준비되지 않은 경우에 대한 UI
                    return const Expanded(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          CircularProgressIndicator(),
                        ],
                      ),
                    );
                  } else if (snapshot.hasError) {
                    // 에러 발생 시에 대한 UI
                    return Text('Error: ${snapshot.error}');
                  } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
                    // 데이터가 비어 있는 경우에 대한 UI
                    return const Text('No data available.');
                  } else {
                    // 데이터를 사용하여 ListView.builder 생성
                    List<String> categories = snapshot.data!;
                    return Padding(
                      padding: const EdgeInsets.symmetric(vertical: 10.0),
                      child: GridView.count(
                        childAspectRatio: 0.75,
                        crossAxisCount: 3,
                        children: List.generate(categories.length, (index) {
                          return Column(
                            children: [
                              InkWell(
                                onTap: () {
                                  context
                                      .read<SelectedAppliance>()
                                      .select(categories[index]);
                                  Navigator.push(
                                    context,
                                    MaterialPageRoute(
                                        builder: (context) =>
                                            const NewApplianceCategoryEach()), // SignUpPage는 회원가입 페이지 위젯입니다.
                                  );
                                },
                                borderRadius: BorderRadius.circular(20),
                                child: Container(
                                  width:
                                      MediaQuery.of(context).size.width * 0.22,
                                  height:
                                      MediaQuery.of(context).size.width * 0.22,
                                  decoration: BoxDecoration(
                                    borderRadius: BorderRadius.circular(20),
                                    color: Theme.of(context)
                                        .colorScheme
                                        .surfaceVariant
                                        .withOpacity(0.4),
                                  ),
                                  child: Center(
                                    child: Image.asset(
                                      'assets/icons/${categories[index]}.png',
                                      scale: 1.15,
                                    ),
                                  ),
                                ),
                              ),
                              Text(
                                categories[index],
                              ),
                              // const SizedBox(
                              //   height: 10,
                              // ),
                            ],
                          );
                        }),
                      ),
                    );
                  }
                },
              ),
            ),
          ),
        ],
      ),
    );
  }
}
