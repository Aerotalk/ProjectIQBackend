$ErrorActionPreference = "Continue"

Write-Host "============================================="
Write-Host " Testing ALL InvoiceIQ API Endpoints "
Write-Host "============================================="

$randomSuffix = Get-Random -Maximum 99999
$baseUrl = "http://localhost:8080/api"

# Helper function for printing errors
function Print-Error($ErrorRecord) {
    Write-Host "Failed!" -ForegroundColor Red
    if ($ErrorRecord.Exception.Response) {
        $stream = $ErrorRecord.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        Write-Host $reader.ReadToEnd() -ForegroundColor Yellow
    } else {
        Write-Host $ErrorRecord.Exception.Message -ForegroundColor Yellow
    }
}

# 1. Setup Super Admin
Write-Host "`n[1] Setting up Super Admin..."
$setupBody = @{
    name = "Super Admin"
    email = "admin-$randomSuffix@grivety.com"
    password = "SecurePassword123"
} | ConvertTo-Json
try {
    Invoke-RestMethod -Uri "$baseUrl/auth/setup-super-admin" -Method Post -Body $setupBody -ContentType "application/json" | Out-Null
    Write-Host "Super Admin created." -ForegroundColor Green
} catch {
    Write-Host "Super Admin setup skipped (might already exist)." -ForegroundColor Yellow
}

# 2. Login
Write-Host "`n[2] Logging in as Super Admin..."
$loginBody = @{
    email = "admin-$randomSuffix@grivety.com"
    password = "SecurePassword123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.token
    Write-Host "Token received successfully!" -ForegroundColor Green
} catch {
    Print-Error $_
    exit
}

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# 3. Create Organization
Write-Host "`n[3] Creating Organization..."
$orgBody = @{
    organizationCode = "ORG-$randomSuffix"
    organizationName = "Test Organization $randomSuffix"
    legalName = "Test Org Pvt Ltd"
    organizationType = "Pvt Ltd"
    industry = "Tech"
    status = "Active"
} | ConvertTo-Json

try {
    $org = Invoke-RestMethod -Uri "$baseUrl/admin/organizations" -Method Post -Headers $headers -Body $orgBody
    $orgId = $org.id
    Write-Host "Created Organization with ID: $orgId" -ForegroundColor Green
} catch {
    Print-Error $_
    exit
}

# Generate a mock user ID for tracking
$userId = "123e4567-e89b-12d3-a456-426614174000"

# 4. Create Company
Write-Host "`n[4] Creating Company..."
$companyBody = @{
    organizationId = $orgId
    companyName = "Subsidiary A $randomSuffix"
    legalName = "Subsidiary A Pvt Ltd"
    companyCode = "SUBA"
    status = "Active"
} | ConvertTo-Json

try {
    $company = Invoke-RestMethod -Uri "$baseUrl/admin/companies?userId=$userId" -Method Post -Headers $headers -Body $companyBody
    $companyId = $company.id
    Write-Host "Created Company with ID: $companyId" -ForegroundColor Green
} catch {
    Print-Error $_
}

# 5. Application Registry
Write-Host "`n[5] Registering new Application..."
$appBody = @{
    applicationCode = "HRMS-$randomSuffix"
    applicationName = "HRMS $randomSuffix"
    applicationRoute = "/hrms"
    icon = "users"
    description = "HR Management"
    status = "Active"
} | ConvertTo-Json
try {
    $app = Invoke-RestMethod -Uri "$baseUrl/admin/applications?userId=$userId&organizationId=$orgId" -Method Post -Headers $headers -Body $appBody
    $appId = $app.id
    Write-Host "Registered Application: $($app.applicationName)" -ForegroundColor Green
} catch {
    Print-Error $_
}

# 6. Settings Management
Write-Host "`n[6] Testing Settings Management..."
$settingBody = @{
    key = "THEME_COLOR"
    value = "#FF5733"
    category = "UI"
} | ConvertTo-Json
try {
    $setting = Invoke-RestMethod -Uri "$baseUrl/admin/settings?organizationId=$orgId" -Method Post -Headers $headers -Body $settingBody
    Write-Host "Created Setting: THEME_COLOR = $($setting.settingValue)" -ForegroundColor Green
    
    $settingsList = Invoke-RestMethod -Uri "$baseUrl/admin/settings?organizationId=$orgId" -Method Get -Headers $headers
    Write-Host "Total settings fetched: $($settingsList.Count)" -ForegroundColor Green
} catch {
    Print-Error $_
}

# 7. Role Management
Write-Host "`n[7] Testing Role Management..."
$roleBody = @{
    name = "Manager $randomSuffix"
} | ConvertTo-Json
try {
    $role = Invoke-RestMethod -Uri "$baseUrl/admin/roles?organizationId=$orgId&userId=$userId" -Method Post -Headers $headers -Body $roleBody
    $roleId = $role.id
    Write-Host "Created Role: $($role.roleName)" -ForegroundColor Green
} catch {
    Print-Error $_
}

# 8. Dashboard Metrics
Write-Host "`n[8] Testing Dashboard Metrics..."
try {
    $metrics = Invoke-RestMethod -Uri "$baseUrl/admin/dashboard/metrics?organizationId=$orgId" -Method Get -Headers $headers
    Write-Host "Total Companies: $($metrics.totalCompanies)" -ForegroundColor Green
    Write-Host "Total Roles: $($metrics.totalRoles)" -ForegroundColor Green
} catch {
    Print-Error $_
}

# 9. Audit Logs
Write-Host "`n[9] Fetching Audit Logs..."
Start-Sleep -Seconds 1 # Wait for async logs
try {
    $auditLogs = Invoke-RestMethod -Uri "$baseUrl/admin/audit-logs?organizationId=$orgId" -Method Get -Headers $headers
    Write-Host "Total Audit Logs captured: $($auditLogs.totalElements)" -ForegroundColor Green
    $recentLog = $auditLogs.content[0]
    Write-Host "Most recent action: $($recentLog.action)" -ForegroundColor Green
} catch {
    Print-Error $_
}

Write-Host "`n============================================="
Write-Host " ALL API TESTS COMPLETED "
Write-Host "============================================="
