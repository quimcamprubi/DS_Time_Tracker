import 'dart:async';

import 'package:flutter_gen/gen_l10n/app_localizations.dart';
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
import 'globals.dart' as globals;

extension on Duration {
  String format() => '$this'.split('.')[0].padLeft(8, '0');
}

class PageSearch extends StatefulWidget {
  final String tag;
  final List<dynamic> searchRes;

  PageSearch(this.searchRes, this.tag);

  @override
  _PageSearchState createState() => _PageSearchState();
}

class _PageSearchState extends State<PageSearch> {
  late List<dynamic> searchRes;
  late String tag;
  late Timer _timer;
  static const int periodeRefresh = 2;
  late int id_act;


  @override
  void initState() {
    super.initState();
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
        title: Text(AppLocalizations.of(context)!.searchResults + widget.tag),
        automaticallyImplyLeading: false,
        actions: <Widget>[
          IconButton(
              icon: Icon(Icons.home),
              onPressed: () {
                while (Navigator.of(context).canPop()) {
                  print("pop");
                  Navigator.of(context).pop();
                }
                PageActivities(0, "root");
              }),
        ],
      ),
      body: ListView.separated(
        // it's like ListView.builder() but better because it includes a separator between items
        padding: const EdgeInsets.all(16.0),
        itemCount: widget.searchRes.length,
        itemBuilder: (BuildContext context, int index) =>
            _buildRow(widget.searchRes[index], index),
        separatorBuilder: (BuildContext context, int index) =>
        const Divider(),
      ),
    );

  }

  Widget _buildRow(Activity activity, int index) {
    String strDuration = Duration(seconds: activity.duration).format();
    // split by '.' and taking first element of resulting list removes the microseconds part
    if (activity is Project) {
      return ListTile(
        leading: Icon(Icons.folder_open_rounded),
        title: Text('${activity.name}'),
        subtitle: Text(
          AppLocalizations.of(context)!.project,
          style: const TextStyle(
            fontSize: 18.0,
            color: CupertinoColors.inactiveGray,
          ),
        ),
        trailing: Text('$strDuration'),
        onTap: () => _navigateDownActivities(activity.id, activity.name),
      );
    } else if (activity is Task) {
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
        onTap: () => _addTaskAndNavigate(task),
      );
    } else {
      throw (Exception("Activity that is neither a Task or a Project"));
      // this solves the problem of return Widget is not nullable because an
      // Exception is also a Widget?
    }
  }

  void _navigateDownActivities(int childId, String name) {
    _timer.cancel();
    // we can not do just _refresh() because then the up arrow doesn't appear in the appbar
    Navigator.of(context)
        .push(MaterialPageRoute<void>(
      builder: (context) => PageActivities(childId, name),
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

  void _addTaskAndNavigate(Task task){
    _navigateDownIntervals(task.id);
    //add to recent task
    if (isInList(task)) {
      globals.recentTasks.removeAt(globals.recentTasks.indexOf(task));
      globals.recentTasks.add(task);
    }
    else{
      if (globals.recentTasks.length >=5){
        globals.recentTasks.removeAt(0);
      }
      globals.recentTasks.add(task);
    }  }

  bool isInList(Task task){
    bool varbool = false;
    for (var name in globals.recentTasks){
      if (name.id == task.id)varbool = true;
    }
    return varbool;
  }



}
