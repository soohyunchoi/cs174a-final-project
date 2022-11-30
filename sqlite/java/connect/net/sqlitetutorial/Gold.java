package net.sqlitetutorial;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.*;

import net.sqlitetutorial.Enrolled;
public class Gold {
    Connection conn;
    Gold(Connection conn) {
        this.conn = conn;
    }
    
    /*
     *      PRIVATE FUNCTIONS
     */
    private void insert_enrolled(Enrolled en) {
        String query = MessageFormat.format(
            "INSERT INTO Enrolled (stud_id, course_code, grade) VALUES (''{0}'', ''{1}'', NULL)", 
            en.get_stud_id(), 
            en.get_course_code());
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    private void delete_enrolled(Enrolled en) {
        String query = MessageFormat.format(
            "DELETE FROM Enrolled WHERE stud_id = ''{0}'' AND course_code = ''{1}''", 
            en.get_stud_id(), 
            en.get_course_code());
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    private List<Enrolled> list_enrollments(String stud_id) {
        List<Enrolled> enrollments = new ArrayList<Enrolled>();
        String query = MessageFormat.format(
            "SELECT * FROM Enrolled WHERE stud_id = ''{0}''", 
            stud_id);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                enrollments.add(new Enrolled(rs.getString("stud_id"), rs.getString("course_code"), rs.getString("grade")));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return enrollments;
    }
    private List<Enrolled> list_enrollments_by_course(String stud_id, String course_code) {
        List<Enrolled> enrollments = new ArrayList<Enrolled>();
        String query = MessageFormat.format(
            "SELECT * FROM Enrolled WHERE stud_id = ''{0}'' AND course_code = ''{1}''", 
            stud_id,
            course_code);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                enrollments.add(new Enrolled(rs.getString("stud_id"), rs.getString("course_code"), rs.getString("grade")));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return enrollments;
    }
    private List<Courses> list_enrolled_courses(String stud_id) {
        // Retrieve course codes related to stud_id
        List<String> course_codes = new ArrayList<String>();
        List<Courses> courses = new ArrayList<Courses>();
        String query = MessageFormat.format(
            "SELECT course_code FROM Enrolled WHERE stud_id = ''{0}''", 
            stud_id);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String course_code = rs.getString("course_code");
                course_codes.add(course_code);
              }
        } catch (SQLException e) {
            System.out.println(e);
        }
        for(int i = 0; i < course_codes.size(); i++) {
            query = MessageFormat.format(
                "SELECT * FROM Courses WHERE course_number = ''{0}''", 
                course_codes.get(i));
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    courses.add(new Courses(course_codes.get(i), rs.getString("title")));
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
        }
        return courses;
    }
    private List<CourseOfferings> list_course_offerings_by_number(String course_number) {
        // Retrieve course codes related to stud_id
        List<CourseOfferings> course_offerings = new ArrayList<CourseOfferings>();
        String query = MessageFormat.format(
            "SELECT * FROM CourseOfferings WHERE course_number = ''{0}''", 
            course_number);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String course_code = rs.getString("course_number");
                int unique_enroll_code = rs.getInt("unique_enroll_code");
                String year_and_quarter = rs.getString("year_and_quarter");
                String location = rs.getString("location");
                int max_enrollment = rs.getInt("max_enrollment");
                String professor_fname = rs.getString("professor_fname");
                String professor_lname = rs.getString("professor_lname");
                course_offerings.add(new CourseOfferings(course_number, unique_enroll_code, year_and_quarter, location, max_enrollment, professor_fname, professor_lname));
              }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return course_offerings;
    }
    /*
     *      PUBLIC FUNCTIONS
     */

    public void addCourse(String stud_id, String course_code) {
        Enrolled enrolled = new Enrolled(stud_id, course_code);
        insert_enrolled(enrolled);
    }
    public void dropCourse(String stud_id, String course_code) {
        Enrolled enrolled = new Enrolled(stud_id, course_code);
        delete_enrolled(enrolled);
    }
    public void listCoursesEnrolled(String stud_id) {
        List<Courses> courses = list_enrolled_courses(stud_id);
        for(int i = 0; i < courses.size(); i++) {
            System.out.println(courses.get(i).course_number + ", " + courses.get(i).title);
        }
    }
    public List<Courses> listCoursesEnrolledQuarter(String stud_id, String year_and_quarter) {
        List<Courses> courses = list_enrolled_courses(stud_id);
        List<Courses> result = new ArrayList<Courses>();
        for(int i = 0; i < courses.size(); i++) {
            List<CourseOfferings> course_offerings = list_course_offerings_by_number(courses.get(i).course_number);
            for(int j = 0; j < course_offerings.size(); j++) {
                if(course_offerings.get(j).year_and_quarter.equals(year_and_quarter)) {
                    result.add(courses.get(i));
                    break;
                }
            }
        }
        for(int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i).course_number + ", " + result.get(i).title);
        }
        return result;
    }
    public List<String> listGradesEnrolledQuarter(String stud_id, String year_and_quarter) {
        List<Courses> courses = listCoursesEnrolledQuarter(stud_id, year_and_quarter);
        for(int i = 0; i < courses.size(); i++) {

        }
        List<String> result = new ArrayList<String>();

        return result;
    }
}
