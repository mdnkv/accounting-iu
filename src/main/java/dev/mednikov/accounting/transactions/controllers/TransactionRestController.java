package dev.mednikov.accounting.transactions.controllers;

import dev.mednikov.accounting.transactions.dto.TransactionDto;
import dev.mednikov.accounting.transactions.services.TransactionService;
import dev.mednikov.accounting.users.models.User;
import dev.mednikov.accounting.users.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {

    private final TransactionService transactionService;

    public TransactionRestController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('transactions:create') and hasAuthority(#body.organizationId)")
    public @ResponseBody TransactionDto createTransaction(@RequestBody @Valid TransactionDto body, @AuthenticationPrincipal Jwt jwt) {
        return this.transactionService.createTransaction(body);
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAuthority('transactions:view') and hasAuthority(#organizationId)")
    public @ResponseBody List<TransactionDto> getAllTransactions(@PathVariable UUID organizationId) {
        return this.transactionService.getTransactions(organizationId);
    }

    @DeleteMapping("/delete/{transactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('transactions:delete')")
    public void deleteTransaction(@PathVariable UUID transactionId) {
        this.transactionService.deleteTransaction(transactionId);
    }

}
