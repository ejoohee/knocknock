import 'package:flutter/material.dart';

import 'package:flutter/material.dart';

class KnockButton extends ElevatedButton {
  final double width;
  final double height;
  final String label;
  final Color bColor;
  final Color fColor;

  @override
  KnockButton({
    Key? key,
    required VoidCallback onPressed,
    required this.width,
    required this.height,
    required this.label,
    required this.bColor,
    required this.fColor,
  }) : super(
          key: key,
          onPressed: onPressed,
          child: Text(
            label,
            style: TextStyle(
              fontSize: 17,
              color: fColor,
            ),
          ),
          style: ButtonStyle(
            backgroundColor: MaterialStateProperty.all<Color>(bColor),
            fixedSize: MaterialStateProperty.all<Size>(
              Size(width, height),
            ),
            shape: MaterialStateProperty.all<RoundedRectangleBorder>(
              RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(7.0),
              ),
            ),
            elevation: MaterialStateProperty.all<double>(3),
          ),
        );
}
