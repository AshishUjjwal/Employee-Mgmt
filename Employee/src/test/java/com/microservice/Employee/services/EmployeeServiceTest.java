package com.microservice.Employee.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.microservice.Employee.dto.EmployeeDto;
import com.microservice.Employee.entity.Employee;
import com.microservice.Employee.exception.ResourceNotFoundException;
import com.microservice.Employee.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp() {
        employee = new Employee(1L, "John Doe", "john@example.com", "1234567890", "123 Main St");
        employeeDto = new EmployeeDto(1L, "John Doe", "john@example.com", "1234567890", "123 Main St");
    }

    @Test
    void testSaveEmployee() {
        // Arrange
        when(repository.save(any(Employee.class))).thenReturn(employee);

        // Act
        EmployeeDto result = employeeService.saveEmployee(employeeDto);

        // Assert
        assertNotNull(result);
        assertEquals(employee.getName(), result.getName());
        assertEquals(employee.getEmail(), result.getEmail());
        verify(repository, times(1)).save(any(Employee.class));
    }

    @Test
    void testGetAllEmployee() {
        // Arrange
        when(repository.findAll()).thenReturn(Arrays.asList(employee));

        // Act
        List<EmployeeDto> result = employeeService.getAllEmployee();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(employee.getName(), result.get(0).getName());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetEmployeeById_Success() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(employee));

        // Act
        EmployeeDto result = employeeService.getEmployeeById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(employee.getName(), result.getName());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testGetEmployeeById_NotFound() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(1L));
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testUpdateEmployee_Success() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(repository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDto updatedDto = new EmployeeDto(1L, "Jane Doe", "jane@example.com", "0987654321", "456 Oak St");

        // Act
        EmployeeDto result = employeeService.updateEmployee(1L, updatedDto);

        // Assert
        assertNotNull(result);
        assertEquals("Jane Doe", employee.getName()); 
        assertEquals("jane@example.com", employee.getEmail());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Employee.class));
    }

    @Test
    void testUpdateEmployee_NotFound() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(1L, employeeDto));
        verify(repository, times(1)).findById(1L);
        verify(repository, never()).save(any(Employee.class));
    }

    @Test
    void testDeleteEmployee_Success() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        doNothing().when(repository).delete(employee);

        // Act
        employeeService.deleteEmployee(1L);

        // Assert
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).delete(employee);
    }

    @Test
    void testDeleteEmployee_NotFound() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee(1L));
        verify(repository, times(1)).findById(1L);
        verify(repository, never()).delete(any(Employee.class));
    }
}
