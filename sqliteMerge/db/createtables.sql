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
    course_number CHAR(7) NOT NULL,
    unique_enroll_code INTEGER,
    year_and_quarter CHAR(11),
    location CHAR(9),
    max_enrollment INTEGER,
    course_time CHAR(20),
    professor_fname CHAR(15),
    professor_lname CHAR(15),
    PRIMARY KEY (unique_enroll_code)
    FOREIGN KEY (course_number) REFERENCES Courses
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE Majors (
    name CHAR(20),
    department CHAR(20) NOT NULL,
    min_electives_to_graduate INTEGER,
    PRIMARY KEY(name),
    FOREIGN KEY(department) REFERENCES Departments (name)
    ON UPDATE CASCADE
);

CREATE TABLE Prerequisites (
    dependent_course CHAR(7),
    prereq CHAR(7),
    PRIMARY KEY(prereq,dependent_course),
    FOREIGN KEY(prereq) REFERENCES Courses(course_number)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    FOREIGN KEY(dependent_course) REFERENCES Courses(course_number)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE ElectiveCourses (
    major CHAR(20),
    course_number CHAR(7),
    PRIMARY KEY(major, course_number),
    FOREIGN KEY(major) REFERENCES Majors(name)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    FOREIGN KEY(course_number) REFERENCES Courses
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE MandatoryCourses (
    major CHAR(20),
    course_number CHAR(7),
    PRIMARY KEY(major, course_number),
    FOREIGN KEY(major) REFERENCES Majors(name)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    FOREIGN KEY(course_number) REFERENCES Courses
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE Enrolled (
    stud_id CHAR(7),
    course_code INTEGER,
    grade CHAR(2),
    PRIMARY KEY (stud_id, course_code),
    FOREIGN KEY (stud_id) REFERENCES Students (perm_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    FOREIGN KEY (course_code) REFERENCES CourseOfferings (unique_enroll_code)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE Students (
    perm_id CHAR(7),
    name CHAR(40),
    address CHAR(40),
    major CHAR(20) NOT NULL,
    pin CHAR(64),
    FOREIGN KEY (major) REFERENCES Majors (name)
    ON UPDATE CASCADE,
    PRIMARY KEY (perm_id)
);

CREATE TRIGGER check_enrolled_count
    BEFORE INSERT ON Enrolled
BEGIN
    SELECT
        CASE
            WHEN (5<=(SELECT COUNT(*) FROM ENROLLED E WHERE NEW.stud_id = E.stud_id AND E.course_code IN (SELECT TMP2.unique_enroll_code FROM CourseOfferings TMP2 WHERE TMP2.year_and_quarter = (SELECT TMP.year_and_quarter FROM CourseOfferings TMP WHERE NEW.course_code=TMP.unique_enroll_code)))) THEN
                RAISE (ABORT, 'Student already enrolled in maximum number of courses')
        END;
END;

CREATE TRIGGER prereqs_not_met
    BEFORE INSERT ON Enrolled
BEGIN
    SELECT
        CASE
            WHEN (0<(SELECT COUNT(*) FROM (SELECT P.prereq FROM Prerequisites P WHERE P.dependent_course IN (SELECT C1.course_number FROM CourseOfferings C1 WHERE C1.unique_enroll_code=NEW.course_code) EXCEPT SELECT DISTINCT COff.course_number FROM (SELECT E.course_code FROM Enrolled E WHERE NEW.stud_id = E.stud_id) TAKENCOURSES INNER JOIN CourseOfferings COff ON COff.unique_enroll_code=TAKENCOURSES.course_code)  EXCEPTTABLE )) THEN
                RAISE (ABORT, 'Prerequisites not met')
        END;
END;

CREATE TRIGGER class_is_full
    BEFORE INSERT ON Enrolled
BEGIN
    SELECT
        CASE
            WHEN ((SELECT C1.max_enrollment FROM CourseOfferings C1 WHERE NEW.course_code = C1.unique_enroll_code) <= (SELECT COUNT(*) FROM Enrolled E WHERE NEW.course_code = E.course_code) ) THEN
                RAISE (ABORT, 'Course is Full!')
        END;
END;

CREATE TRIGGER check_valid_course
    BEFORE INSERT ON Enrolled
BEGIN
    SELECT
        CASE
            WHEN NOT EXISTS(SELECT * FROM COURSEOFFERINGS C WHERE NEW.course_code = C.unique_enroll_code) THEN
                RAISE (ABORT, 'Not a valid course')
        END;
END;


CREATE TRIGGER check_valid_student
    BEFORE INSERT ON Enrolled
BEGIN
    SELECT
        CASE
            WHEN NOT EXISTS(SELECT * FROM Students S WHERE NEW.stud_id = S.perm_id) THEN
                RAISE (ABORT, 'Not a valid perm number')
        END;
END;

CREATE TRIGGER major_invalid
    BEFORE INSERT ON Students
BEGIN
    SELECT
        CASE
            WHEN NOT EXISTS(SELECT * FROM Majors M WHERE NEW.major = M.name) THEN
                RAISE (ABORT, 'Invalid Major, Student must have a Major')
        END;
END;

CREATE TRIGGER major_invalid_update
    BEFORE UPDATE ON Students
BEGIN
    SELECT
        CASE
            WHEN NOT EXISTS(SELECT * FROM Majors M WHERE NEW.major = M.name) THEN
                RAISE (ABORT, 'Invalid Major, Student must have a Major')
        END;
END;

CREATE TRIGGER department_invalid_major
    BEFORE INSERT ON Majors
BEGIN
    SELECT
        CASE
            WHEN NOT EXISTS(SELECT * FROM Departments D WHERE NEW.department = D.name) THEN
                RAISE (ABORT, 'Invalid Department, Major must be in Department')
        END;
END;

CREATE TRIGGER department_invalid_major_update
    BEFORE UPDATE ON Majors
BEGIN
    SELECT
        CASE
            WHEN NOT EXISTS(SELECT * FROM Departments D WHERE NEW.department = D.name) THEN
                RAISE (ABORT, 'Invalid Department, Major must be in Department')
        END;
END;

CREATE TRIGGER courseoffering_course_is_valid
    BEFORE INSERT ON CourseOfferings
BEGIN
    SELECT
        CASE
            WHEN NOT EXISTS(SELECT * FROM Courses C WHERE NEW.course_number = C.course_number) THEN
                RAISE (ABORT, 'Invalid Course Number, Course Offering must be valid course')
        END;
END;

CREATE TRIGGER courseoffering_course_is_valid_update
    BEFORE UPDATE ON CourseOfferings
BEGIN
    SELECT
        CASE
            WHEN NOT EXISTS(SELECT * FROM Courses C WHERE NEW.course_number = C.course_number) THEN
                RAISE (ABORT, 'Invalid Course Number, Course Offering must be valid course')
        END;
END;

PRAGMA foreign_keys = ON;