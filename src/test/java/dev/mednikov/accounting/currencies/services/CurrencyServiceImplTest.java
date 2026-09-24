package dev.mednikov.accounting.currencies.services;

import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.mednikov.accounting.currencies.domain.CreateCurrencyRequestDto;
import dev.mednikov.accounting.currencies.domain.CurrencyResponseDto;
import dev.mednikov.accounting.currencies.domain.UpdateCurrencyRequestDto;
import dev.mednikov.accounting.currencies.exceptions.CurrencyAlreadyExistsException;
import dev.mednikov.accounting.currencies.exceptions.CurrencyNotFoundException;
import dev.mednikov.accounting.currencies.mappers.CurrencyResponseDtoMapper;
import dev.mednikov.accounting.currencies.models.Currency;
import dev.mednikov.accounting.currencies.repositories.CurrencyRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock private CurrencyRepository currencyRepository;
    @Mock private OrganizationRepository organizationRepository;

    private CurrencyServiceImpl service;

    @BeforeEach
    void setup (){
        CurrencyResponseDtoMapper mapper = Mappers.getMapper(CurrencyResponseDtoMapper.class);
        service = new CurrencyServiceImpl(currencyRepository, organizationRepository, mapper);
    }

    @Test
    void createCurrency_currencyCodeAlreadyExistsTest(){
        String currencyCode = "EUR";
        String currencyName = "Euro";
        UUID organizationId = UUID.randomUUID();

        CreateCurrencyRequestDto requestDto = new CreateCurrencyRequestDto(currencyCode, currencyName, organizationId);

        when(currencyRepository.existsByCodeAndOrganizationId(currencyCode, organizationId)).thenReturn(true);

        assertThatThrownBy(() -> service.createCurrency(requestDto)).isInstanceOf(CurrencyAlreadyExistsException.class);

    }

    @Test
    void createCurrency_createdTest(){
        String currencyCode = "EUR";
        String currencyName = "Euro";
        UUID currencyId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        CreateCurrencyRequestDto requestDto = new CreateCurrencyRequestDto(currencyCode, currencyName, organizationId);

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Heck Schütz Stiftung & Co. KG");

        Currency currency = new Currency();
        currency.setId(currencyId);
        currency.setCode(currencyCode);
        currency.setName(currencyName);
        currency.setOrganization(organization);
        currency.setActive(true);
        currency.setPrimary(true);

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(currencyRepository.existsByCodeAndOrganizationId(currencyCode, organizationId)).thenReturn(false);
        when(currencyRepository.getPrimaryCurrency(organizationId)).thenReturn(Optional.empty());
        when(currencyRepository.save(any(Currency.class))).thenReturn(currency);

        CurrencyResponseDto result = service.createCurrency(requestDto);
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", currencyName)
                .hasFieldOrPropertyWithValue("organizationId", organizationId)
                .hasFieldOrPropertyWithValue("code", currencyCode)
                .hasFieldOrPropertyWithValue("active", true)
                .hasFieldOrPropertyWithValue("primary", true)
                .hasFieldOrPropertyWithValue("id", currencyId);

    }

    @Test
    void updateCurrency_currencyDoesNotExistTest(){
        String currencyCode = "EUR";
        String currencyName = "Euro";
        UUID currencyId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        UpdateCurrencyRequestDto requestDto = new UpdateCurrencyRequestDto(
                currencyId,
                currencyCode,
                currencyName,
                organizationId,
                true,
                false);

        when(currencyRepository.findById(currencyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateCurrency(requestDto)).isInstanceOf(CurrencyNotFoundException.class);
    }

    @Test
    void updateCurrency_currencyCodeAlreadyExistsTest(){
        String currencyCode = "EUR";
        String currencyName = "Euro";
        UUID currencyId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        UpdateCurrencyRequestDto requestDto = new UpdateCurrencyRequestDto(
                currencyId,
                currencyCode,
                currencyName,
                organizationId,
                true,
                false);

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Kolb Grimm AG");

        Currency currency = new Currency();
        currency.setId(currencyId);
        currency.setCode("CHF");
        currency.setName(currencyName);
        currency.setOrganization(organization);

        when(currencyRepository.findById(currencyId)).thenReturn(Optional.of(currency));
        when(currencyRepository.existsByCodeAndOrganizationId(currencyCode, organizationId)).thenReturn(true);

        assertThatThrownBy(() -> service.updateCurrency(requestDto)).isInstanceOf(CurrencyAlreadyExistsException.class);
    }

    @Test
    void updateCurrency_updatedTest(){
        String currencyCode = "EUR";
        String currencyName = "Euro";
        UUID currencyId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        UpdateCurrencyRequestDto requestDto = new UpdateCurrencyRequestDto(
                currencyId,
                currencyCode,
                currencyName,
                organizationId,
                true,
                false);

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Bär Menzel GmbH");

        Currency currency = new Currency();
        currency.setId(currencyId);
        currency.setCode("CHF");
        currency.setName(currencyName);
        currency.setOrganization(organization);

        when(currencyRepository.findById(currencyId)).thenReturn(Optional.of(currency));
        when(currencyRepository.existsByCodeAndOrganizationId(currencyCode, organizationId)).thenReturn(false);
        when(currencyRepository.save(any(Currency.class))).thenReturn(currency);

        CurrencyResponseDto result = service.updateCurrency(requestDto);
        assertThat(result).isNotNull();
    }

    @Test
    void getCurrencyById_existsTest(){
        String currencyCode = "EUR";
        String currencyName = "Euro";
        UUID currencyId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Peter Schumann GmbH & Co. KGaA");

        Currency currency = new Currency();
        currency.setId(currencyId);
        currency.setCode(currencyCode);
        currency.setName(currencyName);
        currency.setOrganization(organization);

        when(currencyRepository.findById(currencyId)).thenReturn(Optional.of(currency));

        Optional<CurrencyResponseDto> result = service.getCurrencyById(currencyId);
        assertThat(result).isPresent();
    }

    @Test
    void getCurrencyById_doesNotExistTest(){
        UUID currencyId = UUID.randomUUID();

        when(currencyRepository.findById(currencyId)).thenReturn(Optional.empty());

        Optional<CurrencyResponseDto> result = service.getCurrencyById(currencyId);
        assertThat(result).isEmpty();
    }

    @Test
    void getPrimaryCurrency_existsTest(){
        String currencyCode = "EUR";
        String currencyName = "Euro";
        UUID currencyId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Kunze Baumgartner GmbH");

        Currency currency = new Currency();
        currency.setId(currencyId);
        currency.setCode(currencyCode);
        currency.setName(currencyName);
        currency.setOrganization(organization);
        currency.setActive(true);
        currency.setPrimary(true);

        when(currencyRepository.getPrimaryCurrency(organizationId)).thenReturn(Optional.of(currency));

        Optional<CurrencyResponseDto> result = service.getPrimaryCurrency(organizationId);
        assertThat(result).isPresent();
    }

    @Test
    void getPrimaryCurrency_doesNotExistTest(){
        UUID organizationId = UUID.randomUUID();

        when(currencyRepository.getPrimaryCurrency(organizationId)).thenReturn(Optional.empty());

        Optional<CurrencyResponseDto> result = service.getPrimaryCurrency(organizationId);
        assertThat(result).isEmpty();
    }

}
