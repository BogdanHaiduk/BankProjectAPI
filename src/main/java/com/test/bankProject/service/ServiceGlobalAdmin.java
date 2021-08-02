package com.test.bankProject.service;

import com.test.bankProject.dto.*;
import com.test.bankProject.entity.BankAccount;
import com.test.bankProject.entity.BankFamilyAccount;
import com.test.bankProject.entity.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceGlobalAdmin {

    private final ComponentForService componentForService;

    @Autowired
    public ServiceGlobalAdmin(ComponentForService componentForService) {
        this.componentForService = componentForService;
    }

    private void allowLogicStaticOrDayLimit(BaseEntity baseEntity, AuthenticationRequestDto authenticationRequestDto,
                                            boolean onDay){
        baseEntity.setLockAccount(false);
        componentForService.logicStaticOrDayLimit(baseEntity, authenticationRequestDto, onDay);
        baseEntity.setLockAccount(true);
    }

    private void allowLogicForLiftingOfRestrictions(BaseEntity baseEntity){
        baseEntity.setLockAccount(false);
        componentForService.logicForLiftingOfRestrictions(baseEntity);
    }


    public List<AbstractBankAccountDto> allUserInBankSystem(String token) {
        BankAccount adminGlobal = componentForService.searchBankAccountByToken(token);
        if (componentForService.checkRoleGlobalAdmin(adminGlobal)) {
            return GlobalAdminUserDto.forAllUserBankInSystem(componentForService.getBankAccountRepository().findAll());
        }
        return null;
    }

    public void limitForAll(String token, AuthenticationRequestDto authenticationRequestDto, boolean onDay) {
        BankAccount adminGlobal = componentForService.searchBankAccountByToken(token);

        if (componentForService.checkRoleGlobalAdmin(adminGlobal)) {
            for(BankAccount bankAccount:componentForService.getBankAccountRepository().findAll()){
                bankAccount.setLockAccount(false);
                componentForService.logicStaticOrDayLimit(bankAccount,authenticationRequestDto,onDay);
                bankAccount.setLockAccount(true);
                bankAccount.setFamilyLimitLock(false);
                componentForService.saveBankAccount(bankAccount);
            }
            for(BankFamilyAccount bankFamilyAccount : componentForService.getBankAccountFamilyRepository().findAll() ){
                bankFamilyAccount.setLockAccount(false);
                componentForService.logicForLiftingOfRestrictions(bankFamilyAccount);
                bankFamilyAccount.setLockAccount(true);
                componentForService.saveBankFamilyAccount(bankFamilyAccount);
            }
        }
    }

    public AbstractBankAccountDto limitForSelectedFamily(String token, AuthenticationRequestDto authenticationRequestDto, boolean onDay) {
        BankAccount adminGlobal = componentForService.searchBankAccountByToken(token);
        BankFamilyAccount bankFamilyAccount = componentForService.searchBankFamilyAccountByID(authenticationRequestDto.getIdBankFamilyAccount());

        if (componentForService.checkRoleGlobalAdmin(adminGlobal)) {

            List<BankAccount> bankAccountList = bankFamilyAccount.getBankAccounts();
            allowLogicStaticOrDayLimit(bankFamilyAccount,authenticationRequestDto,onDay);

            for (BankAccount bankAccount : bankAccountList) {
                allowLogicForLiftingOfRestrictions(bankAccount);
                bankAccount.setFamilyLimitLock(true);
                bankAccount.setLockAccount(true);
            }

            bankFamilyAccount.setBankAccounts(bankAccountList);
            componentForService.saveBankFamilyAccount(bankFamilyAccount);
        }

        return BankFamilyAccountDto.anyBankFamilyAccountDto(bankFamilyAccount);
    }

    public AbstractBankAccountDto limitForSelectedPerson (String token, AuthenticationRequestDto authenticationRequestDto, boolean onDay){
        BankAccount adminGlobal = componentForService.searchBankAccountByToken(token);
        BankAccount bankAccount = componentForService.searchBankAccountByID(authenticationRequestDto.getId());
        BankAccount adminFamily = componentForService.searchBankAccountByID(bankAccount.getBankFamilyAccount().getBankAdminFamilyAccountId());

        if (componentForService.checkRoleGlobalAdmin(adminGlobal)) {
            bankAccount.setFamilyLimitLock(false);
            allowLogicStaticOrDayLimit(bankAccount,authenticationRequestDto,onDay);
            componentForService.saveBankAccount(bankAccount);
        }

        return BankAccountDto.infoAnyAccount(bankAccount, adminFamily);
    }

    public void liftingOfPersonalRestrictionsForFamily(String token, AuthenticationRequestDto authenticationRequestDto) {
        BankAccount adminGlobal = componentForService.searchBankAccountByToken(token);
        BankFamilyAccount bankFamilyAccount = componentForService.searchBankFamilyAccountByID(authenticationRequestDto.getIdBankFamilyAccount());

        if(componentForService.checkRoleGlobalAdmin(adminGlobal)){
            allowLogicForLiftingOfRestrictions(bankFamilyAccount);

            List<BankAccount> bankAccountList = bankFamilyAccount.getBankAccounts();

            for (BankAccount bankAccount : bankAccountList){
                allowLogicForLiftingOfRestrictions(bankAccount);
                bankAccount.setFamilyLimitLock(true);
            }
            bankFamilyAccount.setBankAccounts(bankAccountList);
            componentForService.saveBankFamilyAccount(bankFamilyAccount);
        }

    }

    public void liftingOfPersonalRestrictionsForPerson(String token, AuthenticationRequestDto authenticationRequestDto) {
        BankAccount adminGlobal = componentForService.searchBankAccountByToken(token);
        BankAccount bankAccount = componentForService.searchBankAccountByID(authenticationRequestDto.getId());

        if(componentForService.checkRoleGlobalAdmin(adminGlobal)){
            allowLogicForLiftingOfRestrictions(bankAccount);
            bankAccount.setFamilyLimitLock(false);
            componentForService.logicForLiftingOfRestrictions(bankAccount);
            componentForService.saveBankAccount(bankAccount);
        }

    }

    public void liftingOfPersonalRestrictionsForAll(String token){
        BankAccount adminGlobal = componentForService.searchBankAccountByToken(token);
        if (componentForService.checkRoleGlobalAdmin(adminGlobal)) {
            List<BankAccount> listAllBankAccounts = componentForService.getBankAccountRepository().findAll();
            List<BankFamilyAccount> listAllBankFamilyAccounts  = componentForService.getBankAccountFamilyRepository().findAll();
            for (BankAccount bankAccount: listAllBankAccounts){
                allowLogicForLiftingOfRestrictions(bankAccount);
                componentForService.saveBankAccount(bankAccount);
            }
            for(BankFamilyAccount bankFamilyAccount: listAllBankFamilyAccounts){
                allowLogicForLiftingOfRestrictions(bankFamilyAccount);
                componentForService.saveBankFamilyAccount(bankFamilyAccount);
            }

        }
    }

}