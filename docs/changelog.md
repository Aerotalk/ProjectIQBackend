# Changelog - July 15, 2026

This is a living document that tracks the structural enhancements and code changes made to the ERP system today.

## Backend Data Model Alignments

The following modules have been updated in the backend to align with the standard ERP "Header + Items" pattern used in the frontend.

### 1. Purchase Orders Module
* **Added Entities & DTOs**: 
  * `PurchaseOrderLineItem.java`
  * `PurchaseOrderLineItemDto.java`
* **Updated `PurchaseOrder.java` & `PurchaseOrderDto.java`**:
  * Added `List<PurchaseOrderLineItem> lineItems`
  * Added `grandTotal`, `description`, `internalNotes`, `expectedDelivery`, `attachmentName`
  * Removed the flat `amount` field.
* **Refactored `PurchaseOrderService.java`**:
  * Implemented nested saving for line items.
  * Updated `mapToEntity` and `mapToDto` to handle the new fields.

### 2. Delivery Challans Module
* **Added Entities & DTOs**: 
  * `ChallanLineItem.java`
  * `ChallanLineItemDto.java`
* **Updated `Challan.java` & `ChallanDto.java`**:
  * Added `List<ChallanLineItem> lineItems`
  * Added `description`, `linkedVendorPoId`, `attachmentName`
* **Refactored `ChallanService.java`**:
  * Implemented nested saving for line items.
  * Updated `mapToEntity` and `mapToDto` to handle the new fields.

### 3. Expenses Module
* **Updated `Expense.java` & `ExpenseDto.java`**:
  * Transformed from a flat object to a robust expense record.
  * Added `category`, `description`, `paidBy`, `isGstApplicable`, `gstAmount`, `isInputCreditClaimable`, `receiptName`.
* **Refactored `ExpenseService.java`**:
  * Updated `mapToEntity` and `mapToDto` to seamlessly handle the new GST and expense tracking fields.

### 4. Database Migrations
* **Added `V3__update_finance_schema.sql`**:
  * Created a Flyway migration script to apply all entity changes to the PostgreSQL database safely. 
  * Renames `amount` to `grand_total` in `purchase_orders`.
  * Creates `purchase_order_line_items` and `challan_line_items` tables.
  * Adds all missing descriptor and flag columns to `purchase_orders`, `challans`, and `expenses`.

## Phase 2 Alignments (Contact & Tax Details)

### 1. Clients & Vendors Modules
* **Updated `Client.java` & `Vendor.java` (and DTOs)**:
  * Added `billingAttention`, `billingPhone`, `shippingAttention`, `shippingPhone`.
* **Refactored Services**:
  * Updated `ClientService.java` and `VendorService.java` to map these four new fields.

### 2. Purchase Orders Module (Line Items)
* **Updated `PurchaseOrderLineItem.java` (and DTO)**:
  * Added `productId`, `itemName`, `discount`, `taxableAmount`, `gstRate`, `gstAmount`.
  * Renamed `unitPrice` to `rate`.
* **Refactored `PurchaseOrderService.java`**:
  * Mapped all new taxation and product fields.

### 3. Delivery Challans Module
* **Updated `Challan.java`**:
  * Altered `linkedVendorPoId` from a raw UUID column into a proper `@ManyToOne` relationship to the `PurchaseOrder` entity.
* **Refactored `ChallanService.java`**:
  * Updated to fetch the `PurchaseOrder` entity and link it accurately on creation and update.

### 4. Phase 2 Database Migrations
* **Added `V4__phase2_finance_schema.sql`**:
  * Adds the new contact fields to the client/vendor tables.
  * Updates the PO line items table with new taxation columns and product linkage.
  * Adds the foreign key constraint `fk_challan_purchase_order` to the `challans` table.

## Phase 3 Alignments (Minor Tweaks)

### 1. Products Module
* **Updated `Product.java`**:
  * Removed the `nullable = false` constraint from `itemCode`, making it completely optional.

### 2. Quotations Module
* **Updated `Quotation.java` (and DTO/Service)**:
  * Added a new `deliveryCost` (`BigDecimal`) field to track delivery costs within quotations.

### 3. Delivery Challans Module
* **Updated `Challan.java` (and DTO/Service)**:
  * Added a new `ewayBillNo` (String) field.

### 4. Expenses Module
* **Refactored `ExpenseController.java`**:
  * Moved the API endpoint mapping from `/api/admin/finance/expenses` to `/api/admin/projects/expenses` to logically group expenses under projects rather than finance.

### 5. Minor Database Migrations
* **Added `V5__minor_tweaks.sql`**:
  * Drops the `NOT NULL` constraint from `item_code` in `sales_products`.
  * Adds the `delivery_cost` column to `sales_quotations`.
  * Adds the `eway_bill_no` column to `challans`.

---

*Note: This document will be continuously updated as further changes are made.*
