import 'package:codelab_timetracker/tree.dart';
import 'package:flutter/material.dart';
import 'package:codelab_timetracker/requests.dart';

class PageNewActivity extends StatefulWidget {
  final String type;
  final int parentId;
  PageNewActivity(this.type, this.parentId);

  @override
  _PageNewActivityState createState() => _PageNewActivityState();
}

class _PageNewActivityState extends State<PageNewActivity> {
  late String title;
  late Activity newActivity;
  late String activityName = "";
  late String activityTags = "";

  @override
  void initState() {
    super.initState();
    if (widget.type == "Project") {
      title = "New Project";
    } else {
      title = "New Task";
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.cyanAccent[700],
        title: Text(title),
      ),
      body: Container(
          alignment: Alignment.topLeft,
          margin: const EdgeInsets.all(30.0),
          child: Column(
            children: <Widget>[
              Row(
                  children: <Widget>[
                    Expanded(
                      child: TextFormField(
                        decoration: const InputDecoration(
                          border: UnderlineInputBorder(),
                          labelText: 'Name',
                        ),
                        onChanged: (value) => setState(() => activityName =
                            value),
                      ),
                    ),
                  ]
              ),
              SizedBox(height: 20),
              Row(
                  children: <Widget>[
                    Expanded(
                      child: TextFormField(
                        decoration: const InputDecoration(
                          border: UnderlineInputBorder(),
                          labelText: 'Tags (comma separated)',
                        ),
                        onChanged: (value) => setState(() => activityTags =
                            value),
                      ),
                    ),
                  ]
              )
            ],
          )
      ),
      floatingActionButton:
          Container(
            width: 200,
            height: 65,
            child: FloatingActionButton.extended(
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
              label: const Text(
                "Create",
                style: TextStyle(
                  fontSize: 20.0,
                ),
              ),
              icon: Icon(Icons.add),
              backgroundColor: Colors.cyanAccent[700],
              onPressed: () {
                if (activityName != "" && activityTags != "") {
                  _createActivity(widget.parentId, activityName, activityTags);
                }
              },
              heroTag: null,
            ),
    ),
    floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
    );
  }

  void _createActivity(int parentId, String activityName, String activityTags) {
    if (widget.type == "Project") {
      // Remove whitespace from the tags and attempt to create Project.
      createProject(parentId, activityName, activityTags);
    }
    else {
      // Remove whitespace from the tags and attempt to create Task.
      createTask(parentId, activityName, activityTags);
    }
    Navigator.of(context).pop();
  }
}
