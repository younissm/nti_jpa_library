package com.training.day2.tasks.model;

import jakarta.persistence.Entity;

@Entity
public class Employee extends User {
    public Employee(String name, int age, String role) {
        super(name, age);
        this.role = role;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "role='" + role + '\'' +
                '}';
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    String role;
}
