package com.example.dell.tracker;

/**
 * Created by Dell on 10/07/2018.
 */

public class Admin {
    private String adminName;
    private String adminEmail;

    public Admin() {
    }

    public Admin(String adminName, String adminEmail) {
        this.adminName = adminName;
        this.adminEmail = adminEmail;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }
}
