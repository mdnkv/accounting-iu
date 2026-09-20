package dev.mednikov.accounting.transactions.models;

import dev.mednikov.accounting.accounts.models.Account;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction_transaction_line")
public class TransactionLine {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Account account;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Transaction transaction;

    @Column(nullable = false, name = "credit_amount")
    private BigDecimal creditAmount;

    @Column(nullable = false, name = "debit_amount")
    private BigDecimal debitAmount;

    @Column(nullable = false, name = "original_credit_amount")
    private BigDecimal originalCreditAmount;

    @Column(nullable = false, name = "original_debit_amount")
    private BigDecimal originalDebitAmount;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof TransactionLine that)) return false;

        return account.equals(that.account)
                && transaction.equals(that.transaction)
                && creditAmount.equals(that.creditAmount)
                && debitAmount.equals(that.debitAmount);
    }

    @Override
    public int hashCode() {
        int result = account.hashCode();
        result = 31 * result + transaction.hashCode();
        result = 31 * result + creditAmount.hashCode();
        result = 31 * result + debitAmount.hashCode();
        return result;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
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

    public BigDecimal getOriginalCreditAmount() {
        return originalCreditAmount;
    }

    public void setOriginalCreditAmount(BigDecimal originalCreditAmount) {
        this.originalCreditAmount = originalCreditAmount;
    }

    public BigDecimal getOriginalDebitAmount() {
        return originalDebitAmount;
    }

    public void setOriginalDebitAmount(BigDecimal originalDebitAmount) {
        this.originalDebitAmount = originalDebitAmount;
    }
}
