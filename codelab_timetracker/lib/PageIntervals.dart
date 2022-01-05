// ignore: file_names
// ignore_for_file: file_names

import 'dart:async';
import 'dart:io';

import 'package:codelab_timetracker/page_activities.dart';
import 'package:codelab_timetracker/tree.dart' as Tree hide getTree;
import 'package:codelab_timetracker/requests.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:intl/date_symbol_data_local.dart';

extension on Duration {
  String format() => '$this'.split('.')[0].padLeft(8, '0');
}

class PageIntervals extends StatefulWidget {
  final int id; // final because StatefulWidget is immutable
  PageIntervals(this.id);

  @override
  _PageIntervalsState createState() => _PageIntervalsState();
}

class _PageIntervalsState extends State<PageIntervals> {
  late int id;
  late Future<Tree.Tree> futureTree;
  late Timer _timer;
  static const int periodeRefresh = 2;
  late String activityName;
  late String activityIdString;
  late String activityTags;
  String initialDateString = "";
  String finalDateString = "";
  //final DateFormat formatter = DateFormat('dd-MM-yy hh:mm:ss');
  final String defaultLocale = Platform.localeName;
  late DateFormat formatter;
  String durationString = "";
  String childrenText = "This project does not contain any Intervals yet.";
  late Tree.Task task;

  @override
  void initState() {
    super.initState();
    formatter = DateFormat(null, defaultLocale);
    id = widget.id;
    futureTree = getTree(id);
    _activateTimer();
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<Tree.Tree>(
      future: futureTree,
      // this makes the tree of children, when available, go into snapshot.data
      builder: (context, snapshot) {
        // anonymous function
        if (snapshot.hasData) {
          task = snapshot.data!.root as Tree.Task;
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
            childrenText = AppLocalizations.of(context)!.intervals;
          } else {
            childrenText = AppLocalizations.of(context)!.no_intervals;
          }
          int numChildren = snapshot.data!.root.children.length;
          return Scaffold(
            appBar: AppBar(
              backgroundColor: Colors.cyanAccent[700],
              title: Text(snapshot.data!.root.name),
              actions: <Widget>[
                IconButton(icon: Icon(Icons.home),
                    onPressed: () {
                      while(Navigator.of(context).canPop()) {
                        Navigator.of(context).pop();
                      }
                      PageActivities(0);
                    }),
              ],
            ),
            body:
            Container(
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
                            child: IconButton(
                              padding: EdgeInsets.zero,
                              constraints: BoxConstraints(),
                              iconSize: 50,
                              icon: task.active ? Icon(Icons.pause) : Icon(Icons.play_arrow),
                              color: task.active ? Colors.redAccent : Colors.greenAccent[400],
                              onPressed: () {
                                if (task.active) {
                                  stop(task.id);
                                  _refresh(); // to show immediately that
                                  // task has started
                                } else {
                                  start(task.id);
                                  _refresh(); // to show immediately that task has stopped
                                }
                              },
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
                              AppLocalizations.of(context)!.task,
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
                        // it's like ListView.builder() but better because it includes a separator between items
                        padding: const EdgeInsets.all(16.0),
                        itemCount: numChildren,
                        itemBuilder: (BuildContext context, int index) =>
                            _buildRow(task.childrenOrderedByRecent()[index],
                                index),
                        separatorBuilder: (BuildContext context, int index) =>
                        const Divider(),
                      ),
                    ),
                  ],
                )
            ),
          );
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

  Widget _buildRow(Tree.Interval interval, int index) {
    String strDuration = Duration(seconds: interval.duration)
        .toString()
        .split('.')
        .first;
    String strInitialDate = interval.initialDate.toString().split('.')[0];
    // this removes the microseconds part
    String strFinalDate = interval.finalDate.toString().split('.')[0];
    if (!interval.active) {
      return ListTile(
          title: Text('${AppLocalizations.of(context)!.from}:  '
              '${strInitialDate}\n${AppLocalizations.of(context)!.to}:       '
              '${strFinalDate}',
            style: const TextStyle(
              fontSize: 18.0,
            ),
          ),
          trailing: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text('$strDuration'),
            ],
          )
      );
    }
    else {
      return ListTile(
          title: Text('${AppLocalizations.of(context)!.from}:  ${strInitialDate}'
              '\n${AppLocalizations.of(context)!.to}:       ${strFinalDate}',
            style: const TextStyle(
              fontSize: 18.0,
            ),
          ),
          trailing: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              const Icon(
                Icons.circle,
                color: Colors.redAccent,
                size: 15,),
              const SizedBox(width:7),
              Text('$strDuration'),
            ],
          )
      );
    }
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
}
