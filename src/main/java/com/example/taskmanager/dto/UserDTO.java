package com.example.taskmanager.dto;

import java.util.List;

public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String role;
    private List<Long> taskIds;

    public UserDTO(Long id, String username, String email, String role, List<Long> taskIds) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.taskIds = taskIds;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }
}
