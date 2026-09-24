package dev.mednikov.accounting.accounts.services;

import dev.mednikov.accounting.accounts.domain.AccountResponseDto;
import dev.mednikov.accounting.accounts.domain.CreateAccountRequestDto;
import dev.mednikov.accounting.accounts.domain.UpdateAccountRequestDto;
import dev.mednikov.accounting.accounts.exceptions.AccountAlreadyExistsException;
import dev.mednikov.accounting.accounts.exceptions.AccountCategoryNonActiveException;
import dev.mednikov.accounting.accounts.exceptions.AccountCategoryNotFoundException;
import dev.mednikov.accounting.accounts.exceptions.AccountNotFoundException;
import dev.mednikov.accounting.accounts.mappers.AccountCategoryResponseDtoMapper;
import dev.mednikov.accounting.accounts.mappers.AccountResponseDtoMapper;
import dev.mednikov.accounting.accounts.mappers.AccountResponseDtoMapperImpl;
import dev.mednikov.accounting.accounts.models.Account;
import dev.mednikov.accounting.accounts.models.AccountBalanceType;
import dev.mednikov.accounting.accounts.models.AccountCategory;
import dev.mednikov.accounting.accounts.models.AccountType;
import dev.mednikov.accounting.accounts.repositories.AccountCategoryRepository;
import dev.mednikov.accounting.accounts.repositories.AccountRepository;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock private AccountRepository accountRepository;
    @Mock private AccountCategoryRepository accountCategoryRepository;
    @Mock private OrganizationRepository organizationRepository;

    private AccountServiceImpl service;

    @BeforeEach
    void setup(){
        AccountCategoryResponseDtoMapper acMapper = Mappers.getMapper(AccountCategoryResponseDtoMapper.class);
        AccountResponseDtoMapper mapper = new AccountResponseDtoMapperImpl(acMapper);
        service = new AccountServiceImpl(accountRepository, accountCategoryRepository, organizationRepository, mapper);
    }

    @Test
    void createAccount_accountCategoryDoesNotExistTest(){
        UUID accountCategoryId = UUID.randomUUID();
        String accountCode = "101";
        String accountName = "Cash";
        UUID organizationId = UUID.randomUUID();

        CreateAccountRequestDto requestDto = new CreateAccountRequestDto(
                accountCategoryId,
                accountCode,
                accountName,
                AccountBalanceType.DEBIT,
                organizationId
        );

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Hermann Bertram Stiftung & Co. KG");

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(accountRepository.existsByCodeAndOrganizationId(accountCode, organizationId)).thenReturn(false);
        when(accountCategoryRepository.findById(accountCategoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createAccount(requestDto)).isInstanceOf(AccountCategoryNotFoundException.class);
    }

    @Test
    void createAccount_codeAlreadyExistsTest(){
        UUID accountCategoryId = UUID.randomUUID();
        String accountCode = "101";
        String accountName = "Cash";
        UUID organizationId = UUID.randomUUID();

        CreateAccountRequestDto requestDto = new CreateAccountRequestDto(
                accountCategoryId,
                accountCode,
                accountName,
                AccountBalanceType.DEBIT,
                organizationId
        );

        when(accountRepository.existsByCodeAndOrganizationId(accountCode, organizationId)).thenReturn(true);

        assertThatThrownBy(() -> service.createAccount(requestDto)).isInstanceOf(AccountAlreadyExistsException.class);
    }

    @Test
    void createAccount_categoryNonActiveTest(){
        UUID accountCategoryId = UUID.randomUUID();
        String accountCode = "101";
        String accountName = "Cash";
        UUID organizationId = UUID.randomUUID();

        CreateAccountRequestDto requestDto = new CreateAccountRequestDto(
                accountCategoryId,
                accountCode,
                accountName,
                AccountBalanceType.DEBIT,
                organizationId
        );

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Krauß AG");

        AccountCategory accountCategory = new AccountCategory();
        accountCategory.setId(accountCategoryId);
        accountCategory.setName("Current assets");
        accountCategory.setAccountType(AccountType.ASSET);
        accountCategory.setOrganization(organization);
        accountCategory.setActive(false);

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(accountCategoryRepository.findById(accountCategoryId)).thenReturn(Optional.of(accountCategory));
        when(accountRepository.existsByCodeAndOrganizationId(accountCode, organizationId)).thenReturn(false);

        assertThatThrownBy(() -> service.createAccount(requestDto)).isInstanceOf(AccountCategoryNonActiveException.class);
    }

    @Test
    void createAccount_createdTest(){
        UUID accountId = UUID.randomUUID();
        UUID accountCategoryId = UUID.randomUUID();
        String accountCode = "101";
        String accountName = "Cash";
        UUID organizationId = UUID.randomUUID();

        CreateAccountRequestDto requestDto = new CreateAccountRequestDto(
                accountCategoryId,
                accountCode,
                accountName,
                AccountBalanceType.DEBIT,
                organizationId
        );

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Weber KG");

        AccountCategory accountCategory = new AccountCategory();
        accountCategory.setId(accountCategoryId);
        accountCategory.setName("Current assets");
        accountCategory.setAccountType(AccountType.ASSET);
        accountCategory.setOrganization(organization);
        accountCategory.setActive(true);

        Account account = new Account();
        account.setId(accountId);
        account.setAccountCategory(accountCategory);
        account.setCode(accountCode);
        account.setName(accountName);
        account.setNormalBalance(AccountBalanceType.DEBIT);
        account.setOrganization(organization);
        account.setActive(true);

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(accountCategoryRepository.findById(accountCategoryId)).thenReturn(Optional.of(accountCategory));
        when(accountRepository.existsByCodeAndOrganizationId(accountCode, organizationId)).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountResponseDto result = service.createAccount(requestDto);
        assertThat(result).isNotNull()
                .hasFieldOrPropertyWithValue("code", accountCode)
                .hasFieldOrPropertyWithValue("name", accountName)
                .hasFieldOrPropertyWithValue("normalBalance", AccountBalanceType.DEBIT)
                .hasFieldOrPropertyWithValue("id", accountId)
                .hasFieldOrPropertyWithValue("organizationId", organizationId)
                .hasFieldOrPropertyWithValue("active", true)
                .hasFieldOrProperty("accountCategory");
    }

    @Test
    void updateAccount_accountDoesNotExistTest(){
        UUID accountId = UUID.randomUUID();
        UUID accountCategoryId = UUID.randomUUID();
        String accountCode = "101";
        String accountName = "Cash";
        UUID organizationId = UUID.randomUUID();

        UpdateAccountRequestDto requestDto = new UpdateAccountRequestDto(
                accountId,
                accountCategoryId,
                accountCode,
                accountName,
                AccountBalanceType.DEBIT,
                organizationId,
                true
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateAccount(requestDto)).isInstanceOf(AccountNotFoundException.class);

    }

    @Test
    void updateAccount_accountCategoryDoesNotExistTest(){
        UUID accountId = UUID.randomUUID();
        UUID accountCategoryId = UUID.randomUUID();
        String accountCode = "101";
        String accountName = "Cash";
        UUID organizationId = UUID.randomUUID();

        UpdateAccountRequestDto requestDto = new UpdateAccountRequestDto(
                accountId,
                accountCategoryId,
                accountCode,
                accountName,
                AccountBalanceType.DEBIT,
                organizationId,
                true
        );

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Kretschmer Buchholz AG & Co. OHG");

        AccountCategory accountCategory = new AccountCategory();
        accountCategory.setId(UUID.randomUUID());
        accountCategory.setName("Current assets");
        accountCategory.setAccountType(AccountType.ASSET);
        accountCategory.setOrganization(organization);

        Account account = new Account();
        account.setId(accountId);
        account.setAccountCategory(accountCategory);
        account.setCode("1001");
        account.setName(accountName);
        account.setNormalBalance(AccountBalanceType.DEBIT);
        account.setOrganization(organization);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.existsByCodeAndOrganizationId(accountCode, organizationId)).thenReturn(false);
        when(accountCategoryRepository.findById(accountCategoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateAccount(requestDto)).isInstanceOf(AccountCategoryNotFoundException.class);
    }

    @Test
    void updateAccount_accountCategoryNotActiveTest(){
        UUID accountId = UUID.randomUUID();
        UUID accountCategoryId = UUID.randomUUID();
        String accountCode = "101";
        String accountName = "Cash";
        UUID organizationId = UUID.randomUUID();

        UpdateAccountRequestDto requestDto = new UpdateAccountRequestDto(
                accountId,
                accountCategoryId,
                accountCode,
                accountName,
                AccountBalanceType.DEBIT,
                organizationId,
                true
        );

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Kretschmer Buchholz AG & Co. OHG");

        AccountCategory accountCategory = new AccountCategory();
        accountCategory.setId(UUID.randomUUID());
        accountCategory.setName("Current assets");
        accountCategory.setAccountType(AccountType.ASSET);
        accountCategory.setOrganization(organization);

        AccountCategory accountCategory2 = new AccountCategory();
        accountCategory2.setId(accountCategoryId);
        accountCategory2.setName("Current assets");
        accountCategory2.setAccountType(AccountType.ASSET);
        accountCategory2.setOrganization(organization);
        accountCategory2.setActive(false);

        Account account = new Account();
        account.setId(accountId);
        account.setAccountCategory(accountCategory);
        account.setCode("1001");
        account.setName(accountName);
        account.setNormalBalance(AccountBalanceType.DEBIT);
        account.setOrganization(organization);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.existsByCodeAndOrganizationId(accountCode, organizationId)).thenReturn(false);
        when(accountCategoryRepository.findById(accountCategoryId)).thenReturn(Optional.of(accountCategory2));

        assertThatThrownBy(() -> service.updateAccount(requestDto)).isInstanceOf(AccountCategoryNonActiveException.class);
    }

    @Test
    void updateAccount_codeAlreadyExistsTest(){
        UUID accountId = UUID.randomUUID();
        UUID accountCategoryId = UUID.randomUUID();
        String accountCode = "101";
        String accountName = "Cash";
        UUID organizationId = UUID.randomUUID();

        UpdateAccountRequestDto requestDto = new UpdateAccountRequestDto(
                accountId,
                accountCategoryId,
                accountCode,
                accountName,
                AccountBalanceType.DEBIT,
                organizationId,
                true
        );

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Sommer Schütz OH GmbH");

        AccountCategory accountCategory = new AccountCategory();
        accountCategory.setId(accountCategoryId);
        accountCategory.setName("Current assets");
        accountCategory.setAccountType(AccountType.ASSET);
        accountCategory.setOrganization(organization);
        accountCategory.setActive(true);

        Account account = new Account();
        account.setId(accountId);
        account.setAccountCategory(accountCategory);
        account.setCode("1001");
        account.setName(accountName);
        account.setNormalBalance(AccountBalanceType.DEBIT);
        account.setOrganization(organization);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.existsByCodeAndOrganizationId(accountCode, organizationId)).thenReturn(true);

        assertThatThrownBy(() -> service.updateAccount(requestDto)).isInstanceOf(AccountAlreadyExistsException.class);
    }

    @Test
    void updateAccount_updatedTest(){
        UUID accountId = UUID.randomUUID();
        UUID accountCategoryId = UUID.randomUUID();
        String accountCode = "101";
        String accountName = "Cash";
        UUID organizationId = UUID.randomUUID();

        UpdateAccountRequestDto requestDto = new UpdateAccountRequestDto(
                accountId,
                accountCategoryId,
                accountCode,
                accountName,
                AccountBalanceType.DEBIT,
                organizationId,
                true
        );

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Seeger Brandt AG");

        AccountCategory accountCategory = new AccountCategory();
        accountCategory.setId(accountCategoryId);
        accountCategory.setName("Current assets");
        accountCategory.setAccountType(AccountType.ASSET);
        accountCategory.setOrganization(organization);
        accountCategory.setActive(true);

        Account account = new Account();
        account.setId(accountId);
        account.setAccountCategory(accountCategory);
        account.setCode("1001");
        account.setName(accountName);
        account.setNormalBalance(AccountBalanceType.DEBIT);
        account.setOrganization(organization);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.existsByCodeAndOrganizationId(accountCode, organizationId)).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountResponseDto result = service.updateAccount(requestDto);
        assertThat(result).isNotNull();
    }

    @Test
    void getAccountById_existsTest(){
        UUID accountId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(UUID.randomUUID());
        organization.setName("Jacobs Metzger KGaA");

        AccountCategory accountCategory = new AccountCategory();
        accountCategory.setId(UUID.randomUUID());
        accountCategory.setName("Current liabilities");
        accountCategory.setAccountType(AccountType.LIABILITY);
        accountCategory.setOrganization(organization);

        Account account = new Account();
        account.setId(accountId);
        account.setAccountCategory(accountCategory);
        account.setCode("201");
        account.setName("Accounts Payable");
        account.setNormalBalance(AccountBalanceType.CREDIT);
        account.setOrganization(organization);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        Optional<AccountResponseDto> result = service.getAccountById(accountId);
        assertThat(result).isPresent();
    }

    @Test
    void getAccountById_doesNotExistTest(){
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        Optional<AccountResponseDto> result = service.getAccountById(accountId);
        assertThat(result).isEmpty();
    }



}
