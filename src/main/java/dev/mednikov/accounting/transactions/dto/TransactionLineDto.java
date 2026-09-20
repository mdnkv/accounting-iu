package dev.mednikov.accounting.transactions.dto;

import dev.mednikov.accounting.accounts.dto.AccountDto;

import java.math.BigDecimal;
import java.util.UUID;

public final class TransactionLineDto {

    private UUID id;
    private UUID accountId;
    private AccountDto account;
    private BigDecimal creditAmount;
    private BigDecimal debitAmount;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

    public AccountDto getAccount() {
        return account;
    }

    public void setAccount(AccountDto account) {
        this.account = account;
    }
}
