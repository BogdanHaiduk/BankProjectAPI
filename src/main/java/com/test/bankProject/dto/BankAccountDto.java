package com.test.bankProject.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.test.bankProject.entity.BankAccount;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankAccountDto extends AbstractBankAccountDto {

    private Long familyId;
    private boolean accessToTheTotalLimit;
    private String username;
    private String role;
    private String adminFamily;

    public static List<AbstractBankAccountDto> listUsers(List<BankAccount> userList, BankAccount adminFamily) {
        List<AbstractBankAccountDto> userDtoList = new ArrayList<>();
        for (BankAccount bankAccount:userList) {
            var userDto = new BankAccountDto();
            var result = userDto.impl(userDto, bankAccount, adminFamily);
            if (bankAccount.getId().equals(adminFamily.getId()))userDtoList.add(0,result);
            else userDtoList.add(result);
        }
        return userDtoList;
    }

    public static AbstractBankAccountDto infoAnyAccount(BankAccount bankAccount, BankAccount adminFamily){
        var userDto = new BankAccountDto();
        return userDto.impl(userDto,bankAccount,adminFamily);
    }

    public AbstractBankAccountDto impl(BankAccountDto userDto, BankAccount bankAccount, BankAccount adminFamily){

        userDto.setId(bankAccount.getId());
        userDto.setUsername(bankAccount.getUsername());
        userDto.setRole(bankAccount.getRoles().toString());
        userDto.setLimitStatic(bankAccount.getLimitStatic());
        userDto.setLimitOnDay(bankAccount.isLimitLockTime());
        userDto.setLimitOnToday(bankAccount.getLimitToday());
        userDto.setLockAccount(bankAccount.isLockAccount());

        if (bankAccount.getBankFamilyAccount()!=null && adminFamily!=null) {
            userDto.setFamilyId(bankAccount.getBankFamilyAccount().getId());
            userDto.setAccessToTheTotalLimit(bankAccount.isFamilyLimitLock());
            userDto.setFamilyBalance(bankAccount.getBankFamilyAccount().getBalanceMoneyFamily());
            if (bankAccount.isFamilyLimitLock()){
                userDto.setLimitOnToday(bankAccount.getBankFamilyAccount().getLimitToday());
                userDto.setLimitOnDay(bankAccount.getBankFamilyAccount().isLimitLockTime());
                userDto.setLimitStatic(bankAccount.getBankFamilyAccount().getLimitStatic());
            }
            userDto.setAdminFamily(adminFamily.getUsername());
        }

        return userDto;
    }

}