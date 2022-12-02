package net.sqlitetutorial;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.*;
import java.security.NoSuchAlgorithmException;


import net.sqlitetutorial.Enrolled;
public class Gold {

    List<String> QUARTERS = Arrays.asList("Winter", "Spring", "Fall");

    class YearQuarter {
        int year;
        String quarter;
        YearQuarter(int year, String quarter) {
            this.year = year;
            this.quarter = quarter;
        }
        public boolean lessThan(YearQuarter b) {
            if(this.year < b.year)
                return true;
            else if (this.year > b.year)
                return false;
            return QUARTERS.indexOf(this.quarter) < QUARTERS.indexOf(b.quarter);
        }
        @Override
        public boolean equals(Object other){
            if (other == null) return false;
            if (other == this) return true;
            if (!(other instanceof YearQuarter)) return false;
            YearQuarter otherMyClass = (YearQuarter)other;
            return otherMyClass.year == this.year && otherMyClass.quarter.equals(this.quarter);
        }
    }
    public class Pair<L,R> {
        private L l;
        private R r;
        public Pair(L l, R r){
            this.l = l;
            this.r = r;
        }
        public L getL(){ return l; }
        public R getR(){ return r; }
        public void setL(L l){ this.l = l; }
        public void setR(R r){ this.r = r; }
    }

    Connection conn;
    Gold(Connection conn) {
        this.conn = conn;
    }
    
