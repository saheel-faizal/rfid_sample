String baseUrl = "http://192.46.210.52:8081/";
String db = "f1_race";
String gateUserType = "";

void configureGateUser(String gateUser) {
  switch (gateUser) {
    case "Gate_1":
      gateUserType = "1";
      print("Gate user type set to: Gate_1");
      break;
    case "Gate_2":
      gateUserType = "2";
      print("Gate user type set to: Gate_2");
      break;
    case "Gate_3":
      gateUserType = "3";
      print("Gate user type set to: Gate_3");
      break;
    case "Gate_4":
      gateUserType = "4";
      print("Gate user type set to: Gate_4");
      break;
    case "Gate_5":
      gateUserType = "5";
      print("Gate user type set to: Gate_5");
      break;
    default:
      print("Invalid gate user type. Defaulting to 'Gate_1'.");
  }
}


