package dev.mednikov.accounting.accounts.models;

import dev.mednikov.accounting.organizations.models.Organization;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "accounts_account_category",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"organization_id", "name"})}
)
public class AccountCategory {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Organization organization;

    @Column(name = "account_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private AccountType accountType;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof AccountCategory that)) return false;

        return name.equals(that.name)
                && organization.equals(that.organization)
                && accountType == that.accountType;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + organization.hashCode();
        result = 31 * result + accountType.hashCode();
        return result;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

}
