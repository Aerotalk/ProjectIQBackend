# InvoiceIQ System Architecture

This document provides a comprehensive overview of the backend system architecture for the InvoiceIQ Core ERP application. It is intended for Backend Developers and Technical Architects to understand the foundational patterns established in the project.

## 1. Technology Stack
- **Framework:** Java 21 + Spring Boot 3.5.4
- **Database:** PostgreSQL (accessed via Spring Data JPA / Hibernate)
- **Security:** Spring Security with stateless JWT (JSON Web Tokens)
- **API Documentation:** SpringDoc OpenAPI 3
- **Build Tool:** Gradle

## 2. Directory Structure (`src/main/java/com/grivetyglobals/invoiceiq`)
The application follows a standard layered monolithic architecture:
- `config/`: Global configurations (e.g., Security Config, Swagger Config, Data Seeder).
- `controller/`: REST endpoints exposed to the frontend. Controllers are thin and only handle HTTP routing and authorization checks.
- `service/`: The core business logic layer. Services enforce Data Scopes, handle transactions, and interact with the database.
- `repository/`: Spring Data JPA interfaces for database access.
- `entity/`: JPA entities representing database tables.
- `dto/`: Data Transfer Objects for Request/Response payloads (decouples API contracts from DB entities).
- `security/`: Custom security components, JWT filters, and the `SecurityUtils` context extractor.
- `exception/`: Global Exception Handler and custom exception definitions.
- `enums/`: Standardized enumerations (e.g., `DataScope`, `OrganizationType`).

## 3. Core Design Patterns

### 3.1 Multi-Tenancy (Data Isolation)
InvoiceIQ operates as a multi-tenant SaaS application. Data isolation is maintained logically at two levels:
1. **Organization Level:** Represents a top-level client (Tenant).
2. **Company Level:** Represents child companies/subsidiaries underneath an Organization.

**Enforcement:**
- Almost every entity in the database (e.g., `Employee`, `Department`, `Role`) contains an `organization_id` (and optionally a `company_id`).
- We NEVER trust the frontend to provide these IDs.
- Data isolation is strictly enforced in the Service Layer by dynamically appending `WHERE organization_id = X` to all database queries.

### 3.2 Context Extraction (`SecurityUtils.java`)
The `SecurityUtils` class is the backbone of our secure multi-tenant architecture. 
When a user logs in, a JWT is issued. On every subsequent request, the `JwtAuthenticationFilter` intercepts the request and loads the `User` object (which contains their `Organization` and `Company` relationships) into the Spring Security Context.

Services use `SecurityUtils` to perform their business logic safely:
```java
UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
UUID currentCompanyId = SecurityUtils.getCurrentCompanyId();
// Use these IDs to filter repository queries or validate record ownership
```

### 3.3 Authorization (`@PreAuthorize`)
Authorization is enforced directly on the Controller methods using standard Spring annotations, combined with our custom permissions.
- **Basic Permission Check:** `@PreAuthorize("hasAuthority('department.create')")`
- **Record-Level Scope Check:** `@PreAuthorize("hasPermission(#id, 'Department', 'department.edit')")` (Uses `CustomPermissionEvaluator` to ensure the user has permission *and* the record falls within their Data Scope).

### 3.4 Service-Level Data Scope Filtering
When querying lists of data (e.g., fetching all Employees), the Service layer must apply Data Scopes (e.g., `COMPANY`, `TEAM`). 

*Example Pattern:*
1. Controller calls `departmentService.getAllDepartments()`.
2. Service fetches `currentOrgId` and `currentCompanyId` from `SecurityUtils`.
3. Service calls `departmentRepository.findByOrganizationIdAndCompanyId(currentOrgId, currentCompanyId)`.
4. The Repository query handles the dynamic filtering:
   ```sql
   SELECT d FROM Department d 
   WHERE d.organization.id = :organizationId 
   AND (:companyId IS NULL OR d.company.id = :companyId)
   ```
*(Note: If `companyId` is null—e.g., for an Org Admin—the query returns all departments for the Organization).*

## 4. Error Handling
The application uses a centralized `@ControllerAdvice` (GlobalExceptionHandler) to intercept exceptions thrown by the Service layer (e.g., "Access Denied", "Entity Not Found") and format them into standard JSON error responses with appropriate HTTP status codes (403 Forbidden, 404 Not Found, etc.).
