import 'package:flutter/material.dart';
import 'package:knocknock/components/buttons.dart';
import 'package:camera/camera.dart';
import 'package:flutter_layout_grid/flutter_layout_grid.dart';

// A screen that allows users to take a picture using a given camera.
import 'package:flutter/material.dart';
import 'package:camera/camera.dart';
import 'package:knocknock/screens/display_picture_screen.dart';
import 'package:knocknock/screens/manual_register.dart';

class TakePictureScreen extends StatefulWidget {
  const TakePictureScreen({
    super.key,
    required this.camera,
  });

  final CameraDescription camera;

  @override
  TakePictureScreenState createState() => TakePictureScreenState(camera);
}

class TakePictureScreenState extends State<TakePictureScreen> {
  CameraDescription camera;

  TakePictureScreenState(this.camera);

  late CameraController _controller;
  late Future<void> _initializeControllerFuture;

  @override
  void initState() {
    super.initState();
    // To display the current output from the Camera,
    // create a CameraController.
    _controller = CameraController(
      // Get a specific camera from the list of available cameras.
      widget.camera,
      // Define the resolution to use.
      ResolutionPreset.medium,
    );

    // Next, initialize the controller. This returns a Future.
    _initializeControllerFuture = _controller.initialize();
  }

  @override
  void dispose() {
    // Dispose of the controller when the widget is disposed.
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    // Fill this out in the next steps.
    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(
            horizontal: 30,
            vertical: 30,
          ),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Padding(
                padding: EdgeInsets.symmetric(vertical: 10.0),
                child: Text(
                  '📸 에너지소비효율등급 라벨 촬영하기',
                  style: TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.w700,
                  ),
                ),
              ),
              Stack(
                alignment: Alignment.center,
                children: [
                  FutureBuilder<void>(
                    future: _initializeControllerFuture,
                    builder: (context, snapshot) {
                      if (snapshot.connectionState == ConnectionState.done) {
                        // If the Future is complete, display the preview.
                        return CameraPreview(_controller);
                        // return Center(
                        //     child: ClipRRect(
                        //         child: SizedOverflowBox(
                        //   size: const Size(300, 450), // aspect
                        //   alignment: Alignment.center,
                        //   child: CameraPreview(_controller),
                        // )));
                      } else {
                        // Otherwise, display a loading indicator.
                        return const Center(child: CircularProgressIndicator());
                      }
                    },
                  ),
                  Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Image.asset(
                        'assets/images/label_example.png',
                        opacity: const AlwaysStoppedAnimation(0.3),
                        scale: 1.3,
                      ),
                      IconButton(
                        splashColor: Colors.transparent,
                        onPressed: () async {
                          // Take the Picture in a try / catch block. If anything goes wrong,
                          // catch the error.
                          try {
                            // Ensure that the camera is initialized.
                            await _initializeControllerFuture;

                            // Attempt to take a picture and then get the location
                            // where the image file is saved.
                            final image = await _controller.takePicture();
                            if (!mounted) return;

                            // If the picture was taken, display it on a new screen.
                            await Navigator.of(context).push(
                              MaterialPageRoute(
                                builder: (context) => DisplayPictureScreen(
                                  // Pass the automatically generated path to
                                  // the DisplayPictureScreen widget.
                                  imagePath: image.path,
                                ),
                              ),
                            );
                          } catch (e) {
                            // If an error occurs, log the error to the console.
                            print(e);
                          }
                        },
                        icon: Icon(
                          Icons.radio_button_checked_sharp,
                          size: 80,
                          color: Theme.of(context).colorScheme.surfaceVariant,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
              const Text(
                '※ 가이드에 맞추어 촬영해주세요',
                style: TextStyle(
                  color: Colors.red,
                ),
              ),
              const SizedBox(
                height: 10,
              ),
              KnockButton(
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) =>
                            const ManualRegister()), // 옷장 페이지로 이동
                  );
                },
                width: MediaQuery.of(context).size.width * 0.8,
                height: MediaQuery.of(context).size.width * 0.16,
                label: '직접 입력하기',
                bColor: Theme.of(context).colorScheme.secondaryContainer,
                fColor: Theme.of(context).colorScheme.onSecondaryContainer,
              )
            ],
          ),
        ),
      ),
    );
  }
}
