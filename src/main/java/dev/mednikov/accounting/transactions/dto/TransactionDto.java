package dev.mednikov.accounting.transactions.dto;

import dev.mednikov.accounting.currencies.dto.CurrencyDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class TransactionDto {

    private UUID id;
    @NotNull @NotBlank private UUID organizationId;
    @NotNull @NotBlank private String description;
    @NotNull @NotBlank private UUID currencyId;
    @NotNull @NotBlank private UUID journalId;
    @NotNull private LocalDate date;
    private boolean draft;
    private BigDecimal totalCreditAmount;
    private BigDecimal totalDebitAmount;
    private CurrencyDto currency;
    @NotNull @NotEmpty private List<TransactionLineDto> lines;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(UUID currency) {
        this.currencyId = currency;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<TransactionLineDto> getLines() {
        return lines;
    }

    public void setLines(List<TransactionLineDto> lines) {
        this.lines = lines;
    }

    public BigDecimal getTotalCreditAmount() {
        return totalCreditAmount;
    }

    public void setTotalCreditAmount(BigDecimal totalCreditAmount) {
        this.totalCreditAmount = totalCreditAmount;
    }

    public BigDecimal getTotalDebitAmount() {
        return totalDebitAmount;
    }

    public void setTotalDebitAmount(BigDecimal totalDebitAmount) {
        this.totalDebitAmount = totalDebitAmount;
    }

    public CurrencyDto getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyDto currency) {
        this.currency = currency;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public UUID getJournalId() {
        return journalId;
    }

    public void setJournalId(UUID journalId) {
        this.journalId = journalId;
    }
}
