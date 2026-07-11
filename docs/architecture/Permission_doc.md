# Implementation Plan: Permission Management Architecture

This document breaks down the business requirements from the PDF and our subsequent discussions into a technical architecture and implementation plan for the InvoiceIQ application.

## Problem Statement
The application requires a robust Role-Based Access Control (RBAC) and Attribute-Based Access Control (ABAC) system that supports a hierarchical multi-tenancy model (System -> Organization -> Company), along with Permission Groups, Data Scopes, and User-level overrides. The current model (`User -> Role -> Permission`) needs significant enhancements to support this.

## 1. Administrative Hierarchy & Multi-Tenancy

As discussed, the system will operate on three primary administrative tiers:

1. **System Level (System Super Admin):**
   - Has global access across the entire platform.
   - Registers new `Organization`s.
   - Creates the initial **Org Super Admin** for those organizations.

2. **Organization Level (Org Super Admin & Org Admins):**
   - Has access scoped to their specific `Organization` (including all underlying companies).
   - Can create new `Company` entities under their organization.
   - Creates the initial **Company Super Admin** for those companies.
   - Can create other Org-level Admins.

3. **Company Level (Company Super Admin & Company Admins):**
   - Has access scoped to their specific `Company`.
   - Creates `Employee`s (and implicitly their `User` accounts) that belong to their company.
   - Can create other Company-level Admins.

### Required Entity Adjustments for Hierarchy
Currently, `Employee` and `User` are tied to `Organization`, but not to `Company`. To support the "Company Super Admin" flow, we must associate users and employees with specific companies.
- **`User` Entity:** Add `company_id` (nullable, as System or Org admins might not belong to a specific company).
- **`Employee` Entity:** Add `company_id` (nullable).
- **`Role` Entity:** Add `company_id` (nullable). A role can now be an Org-level role (e.g., "Org Admin") or a Company-level role (e.g., "Company Sales Exec").

## 2. Breakdown of Business Concepts

1. **Applications & Modules:**
   - Grouped using the existing `module` string field on the `Permission.java` entity.

2. **Actions & Navigation Permissions:**
   - Granular actions (e.g., `customer.view`, `customer.create`) map 1:1 to rows in the `permissions` table.

3. **Permission Groups:**
   - Bundles of permissions (e.g., "Sales Operations" = Customer + Vendor + Product + Quotation permissions).
   - We will introduce a new `PermissionGroup` entity.

4. **Roles vs. Permission Groups:**
   - A Role (e.g., "Company Sales Manager") can have a mix of Permission Groups (e.g., "Sales Operations") AND individual direct Permissions.

5. **User Overrides:**
   - A new `UserPermission` entity to store individual user overrides (both positive and negative).

## 3. Understanding Data Scopes (Updated for Hierarchy)

Data Scopes change permissions from a simple "Yes/No" to "Which data?". 

The scopes will explicitly support your hierarchy:
- **GLOBAL**: Can access the record across the entire platform (System Super Admin).
- **ORGANIZATION**: Can access the record if it belongs to their Organization (Org Admin).
- **COMPANY**: Can access the record if it belongs to their specific Company (Company Admin).
- **DEPARTMENT**: Can access the record if created by someone in their Department.
- **TEAM**: Can access the record if created by someone in their immediate Team.
- **OWN**: Can only access records they created themselves.

*Example:* When an Org Admin tries to view a Customer, the system checks if the Customer belongs to the admin's `organization_id`. When a Company Admin tries to view a Customer, the system checks if the Customer belongs to their specific `company_id`.

## 4. Enforcement at the Service/Business Level

We will enforce these rules using **Spring Security**:

1. **Basic Enforcement (Boolean Checks):**
   We update `UserDetailsServiceImpl` to load all effective permissions (Roles + Groups + Overrides) as Spring Security "Authorities".
   ```java
   @PreAuthorize("hasAuthority('company.create')")
   public Company createCompany(...) { ... }
   ```

2. **Scoped Enforcement (Data Scopes):**
   We will implement a Custom Spring Security `PermissionEvaluator` to handle the hierarchy.
   ```java
   @PreAuthorize("hasPermission(#employeeId, 'Employee', 'employee.edit')")
   public Employee updateEmployee(UUID employeeId, EmployeeRequest request) { ... }
   ```
   The evaluator will:
   1. Find the user's scope for `employee.edit`.
   2. Fetch the target Employee.
   3. Check the scope (e.g., if scope is `COMPANY`, verify `user.companyId == targetEmployee.companyId`).

   Additionally, Service Layer queries will automatically append `WHERE company_id = ?` or `WHERE organization_id = ?` based on the user's context, ensuring list views only return authorized data.

## Proposed Database Changes Summary

1. **Modify existing Entities:** Add `company_id` to `User`, `Employee`, `Role`, `Department`.
2. **New Entity `PermissionGroup` & `PermissionGroupMapping`**: To bundle permissions.
3. **New Join Table `RolePermissionGroup`**: Mapping `Role` to `PermissionGroup`.
4. **New Entity `UserPermission`**: For user-level overrides.
5. **New Enum `DataScope`**: `GLOBAL`, `ORGANIZATION`, `COMPANY`, `DEPARTMENT`, `TEAM`, `OWN`, `CUSTOM`.

## User Review Required

Does this accurately capture the System -> Organization -> Company hierarchy and flow? If you are satisfied with this plan, please click **Proceed** and I will begin implementing the code changes!
