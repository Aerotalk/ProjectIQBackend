$ErrorActionPreference = "Stop"

# 1. Login
$loginResp = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email": "admin@grivety.com", "password": "SecurePassword123"}'
$token = $loginResp.token
Write-Host "✅ Logged in successfully. Token generated."

# 2. Create Company
$companyPayload = @{
    companyName = "Grivety Dev Corp"
    country = "India"
    state = "Karnataka"
    gst = "29ABCDE1234F1Z5"
} | ConvertTo-Json

$companyResp = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/companies" -Method Post -ContentType "application/json" -Headers @{Authorization="Bearer $token"} -Body $companyPayload
Write-Host "✅ Company Created: $($companyResp.companyName) (ID: $($companyResp.id))"

# 3. Create User
$userPayload = @{
    name = "John Doe"
    email = "john@grivetydev.com"
    password = "SecurePassword123"
    companyId = $companyResp.id
    role = "ROLE_COMPANY_ADMIN"
} | ConvertTo-Json

$userResp = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/users" -Method Post -ContentType "application/json" -Headers @{Authorization="Bearer $token"} -Body $userPayload
Write-Host "✅ User Created: $($userResp.name) ($($userResp.email)) for company ID $($userResp.company.id)"

# 4. Login as the newly created user
$tenantLoginResp = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email": "john@grivetydev.com", "password": "SecurePassword123"}'
Write-Host "✅ Logged in as Tenant Admin successfully. Token generated."

Write-Host "`nAll endpoints are working perfectly! 🚀"
