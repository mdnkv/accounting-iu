package dev.mednikov.accounting.transactions.dto;

import dev.mednikov.accounting.accounts.dto.AccountDto;
import dev.mednikov.accounting.accounts.dto.AccountDtoMapper;
import dev.mednikov.accounting.transactions.models.TransactionLine;

import java.util.function.Function;

public final class TransactionLineDtoMapper implements Function<TransactionLine, TransactionLineDto> {

    private final static AccountDtoMapper accountDtoMapper = new AccountDtoMapper();

    @Override
    public TransactionLineDto apply(TransactionLine transactionLine) {
        AccountDto accountDto = accountDtoMapper.apply(transactionLine.getAccount());
        TransactionLineDto result = new TransactionLineDto();
        result.setId(transactionLine.getId());
        result.setAccountId(accountDto.getId());
        result.setCreditAmount(transactionLine.getOriginalCreditAmount());
        result.setDebitAmount(transactionLine.getOriginalDebitAmount());
        result.setAccount(accountDto);
        return result;
    }
}
