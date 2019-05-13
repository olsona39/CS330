import java.sql.*;
import java.lang.*;
import java.util.*;

//pass in database user id and password from the command line
//R`eqy]N*@R0.

public class RegistrationSystem{

    private static String user;
    private static String pass;
    private static String ConnectionString;
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    public static void RegistrationSystem(String user, String pass){
        this.user = user;
        this.pass = pass;
    }

    public void run() {
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
        while(!operation.equals("Exit")){
            System.out.println(sID);
            System.out.println("Select an operation: Get Transcript, Check Degree Requirements, Add Course, Remove Course, or Exit");
            operation = scanner.nextLine();
            if(operation.equals("Get Transcript")){
                getTranscript(connection, sID);
            }
            if(operation.equals("Check Degree Requirements")) {
                getDegreeRequirements(connection, sID);
            }
            if(operation.equals("Add Course")){
                int added = addCourse(connection, sID);
                if(added == 0){
                    System.out.println("Course Added");
                }else{
                    System.out.println("Failed to Add Course");
                }
            }
            if(operation.equals("Remove Course")){
                int removed = removeCourse(connection, sID);
                if(removed == 0){
                    System.out.println("Course Removed");
                }else{
                    System.out.println("Failed to Remove Course");
                }
            }
        }

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
                            "                        AND S.ID = ?);";

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

    public static int removeCourse(Connection connection, String sID) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which Semester?");
        String semester = scanner.nextLine();
        System.out.println("Which Year?");
        String year = scanner.nextLine();
        int count = 1;
        try {
            //all courses being taken by student in chosen Semester & Year
            String coursesQuery =
                    "SELECT course_id, sec_id\n" +
                            "FROM takes\n" +
                            "WHERE takes.ID = ?\n" +
                            "AND semester = ?\n" +
                            "AND year = ?;\n";
            PreparedStatement pstmtTaken = connection.prepareStatement(coursesQuery);
            pstmtTaken.setString(1, sID);
            pstmtTaken.setString(2, semester);
            pstmtTaken.setString(3, year);

            ResultSet rsTaken = pstmtTaken.executeQuery();
            ArrayList<String> courseArray = new ArrayList<String>();
            ArrayList<String> sectionIDArray = new ArrayList<String>();

            if (!rsTaken.isBeforeFirst()) {
                System.out.println("You are not currently enrolled in any courses.");
                return -1;
            }
            while (rsTaken.next()) {
                String courseNum = rsTaken.getString("course_id");
                courseArray.add(courseNum);
                String sectionId = rsTaken.getString("sec_id");
                sectionIDArray.add(sectionId);
                System.out.println("\n" + count + ":\nCourse Number: " + courseNum + "\nSection id:" + sectionId);
                count++;
            }

            //check for valid number selection
            boolean validNumber = false;
            String courseNum = "";
            String sectionId = "";
            while (!validNumber) {
                System.out.println("Which section would you like to remove?");
                int courseIndex = scanner.nextInt() - 1;

                if (courseIndex + 1 >= count || courseIndex + 1 < 1) {
                    System.out.println("Invalid number");
                } else {
                    courseNum = courseArray.get(courseIndex);
                    sectionId = sectionIDArray.get(courseIndex);
                    validNumber = true;
                }
            }

            String deleteQuery = "DELETE FROM takes WHERE ID = ? AND course_id = ? AND sec_id = ?;";
            PreparedStatement pstmtDelete = connection.prepareStatement(deleteQuery);
            pstmtDelete.setString(1, sID);
            pstmtDelete.setString(2, courseNum);
            pstmtDelete.setString(3, sectionId);
            pstmtDelete.executeUpdate();
            System.out.println("Course Removed.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int addCourse(Connection connection, String sID){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which Semester?");
        String semester = scanner.nextLine();
        System.out.println("Which Year?");
        String year = scanner.nextLine();
        int count = 1;
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

            if(count < 1){
                System.out.println("No Courses Available To Add.");
                return -1;
            }

            boolean validNumber = false;
            String courseNum = "";
            String sectionId = "";
            while(!validNumber) {
                System.out.println("Which section would you like to enroll in?");
                int courseIndex = 0;
                if(scanner.hasNextInt()) {
                    courseIndex = scanner.nextInt() - 1;
                }else{
                    return -1;
                }
                courseNum = courseArray.get(courseIndex);
                sectionId = sectionIDArray.get(courseIndex);
                if (courseIndex + 1 > count || courseIndex + 1 < 1) {
                    System.out.println("Invalid number");
                }else{
                    validNumber = true;
                }
            }

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
                    return -1;
                }
            }
            //checks to see if they are enrolled
            String enrolledQuery = "SELECT course_id\n" +
                    "FROM takes\n" +
                    "WHERE takes.course_id = ?\n" +
                    "AND takes.ID = ?\n" +
                    "AND takes.semester = ?\n" +
                    "AND takes.year = ?;";
            PreparedStatement pstmtEnrolled = connection.prepareStatement(enrolledQuery);
            pstmtEnrolled.setString(1, courseNum);
            pstmtEnrolled.setString(2, sID);
            pstmtEnrolled.setString(3, semester);
            pstmtEnrolled.setString(4, year);

            ResultSet rsEnrolled = pstmtEnrolled.executeQuery();
            if(rsEnrolled.next()){
                System.out.println("You are already enrolled in course.");
                return -1;
            }

            //INSERT student selection into takes
            String insertQuery = "insert into takes values(?, ?, ?, ?, ?, null);";
            PreparedStatement pstmtInsert = connection.prepareStatement(insertQuery);
            pstmtInsert.setString(1, sID);
            pstmtInsert.setString(2, courseNum);
            pstmtInsert.setString(3, sectionId);
            pstmtInsert.setString(4, semester);
            pstmtInsert.setString(5, year);
            pstmtInsert.executeUpdate();

        }catch(SQLException e){
            e.printStackTrace();
        }
        return 0;
    }
    public static Connection getConnection(){
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            ConnectionString = "jdbc:mysql://mysql.cs.wwu.edu:3306/" + user + "?useSSL=false";
            conn = DriverManager.getConnection(ConnectionString, user, pass);
        }catch(Exception e){
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return validID;
    }

}