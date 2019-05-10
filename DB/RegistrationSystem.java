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
        //while(!operation.equals("Exit")){
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
       // }

    }

    public static void getTranscript(Connection connection, String sID){
        try {
            String query = "SELECT C.course_id, C.title, T.semester, T.year, T.grade, C.credits FROM course AS C, student AS S, takes AS T WHERE S.id = T.id AND T.course_id = C.course_id AND S.id = ?;";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, sID);
            ResultSet rs = pstmt.executeQuery();
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
                            "        WHERE S.ID = ?\n" +
                            "        AND S.dept_name = C.dept_name\n" +
                            "        AND C.course_id NOT IN\n" +
                            "                (SELECT C.course_id\n" +
                            "                        FROM takes AS T, course AS C, student AS S\n" +
                            "                        WHERE T.ID = S.ID\n" +
                            "                        AND T.grade != 'F'\n" +
                            "                        AND C.course_id = T.course_id\n" +
                            "                        AND C.dept_name = S.dept_name\n" +
                            "                        AND S.ID = ?";

            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, sID);
            pstmt.setString(2, sID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String courseNum = rs.getString("C.course_id");
                String courseTitle = rs.getString("C.title");

                System.out.println("\nCourse Number: " + courseNum + " \nCourse Title: " + courseTitle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //CHECK IF CURRENTLY ENROLLED
    public static void addCourse(Connection connection, String sID){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which Semester?");
        String semester = scanner.nextLine();
        System.out.println("Which Year?");
        String year = scanner.nextLine();
        int count = 1;
        boolean permitted = true;
        try{
            //all available courses given semester and year
            String coursesQuery =
                    "SELECT course_id, sec_id\n" +
                            "FROM section\n" +
                            "WHERE year = ?\n" +
                            "AND semester = ?;";
            PreparedStatement pstmt = connection.prepareStatement(coursesQuery);
            pstmt.setString(1, year);
            pstmt.setString(2, semester);
            ResultSet rs = pstmt.executeQuery();
            ArrayList<String> courseArray = new ArrayList<String>();
            ArrayList<String> sectionIDArray = new ArrayList<String>();

            while(rs.next()){
                String courseNum = rs.getString("course_id");
                courseArray.add(courseNum);
                String sectionId = rs.getString("sec_id");
                sectionIDArray.add(sectionId);
                System.out.println("\n" + count + ":\nCourse Number: " + courseNum + "\nSection id:" + sectionId);
                count++;
            }
            //TODO: Make sure that the number they type in exists.
            //TODO: Check that there are courses available
            System.out.println("Which section would you like to enroll in?");
            int courseIndex = scanner.nextInt() - 1;
            String courseNum = courseArray.get(courseIndex);
            String sectionId = sectionIDArray.get(courseIndex);
            System.out.println(courseNum);
            System.out.println(sectionId);

            //Check that they satisfy prereqs for the course
            //outputs prereqs needed for course choice
            String prereqQuery = "SELECT prereq_id\n" +
                    "FROM prereq\n" +
                    "WHERE course_id = ?;";
            PreparedStatement pstmtPreReq = connection.prepareStatement(prereqQuery);
            pstmtPreReq.setString(1, courseNum);
            ResultSet rsPrereq = pstmtPreReq.executeQuery();
            //if there are prereqs for the course check if they have taken them
            if(rsPrereq.next()){
                String takenPrereqQuery = "SELECT T.course_id\n" +
                        "FROM Takes as T, Student as S\n" +
                        "WHERE S.ID = T.ID\n" +
                        "AND T.course_id = ?\n" +
                        "AND S.ID = ?\n" +
                        "AND T.grade NOT IN ('F', 'NULL');";
                PreparedStatement pstmtTakenPrereq = connection.prepareStatement(takenPrereqQuery);
                pstmtTakenPrereq.setString(1, courseNum);
                pstmtTakenPrereq.setString(2, sID);
                ResultSet rsTakenPrerep = pstmtTakenPrereq.executeQuery();
                if(!rs.next()){
                    System.out.println("You need to take the prereq for this course.");
                    permitted = false;
                }
            }
            //TODO check to see if they are enrolled


            //INSERT student selection into takes
            if(permitted){
                String insertQuery = "insert into takes values(?, ?, ?, ?, ?, null);";
                PreparedStatement pstmtInsert = connection.prepareStatement(insertQuery);
                pstmtInsert.setString(1, sID);
                pstmtInsert.setString(2, courseNum);
                pstmtInsert.setString(3, sectionId);
                pstmtInsert.setString(4, semester);
                pstmtInsert.setString(5, year);
                System.out.println(pstmtInsert);
                pstmtInsert.executeUpdate();
            }
            scanner.close();

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
        PreparedStatement pstmt = null;

        try {
            pstmt = connection.prepareStatement(IDquery);
            ResultSet rs = pstmt.executeQuery();
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