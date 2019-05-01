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
            System.out.println("Select an operation: Get Transcript, Check Degree, Requirements, Add Course, Remove Course, or Exit");
            operation = scanner.nextLine();
            //Use sID to query db to get xcourse number, xcourse title, xsemester, xyear, xgrade, credits of every course student has taken in chronological order.
            if(operation.equals("Get Transcript")){
                System.out.println("Getting Transcript");
                try {
                    String query = "SELECT C.course_id, C.title, T.semester, T.year, T.grade, C.credits FROM course AS C, student AS S, takes AS T WHERE S.id = T.id AND T.course_id = C.course_id AND S.id = " + sID + ";";
                    stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        String courseNum = rs.getString("C.course_id");
                        String courseTitle = rs.getString("C.title");
                        String semester = rs.getString("T.semester");
                        String year = rs.getString("T.year");
                        String grade = rs.getString("T.grade");
                        String credits = rs.getString("C.credits");

                        System.out.println("Course Number: " + courseNum + " Course Title: " + courseTitle + " Semester: " + semester + " Year: " + year + " Grade: " + grade + " credits: " + credits );

                    }
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
            if(operation == "Check Degree"){

            }
            if(operation == "Requirements"){

            }
            if(operation == "Add Course"){

            }
            if(operation == "Remove Course"){

            }







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