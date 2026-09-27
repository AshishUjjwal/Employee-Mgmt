package com.microservice.Employee.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.microservice.Employee.dto.EmployeeDto;
import com.microservice.Employee.services.EmployeeService;

@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp() {
        employeeDto = new EmployeeDto(1L, "John Doe", "john@example.com", "1234567890", "123 Main St");
    }

    @Test
    void testGetEmployeeString() {
        // Act
        String response = employeeController.getEmployee();

        // Assert
        assertEquals("Employee", response);
    }

    @Test
    void testSaveEmployee() {
        // Arrange
        when(employeeService.saveEmployee(any(EmployeeDto.class))).thenReturn(employeeDto);

        // Act
        ResponseEntity<EmployeeDto> response = employeeController.saveEmployee(employeeDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(employeeDto.getName(), response.getBody().getName());
        verify(employeeService, times(1)).saveEmployee(any(EmployeeDto.class));
    }

    @Test
    void testGetAllEmployee() {
        // Arrange
        when(employeeService.getAllEmployee()).thenReturn(Arrays.asList(employeeDto));

        // Act
        ResponseEntity<List<EmployeeDto>> response = employeeController.getAllEmployee();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(employeeDto.getName(), response.getBody().get(0).getName());
        verify(employeeService, times(1)).getAllEmployee();
    }

    @Test
    void testGetEmployeeById() {
        // Arrange
        when(employeeService.getEmployeeById(1L)).thenReturn(employeeDto);

        // Act
        ResponseEntity<EmployeeDto> response = employeeController.getEmployeeById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employeeDto.getName(), response.getBody().getName());
        verify(employeeService, times(1)).getEmployeeById(1L);
    }

    @Test
    void testUpdateEmployee() {
        // Arrange
        when(employeeService.updateEmployee(eq(1L), any(EmployeeDto.class))).thenReturn(employeeDto);

        // Act
        ResponseEntity<EmployeeDto> response = employeeController.updateEmployee(1L, employeeDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employeeDto.getName(), response.getBody().getName());
        verify(employeeService, times(1)).updateEmployee(eq(1L), any(EmployeeDto.class));
    }

    @Test
    void testDeleteEmployee() {
        // Arrange
        doNothing().when(employeeService).deleteEmployee(1L);

        // Act
        ResponseEntity<Void> response = employeeController.deleteEmployee(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(employeeService, times(1)).deleteEmployee(1L);
    }
}
