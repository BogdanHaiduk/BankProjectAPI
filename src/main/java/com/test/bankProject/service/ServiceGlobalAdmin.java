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

    private void allowLogicStaticOrDayLimit(BaseEntity baseEntity, RequestDto requestDto,
                                            boolean onDay){
        baseEntity.setLockAccount(false);
        componentForService.logicStaticOrDayLimit(baseEntity, requestDto, onDay);
        baseEntity.setLockAccount(true);
    }

    private void allowLogicForLiftingOfRestrictions(BaseEntity baseEntity){
        baseEntity.setLockAccount(false);
        componentForService.logicForLiftingOfRestrictions(baseEntity);
    }


    public List<AbstractBankAccountDto> allUserInBankSystem(String token) {
        BankAccount adminGlobal = componentForService.searchBankAccountByToken(token);
        if (componentForService.checkRoleGlobalAdmin(adminGlobal)) {
            return ImplementDto.forAllUserBankInSystem(componentForService.getBankAccountRepository().findAll(),
                    componentForService);
        }
        return null;
    }

    public void limitForAll(String token, RequestDto requestDto, boolean onDay) {
        BankAccount adminGlobal = componentForService.searchBankAccountByToken(token);

        if (componentForService.checkRoleGlobalAdmin(adminGlobal)) {
            for(BankAccount bankAccount:componentForService.getBankAccountRepository().findAll()){
                bankAccount.setLockAccount(false);
                componentForService.logicStaticOrDayLimit(bankAccount, requestDto,onDay);
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

    public AbstractBankAccountDto limitForSelectedFamily(String token, RequestDto requestDto, boolean onDay) {
        BankAccount adminGlobal = componentForService.searchBankAccountByToken(token);
        BankFamilyAccount bankFamilyAccount = componentForService.searchBankFamilyAccountByID(requestDto.getIdBankFamilyAccount());

        if (componentForService.checkRoleGlobalAdmin(adminGlobal)) {

            List<BankAccount> bankAccountList = bankFamilyAccount.getBankAccounts();
            allowLogicStaticOrDayLimit(bankFamilyAccount, requestDto,onDay);

            for (BankAccount bankAccount : bankAccountList) {
                allowLogicForLiftingOfRestrictions(bankAccount);
                bankAccount.setFamilyLimitLock(true);
                bankAccount.setLockAccount(true);
            }

            bankFamilyAccount.setBankAccounts(bankAccountList);
            componentForService.saveBankFamilyAccount(bankFamilyAccount);
        }

        return ImplementDto.infoAnyFamilyAccountDto(bankFamilyAccount,componentForService);
    }

    public AbstractBankAccountDto limitForSelectedPerson (String token, RequestDto requestDto, boolean onDay){
        BankAccount adminGlobal = componentForService.searchBankAccountByToken(token);
        BankAccount bankAccount = componentForService.searchBankAccountByID(requestDto.getId());
        BankAccount adminFamily = componentForService.searchBankAccountByID(bankAccount.getBankFamilyAccount().getBankAdminFamilyAccountId());

        if (componentForService.checkRoleGlobalAdmin(adminGlobal)) {
            bankAccount.setFamilyLimitLock(false);
            allowLogicStaticOrDayLimit(bankAccount, requestDto,onDay);
            componentForService.saveBankAccount(bankAccount);
        }

        return ImplementDto.infoAnyAccount(bankAccount, componentForService);
    }

    public void liftingOfPersonalRestrictionsForFamily(String token, RequestDto requestDto) {
        BankAccount adminGlobal = componentForService.searchBankAccountByToken(token);
        BankFamilyAccount bankFamilyAccount = componentForService.searchBankFamilyAccountByID(requestDto.getIdBankFamilyAccount());

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

    public void liftingOfPersonalRestrictionsForPerson(String token, RequestDto requestDto) {
        BankAccount adminGlobal = componentForService.searchBankAccountByToken(token);
        BankAccount bankAccount = componentForService.searchBankAccountByID(requestDto.getId());

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