package net.sqlitetutorial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

/**
 *
 * @author sqlitetutorial.net
 */

class Student {
    String perm_id;
    String name;
    String address;
    String major;
    String department;
    String pin;

    public Student(String a1, String a2, String a3, String a4, String a5, String a6) {
        perm_id = a1;
        name = a2;
        address = a3;
        major = a4;
        department = a5;
        pin = a6;
    }

    public String getPerm() {
        return perm_id;
    }
}

class Course {
    String course_number;
    String title;

    public String getCourseId() {
        return course_number;
    }
}

class CourseOffering {
    String course_number;
    int unique_enroll_code;
    String year_and_quarter;
    String location;
    int max_enrollment;
    String professor_fname;
    String professor_lname;

    public CourseOffering(String a1, int a2, String a3, String a4, int a5, String a6, String a7) {
        course_number = a1;
        unique_enroll_code = a2;
        year_and_quarter = a3;
        location = a4;
        max_enrollment = a5;
        professor_fname = a6;
        professor_lname = a7;
    }

    public int getUniqueEnrollCode() {
        return unique_enroll_code;
    }

    public String getCourseNumber() {
        return course_number;
    }
}

class Registrar {

    static Connection conn;

    String[] registrarPrompts = {"Add a student to a course", "Drop a student from a course", "List the courses taken by a student", "List the grades of a previous quarter for a student", "Generate a class list for a course", "Enter grades for a course", "Request a transcript to be printed for a student", "Generate a grade mailer for all students"};

    public Registrar(Connection c) {
        conn = c;
    }

    public void prompt() {
        Scanner sc= new Scanner(System.in); 
        System.out.println("Choose a Transaction");
        outputOptions();
        int promptRes = sc.nextInt();
        switch(promptRes) {
            case 1:{
                Student sParameter = promptForStudentPerm(sc);
                CourseOffering cParameter = promptForEnrollCode(sc);
                addStudenttoCoursebyEnrollCode(sParameter, cParameter);
                break;
            }
            case 2:{
                Student sParameter = promptForStudentPerm(sc);
                CourseOffering cParameter = promptForEnrollCode(sc);
                removeStudentfromCourse(sParameter, cParameter);
                break;
            }
            case 3: {
                Student sParameter = promptForStudentPerm(sc);
                listCoursesofStudent(sParameter);
                break;
            }
            case 4: {
                Student sParameter = promptForStudentPerm(sc);
                String quarterValue = promptForString(sc,"Quarter and Year");
                listGradesofStudent(sParameter,quarterValue);
                break;
            }
            case 5: {
                CourseOffering cParameter = promptForEnrollCode(sc);
                generateClassList(cParameter);
                break;
            }
            case 7: {
                Student sParameter = promptForStudentPerm(sc);
                transcriptGeneration(sParameter);
                break;
            }
            case 8: {
                String quarterValue = promptForString(sc,"Quarter and Year");
                gradeMailer(quarterValue);
                break;
            }
        }
    }

    public void outputOptions() {
        int i = 1;
        for (String s: registrarPrompts) {
            System.out.println(i + s);
            i++;
        }
        System.out.println();
    }

    public String promptForString(Scanner sc, String promptParameter) {
        System.out.print("Input " + promptParameter + ": ");
        sc.nextLine();
        String inp = sc.nextLine();
        return inp;
    }

    public Student promptForStudentPerm(Scanner sc) {
        System.out.print("Input Student Perm: ");
        String permNum = sc.next();
        sc.nextLine();
        return new Student(permNum,"","","","","");
    }

    public CourseOffering promptForEnrollCode(Scanner sc) {
        System.out.print("Input Enroll Code: ");
        int enrollCode = sc.nextInt();
        sc.nextLine();
        return new CourseOffering("",enrollCode,"","",0,"","");
    }

