package com.bank.project.dto;

import com.bank.project.entity.BankAccount;
import com.bank.project.entity.BankFamilyAccount;
import com.bank.project.entity.Role;
import com.bank.project.service.ComponentForService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ImplementDto {

    private ImplementDto() {
    }

    public static AbstractBankAccountDto impl(BankAccountDto bankAccountDto, BankAccount bankAccount, ComponentForService componentForService){

        bankAccountDto.setId(bankAccount.getId());
        bankAccountDto.setUsername(bankAccount.getUsername());
        bankAccountDto.setRole(bankAccount.getRoles().toString());
        bankAccountDto.setLimitStatic(bankAccount.getLimitStatic());
        bankAccountDto.setLimitOnDay(bankAccount.isLimitLockTime());
        bankAccountDto.setLimitOnToday(bankAccount.getLimitToday());
        bankAccountDto.setLockAccount(bankAccount.isLockAccount());

        if (bankAccount.getBankFamilyAccount()!=null) {
            var adminFamily = componentForService.searchBankAccountByID(bankAccount.getBankFamilyAccount().getBankAdminFamilyAccountId());
            bankAccountDto.setFamilyId(bankAccount.getBankFamilyAccount().getId());
            bankAccountDto.setAccessToTheTotalLimit(bankAccount.isFamilyLimitLock());
            bankAccountDto.setFamilyBalance(bankAccount.getBankFamilyAccount().getBalanceMoneyFamily());
            if (bankAccount.isFamilyLimitLock()){
                bankAccountDto.setLimitOnToday(bankAccount.getBankFamilyAccount().getLimitToday());
                bankAccountDto.setLimitOnDay(bankAccount.getBankFamilyAccount().isLimitLockTime());
                bankAccountDto.setLimitStatic(bankAccount.getBankFamilyAccount().getLimitStatic());
            }
            bankAccountDto.setAdminFamily(adminFamily.getUsername());
        }

        return bankAccountDto;
    }

    public static List<AbstractBankAccountDto> forAllUserBankInSystem(List<BankAccount> allUserBankSystem,
                                                                      ComponentForService componentForService){
        List<AbstractBankAccountDto> allUserBankSystemDtoList = new LinkedList<>();
        List<AbstractBankAccountDto> globalAdminListDto = new ArrayList<>();
        List<AbstractBankAccountDto> simpleAdminListDto = new ArrayList<>();
        List<AbstractBankAccountDto> simpleUser = new ArrayList<>();
        long idRole = 0 ;

        for (BankAccount bankAccount : allUserBankSystem) {
            var abstractBankAccountDto = ImplementDto.impl(new BankAccountDto(), bankAccount, componentForService);
            for (Role role: bankAccount.getRoles()) idRole = role.getId();
            if (idRole == 3)globalAdminListDto.add(abstractBankAccountDto);
            if (idRole == 2)simpleAdminListDto.add(abstractBankAccountDto);
            if (idRole == 1)simpleUser.add(abstractBankAccountDto);
        }

        allUserBankSystemDtoList.addAll(globalAdminListDto);
        allUserBankSystemDtoList.addAll(simpleAdminListDto);
        allUserBankSystemDtoList.addAll(simpleUser);

        return allUserBankSystemDtoList;
    }

    public static AbstractBankAccountDto infoAnyAccount(BankAccount bankAccount, ComponentForService componentForService){
        var bankAccountDto = new BankAccountDto();
        return ImplementDto.impl(bankAccountDto,bankAccount, componentForService);
    }

    public static AbstractBankAccountDto infoAnyFamilyAccountDto(BankFamilyAccount bankFamilyAccount, ComponentForService componentForService)
    {
        var familyAccountDto = new BankFamilyAccountDto();
        familyAccountDto.setId(bankFamilyAccount.getId());
        familyAccountDto.setFamilyBalance(bankFamilyAccount.getBalanceMoneyFamily());
        familyAccountDto.setLimitStatic(bankFamilyAccount.getLimitStatic());
        familyAccountDto.setLimitOnToday(bankFamilyAccount.getLimitToday());
        familyAccountDto.setLimitOnDay(bankFamilyAccount.isLimitLockTime());
        familyAccountDto.setLockAccount(bankFamilyAccount.isLockAccount());
        familyAccountDto.setUsernameAdmin(componentForService
                        .searchBankAccountByID(bankFamilyAccount.getBankAdminFamilyAccountId())
                        .getUsername());
        return familyAccountDto;
    }

    public static List<AbstractBankAccountDto> listFamilyUsers(List<BankAccount> userList, BankAccount adminFamily,
                                                               ComponentForService componentForService) {
        List<AbstractBankAccountDto> listFamilyUsers = new ArrayList<>();
        for (BankAccount bankAccount:userList) {
            var userDto = new BankAccountDto();
            var result = ImplementDto.impl(userDto, bankAccount, componentForService);
            if (bankAccount.getId().equals(adminFamily.getId())) listFamilyUsers.add(0,result);
            else listFamilyUsers.add(result);
        }
        return listFamilyUsers;
    }

}