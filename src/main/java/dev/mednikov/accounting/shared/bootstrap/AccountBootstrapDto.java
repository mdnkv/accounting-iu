package dev.mednikov.accounting.shared.bootstrap;

import dev.mednikov.accounting.accounts.models.AccountBalanceType;
import dev.mednikov.accounting.accounts.models.AccountType;

final class AccountBootstrapDto {

    private String name;
    private String code;
    private String categoryName;
    private AccountBalanceType normalBalance;

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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public AccountBalanceType getNormalBalance() {
        return normalBalance;
    }

    public void setNormalBalance(AccountBalanceType normalBalance) {
        this.normalBalance = normalBalance;
    }
}
