package com.microservice.Employee.dto;

/**
 * Data Transfer Object (DTO) for the Employee.
 * It is used to transfer employee data between the client and the server without exposing the internal database entity.
 */
public class EmployeeDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;


    public EmployeeDto() {
    }

    public EmployeeDto(Long id, String name, String email, String phone, String address) {
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
