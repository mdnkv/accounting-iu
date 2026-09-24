package dev.mednikov.accounting.accounts.mappers;

import dev.mednikov.accounting.accounts.domain.AccountCategoryResponseDto;
import dev.mednikov.accounting.accounts.models.AccountCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper()
public interface AccountCategoryResponseDtoMapper {

    @Mapping(source = "organization.id", target = "organizationId")
    AccountCategoryResponseDto toDto(AccountCategory accountCategory);

}