    public void printResult(ResultSet res) {
        try {
            ResultSetMetaData rsmd = res.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                System.out.print(rsmd.getColumnName(i));
            }
            System.out.println();
            while (res.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    System.out.print(res.getString(i));
                }
                System.out.println("");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //add a student to a course by Enroll Code
    public void addStudenttoCoursebyEnrollCode(Student s, CourseOffering c) {
        try {
            Statement stmt = conn.createStatement();
            String sqlStatement = "INSERT INTO Enrolled VALUES (" + s.getPerm() +"," + c.getUniqueEnrollCode() + ", NULL)";
            stmt.executeUpdate(sqlStatement);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //drop a student from a course
    public void removeStudentfromCourse(Student s, CourseOffering c) {
        try {
            Statement stmt = conn.createStatement();
            String sqlStatement = "DELETE FROM Enrolled E WHERE E.stud_id=" + s.getPerm() +" AND E.course_code=" + c.getUniqueEnrollCode();
            stmt.executeUpdate(sqlStatement);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //list the courses taken by a student,
    public void listCoursesofStudent(Student s) {
        try {
            Statement stmt = conn.createStatement();
            String sqlStatement = "SELECT C.course_number FROM CourseOfferings C WHERE C.unique_enroll_code IN (SELECT E.course_code FROM Enrolled E WHERE E.stud_id=" + s.getPerm() + ")" ;
            ResultSet res = stmt.executeQuery(sqlStatement);
            printResult(res);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //list the grades of the previous quarter for a student,
    public void listGradesofStudent(Student s, String prevQuarter) {
        try {
            Statement stmt = conn.createStatement();
            String sqlStatement = "SELECT TMPTABLE.course_number, G.grade FROM (SELECT C.course_number,C.unique_enroll_code FROM CourseOfferings C WHERE C.year_and_quarter='2022 Fall' AND C.unique_enroll_code IN (SELECT E.course_code FROM Enrolled E WHERE E.stud_id=" + s.getPerm() + ")) TMPTABLE INNER JOIN (SELECT E1.course_code, E1.grade FROM Enrolled E1 WHERE E1.stud_id="  + s.getPerm() + ") G ON G.course_code=TMPTABLE.unique_enroll_code";
            ResultSet res = stmt.executeQuery(sqlStatement);
            printResult(res);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //generate a class list for a course,
    public void listClassForCourse(CourseOffering c) {
        try {
            Statement stmt = conn.createStatement();
            String sqlStatement = "SELECT NESTEDQUERY.stud_id, S.name FROM Students S INNER JOIN (SELECT E.stud_id FROM Enrolled E WHERE E.course_code=" + c.getUniqueEnrollCode() +") NESTEDQUERY ON NESTEDQUERY.stud_id = S.stud_id" ;
            ResultSet res = stmt.executeQuery(sqlStatement);
            printResult(res);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //enter grades for a course, for all the students that attended it, by keying in the name of a file which contains a list of students and their grades,
    //request a transcript to be printed out for a student, and

    public void transcriptGeneration(Student s) {
        try {
            Statement stmt1 = conn.createStatement();
            String sqlStatement1 = "SELECT s.name FROM STUDENTS S WHERE s.perm_id=" + s.getPerm();
            ResultSet res2 = stmt1.executeQuery(sqlStatement1);
            System.out.println("Transcript for " + res2.getString(1) + ":");

            Statement stmt2 = conn.createStatement();
            String sqlStatement2 = "SELECT TMPTABLE.year_and_quarter, TMPTABLE.course_number, G.grade FROM (SELECT C.course_number,C.unique_enroll_code, C.year_and_quarter FROM CourseOfferings C WHERE C.unique_enroll_code IN (SELECT E.course_code FROM Enrolled E WHERE E.stud_id=" + s.getPerm() + ")) TMPTABLE INNER JOIN (SELECT E1.course_code, E1.grade FROM Enrolled E1 WHERE E1.stud_id="  + s.getPerm() + ") G ON G.course_code=TMPTABLE.unique_enroll_code ORDER BY TMPTABLE.year_and_quarter";
            ResultSet res = stmt2.executeQuery(sqlStatement2);
            printResult(res);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //generate a class list for a course,
    public void generateClassList(CourseOffering c) {
        try {
            Statement stmt = conn.createStatement();
            String sqlStatement = "SELECT PERMLIST.stud_id, S.name FROM (SELECT E.stud_id FROM Enrolled E WHERE E.course_code=" + c.getUniqueEnrollCode() +") PERMLIST INNER JOIN Students S ON S.perm_id = PERMLIST.stud_id";
            ResultSet res = stmt.executeQuery(sqlStatement);
            printResult(res);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    //generate a grade mailer for all students
    public void gradeMailer(String prevQuarter) {
        try {
            Statement stmt = conn.createStatement();
            String sqlStatement = "SELECT G.stud_id, TMPTABLE.course_number, G.grade FROM (SELECT C.course_number,C.unique_enroll_code FROM CourseOfferings C WHERE C.year_and_quarter='" + prevQuarter + "') TMPTABLE INNER JOIN (SELECT E1.course_code, E1.stud_id, E1.grade FROM Enrolled E1) G ON G.course_code=TMPTABLE.unique_enroll_code ORDER BY G.stud_id";
            ResultSet res = stmt.executeQuery(sqlStatement);
            System.out.println("Grade Mailer for " + prevQuarter + ":");
            ResultSetMetaData rsmd = res.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            String currPerm = "";
            while (res.next()) {
                if(currPerm.equals(res.getString(1))) {
                    for (int i = 1; i <= columnsNumber; i++) {
                        if (i > 1) System.out.print(",  ");
                        System.out.print(res.getString(i));
                    }
                }
                else {
                    currPerm = res.getString(1);
                    System.out.println("Grades for Student " + currPerm + ":");
                    for (int i = 2; i <= columnsNumber; i++) {
                        if (i > 2) System.out.print(",  ");
                        System.out.print(rsmd.getColumnName(i));
                    }
                    System.out.println();
                    for (int i = 2; i <= columnsNumber; i++) {
                        if (i > 2) System.out.print(",  ");
                        System.out.print(res.getString(i));
                    }
                }
                System.out.println("");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


}


public class Connect {
     /**
     * Connect to a sample database
     */
    public static Registrar registrarInterface;

    public static void connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:C:/Users/User/Documents/Academics/CS174A/sqlite/db/chinook.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            registrarInterface = new Registrar(conn);
            registrarInterface.prompt();
            
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
