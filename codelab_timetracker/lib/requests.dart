import 'dart:convert' as convert;
import 'package:http/http.dart' as http;
import 'tree.dart';

final http.Client client = http.Client();
// better than http.get() if multiple requests to the same server

// If you connect the Android emulator to the webserver listening to localhost:8080
const String baseUrl = "http://10.0.2.2:8080";

// If instead you want to use a real phone, you need ngrok to redirect
// localhost:8080 to some temporal Url that ngrok.com provides for free: run
// "ngrok http 8080" and replace the address in the sentence below
//const String baseUrl = "http://59c1d5a02fa5.ngrok.io";
// in linux I've installed ngrok with "sudo npm install ngrok -g". On linux, windows,
// mac download it from https://ngrok.com/. More on this here
// https://stackoverflow.com/questions/4779963/how-can-i-access-my-localhost-from-my-android-device
// https://newbedev.com/how-can-i-access-my-localhost-from-my-android-device
// look for "Portable solution with ngrok"

Future<Tree> getTree(int id) async {
  var uri = Uri.parse("$baseUrl/get_tree?$id");
  // see https://pub.dev/packages/http for examples of use
  final response = await client.get(uri);
  // response is NOT a Future because of await but since getTree() is async,
  // execution continues (leaves this function) until response is available,
  // and then we come back here
  if (response.statusCode == 200) {
    print("statusCode=$response.statusCode");
    print(response.body);
    // If the server did return a 200 OK response, then parse the JSON.
    Map<String, dynamic> decoded = convert.jsonDecode(response.body);
    return Tree(decoded);
  } else {
    // If the server did not return a 200 OK response, then throw an exception.
    print("statusCode=$response.statusCode");
    throw Exception('Failed to get children');
  }
}

Future<void> start(int id) async {
  var uri = Uri.parse("$baseUrl/start?$id");
  final response = await client.get(uri);
  if (response.statusCode == 200) {
    print("statusCode=$response.statusCode");
  } else {
    print("statusCode=$response.statusCode");
    throw Exception('Failed to get children');
  }
}

Future<void> stop(int id) async {
  var uri = Uri.parse("$baseUrl/stop?$id");
  final response = await client.get(uri);
  if (response.statusCode == 200) {
    print("statusCode=$response.statusCode");
  } else {
    print("statusCode=$response.statusCode");
    throw Exception('Failed to get children');
  }
}

Future<void> createProject(int parentId, String activityName, String
activityTags) async {
  var encodedName = Uri.encodeComponent(activityName);
  var encodedTags = Uri.encodeComponent(activityTags);
  var url = "$baseUrl/createProject?$parentId?$encodedName"
      "?$encodedTags";
  final response = await client.get(Uri.parse(url));
  if (response.statusCode == 200) {
    print("statusCode=$response.statusCode");
  } else {
    print("statusCode=$response.statusCode");
    throw Exception('Failed to create Project');
  }
}

Future<void> createTask(int parentId, String activityName, String activityTags)
async {
  var encodedName = Uri.encodeComponent(activityName);
  var encodedTags = Uri.encodeComponent(activityTags);
  var url = "$baseUrl/createTask?$parentId?$encodedName"
      "?$encodedTags";
  final response = await client.get(Uri.parse(url));
  if (response.statusCode == 200) {
    print("statusCode=$response.statusCode");
  } else {
    print("statusCode=$response.statusCode");
    throw Exception('Failed to create Task');
  }
}

Future<List> getSearch(String tag) async {
  var uri = Uri.parse("$baseUrl/get_search?$tag");
  final response = await client.get(uri);
  // response is NOT a Future because of await but since getSearch() is async,
  // execution continues (leaves this function) until response is available,
  // and then we come back here
  if (response.statusCode == 200) {
    print("statusCode=$response.statusCode");
    print(response.body);
    // If the server did return a 200 OK response, then parse the JSON.
    List<Activity> finalList = List<Activity>.empty(growable: true);
    List<dynamic> decoded = convert.jsonDecode(response.body);
    decoded.forEach((element) =>  finalList.add(jsonFormatter(element)));
    return finalList;
  } else {
    // If the server did not return a 200 OK response, then throw an exception.
    print("statusCode=$response.statusCode");
    throw Exception('Failed to get search');
  }
}

Activity jsonFormatter(dynamic o){
  if (o['class'] == "project")
    return Project.fromJson(o);
  return Task.fromJson(o);
}