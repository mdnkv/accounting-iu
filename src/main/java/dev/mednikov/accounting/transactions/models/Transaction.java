package dev.mednikov.accounting.transactions.models;

import dev.mednikov.accounting.currencies.models.Currency;
import dev.mednikov.accounting.journals.models.Journal;
import dev.mednikov.accounting.organizations.models.Organization;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "transactions_transaction")
public class Transaction {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Organization organization;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "base_currency_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Currency baseCurrency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_currency_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Currency targetCurrency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "journal_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Journal journal;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, name = "transaction_date")
    private LocalDate date;

    @Column(nullable = false, name = "is_draft")
    private boolean draft;

    @Column(nullable = false, name = "total_credit_amount")
    private BigDecimal totalCreditAmount;

    @Column(nullable = false, name = "total_debit_amount")
    private BigDecimal totalDebitAmount;

    @Column(nullable = false, name = "original_total_credit_amount")
    private BigDecimal originalTotalCreditAmount;

    @Column(nullable = false, name = "original_total_debit_amount")
    private BigDecimal originalTotalDebitAmount;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TransactionLine> lines = new HashSet<>();

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Transaction that)) return false;

        return organization.equals(that.organization)
                && baseCurrency.equals(that.baseCurrency)
                && description.equals(that.description)
                && date.equals(that.date);
    }

    @Override
    public int hashCode() {
        int result = organization.hashCode();
        result = 31 * result + baseCurrency.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + date.hashCode();
        return result;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
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

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public BigDecimal getOriginalTotalDebitAmount() {
        return originalTotalDebitAmount;
    }

    public void setOriginalTotalDebitAmount(BigDecimal originalTotalDebitAmount) {
        this.originalTotalDebitAmount = originalTotalDebitAmount;
    }

    public BigDecimal getOriginalTotalCreditAmount() {
        return originalTotalCreditAmount;
    }

    public void setOriginalTotalCreditAmount(BigDecimal originalTotalCreditAmount) {
        this.originalTotalCreditAmount = originalTotalCreditAmount;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public void setLines(List<TransactionLine> lines) {
        this.lines.addAll(lines);
    }

    public void addTransactionLine(TransactionLine transactionLine) {
        this.lines.add(transactionLine);
    }

    public Set<TransactionLine> getLines() {
        return lines;
    }

    public Journal getJournal() {
        return journal;
    }

    public void setJournal(Journal journal) {
        this.journal = journal;
    }
}
