package dev.mednikov.accounting.authorities.services;

import dev.mednikov.accounting.authorities.dto.AuthorityDto;
import dev.mednikov.accounting.authorities.dto.AuthorityDtoMapper;
import dev.mednikov.accounting.authorities.exceptions.AuthorityAlreadyExistsException;
import dev.mednikov.accounting.authorities.exceptions.AuthorityNotFoundException;
import dev.mednikov.accounting.authorities.models.Authority;
import dev.mednikov.accounting.authorities.repositories.AuthorityRepository;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
import dev.mednikov.accounting.roles.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private final static AuthorityDtoMapper mapper = new AuthorityDtoMapper();

    private final AuthorityRepository authorityRepository;
    private final OrganizationRepository organizationRepository;

    public AuthorityServiceImpl(AuthorityRepository authorityRepository, OrganizationRepository organizationRepository) {
        this.authorityRepository = authorityRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    public AuthorityDto createAuthority(AuthorityDto payload) {
        String name = payload.getName();
        UUID organizationId = payload.getOrganizationId();
        if (this.authorityRepository.findByOrganizationIdAndName(organizationId, name).isPresent()) {
            throw new AuthorityAlreadyExistsException();
        }

        Organization organization = this.organizationRepository.getReferenceById(organizationId);
        Authority authority = new Authority();
        authority.setOrganization(organization);
        authority.setName(name);

        Authority result = this.authorityRepository.save(authority);

        return mapper.apply(result);
    }

    @Override
    public AuthorityDto updateAuthority(AuthorityDto payload) {
        Objects.requireNonNull(payload.getId());
        Authority authority = this.authorityRepository.findById(payload.getId()).orElseThrow(AuthorityNotFoundException::new);

        if (!authority.getName().equals(payload.getName())) {
            if (this.authorityRepository.findByOrganizationIdAndName(payload.getOrganizationId(), payload.getName()).isPresent()){
                throw new AuthorityAlreadyExistsException();
            }
        }

        authority.setName(payload.getName());
        Authority result = this.authorityRepository.save(authority);

        return mapper.apply(result);
    }

    @Override
    public void deleteAuthority(UUID id) {
        this.authorityRepository.deleteById(id);

    }

    @Override
    public List<AuthorityDto> getAuthorities(UUID organizationId) {
        return this.authorityRepository.findByOrganizationId(organizationId)
                .stream()
                .map(mapper)
                .toList();
    }

}
