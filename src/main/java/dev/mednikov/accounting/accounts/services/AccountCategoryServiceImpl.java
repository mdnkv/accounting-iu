package dev.mednikov.accounting.accounts.services;

import dev.mednikov.accounting.accounts.domain.AccountCategoryResponseDto;
import dev.mednikov.accounting.accounts.domain.CreateAccountCategoryRequestDto;
import dev.mednikov.accounting.accounts.domain.UpdateAccountCategoryRequestDto;
import dev.mednikov.accounting.accounts.exceptions.AccountCategoryAlreadyExistsException;
import dev.mednikov.accounting.accounts.exceptions.AccountCategoryNotFoundException;
import dev.mednikov.accounting.accounts.mappers.AccountCategoryResponseDtoMapper;
import dev.mednikov.accounting.accounts.models.AccountCategory;
import dev.mednikov.accounting.accounts.repositories.AccountCategoryRepository;
import dev.mednikov.accounting.organizations.exceptions.OrganizationNotFoundException;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountCategoryServiceImpl implements AccountCategoryService {

    private final AccountCategoryRepository accountCategoryRepository;
    private final OrganizationRepository organizationRepository;
    private final AccountCategoryResponseDtoMapper mapper;

    public AccountCategoryServiceImpl(
            AccountCategoryRepository accountCategoryRepository,
            OrganizationRepository organizationRepository,
            AccountCategoryResponseDtoMapper mapper) {
        this.accountCategoryRepository = accountCategoryRepository;
        this.organizationRepository = organizationRepository;
        this.mapper = mapper;
    }

    @Override
    public AccountCategoryResponseDto createAccountCategory(CreateAccountCategoryRequestDto requestDto) {
        // Check that the name is not used
        String name = requestDto.name();
        if (this.accountCategoryRepository.existsByNameAndOrganizationId(name, requestDto.organizationId())) {
            throw new AccountCategoryAlreadyExistsException(name, requestDto.organizationId());
        }

        // Find organization
        Organization organization = this.organizationRepository
                .findById(requestDto.organizationId())
                .orElseThrow(() -> new OrganizationNotFoundException(requestDto.organizationId()));

        // Create account category
        AccountCategory accountCategory = new AccountCategory();
        accountCategory.setName(name);
        accountCategory.setOrganization(organization);
        accountCategory.setAccountType(requestDto.accountType());
        accountCategory.setActive(true);

        // Persist
        AccountCategory result = this.accountCategoryRepository.save(accountCategory);

        // Return result
        return this.mapper.toDto(result);

    }

    @Override
    public AccountCategoryResponseDto updateAccountCategory(UpdateAccountCategoryRequestDto requestDto) {
        // Find account category
        AccountCategory accountCategory = this.accountCategoryRepository.findById(requestDto.id())
                .orElseThrow(() -> new AccountCategoryNotFoundException(requestDto.id()));

        // If name is changed, check that it is not used
        String newName = requestDto.name();
        if (!newName.equals(accountCategory.getName())) {
            if (this.accountCategoryRepository.existsByNameAndOrganizationId(newName, requestDto.organizationId())) {
                throw new AccountCategoryAlreadyExistsException(newName, requestDto.organizationId());
            }
            accountCategory.setName(newName);
        }

        // Update other fields
        accountCategory.setAccountType(requestDto.accountType());
        accountCategory.setActive(requestDto.active());

        // Persist
        AccountCategory result = this.accountCategoryRepository.save(accountCategory);

        // Return result
        return this.mapper.toDto(result);
    }

    @Override
    public Optional<AccountCategoryResponseDto> getAccountCategoryById(UUID id) {
        return this.accountCategoryRepository.findById(id).map(this.mapper::toDto);
    }

    @Override
    public List<AccountCategoryResponseDto> getAllAccountCategories(UUID organizationId) {
        return this.accountCategoryRepository.findAllByOrganizationId(organizationId).stream().map(this.mapper::toDto).toList();
    }
}
