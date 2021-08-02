package com.test.bankProject.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.test.bankProject.entity.BankFamilyAccount;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankFamilyAccountDto extends AbstractBankAccountDto {

    public static AbstractBankAccountDto anyBankFamilyAccountDto(BankFamilyAccount bankFamilyAccount)
    {
        var bankFamilyAccountDto = new BankFamilyAccountDto();
        bankFamilyAccountDto.setId(bankFamilyAccount.getId());
        bankFamilyAccountDto.setFamilyBalance(bankFamilyAccount.getBalanceMoneyFamily());
        bankFamilyAccountDto.setLimitStatic(bankFamilyAccount.getLimitStatic());
        bankFamilyAccountDto.setLimitOnToday(bankFamilyAccount.getLimitToday());
        bankFamilyAccountDto.setLimitOnDay(bankFamilyAccount.isLimitLockTime());
        bankFamilyAccountDto.setLockAccount(bankFamilyAccount.isLockAccount());
        return bankFamilyAccountDto;
    }

}