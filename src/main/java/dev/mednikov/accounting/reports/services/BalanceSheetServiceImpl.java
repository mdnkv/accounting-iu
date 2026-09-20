package dev.mednikov.accounting.reports.services;

import dev.mednikov.accounting.accounts.dto.AccountDto;
import dev.mednikov.accounting.accounts.dto.AccountDtoMapper;
import dev.mednikov.accounting.accounts.models.Account;
import dev.mednikov.accounting.accounts.models.AccountType;
import dev.mednikov.accounting.accounts.repositories.AccountRepository;
import dev.mednikov.accounting.currencies.dto.CurrencyDto;
import dev.mednikov.accounting.currencies.dto.CurrencyDtoMapper;
import dev.mednikov.accounting.currencies.exceptions.CurrencyNotFoundException;
import dev.mednikov.accounting.currencies.models.Currency;
import dev.mednikov.accounting.currencies.repositories.CurrencyRepository;
import dev.mednikov.accounting.reports.dto.BalanceSheetDto;
import dev.mednikov.accounting.reports.dto.BalanceSheetLineDto;
import dev.mednikov.accounting.transactions.models.TransactionLine;
import dev.mednikov.accounting.transactions.repositories.TransactionLineRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BalanceSheetServiceImpl implements BalanceSheetService {

    private final static AccountDtoMapper accountDtoMapper = new AccountDtoMapper();

    private final TransactionLineRepository transactionLineRepository;
    private final CurrencyRepository currencyRepository;
    private final AccountRepository accountRepository;

    public BalanceSheetServiceImpl(TransactionLineRepository transactionLineRepository, CurrencyRepository currencyRepository, AccountRepository accountRepository) {
        this.transactionLineRepository = transactionLineRepository;
        this.currencyRepository = currencyRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public BalanceSheetDto getBalanceSheet(UUID organizationId, LocalDate date) {
//
//        // Retrieve primary currency
//        Currency primaryCurrency = this.currencyRepository.findPrimaryCurrency(organizationId).orElseThrow(CurrencyNotFoundException::new);
//        CurrencyDtoMapper currencyDtoMapper = new CurrencyDtoMapper();
//        CurrencyDto currency = currencyDtoMapper.apply(primaryCurrency);
//
//        // Retrieve transactions
//        List<TransactionLine> transactionLines = this.transactionLineRepository.getBalanceSheetLines(organizationId, date);
//
//        // Group transactions by account
//        Map<Account, List<TransactionLine>> accounts = transactionLines.stream().collect(Collectors.groupingBy(TransactionLine::getAccount));
//        BigDecimal totalCreditAmount = BigDecimal.ZERO;
//        BigDecimal totalDebitAmount = BigDecimal.ZERO;
//
//        // Calculate debit and credit for each account and produce BalanceSheetItems
//        List<BalanceSheetLineDto> items = new ArrayList<>();
//        for (Map.Entry<Account, List<TransactionLine>> accountEntry : accounts.entrySet()) {
//            BigDecimal debit = BigDecimal.ZERO;
//            BigDecimal credit = BigDecimal.ZERO;
//
//            for (TransactionLine transactionLine : accountEntry.getValue()) {
//                credit = credit.add(transactionLine.getCreditAmount());
//                debit = debit.add(transactionLine.getDebitAmount());
//            }
//
//            totalDebitAmount = totalDebitAmount.add(debit);
//            totalCreditAmount = totalCreditAmount.add(credit);
//
//            // Map account to dto
//            AccountDto account = accountDtoMapper.apply(accountEntry.getKey());
//            // Create balance sheet line dto
//            BalanceSheetLineDto item = new BalanceSheetLineDto(account, credit, debit);
//            items.add(item);
//        }
//
//        boolean balanced = totalCreditAmount.equals(totalDebitAmount);
//        BalanceSheetDto result = new BalanceSheetDto();
//        result.setBalanced(balanced);
//        result.setTotalDebitAmount(totalDebitAmount);
//        result.setTotalCreditAmount(totalCreditAmount);
//        result.setItems(items);
//        result.setCurrency(currency);
//        result.setDate(date);
//        return result;

        // Get accounts
        List<Account> accounts = this.accountRepository.findBalanceSheetAccounts(organizationId);

        BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal totalLiabilities = BigDecimal.ZERO;
        BigDecimal totalEquity = BigDecimal.ZERO;

        // Balance sheet account items
        List<BalanceSheetLineDto> accountItems = new ArrayList<>();

        // Compute balance for each account
        for (Account account : accounts) {
            // Get transaction lines
            List<TransactionLine> transactionLines = transactionLineRepository.findByAccountIdBeforeDate(account.getId(), date);
            // Calculate debit and credit
            BigDecimal debit = transactionLines.stream()
                    .map(TransactionLine::getDebitAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal credit = transactionLines.stream()
                    .map(TransactionLine::getCreditAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // find balance
            BigDecimal balance = BigDecimal.ZERO;
            if (account.getAccountType() == AccountType.ASSET) {
                balance = debit.subtract(credit);
                totalAssets = totalAssets.add(balance);
            } else if (account.getAccountType() == AccountType.LIABILITY) {
                balance = credit.subtract(debit);
                totalLiabilities = totalLiabilities.add(balance);
            } else { // EQUITY
                balance = credit.subtract(debit);
                totalEquity = totalEquity.add(balance);
            }

            // Add to dto list
            BalanceSheetLineDto balanceSheetLineDto = new BalanceSheetLineDto();
            balanceSheetLineDto.setBalance(balance);
            AccountDto accountDto = accountDtoMapper.apply(account);
            balanceSheetLineDto.setAccount(accountDto);
            accountItems.add(balanceSheetLineDto);
        }

        // Find currency
        Currency currency = this.currencyRepository.findPrimaryCurrency(organizationId).orElseThrow(CurrencyNotFoundException::new);
        CurrencyDtoMapper currencyDtoMapper = new CurrencyDtoMapper();
        CurrencyDto currencyDto = currencyDtoMapper.apply(currency);

        boolean balanced = totalAssets.equals(totalLiabilities.add(totalEquity));

        // Return result
        BalanceSheetDto result = new BalanceSheetDto();
        result.setDate(date);
        result.setCurrency(currencyDto);
        result.setBalanced(balanced);
        result.setItems(accountItems);
        result.setTotalAssets(totalAssets);
        result.setTotalLiabilities(totalLiabilities);
        result.setTotalEquity(totalEquity);
        return result;
    }
}
