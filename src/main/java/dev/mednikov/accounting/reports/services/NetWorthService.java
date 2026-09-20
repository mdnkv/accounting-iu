package dev.mednikov.accounting.reports.services;

import dev.mednikov.accounting.reports.dto.NetWorthSummaryDto;

import java.util.UUID;

public interface NetWorthService {

    NetWorthSummaryDto getNetWorthSummary (UUID organizationId, int daysCount);

}
