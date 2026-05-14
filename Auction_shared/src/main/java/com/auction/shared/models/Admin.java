package com.auction.shared.models;

public class Admin extends User {

    private int accessLevel;
    private String department;
    private String internalEmployeeId;

    public Admin(String userName, String password, String id, int accessLevel, String department, String internalEmployeeId) {
        super(userName, password, id);
        this.accessLevel = accessLevel;
        this.department = department;
        this.internalEmployeeId = internalEmployeeId;
    }

    public int getAccessLevel() { return accessLevel; }
    public void setAccessLevel(int accessLevel) { this.accessLevel = accessLevel; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getInternalEmployeeId() { return internalEmployeeId; }
    public void setInternalEmployeeId(String internalEmployeeId) { this.internalEmployeeId = internalEmployeeId; }
}
