package dev.mednikov.accounting.shared.bootstrap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mednikov.accounting.currencies.models.Currency;
import dev.mednikov.accounting.currencies.repositories.CurrencyRepository;
import dev.mednikov.accounting.organizations.events.OrganizationCreatedEvent;
import dev.mednikov.accounting.organizations.models.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CurrencyBootstrapService {

    private final static Logger logger = LoggerFactory.getLogger(CurrencyBootstrapService.class);

    private final CurrencyRepository currencyRepository;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    public CurrencyBootstrapService(CurrencyRepository currencyRepository, ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.currencyRepository = currencyRepository;
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    @EventListener
    public void onOrganizationCreatedEventListener(OrganizationCreatedEvent e){
        Organization organization = e.getOrganization();
        try {
            Resource resource = this.resourceLoader.getResource("classpath:bootstrap/currencies.json");
            TypeReference<List<Currency>> typeReference = new TypeReference<>() {};
            List<Currency> data = this.objectMapper.readValue(resource.getInputStream(), typeReference);
            List<Currency> currencies = new ArrayList<>();
            for (Currency item : data) {
                Currency currency = new Currency();
                currency.setName(item.getName());
                currency.setCode(item.getCode());
                currency.setPrimary(item.isPrimary());
                currency.setOrganization(organization);
                currencies.add(currency);
            }
            this.currencyRepository.saveAll(currencies);
        } catch (Exception ex){
            logger.error(ex.getMessage());
        }
    }
}
