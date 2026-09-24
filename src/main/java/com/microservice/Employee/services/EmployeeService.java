package com.microservice.Employee.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservice.Employee.dto.EmployeeDto;
import com.microservice.Employee.entity.Employee;
import com.microservice.Employee.repository.EmployeeRepository;
import com.microservice.Employee.utils.AppUtils;

/**
 * Service class containing business logic for Employee operations.
 * It acts as an intermediary between the EmployeeController and EmployeeRepository.
 */
@Service 
public class EmployeeService {

    @Autowired
    private EmployeeRepository repository;
    
    public EmployeeDto saveEmployee(EmployeeDto dto) {
        Employee employeeEntity = AppUtils.dtoToEntity(dto);  // Convert DTO to Entity
        Employee savedEntity = repository.save(employeeEntity); // Save Entity to database. The repository automatically writes an INSERT INTO ... SQL query behind the scenes and saves the employee to your database.
        return AppUtils.entityToDto(savedEntity); // Convert Entity to DTO
    }
}
