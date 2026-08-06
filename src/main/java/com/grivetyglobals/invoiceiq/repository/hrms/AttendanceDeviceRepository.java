package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.AttendanceDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceDeviceRepository extends JpaRepository<AttendanceDevice, UUID> {
    List<AttendanceDevice> findByOrganizationId(UUID organizationId);
}
