package net.sqlitetutorial;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * PRIM KEY: (stud_id, unique_enroll_code)
 */
public class Enrolled {
    String stud_id;
    int unique_enroll_code;
    String grade;
    Enrolled(String stud_id, int unique_enroll_code) {
        this.stud_id = stud_id;
        this.unique_enroll_code = unique_enroll_code;
        this.grade = null;
    }
    Enrolled(String stud_id, int unique_enroll_code, String grade) {
        this.stud_id = stud_id;
        this.unique_enroll_code = unique_enroll_code;
        this.grade = grade;
    }
    public String get_stud_id() {
        return this.stud_id;
    }
    public int get_unique_enroll_code() {
        return this.unique_enroll_code;
    }
    public String get_grade() {
        return this.grade;
    }
}
