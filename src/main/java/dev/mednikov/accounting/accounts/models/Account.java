package dev.mednikov.accounting.accounts.models;

import dev.mednikov.accounting.organizations.models.Organization;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(
        name = "accounts_account",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"organization_id", "code"})}
)
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Organization organization;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_category_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private AccountCategory accountCategory;

    @Column(name = "account_type", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false, name = "is_deprecated")
    private boolean deprecated;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Account account)) return false;

        return organization.equals(account.organization) && code.equals(account.code);
    }

    @Override
    public int hashCode() {
        int result = organization.hashCode();
        result = 31 * result + code.hashCode();
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

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public void setAccountCategory(AccountCategory accountCategory) {
        this.accountCategory = accountCategory;
    }

    public Optional<AccountCategory> getAccountCategory() {
        return Optional.ofNullable(accountCategory);
    }
}
