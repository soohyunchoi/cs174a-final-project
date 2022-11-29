package net.sqlitetutorial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 *
 * @author sqlitetutorial.net
 */
public class Connect {
     /**
     * Connect to a sample database
     */
    public static void connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:C:/Users/User/Documents/Academics/CS174A/sqlite/db/chinook.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            String sql = "select * from Courses";
            ResultSet res = stmt.executeQuery(sql);
            
            while(res.next())
            {
                System.out.println(res.getString(1)+"\t"+res.getString(2));
            }
            //CSVLoader loader = new CSVLoader(conn);
            //loader.loadCSV("C:/Users/User/Documents/Academics/CS174A/SmallTestCase-CourseHistory.csv", "CourseHistory", true);

            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.print("hi");
        connect();
    }
}