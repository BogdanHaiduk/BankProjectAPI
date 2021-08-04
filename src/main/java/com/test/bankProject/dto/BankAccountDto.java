package com.test.bankProject.dto;

import lombok.Data;

@Data
public class BankAccountDto extends AbstractBankAccountDto {

    private Long familyId;
    private boolean accessToTheTotalLimit;
    private String username;
    private String role;
    private String adminFamily;

}