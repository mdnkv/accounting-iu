package dev.mednikov.accounting.reports.services;

import dev.mednikov.accounting.reports.dto.BalanceSheetDto;

import java.time.LocalDate;
import java.util.UUID;

public interface BalanceSheetService {

    BalanceSheetDto getBalanceSheet (UUID organizationId, LocalDate date);

}
