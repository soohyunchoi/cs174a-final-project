package net.sqlitetutorial;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.*;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;

import net.sqlitetutorial.Enrolled;
public class Gold {
    Connection conn;
    String stud_id;
    String[] goldPrompts = {"Add a course", "Drop a course", "List the courses enrolled in given quarter", "List the grades of courses enrolled from previous quarter", "Requirements check", "Make a plan to graduate", "Change PIN", "Exit Gold"};
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

    Gold(Connection conn) {
        this.conn = conn;
    }
    public void prompt() {
        Scanner sc= new Scanner(System.in); 
        stud_id = promptForString(sc,"Student Perm Number");
        if((get_student()).getPerm()==null) {
            System.out.println("Not a valid perm");
            return;
        }
        String pinInp = promptForString(sc,"pin for verification");
        String pinHash = "";
        try {
            pinHash = GFG2.toHexString(GFG2.getSHA(pinInp));
        }   catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown for incorrect algorithm: " + e);
        }
        System.out.println("\n\n");
        if(!(get_student()).getPinHash().equals(pinHash)) {
            System.out.println("Not correct pin");
            return;
        }
        System.out.println("Hi " + (get_student()).getName() + ", Welcome to Gold");
        while(true) {
            System.out.println("\n\n");
            System.out.println("Choose a Transaction or Enter 8 to Exit");
            outputOptions();
            int promptRes = sc.nextInt();
            switch(promptRes) {
                case 1:{
                    String uniqueString = promptForString(sc,"Unique Enrollment Code");
                    int unique_enroll_code = Integer.parseInt(uniqueString);
                    addCourse(unique_enroll_code);
                    break;
                }
                case 2:{
                    String uniqueString = promptForString(sc,"Unique Enrollment Code");
                    int unique_enroll_code = Integer.parseInt(uniqueString);
                    dropCourse(unique_enroll_code);
                    break;
                }
                case 3: {
                    String year_and_quarter = promptForString(sc,"Year and Quarter");
                    listCoursesEnrolledQuarter(year_and_quarter);
                    break;
                }
                case 4: {
                    String year_and_quarter = promptForString(sc,"Year and Quarter");
                    System.out.println(year_and_quarter);
                    listGradesEnrolledQuarter(year_and_quarter);
                    break;
                }
                case 5: {
                    String year_and_quarter = promptForString(sc,"Year and Quarter");
                    String count_in_progress_string = promptForString(sc,"Whether you want current courses to count (yes/no)");
                    Boolean count_in_progress = true;
                    if(count_in_progress_string.equals("no")) {
                        count_in_progress = false;
                    }
                    requirementsCheck(year_and_quarter,count_in_progress);
                    break;
                }
                case 6: {
                    makeAPlan("2022 Fall");
                    break;
                }
                case 7: {
                    String pin = promptForString(sc,"Pin");
                    updatePin(pin);
                    break;
                }
                case 8: {
                    sc.close();
                    return;
                }
            }
        }
    }

    private void outputOptions() {
        int i = 1;
        for (String s: goldPrompts) {
            System.out.println(i + " " + s);
            i++;
        }
        System.out.println();
    }

    private String promptForString(Scanner sc, String promptParameter) {
        System.out.print("Input " + promptParameter + ": ");
        String inp = sc.next();
        if(promptParameter == "Year and Quarter") {
            inp += " " + sc.next();
        }
        sc.nextLine();
        return inp;
    }

    /*
     *      PRIVATE FUNCTIONS
     */
    private void insert_enrolled(Enrolled en) {
        String query = MessageFormat.format(
            "INSERT INTO Enrolled (stud_id, course_code, grade) VALUES (''{0}'', ''{1,number,#}'', ''IP'')", 
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
            "DELETE FROM Enrolled WHERE stud_id = ''{0}'' AND course_code = ''{1,number,#}''", 
            en.get_stud_id(), 
            en.get_unique_enroll_code());
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.print("delete_enrolled: ");
            System.out.println(e);
        }
    }
    private List<Enrolled> list_enrollments() {
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
    private List<Enrolled> list_enrollments_by_course(String unique_enroll_code) {
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
    private List<Course> list_enrolled_courses_by_quarter(String year_and_quarter) {
        // Retrieve course codes related to stud_id
        List<String> unique_enrollment_codes = new ArrayList<String>();
        List<String> course_numbers = new ArrayList<String>();
        List<Course> courses = new ArrayList<Course>();
        String query = MessageFormat.format(
            "SELECT * FROM Courses TMP INNER JOIN (SELECT C.course_number FROM (SELECT course_code FROM Enrolled WHERE stud_id = ''{0}'') ENROLLEDCOURSES INNER JOIN CourseOfferings C ON C.unique_enroll_code=ENROLLEDCOURSES.course_code AND C.year_and_quarter=''{1}'') COURSES2 ON COURSES2.course_number=TMP.course_number", 
            stud_id,
            year_and_quarter);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                courses.add(new Course(rs.getString("course_number"), rs.getString("title")));
            }
        } catch (SQLException e) {
            System.out.print("list_enrolled_courses 3: ");
            System.out.println(e);
        }
        return courses;
    }

    private List<Course> list_enrolled_courses() {
        // Retrieve course codes related to stud_id
        List<String> unique_enrollment_codes = new ArrayList<String>();
        List<String> course_numbers = new ArrayList<String>();
        List<Course> courses = new ArrayList<Course>();
        String query = MessageFormat.format(
            "SELECT * FROM Courses TMP INNER JOIN (SELECT C.course_number FROM (SELECT course_code FROM Enrolled WHERE stud_id = ''{0}'') ENROLLEDCOURSES INNER JOIN CourseOfferings C ON C.unique_enroll_code=ENROLLEDCOURSES.course_code) COURSES2 ON COURSES2.course_number=TMP.course_number", 
            stud_id);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                courses.add(new Course(rs.getString("course_number"), rs.getString("title")));
            }
        } catch (SQLException e) {
            System.out.print("list_enrolled_courses 3: ");
            System.out.println(e);
        }
        return courses;
    }
    private List<CourseOffering> list_enrolled_course_offerings() {
        // Retrieve course codes related to stud_id
        List<CourseOffering> course_offerings = new ArrayList<CourseOffering>();
        String query = MessageFormat.format(
            "SELECT * FROM CourseOfferings CO INNER JOIN (SELECT E.course_code FROM Enrolled E WHERE E.stud_id = ''{0}'') EE ON EE.course_code = CO.unique_enroll_code", 
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
                course_offerings.add(new CourseOffering(course_number, unique_enroll_code, year_and_quarter, location, max_enrollment, professor_fname, professor_lname));
            }
        } catch (SQLException e) {
            System.out.print("list_enrolled_course_offerings 3: ");
            System.out.println(e);
        }
        return course_offerings;
    }

    private List<CourseOffering> list_course_offerings_by_number(String course_number) {
        // Retrieve course codes related to stud_id
        List<CourseOffering> course_offerings = new ArrayList<CourseOffering>();
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
                course_offerings.add(new CourseOffering(course_number, unique_enroll_code, year_and_quarter, location, max_enrollment, professor_fname, professor_lname));
              }
        } catch (SQLException e) {
            System.out.print("list_course_offerings_by_number: ");
            System.out.println(e);
        }
        return course_offerings;
    }
    private CourseOffering get_course_offering(int unique_enroll_code) {
        CourseOffering result = null;
        String query = MessageFormat.format(
            "SELECT * FROM CourseOfferings WHERE unique_enroll_Code = ''{0,number,#}''", 
            unique_enroll_code);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            String course_number = rs.getString("course_number");
            String year_and_quarter = rs.getString("year_and_quarter");
            String location = rs.getString("location");
            int max_enrollment = rs.getInt("max_enrollment");
            String professor_fname = rs.getString("professor_fname");
            String professor_lname = rs.getString("professor_lname");
            result = new CourseOffering(course_number, unique_enroll_code, year_and_quarter, location, max_enrollment, professor_fname, professor_lname);
        } catch (SQLException e) {
            System.out.print("get_course_offering: ");
            System.out.println(e);
        }
        return result;
    }
    private Student get_student() {
        Student result = null;
        String query = MessageFormat.format(
            "SELECT * FROM Students WHERE perm_id = ''{0}''", 
            stud_id);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            String perm_id = rs.getString("perm_id");
            String name = rs.getString("name");
            String address = rs.getString("address");
            String major = rs.getString("major");
            String pin = rs.getString("pin");
            result = new Student(perm_id, name, address, major, pin);
        } catch (SQLException e) {
            System.out.print("get_student: ");
            System.out.println(e);
        }
        return result;
    }
    private int getMinNumberofElectives() {
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
    private List<Course> list_mandatory_courses() {
        // Retrieve course codes related to stud_id
        List<Course> result = new ArrayList<Course>();
        String query = MessageFormat.format(
            "SELECT * FROM Courses TMP1 INNER JOIN (SELECT C.course_number FROM MandatoryCourses C WHERE C.major = (SELECT S.major FROM STUDENTS S WHERE S.perm_id=''{0}'')) TMP2 ON TMP1.course_number = TMP2.course_number", 
            stud_id);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                result.add(new Course(rs.getString("course_number"), rs.getString("title")));
            }
        } catch (SQLException e) {
            System.out.print("list_mandatory_courses: ");
            System.out.println(e);
        }
        return result;
    }

    private List<Course> get_prerequisites(String course_number) {
        List<Course> result = new ArrayList<Course>();
        String query = MessageFormat.format(
            "SELECT * FROM Courses COURSES INNER JOIN (SELECT P.prereq FROM Prerequisites P WHERE P.dependent_course = ''{0}'') PREREQS ON COURSES.course_number = PREREQS.prereq", 
            course_number);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String title = rs.getString("title");
                String prereq_course_number = rs.getString("course_number");
                result.add(new Course(prereq_course_number, title));
            }
        } catch (SQLException e) {
            System.out.print("get_prerequisites: ");
            System.out.println(e);
        }
        return result;
    }

    private List<Course> list_elective_courses() {
        // Retrieve course codes related to stud_id
        List<Course> result = new ArrayList<Course>();
        String query = MessageFormat.format(
            "SELECT * FROM Courses TMP1 INNER JOIN (SELECT C.course_number FROM ElectiveCourses C WHERE C.major IN (SELECT S.major FROM STUDENTS S WHERE S.perm_id=''{0}'')) TMP2 ON TMP1.course_number = TMP2.course_number", 
            stud_id);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                result.add(new Course(rs.getString("course_number"), rs.getString("title")));
            }
        } catch (SQLException e) {
            System.out.print("list_elective_courses: ");
            System.out.println(e);
        }
        return result;
    }
    private void update_student_pin(String pin_hash) {
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

    public void addCourse(int unique_enrollment_code) {
        Enrolled enrolled = new Enrolled(stud_id, unique_enrollment_code);
        insert_enrolled(enrolled);
    }
    public void dropCourse(int unique_enrollment_code) {
        Enrolled enrolled = new Enrolled(stud_id, unique_enrollment_code);
        delete_enrolled(enrolled);
    }
    public List<Course> listCoursesEnrolled() {
        List<Course> courses = list_enrolled_courses();
        for(int i = 0; i < courses.size(); i++) {
            //System.out.println(courses.get(i).course_number + ", " + courses.get(i).title);
        }
        return courses;
    }

    public List<Course> listCoursesEnrolledQuarter(String year_and_quarter) {
        return list_enrolled_courses_by_quarter(year_and_quarter);
    }

    public List<String> listGradesEnrolledQuarter(String year_and_quarter) {
        List<Enrolled> enrollments = list_enrollments();
        List<String> grades = new ArrayList<String>();
        for(int i = 0; i < enrollments.size(); i++) {
            CourseOffering offering = get_course_offering(enrollments.get(i).unique_enroll_code);
            if(offering.year_and_quarter.equals(year_and_quarter)) {
                grades.add(enrollments.get(i).grade);
            }
        }
        return grades;
    }

    public void requirementsCheck(String year_and_quarter, boolean count_in_progress) {
        Student s = get_student();
        List<Course> courses = listCoursesEnrolled();
        List<Course> in_progress_courses = listCoursesEnrolledQuarter(year_and_quarter);
        List<Course> mandatory = list_mandatory_courses();
        List<Course> elective = list_elective_courses();
        List<Course> potential_electives_left = new ArrayList<Course>(elective);
        List<Course> done_courses = new ArrayList<Course>(courses);
        List<Course> needed_courses = new ArrayList<Course>(mandatory);
        int minNumOfElectives = getMinNumberofElectives();
        done_courses.removeAll(in_progress_courses);
        needed_courses.removeAll(done_courses);
        potential_electives_left.removeAll(done_courses);
        if(count_in_progress){
            needed_courses.removeAll(in_progress_courses);
            potential_electives_left.removeAll(in_progress_courses);
        }
        System.out.println("Required courses for " + stud_id + ", with major " + s.major);
        for(int i = 0; i < mandatory.size(); i++) {
            System.out.println(mandatory.get(i).course_number + ": " + mandatory.get(i).title);
        }
        System.out.println();
        System.out.println("Completed courses for " + stud_id);
        for(int i = 0; i < done_courses.size(); i++) {
            System.out.println(done_courses.get(i).course_number + ": " + done_courses.get(i).title);
        }
        System.out.println();
        System.out.println("WIP courses for " + stud_id);
        for(int i = 0; i < in_progress_courses.size(); i++) {
            System.out.println(in_progress_courses.get(i).course_number + ": " + in_progress_courses.get(i).title);
        }
        System.out.println();
        System.out.println("Needed mandatory courses for " + stud_id);
        for(int i = 0; i < needed_courses.size(); i++) {
            System.out.println(needed_courses.get(i).course_number + ": " + needed_courses.get(i).title);
        }
        if(minNumOfElectives<=(elective.size()-potential_electives_left.size())) {
            System.out.println("\nElective Requirements Met!");
        }
        else {
            System.out.println("\nNumber of Electives Completed: " + (elective.size()-potential_electives_left.size()) + "\n");
            System.out.println("Elective Count Left to Graduate: " + (minNumOfElectives-(elective.size()-potential_electives_left.size())) + "\n");
            System.out.println("Elective Options Left for " + s.major);
            for(int i = 0; i < potential_electives_left.size(); i++) {
                System.out.println(potential_electives_left.get(i).course_number + ": " + potential_electives_left.get(i).title);
            }
        }
    }

    public void makeAPlan(String current_year_and_quarter) {
        List<Course> courses = listCoursesEnrolled();
        List<Course> mandatory = list_mandatory_courses();
        List<Course> needed_courses = new ArrayList<Course>(mandatory);
        List<Course> done_courses = new ArrayList<Course>(courses);
        List<Course> done_courses_electives = new ArrayList<Course>(courses);
        List<Course> accounted_for_courses = new ArrayList<Course>(done_courses);
        HashMap<String, Pair<CourseOffering, String>> done_course_offerings = new HashMap<String, Pair<CourseOffering, String>>();
        HashMap<String, Pair<CourseOffering, String>> done_course_offerings_electives = new HashMap<String, Pair<CourseOffering, String>>();
        needed_courses.removeAll(done_courses);
        done_courses_electives.removeAll(mandatory);
        List<Pair<CourseOffering, String>> result = new ArrayList<Pair<CourseOffering, String>>();
        List<Pair<CourseOffering, String>> result_electives = new ArrayList<Pair<CourseOffering, String>>();
        List<Course> electives = list_elective_courses();
        // build pair list
        List<CourseOffering> temp = list_enrolled_course_offerings();
        CourseOffering earliest_course = new CourseOffering("9999 Winter");
        for(int i = 0; i < temp.size(); i++) {
            int k = 0;
            for(; k < done_courses.size(); k++) {
                if(done_courses.get(k).course_number.equals(temp.get(i).course_number))
                    break;
            }
            if(mandatory.indexOf(done_courses.get(k)) >= 0) {
                result.add(new Pair<CourseOffering, String>(temp.get(i), done_courses.get(k).title));
            } else {
                result_electives.add(new Pair<CourseOffering, String>(temp.get(i), done_courses.get(k).title));
                done_course_offerings_electives.put(temp.get(i).course_number, new Pair<CourseOffering, String>(temp.get(i), done_courses.get(k).title));
            }
            done_course_offerings.put(temp.get(i).course_number, new Pair<CourseOffering, String>(temp.get(i), done_courses.get(k).title));
            // find earliest course
            if(parseYearQuarter(temp.get(i).year_and_quarter).lessThan(parseYearQuarter(earliest_course.year_and_quarter)))
                earliest_course = temp.get(i);
        }
        // add mandatory courses

        YearQuarter graduation = parseYearQuarter(current_year_and_quarter);
        List<Integer> done = new ArrayList<Integer>(Collections.nCopies(needed_courses.size(), 0));

        while(result.size() < needed_courses.size() + done_courses.size() - done_course_offerings_electives.size()) {
            outer:
            for(int i = 0; i < needed_courses.size(); i++) {
                if(done.get(i) > 0)
                    continue;
                List<Course> prereqs = get_prerequisites(needed_courses.get(i).course_number);
                CourseOffering latest_prereq = new CourseOffering("1111 Winter");
                for(int j = 0; j < prereqs.size(); j++) {
                    if(accounted_for_courses.indexOf(prereqs.get(j)) == -1)
                        continue outer;
                    CourseOffering here = done_course_offerings.get(prereqs.get(j).course_number).l;
                    if(parseYearQuarter(latest_prereq.year_and_quarter).lessThan(parseYearQuarter(here.year_and_quarter)))
                        latest_prereq = here;
                }
                List<CourseOffering> offerings = list_course_offerings_by_number(needed_courses.get(i).course_number);
                CourseOffering earliest_offering = new CourseOffering("9999 Winter");
                for(int j = 0; j < offerings.size(); j++) {
                    if(parseYearQuarter(latest_prereq.year_and_quarter).lessThan(parseYearQuarter(offerings.get(j).year_and_quarter))
                        && parseYearQuarter(offerings.get(j).year_and_quarter).lessThan(parseYearQuarter(earliest_offering.year_and_quarter)))
                        earliest_offering = offerings.get(j);
                }
                result.add(new Pair<CourseOffering, String>(earliest_offering, needed_courses.get(i).title));
                accounted_for_courses.add(needed_courses.get(i));
                done.set(i, 1);
                done_course_offerings.put(earliest_offering.course_number, new Pair<CourseOffering, String>(earliest_offering, needed_courses.get(i).title));
                if(graduation.lessThan(parseYearQuarter(earliest_offering.year_and_quarter)));
                    graduation = parseYearQuarter(earliest_offering.year_and_quarter);
            }
        }

        // load sched with electives
        int electives_left = getMinNumberofElectives();
        for(int i = 0; i < done_courses_electives.size(); i++) {
            electives_left--;
        }
        done = new ArrayList<Integer>(Collections.nCopies(electives.size(), 0));
        boolean start = true;
        electives_done:
        for(int year = parseYearQuarter(current_year_and_quarter).year; year < graduation.year + 1; year++) {
            for(int q = 0, classes_left = 5; q < 3; q++) {
                if(start) {
                    q = QUARTERS.indexOf(parseYearQuarter(current_year_and_quarter).quarter);
                    start = false;
                }
                if(year == graduation.year && q == 2)
                    continue;
                for(int i = 0; i < result.size(); i++) {
                    if(parseYearQuarter(result.get(i).l.year_and_quarter).equals(new YearQuarter(year, QUARTERS.get(q))))
                        classes_left--;
                }
                for(int i = 0; i < result_electives.size(); i++) {
                    if(parseYearQuarter(result_electives.get(i).l.year_and_quarter).equals(new YearQuarter(year, QUARTERS.get(q))))
                        classes_left--;
                }
                outer:
                for(int i = 0; i < electives.size() && classes_left > 0; i++) {
                    if(done.get(i) > 0 || done_courses_electives.indexOf(electives.get(i)) >= 0)
                        continue;
                    if(!(electives_left > 0))
                        break electives_done;
                    // check if offered in current quarter
                    List<CourseOffering> offerings = list_course_offerings_by_number(electives.get(i).course_number);
                    CourseOffering found_offering = new CourseOffering("1111 Winter");
                    for(int j = 0; j < offerings.size(); j++) {
                        if(new YearQuarter(year, QUARTERS.get(q)).equals(parseYearQuarter(offerings.get(j).year_and_quarter))) {
                            found_offering = offerings.get(j);
                            break;
                        }
                    }
                    if(found_offering.course_number.equals("null_test"))
                    continue;
                    // check prereqs
                    List<Course> prereqs = get_prerequisites(electives.get(i).course_number);
                    CourseOffering latest_prereq = new CourseOffering("1111 Winter");
                    for(int j = 0; j < prereqs.size(); j++) {
                        if(accounted_for_courses.indexOf(prereqs.get(j)) == -1)
                            continue outer;
                        CourseOffering here = done_course_offerings.get(prereqs.get(j).course_number).l;
                        if(parseYearQuarter(latest_prereq.year_and_quarter).lessThan(parseYearQuarter(here.year_and_quarter))) {
                            latest_prereq = here;
                        }
                    }
                    if((new YearQuarter(year, QUARTERS.get(q))).lessThan(parseYearQuarter(latest_prereq.year_and_quarter)))
                        continue;
                    result_electives.add(new Pair<CourseOffering, String>(found_offering, electives.get(i).title));
                    accounted_for_courses.add(electives.get(i));
                    done_course_offerings.put(found_offering.course_number, new Pair<CourseOffering, String>(found_offering, electives.get(i).title));
                    done.set(i, 1);
                    electives_left--;
                    classes_left--;
                }


            }
        }

        // print results
        System.out.println("\n- * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * -\n");
        System.out.println("\033[1mQUARTER BY QUARTER PLAN FOR STUDENT " + stud_id + "\033[0m\n(* denotes major a requirement)\n");
        System.out.println("Major elective requirement: " + getMinNumberofElectives() + " electives.\n");
        start = true;
        for(int year = parseYearQuarter(earliest_course.year_and_quarter).year; year < graduation.year + 1; year++) {
            for(int q = 0; q < 3; q++) {
                if(start) {
                    q = QUARTERS.indexOf(parseYearQuarter(current_year_and_quarter).quarter);
                    start = false;
                }
                if(year == graduation.year && q == 2)
                    continue;
                System.out.println(year + " " + QUARTERS.get(q) + ":");
                for(int i = 0; i < result.size(); i++) {
                    if(parseYearQuarter(result.get(i).l.year_and_quarter).equals(new YearQuarter(year, QUARTERS.get(q)))) {
                        System.out.println("\t \033[1m* " + result.get(i).l.course_number + ": " + result.get(i).r + "\033[0m");
                        List<Course> prereqs = get_prerequisites(result.get(i).l.course_number);
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
                        List<Course> prereqs = get_prerequisites(result_electives.get(i).l.course_number);
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

    public void updatePin(String pin) {
        String pin_hash = "";
        try {
            pin_hash = GFG2.toHexString(GFG2.getSHA(pin));
        }   catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown for incorrect algorithm: " + e);
        }
        update_student_pin(pin_hash);
        System.out.println("Pin hash: <" + pin_hash + ">");
        Student updated = get_student();
        System.out.println(updated.perm_id + "- new pin : <" + updated.pin + ">");
    }
}
