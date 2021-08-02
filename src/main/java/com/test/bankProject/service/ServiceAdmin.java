package com.test.bankProject.service;

import com.test.bankProject.dto.BankFamilyAccountDto;
import com.test.bankProject.entity.BankAccount;
import com.test.bankProject.entity.BankFamilyAccount;
import com.test.bankProject.dto.AuthenticationRequestDto;
import com.test.bankProject.dto.BankAccountDto;
import com.test.bankProject.dto.AbstractBankAccountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class ServiceAdmin {

    private final ComponentForService componentForService;

    @Autowired
    public ServiceAdmin(ComponentForService componentForService) {
        this.componentForService = componentForService;
    }

    public List<AbstractBankAccountDto> allFamily(String token){

        BankAccount adminFamily = componentForService.searchBankAccountByToken(token);
        BankFamilyAccount bankFamilyAccount = adminFamily.getBankFamilyAccount();
        List<BankAccount> allFamily = bankFamilyAccount.getBankAccounts();
        return BankAccountDto.listUsers(allFamily, adminFamily);
    }

    public AbstractBankAccountDto familyAccount(String token) {
        BankAccount adminFamily = componentForService.searchBankAccountByToken(token);
        return BankFamilyAccountDto.anyBankFamilyAccountDto(adminFamily.getBankFamilyAccount());
    }

    public void createNewFamilyAccount (String token){
        BankAccount adminFamily = componentForService.searchBankAccountByToken(token);

        if (componentForService.getBankAccountFamilyRepository().findByBankAdminFamilyAccountId(adminFamily.getId())==null){
            adminFamily.setBankFamilyAccount(new BankFamilyAccount(adminFamily.getId(),new BigDecimal(0)));
            componentForService.getBankAccountRepository().save(adminFamily);
        }

    }

    public String addNewPersonFamily(String token, AuthenticationRequestDto authenticationRequestDto){

        BankAccount adminFamily = componentForService.searchBankAccountByToken(token);
        BankAccount bankAccountNewPersonFamily = componentForService.searchBankAccountByID(authenticationRequestDto.getId());
        BankFamilyAccount bankFamilyAccount = adminFamily.getBankFamilyAccount();

        if(bankAccountNewPersonFamily.getBankFamilyAccount()==null) {
            List<BankAccount> bankAccountArrayList = new ArrayList<>();

            bankAccountArrayList.add(bankAccountNewPersonFamily);
            bankFamilyAccount.setBankAccounts(bankAccountArrayList);

            bankAccountNewPersonFamily.setBankFamilyAccount(bankFamilyAccount);

            componentForService.getBankAccountRepository().save(bankAccountNewPersonFamily);
            componentForService.getBankAccountFamilyRepository().save(bankFamilyAccount);
            return "Operation successful";
        }
        return "Operation not successful, because in user have family account";

    }

    public void limitMoneyForAllFamily (String token, AuthenticationRequestDto authenticationRequestDto, boolean onDay){

        BankAccount adminFamily = componentForService.searchBankAccountByToken(token);
        BankFamilyAccount bankFamilyAccount = adminFamily.getBankFamilyAccount();
        List<BankAccount> bankAccountList = bankFamilyAccount.getBankAccounts();

        for (BankAccount bankAccount : bankAccountList) {
            componentForService.logicForLiftingOfRestrictions(bankAccount);
            if(!bankAccount.isLockAccount())bankAccount.setFamilyLimitLock(true);
        }
        bankFamilyAccount.setBankAccounts(bankAccountList);
        componentForService.saveBankFamilyAccount(
                (BankFamilyAccount) componentForService.logicStaticOrDayLimit(bankFamilyAccount,authenticationRequestDto,onDay));

    }

    public AbstractBankAccountDto limitMoneyForOneMemberFamily(String token, AuthenticationRequestDto authenticationRequestDto, boolean onDay) {

        BankAccount adminFamily = componentForService.searchBankAccountByToken(token);
        BankAccount bankAccountLimit = componentForService.searchBankAccountByID(authenticationRequestDto.getId());

        if(adminFamily.getId().equals(bankAccountLimit.getBankFamilyAccount().getBankAdminFamilyAccountId())&&
            !bankAccountLimit.isLockAccount())
        {
            bankAccountLimit.setFamilyLimitLock(false);
            componentForService.logicStaticOrDayLimit(bankAccountLimit,authenticationRequestDto,onDay);
            componentForService.saveBankAccount(bankAccountLimit);
            return BankAccountDto.infoAnyAccount(bankAccountLimit, adminFamily);
        }
        else log.error("error!");
        return null;
    }

    //Снятие личных ограничений с пользователя и открыт доступ к общему лимиту денег для снятия всей семьи
    public AbstractBankAccountDto liftingOfPersonalRestrictions(String token, AuthenticationRequestDto authenticationRequestDto) {
        BankAccount adminFamily = componentForService.searchBankAccountByToken(token);
        BankAccount bankAccountFamilyPerson = componentForService.searchBankAccountByID(authenticationRequestDto.getId());

        if(adminFamily.getId().equals(bankAccountFamilyPerson.getBankFamilyAccount().getBankAdminFamilyAccountId())&&
            !bankAccountFamilyPerson.isLockAccount())
        {
            bankAccountFamilyPerson.setFamilyLimitLock(true);
            componentForService.getBankAccountRepository()
                    .save((BankAccount) componentForService.logicForLiftingOfRestrictions(bankAccountFamilyPerson));
            return BankAccountDto.infoAnyAccount(bankAccountFamilyPerson, adminFamily);
        }
        return null;
    }

}