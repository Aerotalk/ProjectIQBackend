$ErrorActionPreference = "Stop"

# 1. Login as Super Admin
$loginResp = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email": "admin3@grivety.com", "password": "SecurePassword123"}'
$token = $loginResp.token
Write-Host "✅ Logged in as Super Admin successfully."

# 2. Create Organization
$orgPayload = @{
    organizationCode = "ORG003"
    organizationName = "Grivety Global v3"
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
    companyCode = "COMP003"
    companyName = "Grivety India v3"
    legalName = "Grivety India Software Pvt Ltd"
    gstNumber = "29ABCDE1234F1Z7"
    email = "hello3@grivety.com"
    phone = "+919876543212"
    status = "Active"
    addresses = @(
        @{
            addressType = "Registered"
            addressLine1 = "123 Tech Park v3"
            city = "Bengaluru"
            state = "Karnataka"
            country = "India"
            postalCode = "560001"
        }
    )
    bankAccounts = @(
        @{
            bankName = "HDFC Bank"
            accountHolderName = "Grivety India v3"
            accountNumber = "000123456791"
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
    name = "Mike Doe"
    email = "mike@grivety.com"
    password = "SecurePassword123"
    organizationId = $orgResp.id
    role = "ROLE_ORGANIZATION_ADMIN"
} | ConvertTo-Json

$userResp = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/users" -Method Post -ContentType "application/json" -Headers @{Authorization="Bearer $token"} -Body $userPayload
Write-Host "✅ User Created: $($userResp.name) ($($userResp.email)) for Organization ID $($userResp.organization.id)"

# 5. Login as the newly created user
$tenantLoginResp = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email": "mike@grivety.com", "password": "SecurePassword123"}'
Write-Host "✅ Logged in as Organization Admin successfully. Token generated."

Write-Host "`nAll endpoints are working perfectly! 🚀"
