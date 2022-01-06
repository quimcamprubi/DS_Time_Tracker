// see Serializing JSON inside model classes in
// https://flutter.dev/docs/development/data-and-backend/json

import 'package:intl/intl.dart';
import 'dart:convert' as convert;

final DateFormat _dateFormatter = DateFormat("yyyy-MM-dd HH:mm:ss");

abstract class Activity {
  int id;
  String name;
  DateTime? initialDate;
  DateTime? finalDate;
  int duration;
  List<dynamic> children = List<dynamic>.empty(growable: true);
  String tags;
  bool active;

  // formerly List<dynamic>(); but now because of null safety it has to be
  // initialized like that

  Activity.fromJson(Map<String, dynamic> json)
      : id = json['id'],
        name = json['name'],
        initialDate = json['initialDate'] == null ? null : _dateFormatter.parse(json['initialDate']),
        finalDate = json['finalDate'] == null ? null : _dateFormatter.parse(json['finalDate']),
        duration = json['duration'],
        tags = json['tags'].join(', '),
        active = json['active'];

  List<dynamic> childrenOrderedByMethod(String method) {
    switch (method) {
      case "duration":
        children.sort((b, a) => a.duration.compareTo(b.duration));
        break;
      case "startDate":
        List<dynamic> aux = children;
        List<dynamic> aux2 =List<dynamic>.empty(growable: true);
        int i=0;
        while(i < aux.length){
          if(aux[i].initialDate == null) {
            aux2.add(aux[i]);
          }
          i++;
        }
        i = 0;
        while(i < aux2.length){
          aux.remove(aux2[i]);
          i++;}

        aux.sort((b, a) => a.initialDate.compareTo(b.initialDate));

        aux = aux + aux2;
        children = aux;
        break;
      case "endDate":
        List<dynamic> aux = children;
        List<dynamic> aux2 =List<dynamic>.empty(growable: true);
        int i=0;
        while(i < aux.length){
          if(aux[i].finalDate == null) {
            aux2.add(aux[i]);
          }
          i++;
        }
        i = 0;
        while(i < aux2.length){
          aux.remove(aux2[i]);
          i++;}

        aux.sort((b, a) => a.finalDate.compareTo(b.finalDate));

        aux = aux + aux2;
        children = aux;
        break;
      case "name":
        children.sort((a, b) => a.name.compareTo(b.name));
        break;
      default:
        break;
    }

    return children;
  }
}

class Project extends Activity {
  Project.fromJson(Map<String, dynamic> json) : super.fromJson(json) {
    if (json.containsKey('activities')) {
      // json has only 1 level because depth=1 or 0 in time_tracker
      for (Map<String, dynamic> jsonChild in json['activities']) {
        if (jsonChild['class'] == "project") {
          children.add(Project.fromJson(jsonChild));
          // condition on key avoids infinite recursion
        } else if (jsonChild['class'] == "task") {
          children.add(Task.fromJson(jsonChild));
        } else {
          assert(false);
        }
      }
    }
  }
}


class Task extends Activity {
  bool active;
  Task.fromJson(Map<String, dynamic> json) :
        active = json['active'],
        super.fromJson(json) {
    for (Map<String, dynamic> jsonChild in json['intervals']) {
      children.add(Interval.fromJson(jsonChild));
    }
  }
  List<dynamic> childrenOrderedByRecent() {
    //List<Interval> list = children as List<Interval>;
    children.sort((b, a) => a.initialDate.compareTo(b.initialDate));
    return children;
  }
}


class Interval {
  int id;
  DateTime? initialDate;
  DateTime? finalDate;
  int duration;
  bool active;

  Interval.fromJson(Map<String, dynamic> json)
      : id = json['id'],
        initialDate = json['initialDate'] == null ? null : _dateFormatter.parse(json['initialDate']),
        finalDate = json['finalDate'] == null ? null : _dateFormatter.parse(json['finalDate']),
        duration = json['duration'],
        active = json['active'];
}



class Tree {
  late Activity root;

  Tree(Map<String, dynamic> dec) {
    // 1 level tree, root and children only, root is either Project or Task. If Project
    // children are Project or Task, that is, Activity. If root is Task, children are Instance.
    if (dec['class'] == "project") {
      root = Project.fromJson(dec);
    } else if (dec['class'] == "task") {
      root = Task.fromJson(dec);
    } else {
      assert(false, "neither project or task");
    }
  }
}


Tree getTree() {
  String strJson = "{"
      "\"name\":\"root\", \"class\":\"project\", \"id\":0, \"initialDate\":\"2020-09-22 16:04:56\", \"finalDate\":\"2020-09-22 16:05:22\", \"duration\":26,"
      "\"activities\": [ "
      "{ \"name\":\"software design\", \"class\":\"project\", \"id\":1, \"initialDate\":\"2020-09-22 16:05:04\", \"finalDate\":\"2020-09-22 16:05:16\", \"duration\":16 },"
      "{ \"name\":\"software testing\", \"class\":\"project\", \"id\":2, \"initialDate\": null, \"finalDate\":null, \"duration\":0 },"
      "{ \"name\":\"databases\", \"class\":\"project\", \"id\":3,  \"finalDate\":null, \"initialDate\":null, \"duration\":0 },"
      "{ \"name\":\"transportation\", \"class\":\"task\", \"id\":6, \"active\":false, \"initialDate\":\"2020-09-22 16:04:56\", \"finalDate\":\"2020-09-22 16:05:22\", \"duration\":10, \"intervals\":[] }"
      "] "
      "}";
  Map<String, dynamic> decoded = convert.jsonDecode(strJson);
  Tree tree = Tree(decoded);
  return tree;
}

testLoadTree() {
  Tree tree = getTree();
  print("root name ${tree.root.name}, duration ${tree.root.duration}");
  for (Activity act in tree.root.children) {
    print("child name ${act.name}, duration ${act.duration}");
  }
}


void main() {
  testLoadTree();
}

Tree getTreeTask() {
  String strJson = "{"
      "\"name\":\"transportation\",\"class\":\"task\", \"id\":10, \"active\":false, \"initialDate\":\"2020-09-22 13:36:08\", \"finalDate\":\"2020-09-22 13:36:34\", \"duration\":10,"
      "\"intervals\":["
      "{\"class\":\"interval\", \"id\":11, \"active\":false, \"initialDate\":\"2020-09-22 13:36:08\", \"finalDate\":\"2020-09-22 13:36:14\", \"duration\":6 },"
      "{\"class\":\"interval\", \"id\":12, \"active\":false, \"initialDate\":\"2020-09-22 13:36:30\", \"finalDate\":\"2020-09-22 13:36:34\", \"duration\":4}"
      "]}";
  Map <String, dynamic> decoded = convert.jsonDecode(strJson);
  Tree tree = Tree(decoded);
  return tree;
}