package dev.mednikov.accounting.shared.bootstrap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mednikov.accounting.accounts.models.Account;
import dev.mednikov.accounting.accounts.models.AccountCategory;
import dev.mednikov.accounting.accounts.repositories.AccountCategoryRepository;
import dev.mednikov.accounting.accounts.repositories.AccountRepository;
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
import java.util.UUID;

@Service
public class AccountBootstrapService {

    private final static Logger logger = LoggerFactory.getLogger(AccountBootstrapService.class);

    private final AccountCategoryRepository accountCategoryRepository;
    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    public AccountBootstrapService(
            AccountCategoryRepository accountCategoryRepository,
            AccountRepository accountRepository,
            ObjectMapper objectMapper,
            ResourceLoader resourceLoader)
    {
        this.accountCategoryRepository = accountCategoryRepository;
        this.accountRepository = accountRepository;
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    @EventListener
    public void onOrganizationCreatedEventListener(OrganizationCreatedEvent e){
        Organization organization = e.getOrganization();
        try {
            createAccountCategories(organization);
            createAccounts(organization);
        } catch (Exception ex){
            logger.error(ex.getMessage());
        }
    }

    // Create account categories
    private void createAccountCategories (Organization organization) throws Exception{
        Resource resource = this.resourceLoader.getResource("classpath:bootstrap/account_categories.json");
        TypeReference<List<AccountCategoryBootstrapDto>> typeReference = new TypeReference<>() {};
        List<AccountCategoryBootstrapDto> data = this.objectMapper.readValue(resource.getInputStream(), typeReference);
        List<AccountCategory> categories = new ArrayList<>();
        for (AccountCategoryBootstrapDto item : data) {
            AccountCategory category = new AccountCategory();
            category.setName(item.getName());
            category.setAccountType(item.getAccountType());
            category.setOrganization(organization);
            categories.add(category);
        }
        accountCategoryRepository.saveAll(categories);
    }

    // Create accounts
    private void createAccounts (Organization organization) throws Exception{
        Resource resource = this.resourceLoader.getResource("classpath:bootstrap/accounts.json");
        TypeReference<List<AccountBootstrapDto>> typeReference = new TypeReference<>() {};
        List<AccountBootstrapDto> data = this.objectMapper.readValue(resource.getInputStream(), typeReference);
//        Long organizationId = organization.getId();
        UUID organizationId = organization.getId();
        List<Account> accounts = new ArrayList<>();
        for (AccountBootstrapDto item : data) {
            Account account = new Account();
            AccountCategory accountCategory = this.accountCategoryRepository.findByOrganizationIdAndName(organizationId, item.getCategoryName())
                            .orElse(null);
            account.setAccountCategory(accountCategory);
            account.setOrganization(organization);
            account.setCode(item.getCode());
            account.setName(item.getName());
            account.setAccountType(item.getAccountType());
            account.setDeprecated(false);
            accounts.add(account);
        }
        accountRepository.saveAll(accounts);
    }
}
