package dev.mednikov.accounting.reports.controllers;

import dev.mednikov.accounting.reports.dto.BalanceSheetDto;
import dev.mednikov.accounting.reports.dto.ProfitLossDto;
import dev.mednikov.accounting.reports.services.BalanceSheetService;
import dev.mednikov.accounting.reports.services.ProfitLossService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportRestController {

    private final BalanceSheetService balanceSheetService;
    private final ProfitLossService profitLossService;

    public ReportRestController(BalanceSheetService balanceSheetService, ProfitLossService profitLossService) {
        this.balanceSheetService = balanceSheetService;
        this.profitLossService = profitLossService;
    }

    @GetMapping("/balance-sheet/{organizationId}")
    @PreAuthorize("hasAuthority(#organizationId) and hasAuthority('reports:view')")
    public @ResponseBody BalanceSheetDto getBalanceSheet (
            @PathVariable UUID organizationId,
            @RequestParam(required = false) LocalDate date) {
        // return the balance sheet for current date if no date provided
        return this.balanceSheetService.getBalanceSheet(organizationId, Objects.requireNonNullElseGet(date, LocalDate::now));
    }

    @GetMapping("/profit-loss/{organizationId}")
    @PreAuthorize("hasAuthority(#organizationId) and hasAuthority('reports:view')")
    public @ResponseBody ProfitLossDto getProfitLoss (
            @PathVariable UUID organizationId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {
        return this.profitLossService.getProfitLoss(organizationId, fromDate, toDate);
    }

}
