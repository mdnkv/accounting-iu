package dev.mednikov.accounting.shared.bootstrap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mednikov.accounting.authorities.models.Authority;
import dev.mednikov.accounting.authorities.repositories.AuthorityRepository;
import dev.mednikov.accounting.organizations.events.CreateOwnerEvent;
import dev.mednikov.accounting.organizations.events.OrganizationCreatedEvent;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.roles.models.Role;
import dev.mednikov.accounting.roles.repositories.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class RoleAuthorityBootstrapService {

    private final static Logger logger = LoggerFactory.getLogger(RoleAuthorityBootstrapService.class);

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RoleAuthorityBootstrapService(
            RoleRepository roleRepository,
            AuthorityRepository authorityRepository,
            ResourceLoader resourceLoader,
            ObjectMapper objectMapper,
            ApplicationEventPublisher eventPublisher
    ) {
        this.roleRepository = roleRepository;
        this.authorityRepository = authorityRepository;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
    }

    private List<Role> createRoles(Organization organization, Set<String> loaded){
        List<Role> roles = new ArrayList<>();
        for (String roleName: loaded){
           Role role = new Role();
           role.setName(roleName);
           role.setOrganization(organization);
           roles.add(role);
        }
        return this.roleRepository.saveAll(roles);
    }


    private List<Authority> createAuthorities(Organization organization, Set<String> loaded){
        List<Authority> authorities = new ArrayList<>();
        for (String authorityName: loaded){
            Authority authority = new Authority();
            authority.setName(authorityName);
            authority.setOrganization(organization);
            authorities.add(authority);
        }
        return this.authorityRepository.saveAll(authorities);
    }

    private Set<String> extractAuthorities (Map<String, List<String>> data){
        Set<String> authorities = new HashSet<>();
        for (Map.Entry<String, List<String>> entry : data.entrySet()) {
            authorities.addAll(entry.getValue());
        }
        return authorities;
    }

    private Set<String> extractRoles (Map<String, List<String>> data){
        Set<String> roles = new HashSet<>();
        for (Map.Entry<String, List<String>> entry : data.entrySet()) {
            roles.add(entry.getKey());
        }
        return roles;
    }

    @EventListener
    public void onOrganizationCreatedEventListener(OrganizationCreatedEvent event) {
        Organization organization = event.getOrganization();
        try{
            // Load data
            Resource resource = this.resourceLoader.getResource("classpath:bootstrap/roles_authorities.json");
            TypeReference<List<RoleAuthorityDto>> typeReference = new TypeReference<>() {};
            List<RoleAuthorityDto> loadedData = this.objectMapper.readValue(resource.getInputStream(), typeReference);
            // Convert to a hash map where authority is a key
            Map<String, List<String>> data = loadedData.stream().collect(Collectors.toMap(RoleAuthorityDto::getName, RoleAuthorityDto::getAuthorities));
            // Extract authorities and roles
            Set<String> loadedAuthorities = extractAuthorities(data);
            Set<String> loadedRoles = extractRoles(data);
            // Create authorities and persist
            List<Authority> authorities = this.createAuthorities(organization, loadedAuthorities);
            // Create roles and persist
            List<Role> roles = this.createRoles(organization, loadedRoles);
            // Assign authorities for roles
            List<Role> assignedRoles = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : data.entrySet()) {
                // Get a role by name from saved roles
                Role role = roles.stream().filter(r -> r.getName().equals(entry.getKey())).findFirst().get();
                // Filter saved authorities
                Set<Authority> authoritiesList = authorities.stream().filter(authority -> entry.getValue().contains(authority.getName())).collect(Collectors.toSet());
                role.setAuthorities(authoritiesList);
                assignedRoles.add(role);
            }
            List<Role> result = roleRepository.saveAll(assignedRoles);

            // Set up an owner role for the user who created the organization
            Optional<Role> ownerRole = result.stream().filter(r -> r.getName().equals("Owner")).findFirst();
            if (ownerRole.isPresent()){
                Role owner = ownerRole.get();
                CreateOwnerEvent createOwnerEvent = new CreateOwnerEvent(this, organization, event.getOwner(), owner);
                this.eventPublisher.publishEvent(createOwnerEvent);
            }
            // Send event
        } catch (Exception ex){
            logger.error(ex.getMessage());
        }
    }

}
