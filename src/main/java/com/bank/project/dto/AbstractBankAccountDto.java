package com.bank.project.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractBankAccountDto {

private Long id;
private boolean lockAccount;
private BigDecimal familyBalance;
private BigDecimal limitStatic;
private BigDecimal limitOnToday;
private boolean limitOnDay;

}