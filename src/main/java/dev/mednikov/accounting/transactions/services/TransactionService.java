package dev.mednikov.accounting.transactions.services;

import dev.mednikov.accounting.transactions.dto.TransactionDto;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    TransactionDto createTransaction(TransactionDto transactionDto);

    List<TransactionDto> getTransactions (UUID organizationId);

    void deleteTransaction(UUID transactionId);

}
