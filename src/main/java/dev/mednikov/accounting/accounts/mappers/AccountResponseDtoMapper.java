package dev.mednikov.accounting.accounts.mappers;

import dev.mednikov.accounting.accounts.domain.AccountResponseDto;
import dev.mednikov.accounting.accounts.models.Account;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {AccountCategoryResponseDtoMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AccountResponseDtoMapper {

    @Mapping(source = "organization.id", target = "organizationId")
    AccountResponseDto toDto(Account account);

}
