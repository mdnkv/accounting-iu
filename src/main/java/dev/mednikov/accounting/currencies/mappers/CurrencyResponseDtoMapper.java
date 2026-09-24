package dev.mednikov.accounting.currencies.mappers;

import org.mapstruct.Mapper;

import dev.mednikov.accounting.currencies.domain.CurrencyResponseDto;
import dev.mednikov.accounting.currencies.models.Currency;
import org.mapstruct.Mapping;

@Mapper
public interface CurrencyResponseDtoMapper {

    @Mapping(source = "organization.id", target = "organizationId")
    CurrencyResponseDto toDto (Currency currency);

}
