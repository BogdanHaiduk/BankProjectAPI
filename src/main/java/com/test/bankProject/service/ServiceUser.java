package com.test.bankProject.service;

import com.test.bankProject.dto.BankAccountDto;
import com.test.bankProject.dto.AbstractBankAccountDto;
import com.test.bankProject.entity.BankAccount;
import com.test.bankProject.entity.BankFamilyAccount;
import com.test.bankProject.dto.AuthenticationRequestDto;
import com.test.bankProject.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ServiceUser {

    private final ComponentForService componentForService;
    private Map<Object,Object> result ;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public ServiceUser(ComponentForService componentForService) {
        this.componentForService = componentForService;
    }

    public void accountRegistration (AuthenticationRequestDto authenticationRequestDto){
        String password = passwordEncoder.encode(authenticationRequestDto.getPassword());
        List<Role> roles = new ArrayList<>();
        roles.add(componentForService.getRoleRepository().getById(1L));

        BankAccount bankAccount = new BankAccount(authenticationRequestDto.getUsername(),password,roles);
        bankAccount.setBankFamilyAccount(null);
        componentForService.saveBankAccount(bankAccount);
    }

    public AbstractBankAccountDto myAccount(String token) {
        BankAccount bankAccount = componentForService.searchBankAccountByToken(token);
        BankAccount adminFamily = null;
        if(bankAccount.getBankFamilyAccount()!=null) adminFamily = componentForService.searchBankAccountByID(bankAccount.getBankFamilyAccount().getBankAdminFamilyAccountId());
        return BankAccountDto.infoAnyAccount(bankAccount, adminFamily);
    }

    public Map<Object,Object> cashWithdrawal(String token, AuthenticationRequestDto authenticationRequestDto) {
        BankAccount bankAccount = componentForService.searchBankAccountByToken(token);
        BigDecimal cashWithdrawal = authenticationRequestDto.getWithdrawalOrReplenishment();
        LogicMoney logicMoney = new LogicMoney(bankAccount,cashWithdrawal, componentForService);
        logicMoney.startLogic();
        result = new LinkedHashMap<>();
        result.put("IdFamily", bankAccount.getBankFamilyAccount().getId());
        result.put("CashWithdrawal", authenticationRequestDto.getWithdrawalOrReplenishment());
        result.put("FamilyAccountBalance", bankAccount.getBankFamilyAccount().getBalanceMoneyFamily());
        result.put("OperationSuccessful", logicMoney.isResultLogicLimitMoney());
        if(bankAccount.isLimitLockTime()&&!bankAccount.isFamilyLimitLock())result.put("LimitOnToday", bankAccount.getLimitToday());
        if(!bankAccount.isLimitLockTime()&&!bankAccount.isFamilyLimitLock())result.put("PersinalLimit", bankAccount.getLimitToday());
        if(bankAccount.isFamilyLimitLock()&&!bankAccount.isLimitLockTime()) result.put("LimitFamily", bankAccount.getBankFamilyAccount().getLimitToday());

        return result;
    }

    public Map<Object, Object> accountReplenishment(String token, AuthenticationRequestDto authenticationRequestDto) {
        BankFamilyAccount bankFamilyAccount = componentForService.searchBankFamilyAccountByID(authenticationRequestDto.getIdBankFamilyAccount()) ;
        BankAccount bankAccount = componentForService.searchBankAccountByToken(token);

        BigDecimal balance = bankFamilyAccount.getBalanceMoneyFamily();
        BigDecimal cashReplenishment = authenticationRequestDto.getWithdrawalOrReplenishment();
        if(cashReplenishment.signum()>0) {
            bankFamilyAccount.setBalanceMoneyFamily(balance.add(cashReplenishment));
            componentForService.getBankAccountFamilyRepository().save(bankFamilyAccount);
        }
        result = new LinkedHashMap<>();
        result.put("accountReplenishment", authenticationRequestDto.getWithdrawalOrReplenishment());
        result.put("FamilyAccountBalance", bankFamilyAccount.getBalanceMoneyFamily());
        result.put("MoneyReplenisherPerson", bankAccount.getUsername());
        return result;
    }

}