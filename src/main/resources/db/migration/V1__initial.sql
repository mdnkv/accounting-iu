CREATE TABLE IF NOT EXISTS organizations_organization (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    country VARCHAR(2) NOT NULL,
    is_active BOOLEAN NOT NULL,
    tax_number VARCHAR(50) NOT NULL,
    UNIQUE (country, tax_number),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TYPE ACCOUNT_TYPE AS ENUM ('ASSET', 'LIABILITY', 'EQUITY', 'INCOME', 'EXPENSE');

CREATE TABLE IF NOT EXISTS accounts_account_category (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    account_type ACCOUNT_TYPE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (organization_id, name),
    CONSTRAINT fk_account_category_organization FOREIGN KEY (organization_id)
        REFERENCES organizations_organization(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS accounts_account (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(20) NOT NULL,
    account_type ACCOUNT_TYPE NOT NULL,
    organization_id UUID NOT NULL,
    account_category_id UUID,
    is_deprecated BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (organization_id, code),
    CONSTRAINT fk_account_organization FOREIGN KEY (organization_id)
        REFERENCES organizations_organization(id) ON DELETE CASCADE,
    CONSTRAINT fk_account_account_category
        FOREIGN KEY (account_category_id) REFERENCES accounts_account_category ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS users_user (
    id UUID PRIMARY KEY,
    keycloak_id UUID NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    is_superuser BOOLEAN NOT NULL,
    is_active BOOLEAN NOT NULL,
    is_premium BOOLEAN NOT NULL,
    is_email_verified BOOLEAN NOT NULL,
    avatar_url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS currencies_currency (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(3) NOT NULL,
    is_primary BOOLEAN NOT NULL,
    is_deprecated BOOLEAN NOT NULL,
    organization_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (organization_id, code),
    CONSTRAINT fk_currency_organization FOREIGN KEY (organization_id)
        REFERENCES organizations_organization(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS journals_journal (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (organization_id, name),
    CONSTRAINT fk_journal_organization FOREIGN KEY (organization_id)
        REFERENCES organizations_organization(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transactions_transaction (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    base_currency_id UUID NOT NULL,
    target_currency_id UUID NOT NULL,
    journal_id UUID NOT NULL,
    description TEXT NOT NULL,
    transaction_date DATE NOT NULL,
    is_draft BOOLEAN NOT NULL DEFAULT TRUE,
    total_debit_amount DECIMAL(12,2) NOT NULL CHECK (total_debit_amount >= 0.0),
    total_credit_amount DECIMAL(12,2) NOT NULL CHECK (total_credit_amount >= 0.0),
    original_total_debit_amount DECIMAL(12,2) NOT NULL CHECK (total_debit_amount >= 0.0),
    original_total_credit_amount DECIMAL(12,2) NOT NULL CHECK (total_credit_amount >= 0.0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_organization FOREIGN KEY (organization_id)
        REFERENCES organizations_organization(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_base_currency FOREIGN KEY (base_currency_id)
        REFERENCES currencies_currency(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_target_currency FOREIGN KEY (target_currency_id)
        REFERENCES currencies_currency(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_journal FOREIGN KEY (journal_id)
        REFERENCES journals_journal(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transaction_transaction_line (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    transaction_id UUID NOT NULL,
    debit_amount DECIMAL(12,2) NOT NULL CHECK (debit_amount >= 0.0),
    credit_amount DECIMAL(12,2) NOT NULL CHECK (credit_amount >= 0.0),
    original_debit_amount DECIMAL(12,2) NOT NULL CHECK (original_debit_amount >= 0.0),
    original_credit_amount DECIMAL(12,2) NOT NULL CHECK (original_credit_amount >= 0.0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_line_account FOREIGN KEY (account_id)
        REFERENCES accounts_account(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_line_transaction FOREIGN KEY (transaction_id)
        REFERENCES transactions_transaction(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS roles_role (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (organization_id, name),
    CONSTRAINT fk_role_organization FOREIGN KEY (organization_id)
        REFERENCES organizations_organization(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS authorities_authority (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (organization_id, name),
    CONSTRAINT fk_authority_organization FOREIGN KEY (organization_id)
        REFERENCES organizations_organization(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS roles_authorities (
    id UUID PRIMARY KEY,
    authority_id UUID NOT NULL,
    role_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_roles_authorities_authority FOREIGN KEY (authority_id)
        REFERENCES authorities_authority(id),
    CONSTRAINT fk_roles_authorities_role FOREIGN KEY (role_id)
        REFERENCES roles_role(id)
);

CREATE TABLE IF NOT EXISTS organizations_organization_user (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    organization_id UUID NOT NULL,
    role_id UUID NOT NULL,
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, organization_id),
    CONSTRAINT fk_organization_user_organization FOREIGN KEY (organization_id)
        REFERENCES organizations_organization(id) ON DELETE CASCADE,
    CONSTRAINT fk_organization_user_user FOREIGN KEY (user_id)
        REFERENCES users_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_organization_user_role FOREIGN KEY (role_id)
        REFERENCES roles_role(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS organizations_invitation (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    organization_id UUID NOT NULL,
    role_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (email, organization_id),
    CONSTRAINT fk_invitation_organization FOREIGN KEY (organization_id)
        REFERENCES organizations_organization(id) ON DELETE CASCADE,
    CONSTRAINT fk_invitation_role FOREIGN KEY (role_id)
        REFERENCES roles_role(id) ON DELETE CASCADE
);