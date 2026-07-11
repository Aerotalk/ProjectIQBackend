# InvoiceIQ API Reference

This document provides a reference for the RESTful APIs currently available and secured in the InvoiceIQ platform. It is designed for Frontend Developers integrating with the backend.

## General Contract

- **Base URL:** All endpoints are prefixed with `/api` (and often `/api/admin`).
- **Authentication:** All secured endpoints require a valid JWT passed in the `Authorization` header as a Bearer token:
  `Authorization: Bearer <token>`
- **Multi-Tenancy:** You **DO NOT** need to pass `organizationId` or `companyId` in the query parameters or request body for any of the endpoints below. The backend automatically extracts this context from your JWT.
- **Content-Type:** `application/json`

---

## 1. Authentication & Users

### 1.1 Login
- **Endpoint:** `POST /api/auth/login`
- **Description:** Authenticates a user and returns a JWT token containing their contextual IDs and `effectivePermissions`.
- **Requires Permission:** None (Public)

---

## 2. Organizations

### 2.1 Get My Organization Profile
- **Endpoint:** `GET /api/org/profile`
- **Description:** Returns the profile of the organization the current user belongs to.
- **Requires Permission:** `org.view`

### 2.2 Update My Organization Profile
- **Endpoint:** `PUT /api/org/profile`
- **Description:** Updates the profile of the organization the current user belongs to.
- **Requires Permission:** `org.edit`
- **Payload:**
  ```json
  {
    "organizationName": "Acme Corp",
    "organizationType": "LLC",
    "industry": "Software",
    "legalName": "Acme Corporation LLC",
    "organizationEmail": "contact@acme.com"
  }
  ```

---

## 3. Employees

### 3.1 Get All Employees (List View)
- **Endpoint:** `GET /api/admin/employees`
- **Description:** Returns a list of employees. Automatically filtered based on the user's Data Scope (e.g., Company Admin only sees their company's employees).
- **Query Params:** `departmentId` (optional), `status` (optional), `keyword` (optional)
- **Requires Permission:** `employee.view`

### 3.2 Create Employee
- **Endpoint:** `POST /api/admin/employees`
- **Description:** Creates a new employee record within the user's organization.
- **Requires Permission:** `employee.create`

### 3.3 Get Employee by ID
- **Endpoint:** `GET /api/admin/employees/{id}`
- **Requires Permission:** `employee.view` (Enforced via Record-Level Data Scope check)

### 3.4 Update Employee
- **Endpoint:** `PUT /api/admin/employees/{id}`
- **Requires Permission:** `employee.edit` (Enforced via Record-Level Data Scope check)

### 3.5 Delete Employee
- **Endpoint:** `DELETE /api/admin/employees/{id}`
- **Requires Permission:** `employee.delete` (Enforced via Record-Level Data Scope check)

---

## 4. Departments

### 4.1 Get All Departments
- **Endpoint:** `GET /api/admin/departments`
- **Requires Permission:** `department.view`

### 4.2 Create Department
- **Endpoint:** `POST /api/admin/departments`
- **Requires Permission:** `department.create`
- **Payload:**
  ```json
  {
    "departmentCode": "ENG",
    "departmentName": "Engineering",
    "description": "Software development team",
    "parentDepartmentId": null
  }
  ```

### 4.3 Update / Delete Department
- **Endpoints:** `PUT /api/admin/departments/{id}`, `DELETE /api/admin/departments/{id}`
- **Requires Permission:** `department.edit`, `department.delete`

---

## 5. Roles & Permissions

### 5.1 Get All Roles
- **Endpoint:** `GET /api/admin/roles`
- **Requires Permission:** `role.view`

### 5.2 Create Role
- **Endpoint:** `POST /api/admin/roles`
- **Requires Permission:** `role.create`
- **Payload:**
  ```json
  {
    "name": "Sales Manager"
  }
  ```

### 5.3 Assign Role to User
- **Endpoint:** `POST /api/admin/roles/{id}/assign`
- **Query Params:** `targetUserId` (The user receiving the role)
- **Requires Permission:** `role.assign`

### 5.4 Assign Multiple Roles to Employee
- **Endpoint:** `PUT /api/admin/roles/employees/{employeeId}/assign`
- **Requires Permission:** `role.assign`
- **Payload:** Array of Role UUIDs: `["uuid1", "uuid2"]`

---

## 6. Designations

### 6.1 Get / Create / Update / Delete Designations
- **Endpoints:** 
  - `GET /api/admin/designations`
  - `POST /api/admin/designations`
  - `PUT /api/admin/designations/{id}`
  - `DELETE /api/admin/designations/{id}`
- **Requires Permissions:** `designation.view`, `designation.create`, `designation.edit`, `designation.delete`

---

## 7. Settings

### 7.1 Get Settings
- **Endpoint:** `GET /api/admin/settings`
- **Query Params:** `category` (optional)
- **Requires Permission:** `setting.view`

### 7.2 Save Setting
- **Endpoint:** `POST /api/admin/settings`
- **Requires Permission:** `setting.edit`
- **Payload:**
  ```json
  {
    "key": "company_logo_url",
    "value": "https://...",
    "category": "branding"
  }
  ```
