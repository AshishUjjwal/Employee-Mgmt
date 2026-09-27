package com.microservice.Employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservice.Employee.entity.Employee;

/**
 * Repository interface for Employee entity.
 * It extends JpaRepository to provide standard database operations (CRUD) for
 * the Employee table.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}

// JpaRepository is a built-in interface provided by Spring Data JPA that
// contains all the standard database operations (CRUD: Create, Read, Update,
// Delete) so that you don't have to write them yourself!

// Life Before JpaRepository:
// In the old days of Java, if you wanted to save an employee to a database, you
// had to write a lot of boilerplate code: open a database connection, write raw
// SQL queries like INSERT INTO Employee (name, email) VALUES (?, ?), handle the
// parameters safely, execute the query, and close the connection.

// Life With JpaRepository:
// Spring Boot realized that 99% of applications do the exact same database
// operations. So they created JpaRepository.

// When you write this in your code:

// java
// public interface EmployeeRepository extends JpaRepository<Employee, Long> { }
// You are telling Spring Boot: "I want you to manage my Employee table (where
// the primary key is a Long)."

// By simply extending this interface, Spring Boot automatically generates the
// implementation behind the scenes and instantly gives you access to a massive
// list of ready-to-use methods!

// Methods You Get For Free:
// Without writing a single SQL query, you instantly have access to methods
// like:

// repository.save(employee) ➔ Creates a new row (or updates an existing one).
// repository.findAll() ➔ Runs a SELECT * query and returns a List of all
// employees.
// repository.findById(1L) ➔ Finds a specific employee where id = 1.
// repository.deleteById(5L) ➔ Deletes the employee where id = 5.
// repository.count() ➔ Tells you exactly how many employees are in the
// database.
// Real-world Analogy:
// Imagine you own a restaurant. Without JpaRepository, you have to personally
// go into the kitchen, gather ingredients, cook the food, plate it, and bring
// it to the table (writing manual SQL queries). With JpaRepository, you just
// hire a highly-trained Robot Chef. You just press a button that says save() or
// find(), and the Robot Chef does all the complicated database kitchen-work for
// you instantly!