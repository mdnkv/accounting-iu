package dev.mednikov.accounting.accounts.dto;

import dev.mednikov.accounting.accounts.models.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

public final class AccountDto {

    private UUID id;
    @NotNull @NotBlank private UUID organizationId;
    @NotNull @NotBlank @Length(max=255) private String name;
    @NotNull @NotBlank @Length(max=20) private String code;
    @NotNull private AccountType accountType;
    private boolean deprecated;
    private UUID accountCategoryId;
    private AccountCategoryDto accountCategory;

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

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public UUID getAccountCategoryId() {
        return accountCategoryId;
    }

    public void setAccountCategoryId(UUID accountCategoryId) {
        this.accountCategoryId = accountCategoryId;
    }

    public AccountCategoryDto getAccountCategory() {
        return accountCategory;
    }

    public void setAccountCategory(AccountCategoryDto accountCategory) {
        this.accountCategory = accountCategory;
    }
}
