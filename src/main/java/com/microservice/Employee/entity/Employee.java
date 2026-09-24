package com.microservice.Employee.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.persistence.*;


/**
 * Entity class representing an Employee in the database.
 * This class maps directly to the "Employee" table and contains the employee attributes.
 */
@Entity 
@Table(name = "Employee")

public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;

    public Employee() {
    }

    public Employee(Long id, String name, String email, String phone, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
}


// These two annotations work together to manage the Primary Key (the unique identifier) for your database table.

// 1. @Id
// This tells Spring Boot and the Database: "This specific field (id) is the Primary Key for the Employee table." Just like every person has a unique Social Security Number or ID card, every row in a database table must have a unique identifier so you can accurately find, update, or delete it later. The @Id annotation enforces this rule.

// 2. @GeneratedValue(strategy = GenerationType.SEQUENCE)
// This tells the Database: "Please automatically generate a brand new, unique ID for me every time I save a new Employee."

// If you didn't have this annotation, you would have to manually figure out what the next ID should be and assign it yourself before saving (e.g., employee.setId(5);). This is dangerous because two users might try to save an employee at the same time and accidentally use the same ID, causing a crash!

// Why GenerationType.SEQUENCE? There are a few different strategies for generating IDs (like IDENTITY, AUTO, UUID, and SEQUENCE).

// SEQUENCE creates a dedicated "number counter" object directly inside the database.
// When you save a new Employee, Spring Boot asks the database's Sequence Counter for the next available number (e.g., "Give me number 1", then "Give me number 2").
// This is extremely fast and is the recommended best-practice for databases that support sequences (like PostgreSQL, Oracle, and H2).
// In short: @Id = "This is the unique ID column." @GeneratedValue = "Database, please auto-fill this number for me so I don't have to worry about it!"