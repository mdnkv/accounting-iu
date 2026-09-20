package dev.mednikov.accounting.organizations.services;

import dev.mednikov.accounting.organizations.dto.OrganizationDto;
import dev.mednikov.accounting.users.models.User;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationService {

    OrganizationDto createOrganization(User owner, OrganizationDto organizationDto);

    OrganizationDto updateOrganization(OrganizationDto organizationDto);

    void deleteOrganization(UUID id);

    Optional<OrganizationDto> getOrganization(UUID id);

}
