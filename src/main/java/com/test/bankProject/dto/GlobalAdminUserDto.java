package com.test.bankProject.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.test.bankProject.entity.BankAccount;
import com.test.bankProject.entity.Role;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalAdminUserDto extends BankAccountDto {

    public static List<AbstractBankAccountDto> forAllUserBankInSystem(List<BankAccount> allUserBankSystem){
        List<AbstractBankAccountDto> allUserBankSystemDtoList = new LinkedList<>();
        List<AbstractBankAccountDto> globalAdminListDto = new ArrayList<>();
        List<AbstractBankAccountDto> simpleAdminListDto = new ArrayList<>();
        List<AbstractBankAccountDto> simpleUser = new ArrayList<>();

        for (BankAccount bankAccount : allUserBankSystem) {

            var userDto = new GlobalAdminUserDto();
            var abstractBankAccountDto = userDto.impl(userDto, bankAccount, null);

            long idRole = 0 ;
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

}
