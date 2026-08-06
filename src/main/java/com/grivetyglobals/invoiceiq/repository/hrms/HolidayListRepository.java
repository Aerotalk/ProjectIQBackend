package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.HolidayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HolidayListRepository extends JpaRepository<HolidayList, UUID> {
    List<HolidayList> findByOrganizationId(UUID organizationId);
}
