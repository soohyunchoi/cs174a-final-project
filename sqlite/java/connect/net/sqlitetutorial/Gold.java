package net.sqlitetutorial;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.*;
import java.security.NoSuchAlgorithmException;


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
            en.get_unique_enroll_code());
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.print("insert_enrolled: ");
            System.out.println(e);
        }
    }
    private void delete_enrolled(Enrolled en) {
        String query = MessageFormat.format(
            "DELETE FROM Enrolled WHERE stud_id = ''{0}'' AND course_code = ''{1}''", 
            en.get_stud_id(), 
            en.get_unique_enroll_code());
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.print("delete_enrolled: ");
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
                enrollments.add(new Enrolled(rs.getString("stud_id"), rs.getInt("course_code"), rs.getString("grade")));
            }
        } catch (SQLException e) {
            System.out.print("list_enrollments ");
            System.out.println(e);
        }
        return enrollments;
    }
    private List<Enrolled> list_enrollments_by_course(String stud_id, String unique_enroll_code) {
        List<Enrolled> enrollments = new ArrayList<Enrolled>();
        String query = MessageFormat.format(
            "SELECT * FROM Enrolled WHERE stud_id = ''{0}'' AND course_code = ''{1}''", 
            stud_id,
            unique_enroll_code);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                enrollments.add(new Enrolled(rs.getString("stud_id"), rs.getInt("course_code"), rs.getString("grade")));
            }
        } catch (SQLException e) {
            System.out.print("list_enrollments_by_course: ");
            System.out.println(e);
        }
        return enrollments;
    }
    private List<Courses> list_enrolled_courses(String stud_id) {
        // Retrieve course codes related to stud_id
        List<String> unique_enrollment_codes = new ArrayList<String>();
        List<String> course_numbers = new ArrayList<String>();
        List<Courses> courses = new ArrayList<Courses>();
        String query = MessageFormat.format(
            "SELECT * FROM Courses TMP INNER JOIN (SELECT C.course_number FROM (SELECT course_code FROM Enrolled WHERE stud_id = ''{0}'') ENROLLEDCOURSES INNER JOIN CourseOfferings C ON C.unique_enroll_code=ENROLLEDCOURSES.course_code) COURSES2 ON COURSES2.course_number=TMP.course_number", 
            stud_id);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                courses.add(new Courses(rs.getString("course_number"), rs.getString("title")));
            }
        } catch (SQLException e) {
            System.out.print("list_enrolled_courses 3: ");
            System.out.println(e);
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
            System.out.print("list_course_offerings_by_number: ");
            System.out.println(e);
        }
        return course_offerings;
    }
    private CourseOfferings get_course_offering(int unique_enroll_code) {
        CourseOfferings result = null;
        String query = MessageFormat.format(
            "SELECT * FROM CourseOfferings WHERE unique_enroll_Code = ''{0}''", 
            unique_enroll_code);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            String course_number = rs.getString("course_number");
            String year_and_quarter = rs.getString("year_and_quarter");
            String location = rs.getString("location");
            int max_enrollment = rs.getInt("max_enrollment");
            String professor_fname = rs.getString("professor_fname");
            String professor_lname = rs.getString("professor_lname");
            result = new CourseOfferings(course_number, unique_enroll_code, year_and_quarter, location, max_enrollment, professor_fname, professor_lname);
        } catch (SQLException e) {
            System.out.print("get_course_offering: ");
            System.out.println(e);
        }
        return result;
    }
    private Students get_student(String stud_id) {
        Students result = null;
        String query = MessageFormat.format(
            "SELECT * FROM Students WHERE perm_id = ''{0}''", 
            stud_id);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            String perm_id = rs.getString("perm_id");
            String name = rs.getString("name");
            String address = rs.getString("address");
            String major = rs.getString("major");
            String department = rs.getString("department");
            String pin = rs.getString("pin");
            result = new Students(perm_id, name, address, major, department, pin);
        } catch (SQLException e) {
            System.out.print("get_student: ");
            System.out.println(e);
        }
        return result;
    }
    private List<Courses> list_mandatory_courses(String stud_id) {
        // Retrieve course codes related to stud_id
        List<Courses> result = new ArrayList<Courses>();
        String query = MessageFormat.format(
            "SELECT * FROM Courses TMP1 INNER JOIN (SELECT C.course_number FROM MandatoryCourses C WHERE C.major = (SELECT S.major FROM STUDENTS S WHERE S.perm_id=''{0}'')) TMP2 ON TMP1.course_number = TMP2.course_number", 
            stud_id);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                result.add(new Courses(rs.getString("course_number"), rs.getString("title")));
            }
        } catch (SQLException e) {
            System.out.print("list_mandatory_courses: ");
            System.out.println(e);
        }
        return result;
    }
    private void update_student_pin(String stud_id, String pin_hash) {
        String query = MessageFormat.format(
            "UPDATE Students SET pin = ''{0}'' WHERE perm_id = ''{1}''", 
            pin_hash, 
            stud_id);
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.print("update_student_pint: ");
            System.out.println(e);
        }
    }
    /*
     *      PUBLIC FUNCTIONS
     */

    public void addCourse(String stud_id, int unique_enrollment_code) {
        Enrolled enrolled = new Enrolled(stud_id, unique_enrollment_code);
        insert_enrolled(enrolled);
    }
    public void dropCourse(String stud_id, int unique_enrollment_code) {
        Enrolled enrolled = new Enrolled(stud_id, unique_enrollment_code);
        delete_enrolled(enrolled);
    }
    public List<Courses> listCoursesEnrolled(String stud_id) {
        List<Courses> courses = list_enrolled_courses(stud_id);
        for(int i = 0; i < courses.size(); i++) {
            // System.out.println(courses.get(i).course_number + ", " + courses.get(i).title);
        }
        return courses;
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
            // System.out.println(result.get(i).course_number + ", " + result.get(i).title);
        }
        return result;
    }
    public List<String> listGradesEnrolledQuarter(String stud_id, String year_and_quarter) {
        List<Enrolled> enrollments = list_enrollments(stud_id);
        List<String> grades = new ArrayList<String>();
        for(int i = 0; i < enrollments.size(); i++) {
            CourseOfferings offering = get_course_offering(enrollments.get(i).unique_enroll_code);
            if(offering.year_and_quarter.equals(year_and_quarter)) {
                grades.add(enrollments.get(i).grade);
            }
        }
        return grades;
    }
    public void requirementsCheck(String stud_id, String year_and_quarter, boolean count_in_progress) {
        Students s = get_student(stud_id);
        List<Courses> courses = listCoursesEnrolled(stud_id);
        List<Courses> in_progress_courses = listCoursesEnrolledQuarter(stud_id, year_and_quarter);
        List<Courses> mandatory = list_mandatory_courses(stud_id);
        List<Courses> done_courses = new ArrayList<Courses>(courses);
        List<Courses> needed_courses = new ArrayList<Courses>(mandatory);
        done_courses.removeAll(in_progress_courses);
        needed_courses.removeAll(done_courses);
        if(count_in_progress)
            needed_courses.removeAll(in_progress_courses);
        System.out.println("Required courses for " + stud_id + ", with major " + s.major);
        for(int i = 0; i < mandatory.size(); i++) {
            System.out.println(mandatory.get(i).course_number + ": " + mandatory.get(i).title);
        }
        System.out.println();
        System.out.println("Done courses for " + stud_id);
        for(int i = 0; i < done_courses.size(); i++) {
            System.out.println(done_courses.get(i).course_number + ": " + done_courses.get(i).title);
        }
        System.out.println();
        System.out.println("WIP courses for " + stud_id);
        for(int i = 0; i < in_progress_courses.size(); i++) {
            System.out.println(in_progress_courses.get(i).course_number + ": " + in_progress_courses.get(i).title);
        }
        System.out.println();
        System.out.println("Needed courses for " + stud_id);
        for(int i = 0; i < needed_courses.size(); i++) {
            System.out.println(needed_courses.get(i).course_number + ": " + needed_courses.get(i).title);
        }
        System.out.println();
    }
    public void updatePin(String stud_id, String pin) {
        String pin_hash = "";
        try {
            pin_hash = GFG2.toHexString(GFG2.getSHA(pin));
        }   catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown for incorrect algorithm: " + e);
        }
        update_student_pin(stud_id, pin_hash);
        System.out.println("Pin hash: <" + pin_hash + ">");
        Students updated = get_student(stud_id);
        System.out.println(updated.perm_id + " new pin : <" + updated.pin + ">");
    }
}
