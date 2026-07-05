# Entity Relationship Diagram

Here is the complete graphical representation of our database architecture for the Enterprise Tenant system.

```mermaid
erDiagram
    %% Core Tenant Entities
    ORGANIZATION ||--o{ COMPANY : "owns (1:N)"
    ORGANIZATION ||--o{ USER : "employs (1:N)"
    ORGANIZATION ||--o{ EMPLOYEE : "employs (1:N)"
    ORGANIZATION ||--o{ DEPARTMENT : "has departments (1:N)"
    ORGANIZATION ||--o{ DESIGNATION : "has designations (1:N)"
    
    %% Company Details
    COMPANY ||--o{ COMPANY_ADDRESS : "has addresses (1:N)"
    COMPANY ||--o{ COMPANY_BANK_ACCOUNT : "has bank accounts (1:N)"

    %% Employee Details
    USER |o--o| EMPLOYEE : "has profile (1:1)"
    DEPARTMENT ||--o{ EMPLOYEE : "has employees (1:N)"
    DESIGNATION ||--o{ EMPLOYEE : "assigned to (1:N)"
    EMPLOYEE ||--o{ EMPLOYEE : "reports to (1:N)"
    DEPARTMENT ||--o{ DEPARTMENT : "parent of (1:N)"

    %% User & Auth Entities
    USER }|--|{ ROLE : "assigned via UserRoles (N:M)"
    USER ||--o{ REFRESH_TOKEN : "owns (1:N)"
    USER ||--o{ VERIFICATION_TOKEN : "owns (1:N)"

    %% Table Definitions
    ORGANIZATION {
        UUID organization_id PK
        String organization_code UK
        String organization_name
        String legal_name
        String organization_type
        String industry
        String status
        DateTime created_at
        DateTime updated_at
        DateTime deleted_at
    }

    COMPANY {
        UUID company_id PK
        UUID organization_id FK
        String company_code UK
        String company_name
        String legal_name
        String gst_number
        String email
        String status
        DateTime created_at
        DateTime updated_at
        DateTime deleted_at
    }

    COMPANY_ADDRESS {
        UUID address_id PK
        UUID company_id FK
        String address_type
        String address_line_1
        String city
        String state
        String country
        String postal_code
    }

    COMPANY_BANK_ACCOUNT {
        UUID bank_account_id PK
        UUID company_id FK
        String bank_name
        String account_holder_name
        String account_number
        String ifsc_code
        String swift_code
        Boolean is_primary
    }

    DEPARTMENT {
        UUID department_id PK
        UUID organization_id FK
        UUID parent_department FK
        String department_code
        String department_name
        String description
    }

    DESIGNATION {
        UUID designation_id PK
        UUID organization_id FK
        String designation_code
        String designation_name
        Integer hierarchy_level
        String description
    }

    EMPLOYEE {
        UUID employee_id PK
        UUID organization_id FK
        UUID user_id FK
        UUID department_id FK
        UUID designation_id FK
        UUID reporting_manager_id FK
        String employee_code
        String first_name
        String middle_name
        String last_name
        String gender
        Date date_of_birth
        Date joining_date
        UUID profile_picture
        String employment_status
    }

    USER {
        UUID id PK
        UUID organization_id FK
        String name
        String email UK
        String password
        Boolean email_verified
    }

    ROLE {
        UUID id PK
        String name UK
    }

    REFRESH_TOKEN {
        UUID id PK
        UUID user_id FK
        String token UK
        DateTime expiry_date
    }

    VERIFICATION_TOKEN {
        UUID id PK
        UUID user_id FK
        String token UK
        String token_type
        DateTime expiry_date
    }
```
