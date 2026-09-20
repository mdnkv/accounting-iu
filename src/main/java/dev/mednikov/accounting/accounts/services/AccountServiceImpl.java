package dev.mednikov.accounting.accounts.services;

import dev.mednikov.accounting.accounts.dto.AccountDto;
import dev.mednikov.accounting.accounts.dto.AccountDtoMapper;
import dev.mednikov.accounting.accounts.exceptions.AccountAlreadyExistsException;
import dev.mednikov.accounting.accounts.exceptions.AccountCategoryNotFoundException;
import dev.mednikov.accounting.accounts.exceptions.AccountDeletionException;
import dev.mednikov.accounting.accounts.exceptions.AccountNotFoundException;
import dev.mednikov.accounting.accounts.models.Account;
import dev.mednikov.accounting.accounts.models.AccountCategory;
import dev.mednikov.accounting.accounts.repositories.AccountCategoryRepository;
import dev.mednikov.accounting.accounts.repositories.AccountRepository;
import dev.mednikov.accounting.organizations.exceptions.OrganizationNotFoundException;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
import dev.mednikov.accounting.transactions.repositories.TransactionLineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    private final static AccountDtoMapper accountDtoMapper = new AccountDtoMapper();

    private final AccountRepository accountRepository;
    private final AccountCategoryRepository accountCategoryRepository;
    private final TransactionLineRepository transactionLineRepository;
    private final OrganizationRepository organizationRepository;

    public AccountServiceImpl(
            AccountRepository accountRepository,
            OrganizationRepository organizationRepository,
            TransactionLineRepository transactionLineRepository,
            AccountCategoryRepository accountCategoryRepository) {
        this.accountRepository = accountRepository;
        this.organizationRepository = organizationRepository;
        this.transactionLineRepository = transactionLineRepository;
        this.accountCategoryRepository = accountCategoryRepository;
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        String code = accountDto.getCode();
        if (this.accountRepository.findByOrganizationIdAndCode(accountDto.getOrganizationId(),code).isPresent()) {
            throw new AccountAlreadyExistsException();
        }

        Organization organization = this.organizationRepository.findById(accountDto.getOrganizationId())
                .orElseThrow(() -> new OrganizationNotFoundException());

        Account account = new Account();
        account.setCode(code);
        account.setName(accountDto.getName());
        account.setAccountType(accountDto.getAccountType());
        account.setOrganization(organization);
        account.setDeprecated(false);

        // Set category
        if (accountDto.getAccountCategoryId() != null) {
            AccountCategory accountCategory = this.accountCategoryRepository.getReferenceById(accountDto.getAccountCategoryId());
            account.setAccountCategory(accountCategory);
        } else {
            account.setAccountCategory(null);
        }

        Account result = this.accountRepository.save(account);

        return accountDtoMapper.apply(result);
    }

    @Override
    public AccountDto updateAccount(AccountDto accountDto) {
        Objects.requireNonNull(accountDto.getId());
        Account account = this.accountRepository.findById(accountDto.getId()).orElseThrow(AccountNotFoundException::new);

        account.setName(accountDto.getName());
        account.setAccountType(accountDto.getAccountType());
        account.setDeprecated(accountDto.isDeprecated());

        if (!account.getCode().equals(accountDto.getCode())) {
            String code = accountDto.getCode();
            if (this.accountRepository.findByOrganizationIdAndCode(accountDto.getOrganizationId(), code).isPresent()) {
                throw new AccountAlreadyExistsException();
            }
            account.setCode(code);
        }

        if (accountDto.getAccountCategoryId() != null) {
            AccountCategory accountCategory = this.accountCategoryRepository.findById(accountDto.getAccountCategoryId())
                    .orElseThrow(() -> new AccountCategoryNotFoundException());
            account.setAccountCategory(accountCategory);
        } else {
            account.setAccountCategory(null);
        }

        Account result = this.accountRepository.save(account);
        return accountDtoMapper.apply(result);
    }

    @Override
    public void deleteAccount(UUID id) {
        if (this.transactionLineRepository.findByAccountId(id).isEmpty()){
            this.accountRepository.deleteById(id);
        } else {
            throw new AccountDeletionException();
        }
    }

    @Override
    public List<AccountDto> getAccounts(UUID organizationId, boolean includeDeprecated) {
        if (includeDeprecated) {
            return this.accountRepository.findAllByOrganizationId(organizationId)
                    .stream()
                    .map(accountDtoMapper)
                    .toList();
        } else {
            return this.accountRepository.findActiveByOrganizationId(organizationId)
                    .stream()
                    .map(accountDtoMapper)
                    .toList();
        }
    }

    @Override
    public Optional<AccountDto> getAccount(UUID id) {
        return this.accountRepository.findById(id).map(accountDtoMapper);
    }
}
