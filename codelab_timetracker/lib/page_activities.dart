import 'dart:async';
import 'dart:ffi';

import 'package:codelab_timetracker/main.dart';
import 'package:codelab_timetracker/page_new_activity.dart';
import 'package:codelab_timetracker/page_recent_activities.dart';
import 'package:codelab_timetracker/page_report.dart';
import 'package:codelab_timetracker/page_search.dart';
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
import 'globals.dart' as globals;

extension on Duration {
  String format() => '$this'.split('.')[0].padLeft(8, '0');
}

class PageActivities extends StatefulWidget {
  final int id;

  PageActivities(this.id);

  @override
  _PageActivitiesState createState() => _PageActivitiesState();
}

class _PageActivitiesState extends State<PageActivities> {
  late int id;
  late Future<Tree> futureTree;
  late Timer _timer;
  static const int periodeRefresh = 2;
  late String pageTitle;
  late String activityName;
  late String activityIdString;
  late String activityTags;
  String initialDateString = "";
  String finalDateString = "";
  //final DateFormat formatter = DateFormat('dd-MM-yy hh:mm:ss');
  String durationString = "";
  late String childrenText;
  late int id_act;
  final myController = TextEditingController();
  Icon customIcon = const Icon(Icons.search);
  late Widget customSearchBar;
  String searchByTag = "Search by tag...";
  final String defaultLocale = Platform.localeName;
  late DateFormat formatter;


