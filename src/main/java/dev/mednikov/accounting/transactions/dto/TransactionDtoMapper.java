package dev.mednikov.accounting.transactions.dto;

import dev.mednikov.accounting.currencies.dto.CurrencyDto;
import dev.mednikov.accounting.currencies.dto.CurrencyDtoMapper;
import dev.mednikov.accounting.transactions.models.Transaction;
import dev.mednikov.accounting.transactions.models.TransactionLine;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

public final class TransactionDtoMapper implements Function<Transaction, TransactionDto> {

    private final static CurrencyDtoMapper currencyDtoMapper = new CurrencyDtoMapper();
    private final static TransactionLineDtoMapper transactionLineDtoMapper = new TransactionLineDtoMapper();

    @Override
    public TransactionDto apply(Transaction transaction) {
        CurrencyDto currencyDto = currencyDtoMapper.apply(transaction.getTargetCurrency());
        List<TransactionLineDto> lines = transaction.getLines().stream().map(transactionLineDtoMapper).toList();
        TransactionDto result = new TransactionDto();
        result.setId(transaction.getId());
        result.setOrganizationId(transaction.getOrganization().getId());
        result.setDescription(transaction.getDescription());
        result.setDate(transaction.getDate());
        result.setCurrencyId(currencyDto.getId());
        result.setCurrency(currencyDto);
        result.setLines(lines);
        result.setTotalCreditAmount(transaction.getOriginalTotalCreditAmount());
        result.setTotalDebitAmount(transaction.getOriginalTotalDebitAmount());
        result.setDraft(transaction.isDraft());
        result.setJournalId(transaction.getJournal().getId());
        return result;
    }

}
