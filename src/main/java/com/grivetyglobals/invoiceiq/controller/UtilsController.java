package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.dto.GstInfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/utils")
public class UtilsController {

    private static final Map<String, String> STATE_CODES = new HashMap<>();

    static {
        STATE_CODES.put("01", "Jammu and Kashmir");
        STATE_CODES.put("02", "Himachal Pradesh");
        STATE_CODES.put("03", "Punjab");
        STATE_CODES.put("04", "Chandigarh");
        STATE_CODES.put("05", "Uttarakhand");
        STATE_CODES.put("06", "Haryana");
        STATE_CODES.put("07", "Delhi");
        STATE_CODES.put("08", "Rajasthan");
        STATE_CODES.put("09", "Uttar Pradesh");
        STATE_CODES.put("10", "Bihar");
        STATE_CODES.put("11", "Sikkim");
        STATE_CODES.put("12", "Arunachal Pradesh");
        STATE_CODES.put("13", "Nagaland");
        STATE_CODES.put("14", "Manipur");
        STATE_CODES.put("15", "Mizoram");
        STATE_CODES.put("16", "Tripura");
        STATE_CODES.put("17", "Meghalaya");
        STATE_CODES.put("18", "Assam");
        STATE_CODES.put("19", "West Bengal");
        STATE_CODES.put("20", "Jharkhand");
        STATE_CODES.put("21", "Odisha");
        STATE_CODES.put("22", "Chhattisgarh");
        STATE_CODES.put("23", "Madhya Pradesh");
        STATE_CODES.put("24", "Gujarat");
        STATE_CODES.put("26", "Dadra and Nagar Haveli and Daman and Diu");
        STATE_CODES.put("27", "Maharashtra");
        STATE_CODES.put("29", "Karnataka");
        STATE_CODES.put("30", "Goa");
        STATE_CODES.put("31", "Lakshadweep");
        STATE_CODES.put("32", "Kerala");
        STATE_CODES.put("33", "Tamil Nadu");
        STATE_CODES.put("34", "Puducherry");
        STATE_CODES.put("35", "Andaman and Nicobar Islands");
        STATE_CODES.put("36", "Telangana");
        STATE_CODES.put("37", "Andhra Pradesh");
        STATE_CODES.put("38", "Ladakh");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/gst-info")
    public ResponseEntity<GstInfoResponse> getGstInfo(@RequestParam String gstNumber) {
        if (gstNumber == null || gstNumber.length() < 15) {
            return ResponseEntity.badRequest().build();
        }

        String stateCode = gstNumber.substring(0, 2);
        String pan = gstNumber.substring(2, 12);
        String stateName = STATE_CODES.getOrDefault(stateCode, "Unknown");

        return ResponseEntity.ok(GstInfoResponse.builder()
                .pan(pan)
                .stateCode(stateCode)
                .stateName(stateName)
                .build());
    }
}
