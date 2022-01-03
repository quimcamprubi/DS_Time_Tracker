import 'dart:async';

import 'package:codelab_timetracker/page_new_activity.dart';
import 'package:codelab_timetracker/page_report.dart';
import 'package:codelab_timetracker/tree.dart' hide getTree;
import 'package:codelab_timetracker/requests.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'PageIntervals.dart';
import 'package:flutter_speed_dial/flutter_speed_dial.dart';

class PageActivities extends StatefulWidget {
  final int id;

  PageActivities(this.id);

  @override
  _PageActivitiesState createState() => _PageActivitiesState();
}

class _PageActivitiesState extends State<PageActivities> {
  late int id;
  late int id_act;
  late Future<Tree> futureTree;
  late Timer _timer;
  static const int periodeRefresh = 2;
  late String pageTitle;
  late String activityName;
  final myController = TextEditingController();
  Icon customIcon = const Icon(Icons.search);
  Widget customSearchBar = const Text('Home');

  @override
  void initState() {
    super.initState();
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
    return FutureBuilder<Tree>(
      future: futureTree,
      // this makes the tree of children, when available, go into snapshot.data
      builder: (context, snapshot) {
        // anonymous function
        if (snapshot.hasData) {
          if (snapshot.data!.root.name == "root") {
            pageTitle = "Home";
          } else {
            pageTitle = snapshot.data!.root.name;
          }
          return Scaffold(
            appBar: AppBar(
              title: customSearchBar,
              automaticallyImplyLeading: false,
              actions: <Widget>[
                IconButton(
                  onPressed: () {
                    setState(() {
                      if (customIcon.icon == Icons.search) {
                        customIcon = const Icon(Icons.cancel);
                        customSearchBar = ListTile(
                          leading: const Icon(
                            Icons.search,
                            color: Colors.white,
                            size: 28,
                          ),
                          title: TextField(
                            onSubmitted: (tag) {
                              //convert variable "tag" to id
                              id_act = 4;
                              _timer.cancel();
                            Navigator.of(context)
                                .push(MaterialPageRoute<void>(
                              builder: (context) => PageActivities(id_act),
                            ))
                                .then((var value) {
                              _activateTimer();
                              _refresh();
                            });

                            },
                            decoration: const InputDecoration(
                              hintText: 'Search by tag...',
                              hintStyle: TextStyle(
                                color: Colors.white,
                                fontSize: 18,
                                fontStyle: FontStyle.italic,
                              ),
                              border: InputBorder.none,
                            ),
                            style: const TextStyle(
                              color: Colors.white,
                            ),

                          ),
                        );
                      }else {
                        customIcon = const Icon(Icons.search);
                        customSearchBar  = const Text('Home');
                      }
                    });
                  },
                  icon: customIcon,
                ),
                IconButton(
                    icon: Icon(Icons.home),
                    onPressed: () {
                      while (Navigator.of(context).canPop()) {
                        print("pop");
                        Navigator.of(context).pop();
                      }
                      PageActivities(0);
                    }),
                //TODO other actions


              ],
            ),
            body: ListView.separated(
              // it's like ListView.builder() but better because it includes a separator between items
              padding: const EdgeInsets.all(16.0),
              itemCount: snapshot.data!.root.children.length,
              itemBuilder: (BuildContext context, int index) =>
                  _buildRow(snapshot.data!.root.children[index], index),
              separatorBuilder: (BuildContext context, int index) =>
                  const Divider(),
            ),
            floatingActionButton: SpeedDial(
                animatedIcon: AnimatedIcons.add_event,
                spacing: 10,
                spaceBetweenChildren: 15,
                children: [
                  SpeedDialChild(
                    child: Icon(Icons.text_snippet_outlined),
                    label: 'New Task',
                    backgroundColor: Colors.blue,
                    onTap: () {
                      _createTask(snapshot.data!.root.id);
                    },
                  ),
                  SpeedDialChild(
                    child: Icon(Icons.folder_open_rounded),
                    label: 'New Project',
                    backgroundColor: Colors.blue,
                    onTap: () {
                      _createProject(snapshot.data!.root.id);
                    },
                  )
                ]),
          );
        } else if (snapshot.hasError) {
          return Text("${snapshot.error}");
        }
        // By default, show a progress indicator
        return Container(
            height: MediaQuery.of(context).size.height,
            color: Colors.white,
            child: Center(
              child: CircularProgressIndicator(),
            ));
      },
    );
  }

  Widget _buildRow(Activity activity, int index) {
    String strDuration =
        Duration(seconds: activity.duration).toString().split('.').first;
    // split by '.' and taking first element of resulting list removes the microseconds part
    if (activity is Project) {
      return ListTile(
        leading: Icon(Icons.folder_open_rounded),
        title: Text('${activity.name}'),
        subtitle: const Text(
          "Project",
          style: TextStyle(
            fontSize: 18.0,
            color: CupertinoColors.inactiveGray,
          ),
        ),
        trailing: Text('$strDuration'),
        onTap: () => _navigateDownActivities(activity.id),
      );
    } else if (activity is Task) {
      Task task = activity as Task;
      // at the moment is the same, maybe changes in the future
      Widget trailing;
      trailing = Text('$strDuration');
      return ListTile(
        leading:
            IconButton(
              icon: task.active ? Icon(Icons.pause) : Icon(Icons.play_arrow),
              color: task.active ? Colors.redAccent : Colors.greenAccent,
              padding: EdgeInsets.all(0),
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
        title:
          Text('${activity.name}'),
        subtitle: const Text(
          "Task",
          style: TextStyle(
            fontSize: 18.0,
            color: CupertinoColors.inactiveGray,
          ),
        ),
        trailing: trailing,
        onTap: () => _navigateDownIntervals(activity.id),
      );
    } else {
      throw (Exception("Activity that is neither a Task or a Project"));
      // this solves the problem of return Widget is not nullable because an
      // Exception is also a Widget?
    }
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
