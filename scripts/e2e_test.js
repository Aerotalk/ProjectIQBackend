const fs = require('fs');

async function run() {
    console.log("1. Login as Super Admin...");
    let res = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: 'superadmin@aerotalk.in', password: 'password123' })
    });
    let data = await res.json();
    let superAdminToken = data.token;
    if (!superAdminToken) throw new Error("Super Admin Login failed: " + JSON.stringify(data));
    console.log("   - Success! Token received.");

    console.log("2. Create Organization...");
    const r = Math.floor(Math.random() * 100000);
    const orgPayload = {
        organizationCode: "TESTORG" + r,
        organizationName: "Test Organization " + r,
        organizationEmail: "admin" + r + "@testorg.com",
        organizationPassword: "password123",
        legalName: "Test Organization Pvt Ltd",
        organizationType: "Private",
        status: "Active"
    };
    res = await fetch('http://localhost:8080/api/admin/organizations', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + superAdminToken
        },
        body: JSON.stringify(orgPayload)
    });
    data = await res.json();
    const orgId = data.id;
    if (!orgId) throw new Error("Organization Creation failed: " + JSON.stringify(data));
    console.log("   - Success! Org created with ID:", orgId);

    console.log("3. Login to created Org...");
    res = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: "admin" + r + "@testorg.com", password: 'password123' })
    });
    data = await res.json();
    const orgAdminToken = data.token;
    if (!orgAdminToken) throw new Error("Org Admin Login failed: " + JSON.stringify(data));
    console.log("   - Success! Token received.");

    console.log("4. Check Org view profile...");
    res = await fetch('http://localhost:8080/api/org/profile', {
        method: 'GET',
        headers: { 'Authorization': 'Bearer ' + orgAdminToken }
    });
    let text = await res.text();
    if (res.status !== 200) throw new Error("Org profile failed: Status " + res.status + " Body: " + text);
    console.log("   - Success! Org Profile:", text.substring(0, 50) + "...");

    console.log("5. Create Company...");
    const compPayload = {
        organizationId: orgId,
        companyCode: "TESTCOMP" + r,
        companyName: "Test Company " + r,
        email: "admin" + r + "@testcompany.com",
        adminPassword: "password123",
        status: "Active"
    };
    res = await fetch('http://localhost:8080/api/admin/companies', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + orgAdminToken
        },
        body: JSON.stringify(compPayload)
    });
    data = await res.json();
    const compId = data.id;
    if (!compId) throw new Error("Company Creation failed: " + JSON.stringify(data));
    console.log("   - Success! Company created with ID:", compId);

    console.log("6. Login to created Company...");
    res = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: "admin" + r + "@testcompany.com", password: 'password123' })
    });
    data = await res.json();
    const compAdminToken = data.token;
    if (!compAdminToken) throw new Error("Company Admin Login failed: " + JSON.stringify(data));
    console.log("   - Success! Token received.");

    console.log("7. View Company profile...");
    res = await fetch('http://localhost:8080/api/admin/company/profile', {
        method: 'GET',
        headers: { 'Authorization': 'Bearer ' + compAdminToken }
    });
    text = await res.text();
    if (res.status !== 200) throw new Error("Company profile failed: Status " + res.status + " Body: " + text);
    console.log("   - Success! Company Profile:", text.substring(0, 50) + "...");

    console.log("8. Test /me endpoint...");
    res = await fetch('http://localhost:8080/api/auth/me', {
        method: 'GET',
        headers: { 'Authorization': 'Bearer ' + compAdminToken }
    });
    data = await res.json();
    if (!data.effectivePermissions || data.effectivePermissions.length === 0) {
        throw new Error("Me endpoint failed, permissions missing: " + JSON.stringify(data));
    }
    console.log("   - Success! /me returned effective permissions.");

    console.log("9. Test paginated companies list...");
    res = await fetch('http://localhost:8080/api/admin/companies?page=0&size=10', {
        method: 'GET',
        headers: { 'Authorization': 'Bearer ' + orgAdminToken }
    });
    data = await res.json();
    if (data.content === undefined || data.totalElements === undefined) {
        throw new Error("Pagination failed, expected Page object: " + JSON.stringify(data));
    }
    console.log("   - Success! Paginated response received.");

    console.log("ALL TESTS PASSED SUCCESSFULLY!");
}

run().catch(console.error);
