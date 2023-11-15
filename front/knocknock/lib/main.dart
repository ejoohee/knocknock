import 'package:flutter/material.dart';
import 'package:knocknock/color_schemes.g.dart';
import 'package:knocknock/providers/my_appliance.dart';
import 'package:knocknock/providers/page_index.dart';
import 'package:knocknock/screens/google.dart';

import 'package:knocknock/screens/home_screen.dart';

import 'package:knocknock/providers/appliance.dart';
import 'package:knocknock/screens/check_appliance.dart';

import 'package:knocknock/screens/log_in.dart';
import 'package:knocknock/screens/main_page.dart';
import 'package:knocknock/screens/manage_appliances.dart';
import 'package:knocknock/screens/my_page.dart';
import 'package:knocknock/screens/new_appliance_category_each.dart';
import 'package:knocknock/screens/nickname_assign.dart';
import 'package:knocknock/screens/sign_up_google.dart';
import 'package:knocknock/screens/start_page.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:provider/provider.dart';
import 'firebase_options.dart';
import 'package:firebase_analytics/firebase_analytics.dart';
import 'package:uni_links/uni_links.dart';
import 'package:knocknock/providers/theme_provider.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => SelectedAppliance()),
        ChangeNotifierProvider(create: (_) => RegisterAppliance()),
        ChangeNotifierProvider(create: (_) => CurrentPageIndex()),
        ChangeNotifierProvider(create: (_) => ThemeProvider()),
      ],
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  static FirebaseAnalytics analytics = FirebaseAnalytics.instance;
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    // return ChangeNotifierProvider(
    //   create: (BuildContext context) => SelectedAppliance(),
    //   child: MaterialApp(
    //     title: 'Flutter Demo',
    //     theme: ThemeData(
    //       useMaterial3: true,
    //       colorScheme: lightColorScheme,
    //     ),
    //     darkTheme: ThemeData(
    //       useMaterial3: true,
    //       colorScheme: darkColorScheme,
    //     ),
    //     home: const Login(),
    //   ),
    // );
    return Consumer<ThemeProvider>(
      builder: (context, themeProvider, child) {
        return MaterialApp(
          title: 'Flutter Demo',
          theme: themeProvider.isDarkModeProvider
              ? ThemeData(useMaterial3: true, colorScheme: darkColorScheme)
              : ThemeData(useMaterial3: true, colorScheme: lightColorScheme),
          // 기존에 정의된 테마 설정을 사용합니다. 여기서는 lightColorScheme과 darkColorScheme을 사용합니다.
          // theme: ThemeData(
          //   useMaterial3: true,
          //   colorScheme: lightColorScheme,
          // ),
          // darkTheme: ThemeData(
          //   useMaterial3: true,
          //   colorScheme: darkColorScheme,
          // ),
          home: const Login(),
        );
      },
    );
  }
}
