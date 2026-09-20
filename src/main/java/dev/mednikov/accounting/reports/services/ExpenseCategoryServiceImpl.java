package dev.mednikov.accounting.reports.services;

import dev.mednikov.accounting.accounts.models.Account;
import dev.mednikov.accounting.currencies.exceptions.CurrencyNotFoundException;
import dev.mednikov.accounting.currencies.models.Currency;
import dev.mednikov.accounting.currencies.repositories.CurrencyRepository;
import dev.mednikov.accounting.reports.dto.ExpenseCategoryDto;
import dev.mednikov.accounting.reports.utils.ExpenseCategoryComparator;
import dev.mednikov.accounting.transactions.models.TransactionLine;
import dev.mednikov.accounting.transactions.repositories.TransactionLineRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    private final TransactionLineRepository transactionLineRepository;
    private final CurrencyRepository currencyRepository;

    public ExpenseCategoryServiceImpl(TransactionLineRepository transactionLineRepository, CurrencyRepository currencyRepository) {
        this.transactionLineRepository = transactionLineRepository;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public List<ExpenseCategoryDto> getExpenseCategories(UUID organizationId, int daysCount) {
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = toDate.minusDays(daysCount);
        Currency primaryCurrency = currencyRepository.findPrimaryCurrency(organizationId).orElseThrow(CurrencyNotFoundException::new);
        List<TransactionLine> transactionLines = this.transactionLineRepository.getExpenseLines(organizationId, fromDate, toDate);
        Map<Account, List<TransactionLine>> accounts = transactionLines.stream().collect(Collectors.groupingBy(TransactionLine::getAccount));
        List<ExpenseCategoryDto> expenseCategories = new ArrayList<>();
        for (Map.Entry<Account, List<TransactionLine>> entry : accounts.entrySet()) {

            BigDecimal debit = BigDecimal.ZERO;
            BigDecimal credit = BigDecimal.ZERO;

            for (TransactionLine transactionLine : entry.getValue()) {
                if (!transactionLine.getTransaction().isDraft()){
                    if (transactionLine.getTransaction().getTargetCurrency().equals(primaryCurrency)){
                        debit = debit.add(transactionLine.getOriginalDebitAmount());
                        credit = credit.add(transactionLine.getOriginalDebitAmount());
                    } else {
                        debit = debit.add(transactionLine.getDebitAmount());
                        credit = credit.add(transactionLine.getCreditAmount());
                    }
                }

            }

            BigDecimal amount = debit.subtract(credit);
            ExpenseCategoryDto categoryDto = new ExpenseCategoryDto(entry.getKey().getName(), amount);
            expenseCategories.add(categoryDto);
        }
        ExpenseCategoryComparator comparator = new ExpenseCategoryComparator();
        return expenseCategories.stream().sorted(comparator).toList();
    }

}
