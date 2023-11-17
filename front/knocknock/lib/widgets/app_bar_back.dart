import 'package:flutter/material.dart';
import 'package:knocknock/providers/theme_provider.dart';
import 'package:provider/provider.dart';

class AppBarBack extends StatelessWidget implements PreferredSizeWidget {
  final String title;
  bool isLeadingNeeded;
  AppBarBack({
    Key? key,
    required this.title,
    this.isLeadingNeeded = true,
  }) : super(key: key);

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);

  @override
  Widget build(BuildContext context) {
    bool isNeed = true;
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;

    return AppBar(
      backgroundColor: Theme.of(context).colorScheme.background,
      leading: isLeadingNeeded
          ? Builder(
              builder: (BuildContext context) {
                return Padding(
                  padding: const EdgeInsets.only(left: 30.0),
                  child: IconButton(
                    icon: const Icon(Icons.keyboard_backspace_rounded),
                    onPressed: () {
                      Navigator.of(context).pop();
                    },
                    tooltip:
                        MaterialLocalizations.of(context).openAppDrawerTooltip,
                  ),
                );
              },
            )
          : Container(),
      title: Text(
        title,
        style: const TextStyle(
            // fontWeight: FontWeight.bold,
            ),
      ),
      centerTitle: true,
      actions: [
        Padding(
          padding: const EdgeInsets.only(right: 30.0),
          child: Switch(
              thumbIcon: isDarkMode
                  ? MaterialStateProperty.all(
                      const Icon(Icons.dark_mode_rounded))
                  : MaterialStateProperty.all(
                      const Icon(Icons.light_mode_rounded)),
              value: isDarkMode,
              onChanged: (value) {
                context.read<ThemeProvider>().toggleTheme();
              }),
        ),
        // IconButton(
        //   icon: Icon(isDarkMode ? Icons.dark_mode : Icons.light_mode),
        //   onPressed: () {
        //     context.read<ThemeProvider>().toggleTheme();
        //   },
        // ),
      ],
    );
  }
}
