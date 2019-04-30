import java.sql.*;
import java.lang.*;
import java.util.*;


public class JDBC{

    public static String ConnectionString;
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    public static final String USER = "olsona39";
    public static final String PASSWORD = "R`eqy]N*@R0.";

    public static void main(String [] args){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your student ID");
        String sID = scanner.nextLine();
        System.out.println("username is " + sID + ".");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            ConnectionString = "jdbc:mysql://mysql.cs.wwu.edu:3306/" + USER + "?useSSL=false";
            Connection conn = DriverManager.getConnection(ConnectionString, USER, PASSWORD);
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery("select * from takes");
            ResultSetMetaData rsmd = result.getMetaData();
            while(result.next()){
                int ID = result.getInt("ID");
                System.out.println(ID);
            }
            System.out.println("yes");
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e);
        }
    }

}