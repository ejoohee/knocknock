import 'package:flutter/material.dart';

class Tile extends StatelessWidget {
  final VoidCallback onTap;
  final VoidCallback onLongPress;

  final Widget child;
  Color color;
  final bool isBookmarked;
  Widget? bookmarkIcon;

  Tile({
    super.key,
    required this.onTap,
    required this.onLongPress,
    required this.child,
    this.color = const Color(0xFFEFEFEF),
    required this.isBookmarked,
    this.bookmarkIcon,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      onLongPress: onLongPress,
      child: SizedBox(
        height: 100,
        child: Card(
          clipBehavior: Clip.hardEdge,
          color: color,
          elevation: 8,
          child: Row(
            // mainAxisAlignment: MainAxisAlignment.start,
            children: [
              if (isBookmarked)
                Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [bookmarkIcon!],
                ),
              Expanded(child: child),
            ],
          ),
        ),
      ),
    );
  }
}
