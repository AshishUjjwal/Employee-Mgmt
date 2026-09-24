package com.microservice.Employee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import com.microservice.Employee.dto.EmployeeDto;
import com.microservice.Employee.services.EmployeeService;

/**
 * REST Controller for Employee API endpoints.
 * It handles incoming HTTP requests (like GET, POST) and delegates business logic to the EmployeeService.
 */
@RestController
@RequestMapping("/v1/Data")
public class EmployeeController {
    @Autowired 
    private EmployeeService service;

    @GetMapping("/employee")
    public String getEmployee() {
        return "Employee";
    }

    @PostMapping("/saveEmployee")
    public ResponseEntity<EmployeeDto> saveEmployee(@RequestBody EmployeeDto dto){
        EmployeeDto employee = service.saveEmployee(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }
}
