# Auth Service Overview

The `AuthService` is responsible for handling user authentication and generating JSON Web Tokens (JWTs) for secure communication across our microservices architecture. It acts as the central authority for registering new users and verifying credentials. It operates on port `8083` and registers itself with the Eureka Service Registry.

---

## 📂 Folder Structure

```text
AuthService
├── pom.xml
└── src
    └── main
        ├── resources
        │   └── application.properties
        └── java
            └── com
                └── microservice
                    └── AuthService
                        ├── AuthServiceApplication.java
                        ├── config
                        │   ├── CustomUserDetails.java
                        │   ├── CustomUserDetailsService.java
                        │   └── SecurityConfig.java
                        ├── controller
                        │   └── AuthController.java
                        ├── dto
                        │   └── AuthRequest.java
                        ├── entity
                        │   └── UserCredential.java
                        ├── repository
                        │   └── UserCredentialRepository.java
                        └── service
                            ├── AuthService.java
                            └── JwtService.java
```

---

## 🧩 Implementation Details

Below is a breakdown of the packages and the role of each file within them:

### 1. `config` (Security Configuration)
This package handles everything related to Spring Security and how users are verified.
* **`SecurityConfig.java`**: The core configuration file for Spring Security. It disables CSRF (common for REST APIs), permits unauthenticated access to our `/auth/**` endpoints, and configures the `BCryptPasswordEncoder` (for hashing passwords) and `AuthenticationManager`.
* **`CustomUserDetails.java`**: Implements Spring Security's `UserDetails` interface. It acts as an adapter that translates our `UserCredential` entity into a format that Spring Security understands during the login process.
* **`CustomUserDetailsService.java`**: Implements `UserDetailsService`. It is used by Spring Security to look up user information from the database (via our repository) given a username.

### 2. `controller` (API Endpoints)
* **`AuthController.java`**: Exposes the REST API endpoints to the outside world.
  * `POST /auth/register`: Accepts a `UserCredential` object, encrypts the password, and saves the new user to the database.
  * `POST /auth/token`: Accepts an `AuthRequest` (username/password). It uses the `AuthenticationManager` to verify the credentials. If valid, it returns a signed JWT string.
  * `GET /auth/validate`: Takes a `token` query parameter and checks if it is valid (not expired, correct signature).

### 3. `dto` (Data Transfer Objects)
* **`AuthRequest.java`**: A simple POJO used to map the incoming JSON payload when a user attempts to log in (contains only `username` and `password`).

### 4. `entity` (Database Models)
* **`UserCredential.java`**: The JPA Entity that maps to the `user_credentials` table in the database. It stores the `id`, `username`, `email`, and the encrypted `password`.

### 5. `repository` (Database Access)
* **`UserCredentialRepository.java`**: A Spring Data JPA interface extending `JpaRepository`. It provides all basic CRUD operations and includes a custom query method `findByUsername(String username)` used during login.

### 6. `service` (Business Logic)
* **`AuthService.java`**: Contains the core business logic. It handles encrypting the password before delegating the save operation to the repository, and it acts as a facade for JWT generation/validation.
* **`JwtService.java`**: Responsible for the cryptography behind JSON Web Tokens. It contains logic to build the JWT, sign it using an HMAC SHA key (derived from a secret in `application.properties`), set an expiration time, and parse/validate incoming tokens.

### 7. Main & Configuration
* **`AuthServiceApplication.java`**: The main entry point of the Spring Boot application. It is annotated with `@EnableDiscoveryClient` so the service registers itself with the Eureka server upon startup.
* **`application.properties`**: Contains environment configurations such as:
  * Server port (`8083`)
  * Database connection details (MySQL URL, username, password, JPA properties)
  * Eureka Client configuration (pointing to `localhost:8761`)
  * The JWT Secret and Expiration Time used by `JwtService`.

### 8. `pom.xml` (Dependencies)
The project leverages several key Spring Boot Starter packages:
* `spring-boot-starter-web` (REST APIs)
* `spring-boot-starter-security` (Authentication / Authorization)
* `spring-boot-starter-data-jpa` (Hibernate / Database interaction)
* `mysql-connector-j` (MySQL Driver)
* `spring-cloud-starter-netflix-eureka-client` (Service Registration)
* `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (For creating and parsing JSON Web Tokens)
