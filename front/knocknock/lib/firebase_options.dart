// File generated by FlutterFire CLI.
// ignore_for_file: lines_longer_than_80_chars, avoid_classes_with_only_static_members
import 'package:firebase_core/firebase_core.dart' show FirebaseOptions;
import 'package:flutter/foundation.dart'
    show defaultTargetPlatform, kIsWeb, TargetPlatform;

/// Default [FirebaseOptions] for use with your Firebase apps.
///
/// Example:
/// ```dart
/// import 'firebase_options.dart';
/// // ...
/// await Firebase.initializeApp(
///   options: DefaultFirebaseOptions.currentPlatform,
/// );
/// ```
class DefaultFirebaseOptions {
  static FirebaseOptions get currentPlatform {
    if (kIsWeb) {
      return web;
    }
    switch (defaultTargetPlatform) {
      case TargetPlatform.android:
        return android;
      case TargetPlatform.iOS:
        return ios;
      case TargetPlatform.macOS:
        return macos;
      case TargetPlatform.windows:
        throw UnsupportedError(
          'DefaultFirebaseOptions have not been configured for windows - '
          'you can reconfigure this by running the FlutterFire CLI again.',
        );
      case TargetPlatform.linux:
        throw UnsupportedError(
          'DefaultFirebaseOptions have not been configured for linux - '
          'you can reconfigure this by running the FlutterFire CLI again.',
        );
      default:
        throw UnsupportedError(
          'DefaultFirebaseOptions are not supported for this platform.',
        );
    }
  }

  static const FirebaseOptions web = FirebaseOptions(
    apiKey: 'AIzaSyDqRm05Qca-CohO9mUeP9TfFVHnf-pCcHU',
    appId: '1:775799878924:web:5f16745387ece02d834529',
    messagingSenderId: '775799878924',
    projectId: 'knocknock-4ea17',
    authDomain: 'knocknock-4ea17.firebaseapp.com',
    storageBucket: 'knocknock-4ea17.appspot.com',
    measurementId: 'G-BG31PFRJ0S',
  );

  static const FirebaseOptions android = FirebaseOptions(
    apiKey: 'AIzaSyB6tLJ0n8Yc8TYkROcsrwd5-NP4HOc-6NY',
    appId: '1:775799878924:android:b59f71e13b474671834529',
    messagingSenderId: '775799878924',
    projectId: 'knocknock-4ea17',
    storageBucket: 'knocknock-4ea17.appspot.com',
  );

  static const FirebaseOptions ios = FirebaseOptions(
    apiKey: 'AIzaSyB4_bJorODYXTKMZmdXTYkGP5x3XQM9fM8',
    appId: '1:775799878924:ios:3b9228e206dcdf93834529',
    messagingSenderId: '775799878924',
    projectId: 'knocknock-4ea17',
    storageBucket: 'knocknock-4ea17.appspot.com',
    iosBundleId: 'com.example.knocknock',
  );

  static const FirebaseOptions macos = FirebaseOptions(
    apiKey: 'AIzaSyB4_bJorODYXTKMZmdXTYkGP5x3XQM9fM8',
    appId: '1:775799878924:ios:95c076e87ec87073834529',
    messagingSenderId: '775799878924',
    projectId: 'knocknock-4ea17',
    storageBucket: 'knocknock-4ea17.appspot.com',
    iosBundleId: 'com.example.knocknock.RunnerTests',
  );
}
