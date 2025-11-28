package com.sgtech.pos.dto;

import java.util.UUID;

public class LoginResponse {
    private String token;
    private UUID employeeId;
    private String username;
    private String fullName;
    private String position;
    
    public LoginResponse() {}
    
    public LoginResponse(String token, UUID employeeId, String username, String fullName, String position) {
        this.token = token;
        this.employeeId = employeeId;
        this.username = username;
        this.fullName = fullName;
        this.position = position;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public UUID getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
}

