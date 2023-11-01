import 'package:flutter/material.dart';
import 'package:knocknock/components/buttons.dart';

class NicknameAssign extends StatefulWidget {
  const NicknameAssign({super.key});

  @override
  State<NicknameAssign> createState() => _NicknameAssignState();
}

class _NicknameAssignState extends State<NicknameAssign> {
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.all(
          30,
        ),
        child: Column(
          children: [
            const Text(
              '별명을 지정할 수 있어요!',
              style: TextStyle(),
            ),
            // Image(image: ),
            Form(
              key: _formKey,
              child: Column(
                children: [
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 20.0),
                    child: TextFormField(
                      decoration: const InputDecoration(
                        prefixIcon: Icon(Icons.edit_outlined),
                        hintText: '나의땡땡땡',
                      ),
                      onSaved: (String? value) {
                        // This optional block of code can be used to run
                        // code when the user saves the form.
                      },
                      validator: (String? value) {
                        return (value!.length > 5) ? '5자 이내로 입력해주세요' : null;
                      },
                    ),
                  ),
                  const SizedBox(
                    height: 20,
                  ),
                  KnockButton(
                    onPressed: () {
                      // 버튼 클릭 시 실행할 동작
                      if (_formKey.currentState!.validate()) {
                        // Process data.
                      }
                    },
                    bColor: Theme.of(context).colorScheme.primary,
                    fColor: Theme.of(context).colorScheme.onPrimary,
                    width: MediaQuery.of(context).size.width * 0.8, // 버튼의 너비
                    height: MediaQuery.of(context).size.width * 0.16, // 버튼의 높이
                    label: "완료", // 버튼에 표시할 텍스트
                  ),
                ],
              ),
            )
          ],
        ),
      ),
    );
  }
}
