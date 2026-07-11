# Permission Management Architecture

## Overview
This document outlines the design and implementation of the Role-Based Access Control (RBAC) and Attribute-Based Access Control (ABAC) system in InvoiceIQ. The architecture is designed to support a strict multi-tenant hierarchy while offering fine-grained access control through Permission Groups and Data Scopes.

This document serves as a reference for both Business Stakeholders (to understand the capabilities of the system) and Frontend Developers (to understand how to consume and enforce these rules in the UI).

---

## 1. Administrative Hierarchy & Multi-Tenancy

The platform operates on a three-tier administrative hierarchy:

1. **System Level (System Super Admin):**
   - **Scope**: Global (across the entire platform).
   - **Capabilities**: Can manage any record. Responsible for registering new `Organizations` and assigning the initial Org Super Admins.

2. **Organization Level (Org Super Admin & Org Admins):**
   - **Scope**: Scoped to a specific `Organization` (and all underlying companies).
   - **Capabilities**: Can manage records within their organization. Responsible for registering `Companies` and assigning the initial Company Super Admins.

3. **Company Level (Company Super Admin & Company Admins):**
   - **Scope**: Scoped to a specific `Company`.
   - **Capabilities**: Can manage records within their specific company. Responsible for creating `Employees` (and their `User` accounts).

*Technical Note:* The `User`, `Employee`, `Role`, and `Department` entities are all strictly associated with a specific `company_id` and `organization_id` to enforce these boundaries.

---

## 2. Core Concepts

### Permissions
Permissions are granular actions that a user can perform (e.g., `customer.create`, `invoice.generate`, `dashboard.view`). These map exactly to the Actions and Navigation items defined in the business requirements.

### Permission Groups
To simplify management, granular Permissions are bundled into **Permission Groups**. 
*Example:* A "Sales Operations" Permission Group might contain the permissions: `customer.view`, `customer.create`, `customer.edit`.

### Roles
A Role (e.g., "Company Sales Manager") is a collection of Permission Groups and/or direct individual permissions.

### User Overrides
Specific permissions can be explicitly granted or revoked at the individual User level, overriding their standard Role definitions.

### Data Scopes
Data Scopes determine *which* specific records a user can access when they have a permission.
Available Scopes:
- `GLOBAL`: Access across the entire platform.
- `ORGANIZATION`: Access restricted to the user's Organization.
- `COMPANY`: Access restricted to the user's Company.
- `DEPARTMENT`: Access restricted to records owned by the user's Department.
- `TEAM`: Access restricted to records owned by the user's immediate Team.
- `OWN`: Access restricted to records created directly by the user.

*Example:* If John has the `customer.view` permission with a Data Scope of `TEAM`, he can only view customers created by members of his immediate team.

---

## 3. Frontend Developer Guide

### Effective Permissions
When a user logs in, the backend computes their **Effective Permissions**. This is a flattened array of strings representing all the permissions the user possesses, resolved by combining their Roles, Permission Groups, and User Overrides.

**Authentication Payload Example (JWT or User Profile API):**
```json
{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "organizationId": "...",
  "companyId": "...",
  "effectivePermissions": [
    "dashboard.view",
    "customer.view",
    "customer.create",
    "invoice.generate"
  ]
}
```

### UI Enforcement Matrix
The frontend application MUST use the `effectivePermissions` array to conditionally render UI components:
- **Navigation Menus:** Only show the "Customers" menu item if the user has `customer.view`.
- **Action Buttons:** Only show the "Generate Invoice" button if the user has `invoice.generate`.
- **Dashboard Widgets:** Only show the Revenue widget if the user has `dashboard.sales.revenue`.

### API Contract: Organization & Company IDs
**IMPORTANT:** The frontend is **no longer required** (and should not attempt) to pass `organizationId` or `companyId` in the API request body or URL parameters. 
The backend automatically extracts the contextual `Organization` and `Company` from the user's secure JWT token. 

### Data Scopes & The UI
The frontend **does not** need to manually compute Data Scopes for list views. The backend APIs automatically filter the returned data based on the user's scope, using the tokens contextual IDs. 
*Example:* Calling `GET /api/admin/employees` will only return the employees John's `TEAM` or `COMPANY` scope allows him to see, without John needing to pass his `companyId` in the request. 

However, if John tries to directly access a specific customer record via URL (`/customers/999`) that falls outside his scope, the backend will return a `403 Forbidden` response. The frontend should gracefully handle this `403` by showing an "Access Denied" screen.

---

## 4. Backend Implementation Details

- **Spring Security Integration:** Effective Permissions are loaded into the Spring Security Context as `GrantedAuthority` objects upon authentication via `UserDetailsServiceImpl`.
- **Context Extraction:** The `SecurityUtils` utility class dynamically extracts `getCurrentOrganizationId()` and `getCurrentCompanyId()` securely from the authentication token, ensuring cross-tenant boundaries are strictly enforced.
- **Permission Service (`PermissionService`):** Handles the complex resolution of user capabilities, dynamically merging:
  1. Direct Role Permissions
  2. Role -> Permission Group -> Permissions
  3. User-Level Overrides (Grants or Revokes)
- **Basic Checks:** Enforced using standard annotations: `@PreAuthorize("hasAuthority('customer.create')")`
- **Scoped Checks:** Enforced using `CustomPermissionEvaluator`: `@PreAuthorize("hasPermission(#employeeId, 'Employee', 'employee.edit')")`. This evaluator dynamically cross-references the requested object's company/org ID against the authenticated user's scopes for:
  - `GLOBAL`: Unrestricted access.
  - `ORGANIZATION`: Checks `user.orgId == target.orgId`.
  - `COMPANY`: Checks `user.companyId == target.companyId`.
  - `DEPARTMENT`: Checks if the target belongs to a department within the user's company (or matching logic based on `targetType`).
  - `OWN`: Checks if `target.userId == user.id` (e.g., for Employee records).

### API Endpoints
The backend provides full REST endpoints for managing these capabilities (`PermissionController`, `PermissionGroupController`):
- Get permission matrix (grouped by module)
- CRUD operations for Permission Groups
- Assign/remove Permissions to/from Groups
- Assign/remove Groups to/from Roles with a specified `DataScope`
- Override Permissions for specific Users (Grant/Revoke) with a specified `DataScope`
- Fetch a User's effective resolved permissions

### Secured Controllers
The following Core ERP controllers have been fully secured with this architecture, enforcing Data Scopes and requiring the specified granular permissions:
- `EmployeeController` (`employee.view`, `employee.create`, `employee.edit`, `employee.delete`)
- `DepartmentController` (`department.view`, `department.create`, `department.edit`, `department.delete`)
- `DesignationController` (`designation.view`, `designation.create`, `designation.edit`, `designation.delete`)
- `RoleController` (`role.view`, `role.create`, `role.edit`, `role.delete`, `role.assign`)
- `SettingController` (`setting.view`, `setting.edit`)
- `OrgController` (`org.view`, `org.edit`)
- `PermissionController` and `PermissionGroupController` (`permission.view`, `role.view`, `role.create`, `role.edit`, `role.delete`, `role.assign`)
- `AdminController` (`org.view`, `org.create`, `org.edit`, `org.delete`, `employee.create`)
- `ApplicationController` (`setting.view`, `setting.edit`)
- `DashboardController` (`dashboard.view`)
- `AuditController` (`org.view`)
- `NotificationController` (`setting.edit` for testing)
- `FileController` (Generic authentication `isAuthenticated()`)
---
*Note: This is a living document and will be updated as new Core ERP Business Modules (like Customers, Vendors, and Quotations) are built.*
