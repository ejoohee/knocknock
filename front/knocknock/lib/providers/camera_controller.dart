import 'package:flutter/material.dart';
import 'package:camera/camera.dart';

class CameraControllerProvider with ChangeNotifier {
  CameraController? _controller;
  CameraDescription? _camera;

  CameraController? get controller => _controller;
  CameraDescription? get camera => _camera;

  Future<void> initializeCamera() async {
    final cameras = await availableCameras();
    _camera = cameras.first;
    _controller = CameraController(_camera!, ResolutionPreset.medium);
    await _controller!.initialize();
    notifyListeners();
  }

  @override
  void dispose() {
    _controller?.dispose();
    super.dispose();
  }
}
