import 'package:flutter/material.dart';
import 'package:knocknock/providers/theme_provider.dart';
import 'package:provider/provider.dart';

class AppBarPage extends StatelessWidget implements PreferredSizeWidget {
  final String title;
  final Widget page;
  const AppBarPage({Key? key, required this.title, required this.page})
      : super(key: key);

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);

  @override
  Widget build(BuildContext context) {
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;

    return AppBar(
      backgroundColor: Theme.of(context).colorScheme.background,
      leading: Builder(
        builder: (BuildContext context) {
          return IconButton(
            icon: const Icon(Icons.keyboard_backspace_rounded),
            onPressed: () {
              Navigator.of(context).pop();
              Navigator.of(context).push(
                MaterialPageRoute(
                  builder: (context) => page,
                ),
              );
            },
            tooltip: MaterialLocalizations.of(context).openAppDrawerTooltip,
          );
        },
      ),
      title: Text(
        title,
        style: const TextStyle(
          fontWeight: FontWeight.bold,
        ),
      ),
      centerTitle: true,
      actions: [
        Switch(
            thumbIcon: isDarkMode
                ? MaterialStateProperty.all(const Icon(Icons.dark_mode_rounded))
                : MaterialStateProperty.all(
                    const Icon(Icons.light_mode_rounded)),
            value: isDarkMode,
            onChanged: (value) {
              context.read<ThemeProvider>().toggleTheme();
            }),
      ],
    );
  }
}
