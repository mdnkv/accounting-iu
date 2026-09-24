package dev.mednikov.accounting.accounts.services;

import dev.mednikov.accounting.accounts.domain.AccountResponseDto;
import dev.mednikov.accounting.accounts.domain.CreateAccountRequestDto;
import dev.mednikov.accounting.accounts.domain.UpdateAccountRequestDto;
import dev.mednikov.accounting.accounts.exceptions.AccountAlreadyExistsException;
import dev.mednikov.accounting.accounts.exceptions.AccountCategoryNonActiveException;
import dev.mednikov.accounting.accounts.exceptions.AccountCategoryNotFoundException;
import dev.mednikov.accounting.accounts.exceptions.AccountNotFoundException;
import dev.mednikov.accounting.accounts.mappers.AccountResponseDtoMapper;
import dev.mednikov.accounting.accounts.models.Account;
import dev.mednikov.accounting.accounts.models.AccountCategory;
import dev.mednikov.accounting.accounts.repositories.AccountCategoryRepository;
import dev.mednikov.accounting.accounts.repositories.AccountRepository;
import dev.mednikov.accounting.organizations.exceptions.OrganizationNotFoundException;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountCategoryRepository accountCategoryRepository;
    private final OrganizationRepository organizationRepository;
    private final AccountResponseDtoMapper mapper;

    public AccountServiceImpl(
            AccountRepository accountRepository,
            AccountCategoryRepository accountCategoryRepository,
            OrganizationRepository organizationRepository,
            AccountResponseDtoMapper mapper) {
        this.accountRepository = accountRepository;
        this.organizationRepository = organizationRepository;
        this.accountCategoryRepository = accountCategoryRepository;
        this.mapper = mapper;
    }

    @Override
    public AccountResponseDto createAccount(CreateAccountRequestDto requestDto) {
        // Check that the code is not used
        String accountCode = requestDto.code();
        if (this.accountRepository.existsByCodeAndOrganizationId(accountCode, requestDto.organizationId())) {
            throw new AccountAlreadyExistsException(accountCode, requestDto.organizationId());
        }

        // Find organization
        Organization organization = this.organizationRepository
                .findById(requestDto.organizationId())
                .orElseThrow(() -> new OrganizationNotFoundException(requestDto.organizationId()));

        // Find account category
        UUID accountCategoryId = requestDto.categoryId();
        AccountCategory category = this.accountCategoryRepository.findById(accountCategoryId)
                .orElseThrow(() -> new AccountCategoryNotFoundException(accountCategoryId));

        // Check that category is active
        if (!category.isActive()) {
            throw new AccountCategoryNonActiveException(category.getId());
        }

        // Create new account
        Account account = new Account();
        account.setAccountCategory(category);
        account.setCode(accountCode);
        account.setName(requestDto.name());
        account.setNormalBalance(requestDto.normalBalance());
        account.setOrganization(organization);
        account.setActive(true);

        // Persist
        Account result = this.accountRepository.save(account);

        // Return
        return this.mapper.toDto(result);
    }

    @Override
    public AccountResponseDto updateAccount(UpdateAccountRequestDto requestDto) {
        // Find account or throw exception
        Account account = this.accountRepository.findById(requestDto.id())
                .orElseThrow(() -> new AccountNotFoundException(requestDto.id()));

        // Check that account code is not occupied
        String newCode = requestDto.code();
        if (!account.getCode().equals(newCode)) {
            if (this.accountRepository.existsByCodeAndOrganizationId(newCode, requestDto.organizationId())) {
                throw new AccountAlreadyExistsException(newCode, requestDto.organizationId());
            }
            account.setCode(newCode);
        }

        // Update account category if needed
        if (!account.getAccountCategory().getId().equals(requestDto.categoryId())) {
            AccountCategory newCategory = this.accountCategoryRepository.findById(requestDto.categoryId())
                    .orElseThrow(() -> new AccountCategoryNotFoundException(requestDto.categoryId()));

            // Check is active category
            if (!newCategory.isActive()) {
                throw new AccountCategoryNonActiveException(newCategory.getId());
            }

            account.setAccountCategory(newCategory);


        }

        // Update other fields
        account.setName(requestDto.name());
        account.setNormalBalance(requestDto.normalBalance());
        account.setActive(requestDto.active());

        // Persist
        Account result = this.accountRepository.save(account);

        // Return
        return this.mapper.toDto(result);
    }

    @Override
    public Optional<AccountResponseDto> getAccountById(UUID id) {
        return this.accountRepository.findById(id).map(this.mapper::toDto);
    }

    @Override
    public List<AccountResponseDto> getAllAccounts(UUID organizationId) {
        return this.accountRepository.findAllByOrganizationId(organizationId).stream().map(this.mapper::toDto).toList();
    }
}
