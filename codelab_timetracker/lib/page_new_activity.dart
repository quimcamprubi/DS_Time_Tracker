import 'package:codelab_timetracker/tree.dart';
import 'package:flutter/material.dart';
import 'package:codelab_timetracker/requests.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

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
  }

  @override
  Widget build(BuildContext context) {
    if (widget.type == "Project") {
      title = AppLocalizations.of(context)!.new_project;
    } else {
      title = AppLocalizations.of(context)!.new_task;
    }
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
                        decoration: InputDecoration(
                          border: const UnderlineInputBorder(),
                          labelText: AppLocalizations.of(context)!.name,
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
                        decoration: InputDecoration(
                          border: UnderlineInputBorder(),
                          labelText: AppLocalizations.of(context)!.tags_comma_separated,
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
              label: Text(
                AppLocalizations.of(context)!.create_button,
                style: const TextStyle(
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
