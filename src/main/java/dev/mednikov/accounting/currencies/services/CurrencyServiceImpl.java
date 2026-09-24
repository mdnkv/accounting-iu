package dev.mednikov.accounting.currencies.services;

import dev.mednikov.accounting.organizations.exceptions.OrganizationNotFoundException;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
import org.springframework.stereotype.Service;

import dev.mednikov.accounting.currencies.domain.CreateCurrencyRequestDto;
import dev.mednikov.accounting.currencies.domain.CurrencyResponseDto;
import dev.mednikov.accounting.currencies.domain.UpdateCurrencyRequestDto;
import dev.mednikov.accounting.currencies.exceptions.CurrencyAlreadyExistsException;
import dev.mednikov.accounting.currencies.exceptions.CurrencyNotFoundException;
import dev.mednikov.accounting.currencies.mappers.CurrencyResponseDtoMapper;
import dev.mednikov.accounting.currencies.models.Currency;
import dev.mednikov.accounting.currencies.repositories.CurrencyRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CurrencyServiceImpl implements CurrencyService{

    private final CurrencyRepository currencyRepository;
    private final OrganizationRepository organizationRepository;
    private final CurrencyResponseDtoMapper mapper;

    public CurrencyServiceImpl(
            CurrencyRepository currencyRepository,
            OrganizationRepository organizationRepository,
            CurrencyResponseDtoMapper mapper) {
        this.currencyRepository = currencyRepository;
        this.organizationRepository = organizationRepository;
        this.mapper = mapper;
    }

    @Override
    public CurrencyResponseDto createCurrency(CreateCurrencyRequestDto requestDto) {
        String currencyCode = requestDto.code();

        // TODO check that the currency code is valid

        // Check that the code does not exist yet
        if (this.currencyRepository.existsByCodeAndOrganizationId(currencyCode, requestDto.organizationId())) {
            throw new CurrencyAlreadyExistsException(currencyCode, requestDto.organizationId());
        }

        // Find organization
        Organization organization = this.organizationRepository
                .findById(requestDto.organizationId())
                .orElseThrow(() -> new OrganizationNotFoundException(requestDto.organizationId()));

        // Check if primary currency already exists
        boolean isPrimary = this.currencyRepository.getPrimaryCurrency(requestDto.organizationId()).isEmpty();

        // Create currency
        Currency currency = new Currency();
        currency.setCode(currencyCode);
        currency.setName(requestDto.name());
        currency.setOrganization(organization);
        currency.setActive(true);
        currency.setPrimary(isPrimary);

        // Persist
        Currency result = this.currencyRepository.save(currency);

        // Return dto
        return this.mapper.toDto(result);
    }

    @Override
    public Optional<CurrencyResponseDto> getCurrencyById(UUID id) {
        return this.currencyRepository.findById(id).map(this.mapper::toDto);
    }

    @Override
    public Optional<CurrencyResponseDto> getPrimaryCurrency(UUID organizationId) {
        return this.currencyRepository.getPrimaryCurrency(organizationId).map(this.mapper::toDto);
    }

    @Override
    public CurrencyResponseDto updateCurrency(UpdateCurrencyRequestDto requestDto) {
        // Find currency or throw exception
        Currency currency = this.currencyRepository.findById(requestDto.id())
                .orElseThrow(() -> new CurrencyNotFoundException(requestDto.id()));

        // Check that if the code is updated, it is not used
        if (!currency.getCode().equals(requestDto.code())) {
            String newCurrencyCode = requestDto.code();
            if (this.currencyRepository.existsByCodeAndOrganizationId(newCurrencyCode, requestDto.organizationId())) {
                throw new CurrencyAlreadyExistsException(newCurrencyCode, requestDto.organizationId());
            }
            // Set code otherwise
            currency.setCode(newCurrencyCode);
        }

        // Set primary currency
        if (!currency.isPrimary() && requestDto.primary()) {
            // Update current primary currency
            Optional<Currency> currentPrimary = this.currencyRepository.getPrimaryCurrency(requestDto.organizationId());
            if (currentPrimary.isPresent()) {
                Currency cp = currentPrimary.get();
                if (!cp.getId().equals(requestDto.id())) {
                    cp.setPrimary(false);
                    this.currencyRepository.save(cp);
                }
            }

            // update currency to primary
            currency.setPrimary(true);
        }

        // Change other attributes
        currency.setName(requestDto.name());
        currency.setActive(requestDto.active());

         // Persist
        Currency result = this.currencyRepository.save(currency);

        // Return dto
        return this.mapper.toDto(result);
    }

    @Override
    public List<CurrencyResponseDto> getAllCurrencies(UUID organizationId) {
        return this.currencyRepository
                .findAllByOrganizationId(organizationId)
                .stream()
                .map(this.mapper::toDto)
                .toList();
    }
}
