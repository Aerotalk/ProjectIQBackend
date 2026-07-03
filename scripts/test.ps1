$ErrorActionPreference = "Stop"

# 1. Login as Super Admin
$loginResp = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email": "admin2@grivety.com", "password": "SecurePassword123"}'
$token = $loginResp.token
Write-Host "✅ Logged in as Super Admin successfully."

# 2. Create Organization
$orgPayload = @{
    organizationCode = "ORG002"
    organizationName = "Grivety Global v2"
    legalName = "Grivety Global Pvt Ltd"
    organizationType = "Pvt Ltd"
    industry = "Technology"
    status = "Active"
} | ConvertTo-Json

$orgResp = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/organizations" -Method Post -ContentType "application/json" -Headers @{Authorization="Bearer $token"} -Body $orgPayload
Write-Host "✅ Organization Created: $($orgResp.organizationName) (ID: $($orgResp.id))"

# 3. Create Company under Organization
$companyPayload = @{
    organizationId = $orgResp.id
    companyCode = "COMP002"
    companyName = "Grivety India v2"
    legalName = "Grivety India Software Pvt Ltd"
    gstNumber = "29ABCDE1234F1Z6"
    email = "hello2@grivety.com"
    phone = "+919876543211"
    status = "Active"
    addresses = @(
        @{
            addressType = "Registered"
            addressLine1 = "123 Tech Park v2"
            city = "Bengaluru"
            state = "Karnataka"
            country = "India"
            postalCode = "560001"
        }
    )
    bankAccounts = @(
        @{
            bankName = "HDFC Bank"
            accountHolderName = "Grivety India v2"
            accountNumber = "000123456790"
            ifscCode = "HDFC0001234"
            isPrimary = $true
        }
    )
} | ConvertTo-Json -Depth 5

$companyResp = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/companies" -Method Post -ContentType "application/json" -Headers @{Authorization="Bearer $token"} -Body $companyPayload
Write-Host "✅ Company Created: $($companyResp.companyName) (ID: $($companyResp.id))"
Write-Host "   -> Addresses saved: $($companyResp.addresses.Count)"
Write-Host "   -> Bank Accounts saved: $($companyResp.bankAccounts.Count)"

# 4. Create User under Organization
$userPayload = @{
    name = "John Doe"
    email = "john@grivety.com"
    password = "SecurePassword123"
    organizationId = $orgResp.id
    role = "ROLE_ORGANIZATION_ADMIN"
} | ConvertTo-Json

$userResp = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/users" -Method Post -ContentType "application/json" -Headers @{Authorization="Bearer $token"} -Body $userPayload
Write-Host "✅ User Created: $($userResp.name) ($($userResp.email)) for Organization ID $($userResp.organization.id)"

# 5. Login as the newly created user
$tenantLoginResp = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email": "john@grivety.com", "password": "SecurePassword123"}'
Write-Host "✅ Logged in as Organization Admin successfully. Token generated."

Write-Host "`nAll endpoints are working perfectly! 🚀"
