package dev.mednikov.accounting.reports.services;

import dev.mednikov.accounting.reports.dto.ExpenseCategoryDto;

import java.util.List;
import java.util.UUID;

public interface ExpenseCategoryService {

    List<ExpenseCategoryDto> getExpenseCategories(UUID organizationId, int daysCount);

}
