package net.sqlitetutorial;
class CourseOfferings {
    String course_number;
    int unique_enroll_code;
    String year_and_quarter;
    String location;
    int max_enrollment;
    String professor_fname;
    String professor_lname;
    CourseOfferings(String course_number, int unique_enroll_code, String year_and_quarter, String location, int max_enrollment, String professor_fname, String professor_lname) {
        this.course_number = course_number;
        this.unique_enroll_code = unique_enroll_code;
        this.year_and_quarter = year_and_quarter;
        this.location = location;
        this.max_enrollment = max_enrollment;
        this.professor_fname = professor_fname;
        this.professor_lname = professor_lname;
    }
}