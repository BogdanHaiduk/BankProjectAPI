package com.test.bankProject.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public abstract class AbstractBankAccountDto {

private Long id;
private boolean lockAccount;
private BigDecimal familyBalance;
private BigDecimal limitStatic;
private BigDecimal limitOnToday;
private boolean limitOnDay;

}