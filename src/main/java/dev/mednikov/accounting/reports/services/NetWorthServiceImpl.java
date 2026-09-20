package dev.mednikov.accounting.reports.services;

import dev.mednikov.accounting.accounts.models.AccountType;
import dev.mednikov.accounting.currencies.exceptions.CurrencyNotFoundException;
import dev.mednikov.accounting.currencies.models.Currency;
import dev.mednikov.accounting.currencies.repositories.CurrencyRepository;
import dev.mednikov.accounting.reports.dto.NetWorthSummaryDto;
import dev.mednikov.accounting.transactions.models.TransactionLine;
import dev.mednikov.accounting.transactions.repositories.TransactionLineRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class NetWorthServiceImpl implements NetWorthService {

    private final CurrencyRepository currencyRepository;
    private final TransactionLineRepository transactionLineRepository;

    public NetWorthServiceImpl(TransactionLineRepository transactionLineRepository, CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
        this.transactionLineRepository = transactionLineRepository;
    }

    @Override
    public NetWorthSummaryDto getNetWorthSummary(UUID organizationId, int daysCount) {
//        Currency primaryCurrency = this.currencyRepository.findPrimaryCurrency(organizationId).orElseThrow(CurrencyNotFoundException::new);
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = toDate.minusDays(daysCount);

        List<TransactionLine> transactionLines = this.transactionLineRepository.getAssetsAndLiabilityLines(organizationId, fromDate, toDate);
        BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal totalLiabilities = BigDecimal.ZERO;

        for (TransactionLine transactionLine : transactionLines) {
            if (!transactionLine.getTransaction().isDraft()){
                if (transactionLine.getAccount().getAccountType() == AccountType.ASSET) {
//                    if (transactionLine.getTransaction().getTargetCurrency().equals(primaryCurrency)) {
//                        BigDecimal amount = transactionLine.getOriginalDebitAmount().subtract(transactionLine.getOriginalCreditAmount());
//                        totalAssets = totalAssets.add(amount);
//                    } else {
//                        BigDecimal amount = transactionLine.getDebitAmount().subtract(transactionLine.getCreditAmount());
//                        totalAssets = totalAssets.add(amount);
//                    }
                    BigDecimal amount = transactionLine.getDebitAmount().subtract(transactionLine.getCreditAmount());
                    totalAssets = totalAssets.add(amount);
                } else {
//                    if (transactionLine.getTransaction().getTargetCurrency().equals(primaryCurrency)) {
//                        BigDecimal amount = transactionLine.getOriginalCreditAmount().subtract(transactionLine.getOriginalDebitAmount());
//                        totalLiabilities = totalLiabilities.add(amount);
//                    } else {
//                        BigDecimal amount = transactionLine.getCreditAmount().subtract(transactionLine.getDebitAmount());
//                        totalLiabilities = totalLiabilities.add(amount);
//                    }
                    BigDecimal amount = transactionLine.getCreditAmount().subtract(transactionLine.getDebitAmount());
                    totalLiabilities = totalLiabilities.add(amount);
                }
            }

        }
        BigDecimal netWorth = totalAssets.subtract(totalLiabilities);
        return new NetWorthSummaryDto(
                totalAssets,
                totalLiabilities,
                netWorth
        );
    }
}
