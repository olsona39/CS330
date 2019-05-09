import java.sql.*;
import java.lang.*;
import java.util.*;

public class RegistrationSystem{

    public static String ConnectionString;
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    public static final String USER = "olsona39";
    public static final String PASSWORD = "R`eqy]N*@R0.";

    public void run() {
        String[] commands = new String[] {"Get Transcript", "Check Degree", "Requirements", "Add Course", "Remove Course", "Exit"};
        Scanner scanner = new Scanner(System.in);
        Connection connection = getConnection();

        //Check for valid user input
        String sID = "";
        Statement stmt = null;
        boolean validUser = false;
        System.out.println("Enter your student ID");
        while(validUser == false){
            String idScanner = scanner.nextLine();
            if(verifyID(connection,idScanner)){
                sID = idScanner;
                validUser = true;
            }else{
                System.out.println("Invalid student ID");
                System.out.println("Enter valid username.");
            }
        }
        //Prompt user for command
        String operation = "";
        while(!operation.equals("Exit")){
            System.out.println("Select an operation: Get Transcript, Check Degree Requirements, Add Course, Remove Course, or Exit");
            operation = scanner.nextLine();
            //Use sID to query db to get xcourse number, xcourse title, xsemester, xyear, xgrade, credits of every course student has taken in chronological order.
            if(operation.equals("Get Transcript")){
                System.out.println("Getting Transcript");
                getTranscript(connection, sID);
            }
            if(operation.equals("Check Degree Requirements")) {
                System.out.println("checking degree requirements");
                getDegreeRequirements(connection, sID);
            }
            if(operation.equals("Add Course")){
                System.out.println("adding course.");
                addCourse(connection, sID);
            }
//            if(operation == "Remove Course"){
//
//            }







            System.out.println(operation);
        }

//        boolean validCommand = false;
//        for(int i = 0; i < commands.length(); i++){
//            if(operation.equals(commands[i])){
//                validCommand = true;
//            }
//        }
//        if(validCommand == false){
//            System.out.println("Enter valid command");
//        }

    }

    public static void getTranscript(Connection connection, String sID){
        try {
            String query = "SELECT C.course_id, C.title, T.semester, T.year, T.grade, C.credits FROM course AS C, student AS S, takes AS T WHERE S.id = T.id AND T.course_id = C.course_id AND S.id = " + sID + ";";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String courseNum = rs.getString("C.course_id");
                String courseTitle = rs.getString("C.title");
                String semester = rs.getString("T.semester");
                String year = rs.getString("T.year");
                String grade = rs.getString("T.grade");
                String credits = rs.getString("C.credits");

                System.out.println("\nCourse Number: " + courseNum + "\nCourse Title: " + courseTitle + "\nSemester: " + semester + "\nYear: " + year + "\nGrade: " + grade + "\ncredits: " + credits );
                System.out.println();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void getDegreeRequirements(Connection connection, String sID) {
        try {
            String query =
                    "        SELECT C.course_id, C.title\n" +
                            "        FROM student AS S, course AS C\n" +
                            "        WHERE S.ID = " + sID + "\n" +
                            "        AND S.dept_name = C.dept_name\n" +
                            "        AND C.course_id NOT IN\n" +
                            "                (SELECT C.course_id\n" +
                            "                        FROM takes AS T, course AS C, student AS S\n" +
                            "                        WHERE T.ID = S.ID\n" +
                            "                        AND C.course_id = T.course_id\n" +
                            "                        AND C.dept_name = S.dept_name\n" +
                            "                        AND S.ID = " + sID;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String courseNum = rs.getString("C.course_id");
                String courseTitle = rs.getString("C.title");

                System.out.println("\nCourse Number: " + courseNum + " \nCourse Title: " + courseTitle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addCourse(Connection connection, String sID){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which Semester?");
        String semeseter = scanner.nextLine();
        System.out.println("Which Year?");
        String year = scanner.nextLine();
        int count = 1;
        try{
            String query =
                    "SELECT course_id, sec_id\n" +
                            "FROM section\n" +
                            "WHERE year = '2009'\n" +
                            "AND semester = 'Spring'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                String courseNum = rs.getString("course_id");
                String sectionId = rs.getString("sec_id");
                System.out.println("\n" + count + ":\n Course Number: " + courseNum + "\nSection id:" + sectionId);
                count++;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

    }
    public static Connection getConnection(){
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            ConnectionString = "jdbc:mysql://mysql.cs.wwu.edu:3306/" + USER + "?useSSL=false";
            conn = DriverManager.getConnection(ConnectionString, USER, PASSWORD);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e);
        }
        return conn;
    }

    //verify that student ID exists in database
    public static boolean verifyID(Connection connection, String sID){
        String IDquery = "select ID from student";
        boolean validID = false;

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(IDquery);
            while (rs.next()) {
                String rsID = rs.getString("ID");
                if(sID.equals(rsID)){
                    validID = true;
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return validID;
    }

}