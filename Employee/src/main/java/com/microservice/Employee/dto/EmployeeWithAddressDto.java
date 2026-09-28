package com.microservice.Employee.dto;

/**
 * A combined "master shipping box" DTO.
 * Because a Controller method can only return one object, we use this class
 * to package the Employee data (from our database) and the Address data (from the external microservice)
 * together into a single, clean JSON response for the end user.
 */
public class EmployeeWithAddressDto {
    private EmployeeDto employee;
    private AddressResponseDto address;

    public EmployeeWithAddressDto(EmployeeDto employee, AddressResponseDto address) {
        this.employee = employee;
        this.address = address;
    }

    public EmployeeDto getEmployee() { return employee; }
    public void setEmployee(EmployeeDto employee) { this.employee = employee; }
    public AddressResponseDto getAddress() { return address; }
    public void setAddress(AddressResponseDto address) { this.address = address; }
}
