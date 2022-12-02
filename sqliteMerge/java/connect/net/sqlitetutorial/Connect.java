package net.sqlitetutorial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author sqlitetutorial.net
 */

public class Connect {
     /**
     * Connect to a sample database
     */
    public static Registrar registrarInterface;
    public static Gold goldInterface;

    public static void connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            System.out.print("Type 1 for Gold or 2 for Registrar:");
            Scanner sc= new Scanner(System.in); 
            int userChoice = sc.nextInt();
            if(userChoice == 1){
                goldInterface = new Gold(conn);
                goldInterface.prompt();
            }
            else {
                registrarInterface = new Registrar(conn);
                registrarInterface.prompt();
            }
            sc.close();
            
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
        connect();
    }
}
