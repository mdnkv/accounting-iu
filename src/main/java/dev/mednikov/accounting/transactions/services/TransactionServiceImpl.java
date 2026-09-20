package dev.mednikov.accounting.transactions.services;

import dev.mednikov.accounting.accounts.exceptions.AccountNotFoundException;
import dev.mednikov.accounting.accounts.exceptions.DeprecatedAccountException;
import dev.mednikov.accounting.accounts.models.Account;
import dev.mednikov.accounting.accounts.repositories.AccountRepository;
import dev.mednikov.accounting.currencies.events.CurrencyExchangeEvent;
import dev.mednikov.accounting.currencies.exceptions.CurrencyNotFoundException;
import dev.mednikov.accounting.currencies.exceptions.DeprecatedCurrencyException;
import dev.mednikov.accounting.currencies.models.Currency;
import dev.mednikov.accounting.currencies.repositories.CurrencyRepository;
import dev.mednikov.accounting.journals.exceptions.JournalNotFoundException;
import dev.mednikov.accounting.journals.models.Journal;
import dev.mednikov.accounting.journals.repositories.JournalRepository;
import dev.mednikov.accounting.organizations.exceptions.OrganizationNotFoundException;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
import dev.mednikov.accounting.transactions.dto.TransactionDto;
import dev.mednikov.accounting.transactions.dto.TransactionDtoMapper;
import dev.mednikov.accounting.transactions.dto.TransactionLineDto;
import dev.mednikov.accounting.transactions.exceptions.TransactionDeletionException;
import dev.mednikov.accounting.transactions.exceptions.UnbalancedTransactionException;
import dev.mednikov.accounting.transactions.models.Transaction;
import dev.mednikov.accounting.transactions.models.TransactionLine;
import dev.mednikov.accounting.transactions.repositories.TransactionLineRepository;
import dev.mednikov.accounting.transactions.repositories.TransactionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final static TransactionDtoMapper transactionDtoMapper = new TransactionDtoMapper();

    private final CurrencyRepository currencyRepository;
    private final OrganizationRepository organizationRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionLineRepository transactionLineRepository;
    private final AccountRepository accountRepository;
    private final JournalRepository journalRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TransactionServiceImpl(OrganizationRepository organizationRepository,
                                  TransactionRepository transactionRepository,
                                  TransactionLineRepository transactionLineRepository,
                                  CurrencyRepository currencyRepository,
                                  AccountRepository accountRepository,
                                  JournalRepository journalRepository,
                                  ApplicationEventPublisher eventPublisher) {
        this.organizationRepository = organizationRepository;
        this.transactionRepository = transactionRepository;
        this.transactionLineRepository = transactionLineRepository;
        this.accountRepository = accountRepository;
        this.currencyRepository = currencyRepository;
        this.journalRepository = journalRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public TransactionDto createTransaction(TransactionDto payload) {
        // Check that transaction has at least 2 lines
        if (payload.getLines().size() < 2){
            throw new UnbalancedTransactionException();
        }
        // Verify that credit == debit
        BigDecimal creditAmount = payload.getLines().stream()
                .map(TransactionLineDto::getCreditAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal debitAmount = payload.getLines().stream()
                .map(TransactionLineDto::getDebitAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (!creditAmount.equals(debitAmount)) {
            throw new UnbalancedTransactionException();
        }
        // Get organization
//        Long organizationId = Long.valueOf(payload.getOrganizationId());
//        Organization organization = this.organizationRepository.getReferenceById(organizationId);
        UUID organizationId = payload.getOrganizationId();
        Organization organization = this.organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException());
        // Get currency
//        Long currencyId = Long.valueOf(payload.getCurrencyId());
        Currency targetCurrency = this.currencyRepository.findById(payload.getCurrencyId()).orElseThrow(CurrencyNotFoundException::new);
        if (targetCurrency.isDeprecated()){
            throw new DeprecatedCurrencyException();
        }
        if (!targetCurrency.getOrganization().getId().equals(organizationId)){
            // todo check for currency ownership
            throw new RuntimeException();
        }
        Currency baseCurrency = this.currencyRepository.findPrimaryCurrency(organizationId).orElseThrow(CurrencyNotFoundException::new);
        // Create transaction
        Transaction transaction = new Transaction();
//        transaction.setId(snowflakeGenerator.next());
        transaction.setOrganization(organization);
        transaction.setDescription(payload.getDescription());
        transaction.setDate(payload.getDate());
        transaction.setBaseCurrency(baseCurrency);
        transaction.setTargetCurrency(targetCurrency);
        transaction.setDraft(false);

        // Primary currency amount
        if (baseCurrency.equals(targetCurrency)){
            transaction.setTotalCreditAmount(creditAmount);
            transaction.setTotalDebitAmount(debitAmount);
        } else {
            transaction.setTotalCreditAmount(BigDecimal.ZERO);
            transaction.setTotalDebitAmount(BigDecimal.ZERO);
        }

        // Target currency amount
        transaction.setOriginalTotalCreditAmount(creditAmount);
        transaction.setOriginalTotalDebitAmount(debitAmount);

        // Set journal
//        Long journalId = Long.valueOf(payload.getJournalId());
        Journal journal = this.journalRepository.findById(payload.getJournalId()).orElseThrow(JournalNotFoundException::new);
        if (!journal.getOrganization().getId().equals(organizationId)){
            // Todo check for journal ownership
            throw new RuntimeException();
        }
        transaction.setJournal(journal);

        // Persist transaction to the datasource
        Transaction transactionResult = this.transactionRepository.save(transaction);
        // Process transaction lines
        List<TransactionLine> lines = new ArrayList<>();
        for (TransactionLineDto lineDto: payload.getLines()) {
//            Long accountId = Long.valueOf(lineDto.getAccountId());
            Account account = this.accountRepository.findById(lineDto.getAccountId()).orElseThrow(AccountNotFoundException::new);
            if (account.isDeprecated()){
                throw new DeprecatedAccountException();
            }
            if (!account.getOrganization().getId().equals(organizationId)){
                // Todo check for account ownership
                throw new RuntimeException();
            }
            // Create transaction line
            TransactionLine line = new TransactionLine();
            line.setAccount(account);
            line.setTransaction(transaction);
            BigDecimal lineCreditAmount = lineDto.getCreditAmount();
            BigDecimal lineDebitAmount = lineDto.getDebitAmount();

            // Primary currency amount
            if (baseCurrency.equals(targetCurrency)){
                line.setCreditAmount(lineCreditAmount);
                line.setDebitAmount(lineDebitAmount);
            } else {
                line.setCreditAmount(BigDecimal.ZERO);
                line.setDebitAmount(BigDecimal.ZERO);
            }
            // Target currency amount
            line.setOriginalCreditAmount(lineCreditAmount);
            line.setOriginalDebitAmount(lineDebitAmount);
//            line.setId(snowflakeGenerator.next());
            // Add to list
            lines.add(line);
        }
        // Attach lines to the transaction entity
        List<TransactionLine> linesResult = this.transactionLineRepository.saveAll(lines);
        transactionResult.setLines(linesResult);
//        transactionResult.setDraft(!baseCurrency.equals(targetCurrency));

        // Save transaction with lines
        Transaction result = this.transactionRepository.save(transactionResult);

        if (!baseCurrency.getId().equals(targetCurrency.getId())) {
            CurrencyExchangeEvent currencyExchangeEvent = new CurrencyExchangeEvent(this, result);
            this.eventPublisher.publishEvent(currencyExchangeEvent);
        }

        return transactionDtoMapper.apply(result);
    }

    @Override
    public List<TransactionDto> getTransactions(UUID organizationId) {
        return this.transactionRepository
                .findAllByOrganizationId(organizationId)
                .stream()
                .map(transactionDtoMapper)
                .toList();
    }

    @Override
    public void deleteTransaction(UUID transactionId) {
        // Only draft transaction can be removed
        Optional<Transaction> result = this.transactionRepository.findById(transactionId);
        if (result.isPresent()) {
            Transaction transaction = result.get();
            if (transaction.isDraft()){
                this.transactionRepository.delete(transaction);
            } else {
                throw new TransactionDeletionException();
            }
        }
    }
}
