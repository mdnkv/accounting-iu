package dev.mednikov.accounting.currencies.services;

import dev.mednikov.accounting.currencies.dto.CurrencyDto;
import dev.mednikov.accounting.currencies.exceptions.CurrencyAlreadyExistsException;
import dev.mednikov.accounting.currencies.exceptions.CurrencyNotFoundException;
import dev.mednikov.accounting.currencies.exceptions.CurrencyDeletionException;
import dev.mednikov.accounting.currencies.models.Currency;
import dev.mednikov.accounting.currencies.repositories.CurrencyRepository;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
import dev.mednikov.accounting.transactions.repositories.TransactionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock private OrganizationRepository organizationRepository;
    @Mock private CurrencyRepository currencyRepository;
    @Mock private TransactionRepository transactionRepository;

    @InjectMocks private CurrencyServiceImpl currencyService;

    @Test
    void createCurrency_alreadyExistsTest(){
        UUID organizationId = UUID.randomUUID();
        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Wilhelm Stiftung & Co. KGaA");

        UUID currencyId = UUID.randomUUID();
        String code = "EUR";
        Currency currency = new Currency();
        currency.setId(currencyId);
        currency.setName("Euro");
        currency.setOrganization(organization);
        currency.setPrimary(true);
        currency.setCode(code);

        CurrencyDto payload = new CurrencyDto();
        payload.setOrganizationId(organizationId);
        payload.setName("Euro");
        payload.setCode(code);

        Mockito.when(currencyRepository.findByCodeAndOrganizationId(code, organizationId)).thenReturn(Optional.of(currency));

        Assertions
                .assertThatThrownBy(() -> currencyService.createCurrency(payload))
                .isInstanceOf(CurrencyAlreadyExistsException.class);
    }

    @Test
    void createCurrency_successTest(){
        UUID organizationId = UUID.randomUUID();
        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Bach Lindner Gmbh");

        UUID currencyId = UUID.randomUUID();
        String code = "EUR";
        Currency currency = new Currency();
        currency.setId(currencyId);
        currency.setName("Euro");
        currency.setOrganization(organization);
        currency.setPrimary(true);
        currency.setCode(code);

        CurrencyDto payload = new CurrencyDto();
        payload.setOrganizationId(organizationId);
        payload.setName("Euro");
        payload.setCode(code);

        Mockito.when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        Mockito.when(currencyRepository.findByCodeAndOrganizationId(code, organizationId)).thenReturn(Optional.empty());
        Mockito.when(currencyRepository.findAllByOrganizationId(organizationId)).thenReturn(List.of());
        Mockito.when(currencyRepository.save(Mockito.any())).thenReturn(currency);

        CurrencyDto result = currencyService.createCurrency(payload);
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void getPrimaryCurrency_existsTest(){
        UUID organizationId = UUID.randomUUID();
        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Böhm Metz GmbH");

        UUID currencyId = UUID.randomUUID();
        String code = "EUR";
        Currency currency = new Currency();
        currency.setId(currencyId);
        currency.setName("Euro");
        currency.setOrganization(organization);
        currency.setPrimary(true);
        currency.setCode(code);

        Mockito.when(currencyRepository.findPrimaryCurrency(organizationId)).thenReturn(Optional.of(currency));
        Optional<CurrencyDto> result = currencyService.getPrimaryCurrency(organizationId);
        Assertions.assertThat(result).isPresent();
    }

    @Test
    void getPrimaryCurrency_doesNotExistTest(){
        UUID organizationId = UUID.randomUUID();
        Mockito.when(currencyRepository.findPrimaryCurrency(organizationId)).thenReturn(Optional.empty());
        Optional<CurrencyDto> result = currencyService.getPrimaryCurrency(organizationId);
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void updateCurrency_notFoundTest(){
        UUID organizationId = UUID.randomUUID();
        UUID currencyId = UUID.randomUUID();

        CurrencyDto payload = new CurrencyDto();
        payload.setId(currencyId);
        payload.setOrganizationId(organizationId);
        payload.setName("Euro");
        payload.setCode("EUR");
        payload.setPrimary(false);

        Mockito.when(currencyRepository.findById(currencyId)).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> currencyService.updateCurrency(payload)).isInstanceOf(CurrencyNotFoundException.class);
    }

    @Test
    void updateCurrency_alreadyExistsTest(){
        UUID organizationId = UUID.randomUUID();
        UUID currencyId = UUID.randomUUID();
        String code = "CHF";

        CurrencyDto payload = new CurrencyDto();
        payload.setId(currencyId);
        payload.setOrganizationId(organizationId);
        payload.setName("Euro");
        payload.setCode(code);
        payload.setPrimary(true);

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Schade Vogel GmbH & Co. KG");

        Currency currency = new Currency();
        currency.setId(currencyId);
        currency.setName("Euro");
        currency.setOrganization(organization);
        currency.setPrimary(true);
        currency.setCode("EUR");

        Mockito.when(currencyRepository.findById(currencyId)).thenReturn(Optional.of(currency));
        Mockito.when(currencyRepository.findByCodeAndOrganizationId(code, organizationId)).thenReturn(Optional.of(new Currency()));
        Assertions.assertThatThrownBy(() -> currencyService.updateCurrency(payload)).isInstanceOf(CurrencyAlreadyExistsException.class);
    }

    @Test
    void updateCurrency_successTest(){
        UUID organizationId = UUID.randomUUID();
        UUID currencyId = UUID.randomUUID();

        CurrencyDto payload = new CurrencyDto();
        payload.setId(currencyId);
        payload.setOrganizationId(organizationId);
        payload.setName("Euro");
        payload.setCode("EUR");
        payload.setPrimary(true);

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Schade Vogel GmbH & Co. KG");

        Currency currency = new Currency();
        currency.setId(currencyId);
        currency.setName("Euro");
        currency.setOrganization(organization);
        currency.setPrimary(true);
        currency.setCode("EUR");

        Mockito.when(currencyRepository.findById(currencyId)).thenReturn(Optional.of(currency));
        Mockito.when(currencyRepository.save(Mockito.any())).thenReturn(currency);
        CurrencyDto result = currencyService.updateCurrency(payload);
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void deleteCurrency_primaryCurrencyTest(){
        UUID organizationId = UUID.randomUUID();
        UUID currencyId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Heß Wagner Stiftung & Co. KGaA");

        Currency currency = new Currency();
        currency.setId(currencyId);
        currency.setName("Euro");
        currency.setOrganization(organization);
        currency.setPrimary(true);
        currency.setCode("EUR");

        Mockito.when(currencyRepository.findById(currencyId)).thenReturn(Optional.of(currency));
        Assertions.assertThatThrownBy(() -> currencyService.deleteCurrency(currencyId)).isInstanceOf(CurrencyDeletionException.class);
    }

    @Test
    void deleteCurrency_hasTransactionsTest(){
        UUID organizationId = UUID.randomUUID();
        UUID currencyId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Altmann AG & Co. KGaA");

        Currency currency = new Currency();
        currency.setId(currencyId);
        currency.setName("Swiss Franc");
        currency.setOrganization(organization);
        currency.setPrimary(false);
        currency.setCode("CHF");

        Mockito.when(currencyRepository.findById(currencyId)).thenReturn(Optional.of(currency));
        Mockito.when(transactionRepository.countByCurrencyId(currencyId)).thenReturn(5);
        Assertions.assertThatThrownBy(() -> currencyService.deleteCurrency(currencyId)).isInstanceOf(CurrencyDeletionException.class);
    }


}
