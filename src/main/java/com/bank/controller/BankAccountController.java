package com.bank.controller;

import com.bank.model.BankAccount;
import com.bank.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
public class BankAccountController {
    
    private final BankAccountService accountService;
    
    @Autowired
    public BankAccountController(BankAccountService accountService) {
        this.accountService = accountService;
    }
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("accounts", accountService.findAllAccounts());
        return "index";
    }
    
    @GetMapping("/create")
    public String showCreateForm() {
        return "create";
    }
    
    @PostMapping("/create")
    public String createAccount(@RequestParam String accountHolderName, 
                                RedirectAttributes redirectAttributes) {
        try {
            BankAccount account = accountService.createAccount(accountHolderName);
            redirectAttributes.addFlashAttribute("success", 
                "Account created successfully! Account Number: " + account.getAccountNumber());
            return "redirect:/account/" + account.getAccountNumber();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create account: " + e.getMessage());
            return "redirect:/create";
        }
    }
    
    @GetMapping("/account/{accountNumber}")
    public String viewAccount(@PathVariable String accountNumber, Model model) {
        return accountService.findByAccountNumber(accountNumber)
                .map(account -> {
                    model.addAttribute("account", account);
                    return "account";
                })
                .orElse("redirect:/?error=Account not found");
    }
    
    @GetMapping("/deposit/{accountNumber}")
    public String showDepositForm(@PathVariable String accountNumber, Model model) {
        return accountService.findByAccountNumber(accountNumber)
                .map(account -> {
                    model.addAttribute("account", account);
                    model.addAttribute("action", "deposit");
                    return "transaction";
                })
                .orElse("redirect:/?error=Account not found");
    }
    
    @PostMapping("/deposit/{accountNumber}")
    public String deposit(@PathVariable String accountNumber,
                          @RequestParam BigDecimal amount,
                          RedirectAttributes redirectAttributes) {
        try {
            accountService.deposit(accountNumber, amount);
            redirectAttributes.addFlashAttribute("success", 
                "Successfully deposited $" + amount.setScale(2));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/account/" + accountNumber;
    }
    
    @GetMapping("/withdraw/{accountNumber}")
    public String showWithdrawForm(@PathVariable String accountNumber, Model model) {
        return accountService.findByAccountNumber(accountNumber)
                .map(account -> {
                    model.addAttribute("account", account);
                    model.addAttribute("action", "withdraw");
                    return "transaction";
                })
                .orElse("redirect:/?error=Account not found");
    }
    
    @PostMapping("/withdraw/{accountNumber}")
    public String withdraw(@PathVariable String accountNumber,
                           @RequestParam BigDecimal amount,
                           RedirectAttributes redirectAttributes) {
        try {
            accountService.withdraw(accountNumber, amount);
            redirectAttributes.addFlashAttribute("success", 
                "Successfully withdrew $" + amount.setScale(2));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/account/" + accountNumber;
    }
}
