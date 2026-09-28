# Detailed Implementation Plan: Microservices Architecture

This document outlines the step-by-step roadmap to build, secure, containerize, and deploy your complete microservices architecture, including Docker, Kubernetes, Redis, and CI/CD pipelines.

---

## Phase 1: Basic Microservices (Easiest)
*The most familiar part. We build standard Spring Boot REST APIs without worrying about complex networking yet.*

### 1.1 Employee Service (Port: 8081)
*   **Goal:** Manage employee data.
*   **Tasks:** Finish the CRUD API using standard Spring Boot. Keep it simple and running locally on port 8081.

### 1.2 Address Service (Port: 8082)
*   **Goal:** Manage address data.
*   **Tasks:** Create a brand new Spring Boot project. Implement simple Entity, Repository, and Controller for Addresses. Run on port 8082.

---

## Phase 2: Data & Caching (Easy-Medium)
*Connecting the services to real databases and caching mechanisms.*

### 2.1 MySQL Integration
*   **Goal:** Move away from in-memory (H2) databases.
*   **Tasks:** Install MySQL locally, update `application.properties` in both services to connect to real database schemas.

### 2.2 Redis Integration
*   **Goal:** Speed up read requests using caching.
*   **Tasks:** Install Redis locally. Add `spring-boot-starter-data-redis` to the services. Use `@EnableCaching` and `@Cacheable` on the GET endpoints.

---

## Phase 3: Service Discovery (Medium)
*Now we make the services aware of each other so they can communicate dynamically.*

### 3.1 Eureka Server (Port: 8761)
*   **Goal:** Create the central registry.
*   **Tasks:** Create a new Spring Boot app with `@EnableEurekaServer`.
### 3.2 Registering Clients
*   **Goal:** Connect Employee and Address services to Eureka.
*   **Tasks:** Add Eureka Client dependency to both services. Add `@EnableDiscoveryClient` and configure them to point to `localhost:8761`.

### 3.3 Inter-Service Communication & Load Balancing
*   **Goal:** Allow Employee Service to dynamically fetch data from the Address Service.
*   **Tasks:** Create a `@LoadBalanced RestTemplate`. Create `AddressResponseDto` and `EmployeeWithAddressDto` to map JSON data. Update `EmployeeService` to call `http://ADDRESS/v1/address/{id}` to demonstrate Eureka name resolution and round-robin load balancing.

---

## Phase 4: API Gateway & Security (Hard)
*Securing the network and creating a single entry point. Spring Security can be tricky, making this harder.*

### 4.1 Auth Service & JWT (Port: 8083)
*   **Goal:** Handle logins and issue JSON Web Tokens.
*   **Tasks:** Create Auth Service. Configure Spring Security. Create `/login` endpoint that generates JWTs. Register with Eureka.

### 4.2 API Gateway (Port: 9090)
*   **Goal:** Route traffic and protect routes.
*   **Tasks:** Create Gateway service. Configure routing in `application.yml`. Implement a global filter that intercepts incoming requests, reads the JWT, and validates it with the Auth Service before letting traffic through to Employee/Address.

---

## Phase 5: Containerization (Harder)
*Moving away from running apps on your local machine to running them in isolated Docker containers.*

### 5.1 Docker & Docker Compose
*   **Goal:** Run the entire architecture with one command.
*   **Tasks:** Write a `Dockerfile` for all 5 Spring Boot apps. Write a `docker-compose.yml` that defines networks, environment variables, and brings up MySQL, Redis, Eureka, Gateway, and the Services together.

---

## Phase 6: CI/CD Pipeline (Advanced)
*Automating the boring stuff.*

### 6.1 GitHub Actions
*   **Goal:** Automate testing and Docker image creation.
*   **Tasks:** Write a YAML workflow that automatically triggers on `git push`. It should run `mvn test`, build the jars, build the Docker images, and push them to DockerHub automatically.

---

## Phase 7: Kubernetes Orchestration (Hardest)
*Enterprise-grade scaling and networking. The steepest learning curve.*

### 7.1 KIND / Minikube
*   **Goal:** Deploy the containers into a Kubernetes cluster and manage advanced networking.
*   **Tasks:** Install KIND. Write Kubernetes `.yaml` manifests (Deployments, Services, ConfigMaps, Secrets, Ingress). Apply them to the cluster. Configure native Kubernetes Load Balancing (ClusterIP/Ingress) to distribute traffic across pods, and manage Horizontal Pod Autoscaling (HPA) to set dynamic server limits.
