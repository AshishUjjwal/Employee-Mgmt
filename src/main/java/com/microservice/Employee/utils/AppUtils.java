package com.microservice.Employee.utils;

import org.springframework.beans.BeanUtils;
import com.microservice.Employee.dto.EmployeeDto;
import com.microservice.Employee.entity.Employee;

/**
 * Utility class for the application.
 * Provides helper methods, such as converting between Employee entities and EmployeeDto objects.
 */
public class AppUtils {

    public static EmployeeDto entityToDto(Employee employeeEntity) {
        EmployeeDto employeeDto = new EmployeeDto();
        BeanUtils.copyProperties(employeeEntity, employeeDto);
        return employeeDto;
    }

    public static Employee dtoToEntity(EmployeeDto employeeDto) {
        Employee employeeEntity = new Employee();
        BeanUtils.copyProperties(employeeDto, employeeEntity);
        return employeeEntity;
    }
    
}
