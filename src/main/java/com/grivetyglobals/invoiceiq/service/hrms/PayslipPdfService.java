package com.grivetyglobals.invoiceiq.service.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.PayrollRunDetail;
import com.grivetyglobals.invoiceiq.entity.hrms.PayrollRunDetailComponent;
import com.grivetyglobals.invoiceiq.entity.hrms.PayslipTemplate;
import com.grivetyglobals.invoiceiq.repository.hrms.PayrollRunDetailComponentRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.PayrollRunDetailRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.PayslipTemplateRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayslipPdfService {

    private final PayrollRunDetailRepository detailRepository;
    private final PayrollRunDetailComponentRepository componentRepository;
    private final PayslipTemplateRepository templateRepository;

    public byte[] generatePayslipPdf(UUID detailId) {
        PayrollRunDetail detail = detailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Payroll Run Detail not found"));

        List<PayrollRunDetailComponent> components = componentRepository.findByPayrollRunDetailId(detailId);
        
        // Find default template
        List<PayslipTemplate> templates = templateRepository.findByOrganizationId(detail.getPayrollRun().getOrganization().getId());
        PayslipTemplate template = templates.stream()
                .filter(t -> Boolean.TRUE.equals(t.getSetAsDefault()))
                .findFirst()
                .orElse(templates.isEmpty() ? null : templates.get(0));

        String htmlLayout = (template != null && template.getLayoutHTML() != null) 
                ? template.getLayoutHTML() 
                : getDefaultHtml();

        // Inject Data
        htmlLayout = htmlLayout.replace("{{employeeName}}", detail.getEmployee().getFirstName() + " " + detail.getEmployee().getLastName());
        htmlLayout = htmlLayout.replace("{{employeeCode}}", detail.getEmployee().getEmployeeCode() != null ? detail.getEmployee().getEmployeeCode() : "");
        htmlLayout = htmlLayout.replace("{{payrollPeriod}}", detail.getPayrollRun().getPayrollPeriod());
        htmlLayout = htmlLayout.replace("{{grossPay}}", detail.getGross() != null ? detail.getGross().toString() : "0.00");
        htmlLayout = htmlLayout.replace("{{netPay}}", detail.getNet() != null ? detail.getNet().toString() : "0.00");
        htmlLayout = htmlLayout.replace("{{totalDeductions}}", detail.getTotalDeductions() != null ? detail.getTotalDeductions().toString() : "0.00");

        // Build tables for earnings and deductions
        StringBuilder earningsHtml = new StringBuilder();
        StringBuilder deductionsHtml = new StringBuilder();

        for (PayrollRunDetailComponent comp : components) {
            String row = "<tr><td>" + comp.getComponentName() + "</td><td style='text-align:right'>" + comp.getAmount() + "</td></tr>";
            if ("EARNING".equalsIgnoreCase(comp.getType())) {
                earningsHtml.append(row);
            } else {
                deductionsHtml.append(row);
            }
        }

        htmlLayout = htmlLayout.replace("{{earningsTableRows}}", earningsHtml.toString());
        htmlLayout = htmlLayout.replace("{{deductionsTableRows}}", deductionsHtml.toString());

        // Use Jsoup to clean and ensure valid XML for OpenHTMLToPDF
        Document doc = Jsoup.parse(htmlLayout);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        String finalHtml = doc.html();

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(finalHtml, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private String getDefaultHtml() {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("Document_Templates", "payslip.html");
            if (!java.nio.file.Files.exists(path)) {
                path = java.nio.file.Paths.get("ProjectIQBackend", "Document_Templates", "payslip.html");
            }
            if (java.nio.file.Files.exists(path)) {
                return new String(java.nio.file.Files.readAllBytes(path), java.nio.charset.StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            // ignore and fallback to hardcoded HTML
        }

        return "<!DOCTYPE html><html><head><style>" +
               "body { font-family: sans-serif; } table { width: 100%; border-collapse: collapse; margin-top: 10px; } " +
               "th, td { border: 1px solid #ddd; padding: 8px; } th { background-color: #f2f2f2; text-align: left; } " +
               ".header { text-align: center; margin-bottom: 20px; } " +
               ".summary { margin-top: 20px; font-weight: bold; text-align: right; }" +
               "</style></head><body>" +
               "<div class='header'><h2>Payslip</h2><p>{{employeeName}} ({{employeeCode}}) - {{payrollPeriod}}</p></div>" +
               "<h3>Earnings</h3>" +
               "<table><thead><tr><th>Component</th><th style='text-align:right'>Amount</th></tr></thead><tbody>{{earningsTableRows}}</tbody></table>" +
               "<h3>Deductions</h3>" +
               "<table><thead><tr><th>Component</th><th style='text-align:right'>Amount</th></tr></thead><tbody>{{deductionsTableRows}}</tbody></table>" +
               "<div class='summary'>" +
               "<p>Gross Pay: {{grossPay}}</p>" +
               "<p>Total Deductions: {{totalDeductions}}</p>" +
               "<h3>Net Pay: {{netPay}}</h3>" +
               "</div>" +
               "</body></html>";
    }
}
