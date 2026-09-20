package dev.mednikov.accounting.dashboard.controllers;

import dev.mednikov.accounting.reports.dto.ExpenseCategoryDto;
import dev.mednikov.accounting.reports.dto.NetWorthSummaryDto;
import dev.mednikov.accounting.reports.dto.ProfitLossSummaryDto;
import dev.mednikov.accounting.reports.services.ExpenseCategoryService;
import dev.mednikov.accounting.reports.services.NetWorthService;
import dev.mednikov.accounting.reports.services.ProfitLossService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardRestController {

    private final ProfitLossService profitLossService;
    private final NetWorthService netWorthService;
    private final ExpenseCategoryService expenseCategoryService;

    public DashboardRestController(
            ProfitLossService profitLossService,
            NetWorthService netWorthService,
            ExpenseCategoryService expenseCategoryService) {
        this.profitLossService = profitLossService;
        this.netWorthService = netWorthService;
        this.expenseCategoryService = expenseCategoryService;
    }

    @GetMapping("/profit-loss/{organizationId}")
    @PreAuthorize("hasAuthority(#organizationId)")
    public @ResponseBody ProfitLossSummaryDto getProfitLossSummary (
            @PathVariable UUID organizationId,
            @RequestParam(required = false, defaultValue = "30") int daysCount
    ) {
        return this.profitLossService.getProfitLossSummary(organizationId, daysCount);
    }

    @GetMapping("/net-worth/{organizationId}")
    @PreAuthorize("hasAuthority(#organizationId)")
    public @ResponseBody NetWorthSummaryDto getNetWorthSummary(
            @PathVariable UUID organizationId,
            @RequestParam(required = false, defaultValue = "30") int daysCount
    ) {
        return this.netWorthService.getNetWorthSummary(organizationId, daysCount);
    }

    @GetMapping("/expense-categories/{organizationId}")
    @PreAuthorize("hasAuthority(#organizationId)")
    public @ResponseBody List<ExpenseCategoryDto> getExpenseCategories (
            @PathVariable UUID organizationId,
            @RequestParam(required = false, defaultValue = "30") int daysCount
    ) {
        return this.expenseCategoryService.getExpenseCategories(organizationId, daysCount);
    }

}
