package net.sqlitetutorial;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OldMain{
    public static Connection connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:/Users/sush/Documents/cs174a/finalproj/sqlite/db/chinook.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    public static Connection conn;
    public static void OldMain(String arg[]) {
        conn = connect();
        Gold gold = new Gold(conn);
        gold.addCourse("hi", 1223123);
    }
}