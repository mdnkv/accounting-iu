package dev.mednikov.accounting.accounts.services;

import dev.mednikov.accounting.accounts.domain.AccountCategoryResponseDto;
import dev.mednikov.accounting.accounts.domain.CreateAccountCategoryRequestDto;
import dev.mednikov.accounting.accounts.domain.UpdateAccountCategoryRequestDto;
import dev.mednikov.accounting.accounts.exceptions.AccountCategoryAlreadyExistsException;
import dev.mednikov.accounting.accounts.exceptions.AccountCategoryNotFoundException;
import dev.mednikov.accounting.accounts.mappers.AccountCategoryResponseDtoMapper;
import dev.mednikov.accounting.accounts.models.AccountCategory;
import dev.mednikov.accounting.accounts.models.AccountType;
import dev.mednikov.accounting.accounts.repositories.AccountCategoryRepository;
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
class AccountCategoryServiceImplTest {

    @Mock private AccountCategoryRepository repository;
    @Mock private OrganizationRepository organizationRepository;

    private AccountCategoryServiceImpl service;

    @BeforeEach
    void setup(){
        AccountCategoryResponseDtoMapper mapper = Mappers.getMapper(AccountCategoryResponseDtoMapper.class);
        this.service = new AccountCategoryServiceImpl(repository, organizationRepository, mapper);
    }

    @Test
    void createAccountCategory_nameAlreadyExistsTest(){
        String name = "Current assets";
        UUID organizationId = UUID.randomUUID();

        CreateAccountCategoryRequestDto requestDto = new CreateAccountCategoryRequestDto(name, AccountType.ASSET, organizationId);

        when(repository.existsByNameAndOrganizationId(name, organizationId)).thenReturn(true);

        assertThatThrownBy(()-> service.createAccountCategory(requestDto))
                .isInstanceOf(AccountCategoryAlreadyExistsException.class);
    }

    @Test
    void createAccountCategory_createdTest(){
        String name = "Current assets";
        UUID accountCategoryId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        CreateAccountCategoryRequestDto requestDto = new CreateAccountCategoryRequestDto(name, AccountType.ASSET, organizationId);

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Hermann Simon e.V.");

        AccountCategory accountCategory = new AccountCategory();
        accountCategory.setId(accountCategoryId);
        accountCategory.setName(name);
        accountCategory.setAccountType(AccountType.ASSET);
        accountCategory.setOrganization(organization);
        accountCategory.setActive(true);

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(repository.existsByNameAndOrganizationId(name, organizationId)).thenReturn(false);
        when(repository.save(any(AccountCategory.class))).thenReturn(accountCategory);

        AccountCategoryResponseDto result = service.createAccountCategory(requestDto);
        assertThat(result).isNotNull()
                .hasFieldOrPropertyWithValue("id", accountCategoryId)
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("organizationId", organizationId)
                .hasFieldOrPropertyWithValue("active", true)
                .hasFieldOrPropertyWithValue("accountType", AccountType.ASSET);
    }

    @Test
    void updateAccountCategory_accountCategoryDoesNotExistTest(){
        String name = "Current assets";
        UUID accountCategoryId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        UpdateAccountCategoryRequestDto requestDto = new UpdateAccountCategoryRequestDto(accountCategoryId, name, AccountType.ASSET, organizationId, true);

        when(repository.findById(accountCategoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(()-> service.updateAccountCategory(requestDto)).isInstanceOf(AccountCategoryNotFoundException.class);

    }

    @Test
    void updateAccountCategory_nameAlreadyExistsTest(){
        String name = "Current assets";
        UUID accountCategoryId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        UpdateAccountCategoryRequestDto requestDto = new UpdateAccountCategoryRequestDto(accountCategoryId, name, AccountType.ASSET, organizationId, true);

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Knoll KG");

        AccountCategory accountCategory = new AccountCategory();
        accountCategory.setId(accountCategoryId);
        accountCategory.setName("Property, Plant, and Equipment");
        accountCategory.setAccountType(AccountType.ASSET);
        accountCategory.setOrganization(organization);

        when(repository.findById(accountCategoryId)).thenReturn(Optional.of(accountCategory));
        when(repository.existsByNameAndOrganizationId(name, organizationId)).thenReturn(true);

        assertThatThrownBy(()-> service.updateAccountCategory(requestDto)).isInstanceOf(AccountCategoryAlreadyExistsException.class);
    }

    @Test
    void updateAccountCategory_updatedTest(){
        String name = "Current assets";
        UUID accountCategoryId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        UpdateAccountCategoryRequestDto requestDto = new UpdateAccountCategoryRequestDto(accountCategoryId, name, AccountType.ASSET, organizationId, true);

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Wetzel Weis AG");

        AccountCategory accountCategory = new AccountCategory();
        accountCategory.setId(accountCategoryId);
        accountCategory.setName("Property, Plant, and Equipment");
        accountCategory.setAccountType(AccountType.ASSET);
        accountCategory.setOrganization(organization);

        when(repository.findById(accountCategoryId)).thenReturn(Optional.of(accountCategory));
        when(repository.existsByNameAndOrganizationId(name, organizationId)).thenReturn(false);
        when(repository.save(any(AccountCategory.class))).thenReturn(accountCategory);

        AccountCategoryResponseDto result = service.updateAccountCategory(requestDto);
        assertThat(result).isNotNull();
    }

    @Test
    void getAccountCategoryById_existsTest(){
        UUID accountCategoryId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(UUID.randomUUID());
        organization.setName("Glaser GmbH");

        AccountCategory accountCategory = new AccountCategory();
        accountCategory.setId(accountCategoryId);
        accountCategory.setName("Current Liabilities");
        accountCategory.setAccountType(AccountType.LIABILITY);
        accountCategory.setOrganization(organization);

        when(repository.findById(accountCategoryId)).thenReturn(Optional.of(accountCategory));

        Optional<AccountCategoryResponseDto> result = service.getAccountCategoryById(accountCategoryId);
        assertThat(result).isPresent();
    }

    @Test
    void getAccountCategoryById_doesNotExistTest(){
        UUID accountCategoryId = UUID.randomUUID();

        when(repository.findById(accountCategoryId)).thenReturn(Optional.empty());
        Optional<AccountCategoryResponseDto> result = service.getAccountCategoryById(accountCategoryId);
        assertThat(result).isEmpty();
    }

}
