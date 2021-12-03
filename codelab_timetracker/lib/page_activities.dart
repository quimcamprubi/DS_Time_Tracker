import 'package:codelab_timetracker/page_report.dart';
import 'package:codelab_timetracker/tree.dart';
import 'package:flutter/material.dart';
import 'PageIntervals.dart';

class PageActivities extends StatefulWidget {
  const PageActivities({Key? key}) : super(key: key);

  @override
  _PageActivitiesState createState() => _PageActivitiesState();
}

class _PageActivitiesState extends State<PageActivities> {
  late Tree tree;

  @override
  void initState() {
    super.initState();
    tree = getTree();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(tree.root.name),
        actions: <Widget>[
          IconButton(icon: Icon(Icons.home),
              onPressed: () {Navigator.of(context).popUntil((route) => route.isFirst);
              },
          ),
          IconButton(icon: Icon(Icons.library_books),
            onPressed: () {
              Navigator.of(context)
                  .push(MaterialPageRoute<void>(
                builder: (context) => PageReport(),
              ));
            },
          )
          // TODO other actions
        ]
      ),
      body: ListView.separated(
        // it's like ListView.builder() but better because it includes a
        // separator between items
        padding: const EdgeInsets.all(16.0),
        itemCount: tree.root.children.length,
        itemBuilder: (BuildContext context, int index) =>
            _buildRow(tree.root.children[index], index),
        separatorBuilder: (BuildContext context, int index) =>
          const Divider(),
      ),
    );
  }

  Widget _buildRow(Activity activity, int index) {
    String strDuration = Duration(seconds: activity.duration).toString().split('.').first;
    // split by '.' and taking first element of resulting list
    // removes the microseconds part
    if (activity is Project) {
      return ListTile(
        title: Text('${activity.name}'),
        trailing: Text('$strDuration'),
        onTap: () => {},
        // TODO, navigate down to show children tasks and projects
      );
    } else if (activity is Task){ // must be a task
      assert(activity is Task);
      Task task = activity as Task;
      Widget trailing;
      trailing = Text('$strDuration');
      return ListTile(
        title: Text('${activity.name}'),
        trailing: trailing,
        onTap: () => _navigateDownIntervals(index),
        // TODO, navigate down to show intervals
        onLongPress: () {},
        // TODO start/stop counting the time for tis task
      );
    } else {
      throw(Exception("Activity that is neither a Task or a Project."));
    }
  }

  void _navigateDownIntervals(int childId) {
    Navigator.of(context).push(MaterialPageRoute<void>(builder: (context) =>
        PageIntervals()));
  }
}
