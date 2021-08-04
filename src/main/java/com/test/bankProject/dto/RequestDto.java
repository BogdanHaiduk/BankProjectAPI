package com.test.bankProject.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequestDto {
    private Long id;
    private Long idBankFamilyAccount;
    private String username;
    private String password;
    private BigDecimal withdrawalOrReplenishment;
    private BigDecimal limitMoney;
}
