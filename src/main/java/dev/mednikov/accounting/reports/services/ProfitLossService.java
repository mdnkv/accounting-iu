package dev.mednikov.accounting.reports.services;

import dev.mednikov.accounting.reports.dto.ProfitLossDto;
import dev.mednikov.accounting.reports.dto.ProfitLossSummaryDto;

import java.time.LocalDate;
import java.util.UUID;

public interface ProfitLossService {

    ProfitLossDto getProfitLoss (UUID organizationId, LocalDate fromDate, LocalDate toDate);

    ProfitLossSummaryDto getProfitLossSummary (UUID organizationId, int daysCount);

}
