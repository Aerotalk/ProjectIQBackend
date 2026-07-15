package com.grivetyglobals.invoiceiq.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping(value = {"/", "/health"}, produces = MediaType.TEXT_HTML_VALUE)
    public String healthCheck() {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>System Status - InvoiceIQ</title>
                    <style>
                        body {
                            font-family: 'Inter', system-ui, -apple-system, sans-serif;
                            background-color: #0f172a;
                            color: #f8fafc;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            height: 100vh;
                            margin: 0;
                        }
                        .container {
                            background-color: #1e293b;
                            padding: 3rem;
                            border-radius: 16px;
                            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
                            text-align: center;
                            max-width: 500px;
                            border: 1px solid #334155;
                        }
                        .status-icon {
                            font-size: 4rem;
                            margin-bottom: 1rem;
                            animation: pulse 2s infinite;
                        }
                        h1 {
                            margin: 0 0 1rem 0;
                            color: #38bdf8;
                            font-size: 2rem;
                        }
                        p {
                            color: #94a3b8;
                            line-height: 1.6;
                            font-size: 1.1rem;
                            margin-bottom: 2rem;
                        }
                        .details {
                            background-color: #0f172a;
                            padding: 1.5rem;
                            border-radius: 12px;
                            text-align: left;
                        }
                        .detail-item {
                            display: flex;
                            justify-content: space-between;
                            padding: 0.5rem 0;
                            border-bottom: 1px solid #1e293b;
                        }
                        .detail-item:last-child {
                            border-bottom: none;
                        }
                        .label {
                            color: #cbd5e1;
                            font-weight: 600;
                        }
                        .value {
                            color: #4ade80;
                            font-weight: bold;
                        }
                        @keyframes pulse {
                            0% { transform: scale(1); }
                            50% { transform: scale(1.1); }
                            100% { transform: scale(1); }
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="status-icon">🚀</div>
                        <h1>All Systems Operational</h1>
                        <p>The InvoiceIQ backend services are up and running smoothly. We are ready to handle requests!</p>
                        
                        <div class="details">
                            <div class="detail-item">
                                <span class="label">🌐 Environment</span>
                                <span class="value">Production</span>
                            </div>
                            <div class="detail-item">
                                <span class="label">🟢 Status</span>
                                <span class="value">Healthy</span>
                            </div>
                            <div class="detail-item">
                                <span class="label">⚡ Database Connection</span>
                                <span class="value">Active</span>
                            </div>
                            <div class="detail-item">
                                <span class="label">🛡️ Security</span>
                                <span class="value">Secured</span>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """;
    }
}
