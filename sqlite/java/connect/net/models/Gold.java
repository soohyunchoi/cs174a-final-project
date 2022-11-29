import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Gold {
    Connection conn;
    Gold(Connection conn) {
        this.conn = conn;
    }
    static void addCourse() {
        Courses course = new Courses();
    }
}
