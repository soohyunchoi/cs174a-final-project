package net.sqlitetutorial;
import java.util.Objects;
class Course {
    String course_number;
    String title;
    Course(String course_number, String title) {
        this.course_number = course_number;
        this.title = title;
    }
    public String getCourseNumber() {
        return this.course_number;
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Course)) {
            return false;
        }
        Course otherMember = (Course)obj;
        return otherMember.getCourseNumber().equals(getCourseNumber());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getCourseNumber());
    }
}