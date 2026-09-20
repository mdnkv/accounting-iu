package dev.mednikov.accounting.reports.services;

import dev.mednikov.accounting.accounts.dto.AccountDto;
import dev.mednikov.accounting.accounts.dto.AccountDtoMapper;
import dev.mednikov.accounting.accounts.models.Account;
import dev.mednikov.accounting.accounts.models.AccountType;
import dev.mednikov.accounting.currencies.exceptions.CurrencyNotFoundException;
import dev.mednikov.accounting.currencies.models.Currency;
import dev.mednikov.accounting.currencies.repositories.CurrencyRepository;
import dev.mednikov.accounting.organizations.exceptions.OrganizationNotFoundException;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
import dev.mednikov.accounting.reports.dto.ProfitLossDto;
import dev.mednikov.accounting.reports.dto.ProfitLossLineDto;
import dev.mednikov.accounting.reports.dto.ProfitLossSummaryDto;
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
public class ProfitLossServiceImpl implements ProfitLossService {

    private final static AccountDtoMapper accountDtoMapper = new AccountDtoMapper();

    private final OrganizationRepository organizationRepository;
    private final TransactionLineRepository transactionLineRepository;
    private final CurrencyRepository currencyRepository;

    public ProfitLossServiceImpl(OrganizationRepository organizationRepository, TransactionLineRepository transactionLineRepository, CurrencyRepository currencyRepository) {
        this.organizationRepository = organizationRepository;
        this.transactionLineRepository = transactionLineRepository;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public ProfitLossDto getProfitLoss(UUID organizationId, LocalDate fromDate, LocalDate toDate) {
        // Get organization
        Organization organization = this.organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);
        Currency primaryCurrency = this.currencyRepository.findPrimaryCurrency(organizationId).orElseThrow(CurrencyNotFoundException::new);

        // Get transaction lines
        List<TransactionLine> transactionLines = this.transactionLineRepository.getProfitLossLines(organizationId, fromDate, toDate);

        // Group transactions by account
        Map<Account, List<TransactionLine>> accounts = transactionLines.stream().collect(Collectors.groupingBy(TransactionLine::getAccount));

        // Calculate amount in each account
        List<ProfitLossLineDto> incomeItems = new ArrayList<>();
        List<ProfitLossLineDto> expenseItems = new ArrayList<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Map.Entry<Account, List<TransactionLine>> entry : accounts.entrySet()) {
            AccountDto account = accountDtoMapper.apply(entry.getKey());

            BigDecimal credit = BigDecimal.ZERO;
            BigDecimal debit = BigDecimal.ZERO;

            // Calculate amount for the account
            for (TransactionLine transactionLine : entry.getValue()) {
                if (!transactionLine.getTransaction().isDraft()) {
//                    if (transactionLine.getTransaction().getTargetCurrency().equals(primaryCurrency)) {
//                        // same currency as primary currency
//                        // add an original amount
//                        debit = debit.add(transactionLine.getOriginalDebitAmount());
//                        credit = credit.add(transactionLine.getOriginalCreditAmount());
//                    } else {
//                        // another currency
//                        // add a converted amount
//                        credit = credit.add(transactionLine.getCreditAmount());
//                        debit = debit.add(transactionLine.getDebitAmount());
//                    }
                    credit = credit.add(transactionLine.getCreditAmount());
                    debit = debit.add(transactionLine.getDebitAmount());
                }

            }
//            BigDecimal debit = entry.getValue().stream()
//                    .map(TransactionLine::getDebitAmount)
//                    .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//            BigDecimal credit = entry.getValue().stream()
//                    .map(TransactionLine::getCreditAmount)
//                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal balance = (account.getAccountType() == AccountType.EXPENSE) ? debit.subtract(credit) : credit.subtract(debit);
            ProfitLossLineDto dto = new ProfitLossLineDto(account, balance);
            if (account.getAccountType() == AccountType.EXPENSE) {
                expenseItems.add(dto);
                totalExpense = totalExpense.add(balance);
            } else {
                incomeItems.add(dto);
                totalIncome = totalIncome.add(balance);
            }
        }

        BigDecimal netIncome = totalIncome.subtract(totalExpense);

        return new ProfitLossDto(
                organization.getName(),
                expenseItems,
                incomeItems,
                netIncome
        );
    }

    @Override
    public ProfitLossSummaryDto getProfitLossSummary(UUID organizationId, int daysCount) {
//        Currency primaryCurrency = this.currencyRepository.findPrimaryCurrency(organizationId).orElseThrow(CurrencyNotFoundException::new);

        // Calculate dates
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = toDate.minusDays(daysCount);

        List<TransactionLine> transactionLines = this.transactionLineRepository.getProfitLossLines(organizationId, fromDate, toDate);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (TransactionLine transactionLine : transactionLines) {
            if (!transactionLine.getTransaction().isDraft()) {
                if (transactionLine.getAccount().getAccountType() == AccountType.INCOME) {
//                    if (transactionLine.getTransaction().getTargetCurrency().equals(primaryCurrency)) {
//                        BigDecimal income = transactionLine.getOriginalCreditAmount().subtract(transactionLine.getOriginalDebitAmount());
//                        totalIncome = totalIncome.add(income);
//                    } else {
//                        BigDecimal income = transactionLine.getCreditAmount().subtract(transactionLine.getDebitAmount());
//                        totalIncome = totalIncome.add(income);
//                    }
                    BigDecimal income = transactionLine.getCreditAmount().subtract(transactionLine.getDebitAmount());
                    totalIncome = totalIncome.add(income);
                } else if (transactionLine.getAccount().getAccountType() == AccountType.EXPENSE) {
//                    if (transactionLine.getTransaction().getTargetCurrency().equals(primaryCurrency)) {
//                        BigDecimal expense = transactionLine.getOriginalDebitAmount().subtract(transactionLine.getOriginalCreditAmount());
//                        totalExpense = totalExpense.add(expense);
//                    } else {
//                        BigDecimal expense = transactionLine.getDebitAmount().subtract(transactionLine.getCreditAmount());
//                        totalExpense = totalExpense.add(expense);
//                    }
                    BigDecimal expense = transactionLine.getDebitAmount().subtract(transactionLine.getCreditAmount());
                    totalExpense = totalExpense.add(expense);
                }
            }

        }

        BigDecimal netProfit = totalIncome.subtract(totalExpense);

        return new ProfitLossSummaryDto(
                totalIncome,
                totalExpense,
                netProfit
        );
    }
}
