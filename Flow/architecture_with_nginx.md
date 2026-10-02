# System Architecture with NGINX

Below is an updated architecture diagram showcasing the traffic flow with an NGINX Reverse Proxy and how the API Gateway internally communicates with the Auth Service.

```mermaid
graph TD
    %% Define Styles
    classDef client fill:#2a4570,stroke:#fff,stroke-width:2px,color:#fff
    classDef proxy fill:#8b3a3a,stroke:#fff,stroke-width:2px,color:#fff
    classDef gateway fill:#2d5e38,stroke:#fff,stroke-width:2px,color:#fff
    classDef service fill:#5e3d29,stroke:#fff,stroke-width:2px,color:#fff
    classDef auth fill:#9e6329,stroke:#fff,stroke-width:2px,color:#fff
    classDef eureka fill:#6a2d2d,stroke:#fff,stroke-width:2px,color:#fff

    %% Nodes
    Client["Client<br>(Browser/Mobile)"]:::client
    Nginx["NGINX Reverse Proxy<br>(Port 80/443)"]:::proxy
    
    subgraph Microservices Environment
        Gateway["API Gateway<br>(Port 9090)<br><br>1. RateLimitFilter<br>2. LoggingFilter<br>3. AuthenticationFilter"]:::gateway
        
        subgraph Destination Services
            Employee["Employee Service<br>(Port 8081)"]:::service
            Address["Address Service<br>(Port 8082)"]:::service
        end
        
        Auth["Auth Service<br>(Port 8083)"]:::auth
    end
    
    Eureka["EUREKA SERVER<br>(Port 8761)"]:::eureka

    %% Flow 1: Client to Server
    Client -->|1. HTTPS Request| Nginx
    Nginx -->|"2. Forward to 9090<br>(Adds X-Forwarded-For)"| Gateway
    
    %% Internal Validation (Flow 2)
    Gateway -.->|3. Internal WebClient call<br>to validate JWT token| Auth
    
    %% Traffic passing through filters
    Gateway -->|4. If Valid, Route traffic| Employee
    Gateway -->|4. If Valid, Route traffic| Address
    
    %% Eureka Registrations
    Gateway -.->|Registers/Discovers| Eureka
    Employee -.->|Registers| Eureka
    Address -.->|Registers| Eureka
    Auth -.->|Registers| Eureka

    %% Link Styles 
    linkStyle 0 stroke:#4287f5,stroke-width:2px;
    linkStyle 1 stroke:#f5a442,stroke-width:2px;
    linkStyle 2 stroke:#ff0000,stroke-width:2px,stroke-dasharray: 5 5;
    linkStyle 3,4 stroke:#42f563,stroke-width:2px;
    linkStyle 5,6,7,8 stroke:#a3a3a3,stroke-width:1px,stroke-dasharray: 5 5;
```

### Flow Explanations

**Flow 1 (External Request):**
1. The **Client** sends a request to NGINX on port 443.
2. **NGINX** appends the client's true IP into the `X-Forwarded-For` header and forwards the request to the **API Gateway** on port 9090.
3. The API Gateway runs through the filter chain:
   * **RateLimitFilter**: Checks the IP in `X-Forwarded-For`.
   * **LoggingFilter**: Logs the IP and path.
   * **AuthenticationFilter**: Sees a protected route and needs to validate the token (Triggers Flow 2).
4. After validation succeeds, the Gateway routes the request to the `Employee` or `Address` service.

**Flow 2 (Server-to-Server Internal Validation):**
1. The `AuthenticationFilter` in the Gateway suspends the request.
2. It uses `WebClient` to make an internal HTTP call to `http://AUTHSERVICE/auth/validate`.
3. The internal LoadBalancer asks **Eureka** where `AUTHSERVICE` is located.
4. The request goes directly to the **Auth Service** (bypassing NGINX).
5. The Auth Service returns success, and the Gateway un-suspends the original client request.
