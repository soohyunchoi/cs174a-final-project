package net.sqlitetutorial;
import java.util.Objects;
class Courses {
    String course_number;
    String title;
    Courses(String course_number, String title) {
        this.course_number = course_number;
        this.title = title;
    }
    public String getCourseNumber() {
        return this.course_number;
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Courses)) {
            return false;
        }
        Courses otherMember = (Courses)obj;
        return otherMember.getCourseNumber().equals(getCourseNumber());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getCourseNumber());
    }
}