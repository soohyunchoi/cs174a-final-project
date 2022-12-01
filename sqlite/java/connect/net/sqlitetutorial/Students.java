package net.sqlitetutorial;
class Students {
    String perm_id;
    String name;
    String address;
    String major;
    String department;
    String pin;
    Students(String perm_id, String name, String address, String major, String department, String pin) {
        this.perm_id = perm_id;
        this.name = name;
        this.address = address;
        this.major = major;
        this.department = department;
        this.pin = pin;
    }
}