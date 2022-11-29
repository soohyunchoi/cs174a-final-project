CREATE TABLE Courses (
    course_number CHAR(7),
    title CHAR(20),
    PRIMARY KEY (course_number)
);

CREATE TABLE Departments (
    name CHAR(20),
    PRIMARY KEY (name)
);

CREATE TABLE CourseOfferings (
    course_number CHAR(7),
    unique_enroll_code INTEGER,
    year_and_quarter CHAR(11),
    location CHAR(9),
    max_enrollment INTEGER,
    professor_fname CHAR(15),
    professor_lname CHAR(15),
    PRIMARY KEY (unique_enroll_code)
    FOREIGN KEY (course_number) REFERENCES Courses
    ON DELETE CASCADE
);

CREATE TABLE Majors (
    name CHAR(20),
    department CHAR(20),
    min_electives_to_graduate INTEGER,
    PRIMARY KEY(name),
    FOREIGN KEY(department) REFERENCES Departments (name)
);

CREATE TABLE Prerequisites (
    prereq CHAR(7),
    dependent_course CHAR(7),
    PRIMARY KEY(prereq,dependent_course),
    FOREIGN KEY(prereq) REFERENCES Courses(course_number)
    ON DELETE CASCADE,
    FOREIGN KEY(dependent_course) REFERENCES Courses(course_number)
    ON DELETE CASCADE
);

CREATE TABLE ElectiveCourses (
    major CHAR(20),
    course_number CHAR(7),
    PRIMARY KEY(major, course_number),
    FOREIGN KEY(major) REFERENCES Majors(name)
    ON DELETE CASCADE,
    FOREIGN KEY(course_number) REFERENCES Courses
    ON DELETE CASCADE
);

CREATE TABLE MandatoryCourses (
    major CHAR(20),
    course_number CHAR(7),
    PRIMARY KEY(major, course_number),
    FOREIGN KEY(major) REFERENCES Majors(name)
    ON DELETE CASCADE,
    FOREIGN KEY(course_number) REFERENCES Courses
    ON DELETE CASCADE
);

CREATE TABLE Enrolled (
    stud_id CHAR(7),
    course_code INTEGER,
    grade CHAR(2),
    PRIMARY KEY (stud_id, course_code),
    FOREIGN KEY (stud_id) REFERENCES Students (perm_id)
    ON DELETE CASCADE,
    FOREIGN KEY (course_code) REFERENCES CourseOfferings (course_number)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE Students (
    perm_id CHAR(7),
    name CHAR(40),
    address CHAR(40),
    major CHAR(20),
    department CHAR(20),
    pin CHAR(4),
    FOREIGN KEY (department) REFERENCES Departments (name),
    FOREIGN KEY (major) REFERENCES Majors (name),
    PRIMARY KEY (perm_id)
);

CREATE TRIGGER check_enrolled_count
    BEFORE INSERT ON Enrolled
BEGIN
    SELECT
        CASE
            WHEN ((SELECT COUNT(*) FROM Enrolled E WHERE NEW.stud_id = E.stud_id) >= 5) THEN
                RAISE (ABORT, 'Student already enrolled in maximum number of courses')
        END;
END;