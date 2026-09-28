package com.microservice.Employee.dto;

/**
 * Data Transfer Object (DTO) acting as a "catching mitt".
 * When Employee Service calls the Address Service, the Address Service returns raw JSON.
 * This class provides a Java Object structure for the RestTemplate to map that JSON into,
 * since the original Address.java entity is hidden inside the Address Microservice.
 */
public class AddressResponseDto {
    private Long id;
    private String street;
    private String city;
    private String zipCode;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
}
