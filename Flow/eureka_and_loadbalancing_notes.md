# Microservices Concepts: Eureka & Load Balancing

This document captures the core concepts and queries discussed regarding Service Discovery and Load Balancing in the Employee Management System.

## 1. Why Register on Eureka Server?
Eureka acts as a **"Phonebook"** for your microservices.
- **Dynamic IP Addresses:** In modern cloud/Docker environments, IP addresses change constantly. If we hardcode `localhost:8082`, the system will break when deployed. Eureka solves this by letting services find each other by name (e.g., `ADDRESS`).
- **Load Balancing:** If traffic is heavy, you can run multiple instances of a service. Eureka tracks all of them, allowing clients to distribute traffic across them.
- **API Gateway Support:** An API Gateway relies on Eureka to automatically know where to route incoming user traffic.

## 2. How Services Register (The Flow)
1. **The Dependency:** `spring-cloud-starter-netflix-eureka-client` adds the "agent" logic.
2. **The Switch:** `@EnableDiscoveryClient` turns the agent on.
3. **The Configuration:**
   - `spring.application.name=Employee` (The name it registers as in the phonebook).
   - `eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/` (Where the phonebook is located).
   - `eureka.instance.preferIpAddress=true` (Tells Eureka to record the actual IP address, not the machine's hostname, to prevent DNS connection issues).
4. **Heartbeats:** The service pings Eureka every 30 seconds to say "I'm still alive". If it crashes, Eureka removes it.

## 3. Load Balancing Flow
When `EmployeeService` needs `AddressService` data:
1. `EmployeeService` uses a `@LoadBalanced RestTemplate` to call `http://ADDRESS/v1/address/1`.
2. Notice it uses the logical name `ADDRESS` (which matches `spring.application.name`), not `localhost:8082`.
3. The `@LoadBalanced` annotation acts as an interceptor. It pauses the request, asks Eureka for all IPs registered under `ADDRESS`.
4. It picks one IP (using a Round-Robin algorithm) and forwards the real HTTP request to it.
5. If you run 3 Address services, the Load Balancer will evenly distribute requests among all three!

## 4. Circular Dependencies (Architectural Warning)
- **Bi-directional calls are possible:** Address could easily call Employee using `http://EMPLOYEE/...`.
- **The Danger:** Avoid having Employee call Address AND Address call Employee to fulfill the *same* user request. This creates an infinite loop where they wait for each other forever, eventually crashing the servers. Flow should generally go in one direction.

## 5. Servers, Instances, and Limits
- **IP Address vs Server:** An IP address is just the "phone number" to reach the server. Developers often use the terms interchangeably.
- **How many instances can run?** There is no hard limit imposed by Eureka or Spring Boot. You can run 1,000 instances if needed. Limits are restricted only by your physical hardware (RAM/CPU) or your deployment tool (like Kubernetes maxReplicas).
- **Server Load:** A standard Tomcat embedded server handles 200 simultaneous threads. If your code is fast (1ms), it can handle 200,000 requests per second. If your database queries are slow (1s), it can only handle 200 requests per second. The bottleneck is usually the database, not Spring Boot.

## 6. The Role of the New DTOs
- **AddressResponseDto:** A "catching mitt" used by `RestTemplate` to map the raw JSON returned by the Address service into a usable Java Object.
- **EmployeeWithAddressDto:** A "master shipping box" that combines the Employee data (from the database) and Address data (from the API call) into a single, clean JSON response for the end user.
