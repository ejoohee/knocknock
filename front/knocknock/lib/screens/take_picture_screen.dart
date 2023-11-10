import 'package:flutter/material.dart';
import 'package:knocknock/components/buttons.dart';
import 'package:camera/camera.dart';
import 'package:knocknock/models/my_appliance_model.dart';
import 'package:knocknock/providers/my_appliance.dart';

// A screen that allows users to take a picture using a given camera.
import 'package:knocknock/screens/display_info_screen.dart';
import 'package:knocknock/screens/grid_paint.dart';
import 'package:knocknock/screens/manual_register.dart';
import 'package:knocknock/services/model_service.dart';
import 'package:provider/provider.dart';
import 'package:animated_text_kit/animated_text_kit.dart';

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
  bool isLoading = false;
  ModelService modelService = ModelService();
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
  // late Future<MyModelRegistering> info;
  // ModelService modelService = ModelService();
  // @override
  // void initState() {
  //   // TODO: implement initState
  //   super.initState();
  //   info = loadInfo();
  // }

  // Future<MyModelRegistering> loadInfo() async {
  //   return await modelService.findRegisteringByImage(widget.imagePath);
  // }
  late MyModelRegistering? info;

  getModelInfo(String imagePath) async {
    info = await modelService.findRegisteringByImage(imagePath);
    if (!mounted) return;
    context.read<RegisterAppliance>().register(info);
    setState(() {
      isLoading = false;
    });
  }

  failLoad() {
    showDialog(
      context: context,
      builder: (context) {
        return const AlertDialog(
          // Retrieve the text the that user has entered by using the
          // TextEditingController.
          content: Text('모델명을 조회할 수 없습니다.\n다시 찍어주세요!'),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    // Fill this out in the next steps.
    return Scaffold(
      body: Stack(
        children: [
          SafeArea(
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
                  Expanded(
                    child: Stack(
                      alignment: Alignment.center,
                      children: [
                        FutureBuilder<void>(
                          future: _initializeControllerFuture,
                          builder: (context, snapshot) {
                            if (snapshot.connectionState ==
                                ConnectionState.done) {
                              // If the Future is complete, display the preview.
                              // return CameraPreview(_controller);
                              return Center(
                                  child: ClipRRect(
                                      child: SizedOverflowBox(
                                size: const Size(400, 450), // aspect
                                alignment: Alignment.center,
                                child: CameraPreview(_controller),
                              )));
                            } else {
                              // Otherwise, display a loading indicator.
                              return const Center(
                                  child: CircularProgressIndicator());
                            }
                          },
                        ),
                        CustomPaint(
                          painter: GridPainter(),
                          child: const SizedBox(
                            width: 400,
                            height: 550,
                          ),
                        ),
                        Padding(
                          padding: const EdgeInsets.all(10.0),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.end,
                            children: [
                              // Image.asset(
                              //   'assets/images/label_example.png',
                              //   opacity: const AlwaysStoppedAnimation(0.3),
                              //   scale: 1.3,
                              // ),
                              IconButton(
                                splashColor: Colors.transparent,
                                onPressed: () async {
                                  setState(() {
                                    isLoading = true;
                                  });
                                  print('찍는다잉');
                                  // Take the Picture in a try / catch block. If anything goes wrong,
                                  // catch the error.
                                  try {
                                    // Ensure that the camera is initialized.
                                    await _initializeControllerFuture;
                                    // Attempt to take a picture and then get the location
                                    // where the image file is saved.
                                    final image =
                                        await _controller.takePicture();
                                    String imagePath = image.path;
                                    // 모델 정보 받아오기.
                                    await getModelInfo(imagePath);
                                    if (info == null) {
                                      failLoad();
                                      return;
                                    }
                                    // If the picture was taken, display it on a new screen.
                                    if (!mounted) return;
                                    await Navigator.of(context).push(
                                      MaterialPageRoute(
                                        builder: (context) =>
                                            const DisplayInfoScreen(),
                                      ),
                                    );
                                  } catch (e) {
                                    // If an error occurs, log the error to the console.
                                    print('찍었는데 문제가 생겨따$e');
                                  }
                                },
                                icon: Icon(
                                  Icons.radio_button_checked_sharp,
                                  size: 80,
                                  color: Theme.of(context)
                                      .colorScheme
                                      .surfaceVariant,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                  const Text(
                    '※ 흔들리지 않게 찍어주세요!',
                    style: TextStyle(
                      color: Colors.red,
                    ),
                  ),
                  const SizedBox(
                    height: 10,
                  ),
                  KnockButton(
                    onPressed: () {
                      print('누름');
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
          if (isLoading)
            Positioned.fill(
              child: Container(
                color: Colors.black.withOpacity(0.5),
                child: Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      CircularProgressIndicator(
                        color: Colors.white,
                        backgroundColor: Colors.green[900],
                        strokeWidth: 5.0,
                      ),
                      const SizedBox(height: 20),
                      AnimatedTextKit(
                        animatedTexts: [
                          TyperAnimatedText(
                            '이미지를 읽고 있습니다. . .',
                            textStyle: const TextStyle(
                              color: Colors.white,
                              fontSize: 16.0,
                              fontWeight: FontWeight.bold,
                            ),
                            speed: const Duration(milliseconds: 120),
                          ),
                          TyperAnimatedText(
                            '잠시만 기다려주세요!',
                            textStyle: const TextStyle(
                              color: Colors.white,
                              fontSize: 16.0,
                              fontWeight: FontWeight.bold,
                            ),
                            speed: const Duration(milliseconds: 120),
                          ),
                        ],
                        isRepeatingAnimation: true,
                        // onTap: () {
                        //   print("Tap Event");
                        // },
                      ),
                    ],
                  ),
                ),
              ),
            ),
        ],
      ),
    );
  }
}
