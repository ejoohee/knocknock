import 'package:flutter/material.dart';

class GridPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    var paint = Paint()
      ..color = Colors.white.withOpacity(0.8) // 격자 색상 및 투명도 설정
      ..strokeWidth = 1; // 격자 선의 두께 설정

    // 세로선 그리기
    for (int i = 1; i < 3; i++) {
      canvas.drawLine(
        Offset(size.width / 3 * i, 0),
        Offset(size.width / 3 * i, size.height),
        paint,
      );
    }

    // 가로선 그리기
    for (int i = 1; i < 3; i++) {
      canvas.drawLine(
        Offset(0, size.height / 3 * i),
        Offset(size.width, size.height / 3 * i),
        paint,
      );
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
