import 'dart:async';
import 'dart:ffi';

import 'package:codelab_timetracker/page_activities.dart';
import 'package:codelab_timetracker/page_new_activity.dart';
import 'package:codelab_timetracker/page_report.dart';
import 'package:codelab_timetracker/tree.dart' hide getTree;
import 'package:codelab_timetracker/requests.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'PageIntervals.dart';
import 'package:flutter_speed_dial/flutter_speed_dial.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'dart:io';
import 'package:intl/date_symbol_data_local.dart';

extension on Duration {
  String format() => '$this'.split('.')[0].padLeft(8, '0');
}

class PageRecentActivities extends StatefulWidget {
  final List<Task> recentTasks;

  PageRecentActivities(this.recentTasks);


  @override
  _PageRecentActivitiesState createState() => _PageRecentActivitiesState();
}

class _PageRecentActivitiesState extends State<PageRecentActivities> {
  late int id;
  late Timer _timer;
  static const int periodeRefresh = 2;
  late int id_act;
  final myController = TextEditingController();
  Icon customIcon = const Icon(Icons.search);
  String searchByTag = "Search by tag...";
  final String defaultLocale = Platform.localeName;
  late DateFormat formatter;


  @override
  void initState() {
    super.initState();
    formatter = DateFormat(null, defaultLocale);
    _activateTimer();
  }

  @override
  void dispose() {
    _timer.cancel();
    super.dispose();
  }

  // future with listview
// https://medium.com/nonstopio/flutter-future-builder-with-list-view-builder-d7212314e8c9
  @override
  Widget build(BuildContext context) {
            return Scaffold(
              appBar: AppBar(
                backgroundColor: Colors.cyanAccent[700],
                title: Text(AppLocalizations.of(context)!.recentTaskTitle),
                automaticallyImplyLeading: false,
                actions: <Widget>[
                  IconButton(
                      icon: Icon(Icons.home),
                      onPressed: () {
                        while (Navigator.of(context).canPop()) {
                          print("pop");
                          Navigator.of(context).pop();
                        }
                        PageActivities(0);
                      }),
                ],
              ),
              body: ListView.separated(
                // it's like ListView.builder() but better because it includes a separator between items
                padding: const EdgeInsets.all(16.0),
                itemCount: widget.recentTasks.length,
                itemBuilder: (BuildContext context, int index) =>
                    _buildRow(widget.recentTasks[index], index),
                separatorBuilder: (BuildContext context, int index) =>
                    const Divider(),
              ),
            );

  }

  Widget _buildRow(Task activity, int index) {
    String strDuration = Duration(seconds: activity.duration).format();
    // split by '.' and taking first element of resulting list removes the microseconds part

      Task task = activity as Task;
      // at the moment is the same, maybe changes in the future
      Widget trailing;
      trailing = Text('$strDuration');
    return ListTile(
      leading: Icon(Icons.text_snippet),
      title: Text('${activity.name}'),
      subtitle: Text(
        AppLocalizations.of(context)!.task,
        style: const TextStyle(
          fontSize: 18.0,
          color: CupertinoColors.inactiveGray,
        ),
      ),
      trailing: Text('$strDuration'),
      onTap: () => _navigateDownIntervals(activity.id),
    );

  }

  void _navigateDownActivities(int childId) {
    _timer.cancel();
    // we can not do just _refresh() because then the up arrow doesn't appear in the appbar
    Navigator.of(context)
        .push(MaterialPageRoute<void>(
      builder: (context) => PageActivities(childId),
    ))
        .then((var value) {
      _activateTimer();
      _refresh();
    });
  }

  void _navigateDownIntervals(int childId) {
    _timer.cancel();
    Navigator.of(context)
        .push(MaterialPageRoute<void>(
      builder: (context) => PageIntervals(childId),
    ))
        .then((var value) {
      _activateTimer();
      _refresh();
    });
  }

  void _refresh() async {
    setState(() {});
  }

  void _activateTimer() {
    _timer = Timer.periodic(Duration(seconds: periodeRefresh), (Timer t) {
      setState(() {});
    });
  }


}
