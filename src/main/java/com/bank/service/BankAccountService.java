package com.bank.service;

import com.bank.model.BankAccount;
import com.bank.repository.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class BankAccountService {
    
    private final BankAccountRepository accountRepository;
    
    @Autowired
    public BankAccountService(BankAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    public BankAccount createAccount(String accountHolderName) {
        String accountNumber = generateAccountNumber();
        BankAccount account = new BankAccount(accountHolderName, accountNumber);
        return accountRepository.save(account);
    }
    
    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }
    
    public Optional<BankAccount> findById(Long id) {
        return accountRepository.findById(id);
    }
    
    public List<BankAccount> findAllAccounts() {
        return accountRepository.findAll();
    }
    
    public BankAccount deposit(String accountNumber, BigDecimal amount) {
        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
        account.deposit(amount);
        return accountRepository.save(account);
    }
    
    public BankAccount withdraw(String accountNumber, BigDecimal amount) {
        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
        account.withdraw(amount);
        return accountRepository.save(account);
    }
    
    public BigDecimal getBalance(String accountNumber) {
        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
        return account.getBalance();
    }
    
    private String generateAccountNumber() {
        Random random = new Random();
        String accountNumber;
        do {
            // Generate a 10-digit account number
            accountNumber = String.format("%010d", Math.abs(random.nextLong() % 10000000000L));
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
