package net.sqlitetutorial;
class Student {
    String perm_id;
    String name;
    String address;
    String major;
    String pin;
    Student(String perm_id, String name, String address, String major, String pin) {
        this.perm_id = perm_id;
        this.name = name;
        this.address = address;
        this.major = major;
        this.pin = pin;
    }
    public String getName() {
        return name;
    }
    public String getPerm() {
        return perm_id;
    }
    public String getPinHash() {
        return pin;
    }
}