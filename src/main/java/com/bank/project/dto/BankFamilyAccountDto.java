package com.bank.project.dto;

import lombok.Data;

@Data
public class BankFamilyAccountDto extends AbstractBankAccountDto {
    private String usernameAdmin;
}
