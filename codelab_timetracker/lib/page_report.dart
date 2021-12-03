import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

class PageReport extends StatefulWidget {
  const PageReport({Key? key}) : super(key: key);

  @override
  _PageReportState createState() => _PageReportState();
}

class _PageReportState extends State<PageReport> {
  String periodValue = "Today";
  String contentValue = "Brief";
  String formatValue = "Web page";
  late DateTime fromValue;
  late DateTime toValue;
  late DateTimeRange fromTo;
  late DateTime today;

  @override
  void initState() {
    today = DateTime.now();
    fromValue = today.subtract(new Duration(days:7));
    toValue = today;
    periodValue = "Last week";
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Report'),
      ),
      body: Container(
          alignment: Alignment.center,
          margin: const EdgeInsets.all(40.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: <Widget>[
              Row(
                children: <Widget>[
                  Container(
                    width: 90.0,
                    child: Text("Period"),
                  ),
                  DropdownButton(
                    value: periodValue,
                    onChanged: (String? newValue) {
                      setState(() {
                        periodValue = newValue!;
                        updateDates();
                      });
                    },
                    items: <String>['Last week', 'This week', 'Yesterday',
                        'Today', 'Other']
                        .map<DropdownMenuItem<String>>((String value) {
                      return DropdownMenuItem<String>(
                        value: value,
                        child: Text(value),
                      );
                    }).toList(),
                  )
                  ]
              ),
              Row(
                  children: <Widget>[
                    Container(
                      width: 90.0,
                      child: Text("From"),
                    ),
                    Text(DateFormat('yyyy-MM-dd').format(fromValue)),
                    IconButton(onPressed: _pickFromDate,
                        icon: Icon(Icons
                        .date_range,
                        color: Colors.blue))
                  ]
              ),
              Row(
                  children: <Widget>[
                    Container(
                      width: 90.0,
                      child: Text("To"),
                    ),
                    Text(DateFormat('yyyy-MM-dd').format(toValue)),
                    IconButton(onPressed: _pickToDate, icon: Icon(Icons
                        .date_range,
                        color: Colors.blue))
                  ]
              ),
              Row(
                  children: <Widget>[
                    Container(
                      width: 90.0,
                      child: Text("Content"),
                    ),
                    DropdownButton(
                      value: contentValue,
                      onChanged: (String? newValue) {
                        setState(() {
                          contentValue = newValue!;
                        });
                      },
                      items: <String>['Brief', 'Detailed', 'Statistic']
                          .map<DropdownMenuItem<String>>((String value) {
                        return DropdownMenuItem<String>(
                          value: value,
                          child: Text(value),
                        );
                      }).toList(),
                    )
                  ]
              ),
              Row(
                  children: <Widget>[
                    Container(
                      width: 90.0,
                      child: Text("Format"),
                    ),
                    DropdownButton(
                      value: formatValue,
                      onChanged: (String? newValue) {
                        setState(() {
                          formatValue = newValue!;
                        });
                      },
                      items: <String>['Web page', 'PDF', 'Text']
                          .map<DropdownMenuItem<String>>((String value) {
                        return DropdownMenuItem<String>(
                          value: value,
                          child: Text(value),
                        );
                      }).toList(),
                    )
                  ]
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    Container(
                      width: 125.0,
                      child: TextButton(
                        style: TextButton.styleFrom(
                          textStyle: const TextStyle(fontSize: 19, fontWeight:
                          FontWeight.bold)
                        ),
                        onPressed: () {},
                        child: const Text('Generate'),
                      ),
                    )
                  ]
              )
            ],
          )
      ),
    );
  }

  void updateDates() {
    switch (periodValue) {
      case "Last week":
        fromValue = today.subtract(new Duration(days:7));
        toValue = today;
        break;
      case "This week":
        var mondayThisWeek = DateTime(today.year, today.month,
            today.day - today.weekday + 1);
        fromValue = mondayThisWeek;
        toValue = today;
        break;
      case "Yesterday":
        var yesterday = today.subtract(Duration(days:1));
        fromValue = yesterday;
        toValue = today;
        break;
      case "Today":
        fromValue = today;
        toValue = today;
        break;
      case "Other":
        fromValue = today;
        toValue = today;
        break;
      default:
        break;
    }
  }

  _pickFromDate() async {
    DateTime? newStart = await showDatePicker(
      context: context,
      firstDate: DateTime(today.year - 5),
      lastDate: DateTime(today.year + 5),
      initialDate: today,
    );
    DateTime end = toValue; // the present To date
    if (end.difference(newStart!) >= Duration(days: 0)) {
      fromTo = DateTimeRange(start: newStart, end: end);
      // x is where you store the (From,To) DateTime pairs
      // associated to the ’Other’ option
      fromValue = newStart;
      setState(() {
      periodValue = "Other"; // to redraw the screen
      });
    } else {
    _showAlertDates();
    }
  }

  _pickToDate() async {
    DateTime? newEnd = await showDatePicker(
      context: context,
      firstDate: DateTime(today.year - 5),
      lastDate: DateTime(today.year + 5),
      initialDate: today,
    );
    DateTime start = fromValue; // the present To date
    if (newEnd!.difference(start) >= Duration(days: 0)) {
      fromTo = DateTimeRange(start: start, end: newEnd);
      // x is where you store the (From,To) DateTime pairs
      // associated to the ’Other’ option
      toValue = newEnd;
      setState(() {
        periodValue = "Other"; // to redraw the screen
      });
    } else {
      _showAlertDates();
    }
  }

  Future<void> _showAlertDates() async {
    return showDialog<void>(
      context: context,
      barrierDismissible: false, // user must tap button!
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text('Range dates'),
          content: SingleChildScrollView(
            child: ListBody(
              children: const <Widget>[
                Text('The From date is after the To date.'),
                Text('Please, select a new date.'),
              ],
            ),
          ),
          actions: <Widget>[
            TextButton(
              child: const Text('ACCEPT'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }
}