  @override
  void initState() {
    super.initState();
    formatter = DateFormat(null, defaultLocale);
    switch (defaultLocale) {
      case "ca_ES":
        customSearchBar = const Text("Inici");
        break;
      case "es_ES":
        customSearchBar = const Text("Inicio");
        break;
      default:
        customSearchBar = const Text("Home");
        break;
    }
    id = widget.id; // of PageActivities
    futureTree = getTree(id);
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
    String hint = AppLocalizations.of(context)!.search_by_tag;
    return FutureBuilder<Tree>(
      future: futureTree,
      // this makes the tree of children, when available, go into snapshot.data
      builder: (context, snapshot) {
        // anonymous function
        if (snapshot.hasData) {
          activityIdString = snapshot.data!.root.id.toString();
          activityTags = snapshot.data!.root.tags;
          durationString =
              Duration(seconds: snapshot.data!.root.duration).format();
          if (snapshot.data!.root.initialDate != null) {
            initialDateString =
                formatter.format(snapshot.data!.root.initialDate!).toString();
            finalDateString =
                formatter.format(snapshot.data!.root.finalDate!).toString();
          }
          if (snapshot.data!.root.children.isNotEmpty) {
            childrenText = AppLocalizations.of(context)!.children_intro;
          } else {
            childrenText = AppLocalizations.of(context)!
                .no_children_created_yet;
          }
          if (snapshot.data!.root.id == 0) {
            pageTitle = "Home";
          } else {
            pageTitle = snapshot.data!.root.name;
          }
          if (snapshot.data!.root.id == 0) {
            return Scaffold(
              appBar: AppBar(
                backgroundColor: Colors.cyanAccent[700],
                title: customSearchBar,
                automaticallyImplyLeading: false,
                actions: <Widget>[
                  IconButton(
                    onPressed: () {
                      setState(() {
                        if (customIcon.icon == Icons.search) {
                          customIcon = Icon(Icons.cancel);
                          customSearchBar = ListTile(
                            leading: const Icon(
                              Icons.search,
                              color: Colors.white,
                              size: 28,
                            ),
                            title: TextField(
                              onSubmitted: (tag) {
                                //convert variable "tag" to id
                                _navigateDownSearch(tag);
                              },
                              decoration: InputDecoration(
                                hintText: hint,
                                hintStyle: const TextStyle(
                                  color: Colors.white,
                                  fontSize: 14,
                                  fontStyle: FontStyle.italic,
                                ),
                                border: InputBorder.none,
                              ),
                              style: const TextStyle(
                                color: Colors.white,
                              ),
                            ),
                          );
                        } else {
                          customIcon = Icon(Icons.search);
                          customSearchBar = Text(AppLocalizations.of(context)!
                              .home);
                        }
                      });
                    },
                    icon: customIcon,
                  ),
                  IconButton(
                      icon: Icon(Icons.more_time),
                      onPressed: () {
                        _navigateDownRecentTasks(globals.recentTasks);

                      }),
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
                itemCount: snapshot.data!.root.children.length,
                itemBuilder: (BuildContext context, int index) =>
                    _buildRow(snapshot.data!.root
                        .childrenOrderedByDuration()[index], index),
                separatorBuilder: (BuildContext context, int index) =>
                    const Divider(),
              ),
              floatingActionButton: SpeedDial(
                  backgroundColor: Colors.cyanAccent[700],
                  animatedIcon: AnimatedIcons.add_event,
                  spacing: 10,
                  spaceBetweenChildren: 15,
                  children: [
                    SpeedDialChild(
                      child: const Icon(Icons.text_snippet_outlined),
                      label: AppLocalizations.of(context)!.new_task,
                      backgroundColor: Colors.cyanAccent[700],
                      onTap: () {
                        _createTask(snapshot.data!.root.id);
                      },
                    ),
                    SpeedDialChild(
                      child: const Icon(Icons.folder_open_rounded),
                      label: AppLocalizations.of(context)!.new_project,
                      backgroundColor: Colors.cyanAccent[700],
                      onTap: () {
                        _createProject(snapshot.data!.root.id);
                      },
                    )
                  ]),
            );
          } else {
            return Scaffold(
              appBar: AppBar(
                backgroundColor: Colors.cyanAccent[700],
                title: Text(pageTitle),
                actions: <Widget>[
                  IconButton(
                      icon: Icon(Icons.home),
                      onPressed: () {
                        while (Navigator.of(context).canPop()) {
                          Navigator.of(context).pop();
                        }
                        PageActivities(0);
                      }),
                ],
              ),
              body: Container(
                  alignment: Alignment.topLeft,
                  margin: const EdgeInsets.all(0.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Row(
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: <Widget>[
                            Container(
                              margin: const EdgeInsets.only(
                                top: 20,
                                left: 20,
                                right: 10,
                                bottom: 10,
                              ),
                              height: 30,
                              width: 30,
                              child: const Icon(
                                Icons.folder_open_rounded,
                                size: 50,
                              ),
                            ),
                            Container(
                              margin: const EdgeInsets.only(
                                top: 42,
                                left: 20,
                                right: 10,
                                bottom: 0,
                              ),
                              width: 100,
                              child: Text(
                                AppLocalizations.of(context)!.project,
                                style: TextStyle(
                                    fontSize: 24,
                                    fontWeight: FontWeight.bold,
                                    color: Colors.grey[800]),
                              ),
                            ),
                          ]),
                      Row(
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: <Widget>[
                            Container(
                              margin: const EdgeInsets.only(
                                top: 20,
                                left: 20,
                                right: 10,
                                bottom: 10,
                              ),
                              width: 110.0,
                              child: Text(
                                AppLocalizations.of(context)!.identifier,
                                style: TextStyle(
                                    fontSize: 19, color: Colors.grey[700]),
                              ),
                            ),
                            Container(
                              margin: const EdgeInsets.only(
                                top: 20,
                                left: 20,
                                right: 10,
                                bottom: 10,
                              ),
                              width: 200.0,
                              child: Text(
                                activityIdString,
                                style: TextStyle(
                                    fontSize: 19,
                                    fontWeight: FontWeight.bold,
                                    color: Colors.grey[800]),
                              ),
                            ),
                          ]),
                      Row(children: <Widget>[
                        Container(
                          margin: const EdgeInsets.only(
                            top: 10,
                            left: 20,
                            right: 10,
                            bottom: 10,
                          ),
                          width: 110.0,
                          child: Text(
                            "Tags",
                            style: TextStyle(
                                fontSize: 19, color: Colors.grey[700]),
                          ),
                        ),
                        Container(
                          margin: const EdgeInsets.only(
                            top: 10,
                            left: 20,
                            right: 10,
                            bottom: 10,
                          ),
                          width: 200.0,
                          child: Text(
                            activityTags,
                            style: TextStyle(
                                fontSize: 19,
                                fontWeight: FontWeight.bold,
                                color: Colors.grey[800]),
                          ),
                        ),
                      ]),
                      Row(children: <Widget>[
                        Container(
                          margin: const EdgeInsets.only(
                            top: 10,
                            left: 20,
                            right: 10,
                            bottom: 10,
                          ),
                          width: 110.0,
                          child: Text(
                            AppLocalizations.of(context)!.duration,
                            style: TextStyle(
                                fontSize: 19, color: Colors.grey[700]),
                          ),
                        ),
                        Container(
                          margin: const EdgeInsets.only(
                            top: 10,
                            left: 20,
                            right: 10,
                            bottom: 10,
                          ),
                          width: 200.0,
                          child: Text(
                            durationString,
                            style: TextStyle(
                                fontSize: 19,
                                fontWeight: FontWeight.bold,
                                color: Colors.grey[800]),
                          ),
                        ),
                      ]),
                      Row(children: <Widget>[
                        Container(
                          margin: const EdgeInsets.only(
                            top: 10,
                            left: 20,
                            right: 10,
                            bottom: 10,
                          ),
                          width: 110.0,
                          child: Text(
                            AppLocalizations.of(context)!.initial_date,
                            style: TextStyle(
                                fontSize: 19, color: Colors.grey[700]),
                          ),
                        ),
                        Container(
                          margin: const EdgeInsets.only(
                            top: 10,
                            left: 20,
                            right: 10,
                            bottom: 10,
                          ),
                          width: 200.0,
                          child: Text(
                            initialDateString,
                            style: TextStyle(
                                fontSize: 19,
                                fontWeight: FontWeight.bold,
                                color: Colors.grey[800]),
                          ),
                        ),
                      ]),
                      Row(children: <Widget>[
                        Container(
                          margin: const EdgeInsets.only(
                            top: 10,
                            left: 20,
                            right: 10,
                            bottom: 10,
                          ),
                          width: 110.0,
                          child: Text(
                            AppLocalizations.of(context)!.final_date,
                            style: TextStyle(
                                fontSize: 19, color: Colors.grey[700]),
                          ),
                        ),
                        Container(
                          margin: const EdgeInsets.only(
                            top: 10,
                            left: 20,
                            right: 10,
                            bottom: 10,
                          ),
                          width: 200.0,
                          child: Text(
                            finalDateString,
                            style: TextStyle(
                                fontSize: 19,
                                fontWeight: FontWeight.bold,
                                color: Colors.grey[800]),
                          ),
                        ),
                      ]),
                      Container(
                        margin: const EdgeInsets.only(
                          top: 10,
                          left: 20,
                          right: 10,
                          bottom: 10,
                        ),
                        child: Text(
                          childrenText,
                          style: TextStyle(
                              fontSize: 20,
                              fontWeight: FontWeight.bold,
                              color: Colors.grey[800]),
                        ),
                      ),
                      Expanded(
                        child: ListView.separated(
                          scrollDirection: Axis.vertical,
                          padding: const EdgeInsets.all(16.0),
                          itemCount: snapshot.data!.root.children.length,
                          itemBuilder: (BuildContext context, int index) =>
                              _buildRow(snapshot.data!.root.
                              childrenOrderedByDuration()[index], index),
                          separatorBuilder: (BuildContext context, int index) =>
                              const Divider(),
                        ),
                      ),
                    ],
                  )
                ),
              floatingActionButton: SpeedDial(
                  backgroundColor: Colors.cyanAccent[700],
                  animatedIcon: AnimatedIcons.add_event,
                  spacing: 10,
                  spaceBetweenChildren: 15,
                  children: [
                    SpeedDialChild(
                      child: Icon(Icons.text_snippet_outlined),
                      label: AppLocalizations.of(context)!.new_task,
                      backgroundColor: Colors.cyanAccent[700],
                      onTap: () {
                        _createTask(snapshot.data!.root.id);
                      },
                    ),
                    SpeedDialChild(
                      child: Icon(Icons.folder_open_rounded),
                      label: AppLocalizations.of(context)!.new_project,
                      backgroundColor: Colors.cyanAccent[700],
                      onTap: () {
                        _createProject(snapshot.data!.root.id);
                      },
                    )
                  ]),
            );
          }
        } else if (snapshot.hasError) {
          return Text("${snapshot.error}");
        }
        // By default, show a progress indicator
        return Container(
            height: MediaQuery.of(context).size.height,
            color: Colors.white,
            child: const Center(
              child: CircularProgressIndicator(),
            ));
      },
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
        trailing: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            if (activity.active) const Icon(
              Icons.circle,
              color: Colors.redAccent,
              size: 15,),
            if (activity.active) const SizedBox(width:7),
            Text('$strDuration'),
          ],
        ),
        onTap: () => _navigateDownActivities(activity.id),
      );
    } else if (activity is Task) {
      Task task = activity as Task;
      // at the moment is the same, maybe changes in the future
      Widget trailing;
      trailing = Text('$strDuration');
      return ListTile(
        leading: IconButton(
          padding: EdgeInsets.zero,
          constraints: BoxConstraints(),
          iconSize: 28,
          icon: task.active ? Icon(Icons.pause) : Icon(Icons.play_arrow),
          color: task.active ? Colors.redAccent : Colors.greenAccent[400],
          onPressed: () {
            if (task.active) {
              stop(activity.id);
              _refresh(); // to show immediately that task has started
            } else {
              start(activity.id);
              _refresh(); // to show immediately that task has stopped
            }
          },
        ),
        title: Text('${activity.name}'),
        subtitle: Text(
          AppLocalizations.of(context)!.task,
          style: const TextStyle(
            fontSize: 18.0,
            color: CupertinoColors.inactiveGray,
          ),
        ),
        trailing: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            if (activity.active) const Icon(
              Icons.circle,
              color: Colors.redAccent,
              size: 15,),
            if (activity.active) SizedBox(width:7),
            Text('$strDuration'),
          ],
        ),
        onTap: () => _addTaskAndNavigate(activity),
      );
    } else {
      throw (Exception("Activity that is neither a Task or a Project"));
      // this solves the problem of return Widget is not nullable because an
      // Exception is also a Widget?
    }
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

  void _navigateDownSearch(String tag) async{
    _timer.cancel();
    List<dynamic> searchRes = await getSearch(tag);
    Navigator.of(context)
        .push(MaterialPageRoute<void>(
      builder: (context) => PageSearch(searchRes, tag),
    ))
        .then((var value) {
      _activateTimer();
      _refresh();
    });
  }

  void _navigateDownRecentTasks(List<Task> recentTasks) {
    _timer.cancel();
    Navigator.of(context)
        .push(MaterialPageRoute<void>(
      builder: (context) => PageRecentActivities(recentTasks),
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
    futureTree = getTree(id); // to be used in build()
    setState(() {});
  }

  void _activateTimer() {
    _timer = Timer.periodic(Duration(seconds: periodeRefresh), (Timer t) {
      futureTree = getTree(id);
      setState(() {});
    });
  }

  void _createProject(int parentId) {
    _timer.cancel();
    // we can not do just _refresh() because then the up arrow doesn't appear in the appbar
    Navigator.of(context)
        .push(MaterialPageRoute<void>(
      builder: (context) => PageNewActivity("Project", parentId),
    ))
        .then((var value) {
      _activateTimer();
      _refresh();
    });
  }

  void _createTask(int parentId) {
    _timer.cancel();
    Navigator.of(context)
        .push(MaterialPageRoute<void>(
      builder: (context) => PageNewActivity("Task", parentId),
    ))
        .then((var value) {
      _activateTimer();
      _refresh();
    });
  }
}
