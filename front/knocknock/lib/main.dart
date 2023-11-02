import 'package:flutter/material.dart';
import 'package:knocknock/color_schemes.g.dart';
import 'package:knocknock/screens/home_screen.dart';
import 'package:knocknock/screens/log_in.dart';
import 'package:knocknock/screens/main_page.dart';
import 'package:knocknock/screens/my_page.dart';
import 'package:knocknock/screens/start_page.dart';
import 'package:firebase_core/firebase_core.dart';
import 'firebase_options.dart';
import 'package:firebase_analytics/firebase_analytics.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  static FirebaseAnalytics analytics = FirebaseAnalytics.instance;
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        useMaterial3: true,
        colorScheme: lightColorScheme,
      ),
      darkTheme: ThemeData(
        useMaterial3: true,
        colorScheme: darkColorScheme,
      ),
      home: const HomeScreen(),
    );
  }
}
