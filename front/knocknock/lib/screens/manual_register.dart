import 'package:flutter/material.dart';

class ManualRegister extends StatefulWidget {
  const ManualRegister({super.key});

  @override
  State<ManualRegister> createState() => _ManualRegisterState();
}

const List<String> list = <String>['냉장고', '세탁기', '어쩌구', '저쩌구'];

class _ManualRegisterState extends State<ManualRegister> {
  String dropdownValue = list.first;
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.symmetric(
          horizontal: 30,
          vertical: 30,
        ),
        child: SingleChildScrollView(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Text(
                '제품의 모델명을\n정확히 입력해주세요',
                style: TextStyle(
                  fontSize: 26,
                  fontWeight: FontWeight.w700,
                ),
                textAlign: TextAlign.center,
              ),
              Container(
                decoration: const BoxDecoration(
                    // image: DecorationImage(
                    //   image: AssetImage('assets/images/label_example_blur.png'),
                    // ),
                    ),
                child: Image.asset('assets/images/label_example_blur.png',
                    scale: 1.5),
              ),
              Form(
                key: _formKey,
                child: Column(
                  children: [
                    ListTile(
                      leading: const Text(
                        '제품군 : ',
                        style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.w700,
                        ),
                      ),
                      title: DropdownButton<String>(
                        dropdownColor:
                            Theme.of(context).colorScheme.surfaceVariant,
                        value: dropdownValue,
                        icon: const Icon(Icons.arrow_drop_down),
                        isExpanded: true,
                        elevation: 16,
                        style: TextStyle(
                          fontSize: 16,
                          color: Theme.of(context).colorScheme.surfaceTint,
                        ),
                        underline: Container(
                          height: 2,
                          color: Theme.of(context).colorScheme.surfaceTint,
                        ),
                        onChanged: (String? value) {
                          // This is called when the user selects an item.
                          setState(() {
                            dropdownValue = value!;
                          });
                        },
                        items:
                            list.map<DropdownMenuItem<String>>((String value) {
                          return DropdownMenuItem<String>(
                            value: value,
                            child: Text(value),
                          );
                        }).toList(),
                      ),
                    ),
                    ListTile(
                        leading: const Text(
                          '모델명 : ',
                          style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.w700,
                          ),
                        ),
                        title: TextFormField(
                          decoration: const InputDecoration(
                            hintText: '모델명을 입력하세요.',
                            hintStyle: TextStyle(
                              fontWeight: FontWeight.w300,
                            ),
                          ),
                          onSaved: (String? value) {
                            // This optional block of code can be used to run
                            // code when the user saves the form.
                          },
                          validator: (String? value) {
                            return (value != null && value.contains('@'))
                                ? 'Do not use the @ char.'
                                : null;
                          },
                        )),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
