import java.sql.*;
import java.lang.*;
import java.util.*;


public class RegistrationSystem{

    public static String ConnectionString;
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    public static final String USER = "olsona39";
    public static final String PASSWORD = "R`eqy]N*@R0.";

    public void RegistrationSystem(username, password) {
        String[] commands = new String[] {"Get Transcript", "Check Degree", "Requirements", "Add Course", "Remove Course", "Exit"};
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your student ID");
        String sID = scanner.nextLine();

        Connection connection = getConnection();
        if(verifyID(connection,sID)){
            System.out.println("Valid Username.");
        }else{
            System.out.println("Invalid Username.");
        }
        String operation = "";
        while(!operation.equals("Exit")){
            System.out.println("Select an operation: Get Transcript, Check Degree, Requirements, Add Course, Remove Course, or Exit");
            operation = scanner.nextLine();
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
            connection.close();
        }catch(Exception e){
            System.out.println(e);
        }
        return validID;
    }

}