    /*
     *      PRIVATE FUNCTIONS
     */
    private int getMinNumberofElectives(String stud_id) {
        int result = 0;
        String query = MessageFormat.format(
            "SELECT M.min_electives_to_graduate FROM Majors M INNER JOIN (SELECT S.major FROM Students S WHERE S.perm_id=''{0}'') TMP ON M.name=TMP.major", 
            stud_id);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.print("getMinNumberofElectives: ");
            System.out.println(e);
        }
        return result;
    }
    private void insert_enrolled(Enrolled en) {
        String query = MessageFormat.format(
            "INSERT INTO Enrolled (stud_id, course_code, grade) VALUES (''{0}'', ''{1}'', ''IP'')", 
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
    private List<Courses> list_enrolled_courses_by_quarter(String stud_id, String year_and_quarter) {
        // Retrieve course codes related to stud_id
        List<String> unique_enrollment_codes = new ArrayList<String>();
        List<String> course_numbers = new ArrayList<String>();
        List<Courses> courses = new ArrayList<Courses>();
        String query = MessageFormat.format(
            "SELECT * FROM Courses TMP INNER JOIN (SELECT C.course_number FROM (SELECT course_code FROM Enrolled WHERE stud_id = ''{0}'') ENROLLEDCOURSES INNER JOIN CourseOfferings C ON C.unique_enroll_code=ENROLLEDCOURSES.course_code AND C.year_and_quarter=''{1}'') COURSES2 ON COURSES2.course_number=TMP.course_number", 
            stud_id,
            year_and_quarter);
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
    private List<Courses> list_enrolled_courses(String stud_id) {
        // Retrieve course codes related to stud_id
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
    private List<CourseOfferings> list_enrolled_course_offerings(String stud_id) {
                // Retrieve course codes related to stud_id
                List<CourseOfferings> course_offerings = new ArrayList<CourseOfferings>();
                String query = MessageFormat.format(
                    "SELECT * FROM CourseOfferings CO INNER JOIN (SELECT E.course_code FROM Enrolled E WHERE E.stud_id = '918994') EE ON EE.course_code = CO.unique_enroll_code", 
                    stud_id);
                try (Statement stmt = conn.createStatement()) {
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        String course_number = rs.getString("course_number");
                        int unique_enroll_code = rs.getInt("unique_enroll_code");
                        String year_and_quarter = rs.getString("year_and_quarter");
                        String location = rs.getString("location");
                        int max_enrollment = rs.getInt("max_enrollment");
                        String professor_fname = rs.getString("professor_fname");
                        String professor_lname = rs.getString("professor_lname");
                        course_offerings.add(new CourseOfferings(course_number, unique_enroll_code, year_and_quarter, location, max_enrollment, professor_fname, professor_lname));
                    }
                } catch (SQLException e) {
                    System.out.print("list_enrolled_course_offerings 3: ");
                    System.out.println(e);
                }
                return course_offerings;
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
    private List<Courses> get_prerequisites(String course_number) {
        List<Courses> result = new ArrayList<Courses>();
        String query = MessageFormat.format(
            "SELECT * FROM Courses COURSES INNER JOIN (SELECT P.prereq FROM Prerequisites P WHERE P.dependent_course = ''{0}'') PREREQS ON COURSES.course_number = PREREQS.prereq", 
            course_number);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String title = rs.getString("title");
                String prereq_course_number = rs.getString("course_number");
                result.add(new Courses(prereq_course_number, title));
            }
        } catch (SQLException e) {
            System.out.print("get_prerequisites: ");
            System.out.println(e);
        }
        return result;
    }
    private List<Courses> list_elective_courses(String stud_id) {
        // Retrieve course codes related to stud_id
        List<Courses> result = new ArrayList<Courses>();
        String query = MessageFormat.format(
            "SELECT * FROM Courses TMP1 INNER JOIN (SELECT C.course_number FROM ElectiveCourses C WHERE C.major IN (SELECT S.major FROM STUDENTS S WHERE S.perm_id=''{0}'')) TMP2 ON TMP1.course_number = TMP2.course_number", 
            stud_id);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                result.add(new Courses(rs.getString("course_number"), rs.getString("title")));
            }
        } catch (SQLException e) {
            System.out.print("list_elective_courses: ");
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

    public YearQuarter parseYearQuarter(String year_and_quarter) {
        String[] split = year_and_quarter.split(" ");
        return new YearQuarter(Integer.parseInt(split[0]), split[1]);
    }
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
        return list_enrolled_courses_by_quarter(stud_id, year_and_quarter);
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
    public void makeAPlan(String stud_id, String current_year_and_quarter) {
        List<Courses> courses = listCoursesEnrolled(stud_id);
        List<Courses> mandatory = list_mandatory_courses(stud_id);
        List<Courses> needed_courses = new ArrayList<Courses>(mandatory);
        List<Courses> done_courses = new ArrayList<Courses>(courses);
        List<Courses> accounted_for_courses = new ArrayList<Courses>(done_courses);
        HashMap<String, Pair<CourseOfferings, String>> done_course_offerings = new HashMap<String, Pair<CourseOfferings, String>>();
        needed_courses.removeAll(done_courses);
        List<Pair<CourseOfferings, String>> result = new ArrayList<Pair<CourseOfferings, String>>();
        List<Pair<CourseOfferings, String>> result_electives = new ArrayList<Pair<CourseOfferings, String>>();
        // build pair list
        List<CourseOfferings> temp = list_enrolled_course_offerings(stud_id);
        CourseOfferings earliest_course = new CourseOfferings("9999 Winter");
        for(int i = 0; i < temp.size(); i++) {
            int k = 0;
            for(; k < done_courses.size(); k++) {
                if(done_courses.get(k).course_number.equals(temp.get(i).course_number))
                    break;
            }
            done_course_offerings.put(temp.get(i).course_number, new Pair<CourseOfferings, String>(temp.get(i), done_courses.get(k).title));
            result.add(new Pair<CourseOfferings, String>(temp.get(i), done_courses.get(k).title));
            // find earliest course
            if(parseYearQuarter(temp.get(i).year_and_quarter).lessThan(parseYearQuarter(earliest_course.year_and_quarter)))
                earliest_course = temp.get(i);
        }
        // add mandatory courses

        YearQuarter graduation = parseYearQuarter(current_year_and_quarter);
        // for(int i = 0; i < needed_courses.size(); i++) {
        //     List<CourseOfferings> offerings = list_course_offerings_by_number(needed_courses.get(i).course_number);
        //     CourseOfferings earliest_offering = offerings.get(0);
        //     for(int j = 1; j < offerings.size(); j++) {
        //         if(parseYearQuarter(earliest_offering.year_and_quarter).lessThan(parseYearQuarter(offerings.get(i).year_and_quarter)))
        //             earliest_offering = offerings.get(i);
        //     }
        //     result.add(new Pair<CourseOfferings, String>(earliest_offering, needed_courses.get(i).title));
        //     if(graduation.lessThan(parseYearQuarter(earliest_offering.year_and_quarter)));
        //         graduation = parseYearQuarter(earliest_offering.year_and_quarter);
        // }
        List<Integer> done = new ArrayList<Integer>(Collections.nCopies(needed_courses.size(), 0));
        // for(int i = 0; i < result.size(); i++ ){
        //     System.out.println(result.get(i).l.course_number);
        // }
        while(result.size() < needed_courses.size() + done_courses.size()) {
            outer:
            for(int i = 0; i < needed_courses.size(); i++) {
                if(done.get(i) > 0)
                    continue;
                List<Courses> prereqs = get_prerequisites(needed_courses.get(i).course_number);
                CourseOfferings latest_prereq = new CourseOfferings("1111 Winter");
                for(int j = 0; j < prereqs.size(); j++) {
                    if(accounted_for_courses.indexOf(prereqs.get(j)) == -1)
                        continue outer;
                    CourseOfferings here = done_course_offerings.get(prereqs.get(j).course_number).l;
                    if(parseYearQuarter(latest_prereq.year_and_quarter).lessThan(parseYearQuarter(here.year_and_quarter)))
                        latest_prereq = here;
                }
                List<CourseOfferings> offerings = list_course_offerings_by_number(needed_courses.get(i).course_number);
                CourseOfferings earliest_offering = latest_prereq;
                for(int j = 0; j < offerings.size(); j++) {
                    if(parseYearQuarter(earliest_offering.year_and_quarter).lessThan(parseYearQuarter(offerings.get(j).year_and_quarter)))
                        earliest_offering = offerings.get(j);
                }
                result.add(new Pair<CourseOfferings, String>(earliest_offering, needed_courses.get(i).title));
                accounted_for_courses.add(needed_courses.get(i));
                done.set(i, 1);
                done_course_offerings.put(earliest_offering.course_number, new Pair<CourseOfferings, String>(earliest_offering, needed_courses.get(i).title));
                if(graduation.lessThan(parseYearQuarter(earliest_offering.year_and_quarter)));
                    graduation = parseYearQuarter(earliest_offering.year_and_quarter);
            }
        }

        // load sched with electives
        int electives_left = getMinNumberofElectives(stud_id);
        List<Courses> electives = list_elective_courses(stud_id);
        done = new ArrayList<Integer>(Collections.nCopies(electives.size(), 0));
        electives_done:
        for(int year = parseYearQuarter(current_year_and_quarter).year; year < graduation.year + 1; year++) {
            for(int q = 0, classes_left = 5; q < 3; q++) {
                for(int i = 0; i < result.size(); i++) {
                    if(parseYearQuarter(result.get(i).l.year_and_quarter).equals(new YearQuarter(year, QUARTERS.get(q))))
                        classes_left--;
                }

                outer:
                for(int i = 0; i < electives.size() && classes_left > 0; i++) {
                    if(done.get(i) > 0)
                        continue;
                    if(!(electives_left > 0))
                        break electives_done;
                    // check if offered in current quarter
                    List<CourseOfferings> offerings = list_course_offerings_by_number(electives.get(i).course_number);
                    CourseOfferings found_offering = new CourseOfferings("1111 Winter");
                    for(int j = 0; j < offerings.size(); j++) {
                        if(new YearQuarter(year, QUARTERS.get(q)).equals(parseYearQuarter(offerings.get(j).year_and_quarter))) {
                            found_offering = offerings.get(j);
                            break;
                        }
                    }
                    if(found_offering.course_number.equals("null_test"))
                    continue;
                    // check prereqs
                    List<Courses> prereqs = get_prerequisites(electives.get(i).course_number);
                    CourseOfferings latest_prereq = new CourseOfferings("1111 Winter");
                    for(int j = 0; j < prereqs.size(); j++) {
                        if(accounted_for_courses.indexOf(prereqs.get(j)) == -1)
                            continue outer;
                        CourseOfferings here = done_course_offerings.get(prereqs.get(j).course_number).l;
                        if(parseYearQuarter(latest_prereq.year_and_quarter).lessThan(parseYearQuarter(here.year_and_quarter))) {
                            latest_prereq = here;
                        }
                    }
                    if((new YearQuarter(year, QUARTERS.get(q))).lessThan(parseYearQuarter(latest_prereq.year_and_quarter)))
                        continue;
                    result_electives.add(new Pair<CourseOfferings, String>(found_offering, electives.get(i).title));
                    accounted_for_courses.add(electives.get(i));
                    done_course_offerings.put(found_offering.course_number, new Pair<CourseOfferings, String>(found_offering, electives.get(i).title));
                    done.set(i, 1);
                    electives_left--;
                    classes_left--;
                }


            }
        }

        // print results
        System.out.println("\n- * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * -\n");
        System.out.println("\033[1mQUARTER BY QUARTER PLAN FOR STUDENT " + stud_id + "\033[0m\n(* denotes major a requirement)\n");
        System.out.println("Major elective requirement: " + getMinNumberofElectives(stud_id) + " electives.\n");
        int q = QUARTERS.indexOf(parseYearQuarter(current_year_and_quarter).quarter);
        for(int year = parseYearQuarter(earliest_course.year_and_quarter).year; year < graduation.year + 1; year++) {
            for(q = 0; q < 3; q++) {
                System.out.println(year + " " + QUARTERS.get(q) + ":");
                for(int i = 0; i < result.size(); i++) {
                    if(parseYearQuarter(result.get(i).l.year_and_quarter).equals(new YearQuarter(year, QUARTERS.get(q)))) {
                        System.out.println("\t \033[1m* " + result.get(i).l.course_number + ": " + result.get(i).r + "\033[0m");
                        List<Courses> prereqs = get_prerequisites(result.get(i).l.course_number);
                        if(prereqs.size() > 0) 
                            System.out.print("\t\t ↳ Prerequisites: ");
                        for(int j = 0; j < prereqs.size(); j++) {
                            System.out.print(prereqs.get(j).course_number + ": " + prereqs.get(j).title);
                            if(j < prereqs.size()-1)
                                System.out.print(", ");
                            else
                                System.out.print("\n");
                        }
                    }
                }
                for(int i = 0; i < result_electives.size(); i++) {
                    if(parseYearQuarter(result_electives.get(i).l.year_and_quarter).equals(new YearQuarter(year, QUARTERS.get(q)))) {
                        System.out.println("\t\033[1m" + result_electives.get(i).l.course_number + ": " + result_electives.get(i).r + "\033[0m");
                        List<Courses> prereqs = get_prerequisites(result_electives.get(i).l.course_number);
                        if(prereqs.size() > 0) 
                            System.out.print("\t\t ↳ Prerequisites: ");
                        for(int j = 0; j < prereqs.size(); j++) {
                            System.out.print(prereqs.get(j).course_number + ": " + prereqs.get(j).title);
                            if(j < prereqs.size()-1)
                                System.out.print(", ");
                            else
                                System.out.print("\n");
                        }
                    }
                }
            }
        }
        System.out.println("\n- * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * -\n");
    }
    public void requirementsCheck(String stud_id, String year_and_quarter, boolean count_in_progress) {
        Students s = get_student(stud_id);
        List<Courses> courses = listCoursesEnrolled(stud_id);
        List<Courses> in_progress_courses = listCoursesEnrolledQuarter(stud_id, year_and_quarter);
        List<Courses> mandatory = list_mandatory_courses(stud_id);
        List<Courses> done_courses = new ArrayList<Courses>(courses);
        List<Courses> needed_courses = new ArrayList<Courses>(mandatory);
        needed_courses.removeAll(done_courses);
        done_courses.removeAll(in_progress_courses);
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
