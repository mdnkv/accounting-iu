package dev.mednikov.accounting.shared.bootstrap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mednikov.accounting.journals.models.Journal;
import dev.mednikov.accounting.journals.repositories.JournalRepository;
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
public class JournalBootstrapService {

    private final static Logger logger = LoggerFactory.getLogger(JournalBootstrapService.class);

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    private final JournalRepository journalRepository;

    public JournalBootstrapService(JournalRepository journalRepository, ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.journalRepository = journalRepository;
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    @EventListener
    public void onOrganizationCreatedEventListener(OrganizationCreatedEvent e) {
        Organization organization = e.getOrganization();
        try {
            Resource resource = this.resourceLoader.getResource("classpath:bootstrap/journals.json");
            TypeReference<List<Journal>> typeReference = new TypeReference<>() {};
            List<Journal> data = this.objectMapper.readValue(resource.getInputStream(), typeReference);
            List<Journal> journals = new ArrayList<>();
            for (Journal item : data) {
                Journal journal = new Journal();
                journal.setName(item.getName());
                journal.setOrganization(organization);
                journal.setActive(true);
                journals.add(journal);
            }
            this.journalRepository.saveAll(journals);
        } catch (Exception ex){
            logger.error(ex.getMessage());
        }
    }


}
