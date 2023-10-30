import 'package:flutter/material.dart';

class Tile extends StatelessWidget {
  final VoidCallback onTap;
  final Widget child;
  Color color;
  final bool isBookmarked;
  Widget? bookmarkIcon;

  Tile({
    super.key,
    required this.onTap,
    required this.child,
    this.color = const Color(0xFFEFEFEF),
    required this.isBookmarked,
    this.bookmarkIcon,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Card(
        color: color,
        elevation: 8,
        child: Row(
          children: [
            isBookmarked
                ? Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [bookmarkIcon!],
                  )
                : Container(),
            child,
          ],
        ),
      ),
    );
  }
}
