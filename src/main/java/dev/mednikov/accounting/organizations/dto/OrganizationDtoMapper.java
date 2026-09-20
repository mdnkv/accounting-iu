package dev.mednikov.accounting.organizations.dto;

import dev.mednikov.accounting.organizations.models.Organization;

import java.util.function.Function;

public final class OrganizationDtoMapper implements Function<Organization, OrganizationDto> {

    @Override
    public OrganizationDto apply(Organization organization) {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId(organization.getId());
        organizationDto.setName(organization.getName());
        organizationDto.setCountry(organization.getCountry());
        organizationDto.setActive(organization.isActive());
        organizationDto.setTaxNumber(organization.getTaxNumber());
        return organizationDto;
    }
}
