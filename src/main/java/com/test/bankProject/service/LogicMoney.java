package com.test.bankProject.service;

import com.test.bankProject.entity.BankAccount;
import java.math.BigDecimal;

public class LogicMoney {

    private final BankAccount bankAccount;
    private final BigDecimal cashWithdrawal;
    private boolean resultLogicLimitMoney = false;
    private final ComponentForService componentForService;

    public LogicMoney(BankAccount bankAccount, BigDecimal cashWithdrawal,
                      ComponentForService componentForService) {
        this.bankAccount = bankAccount;
        this.cashWithdrawal = cashWithdrawal;
        this.componentForService = componentForService;
    }

    public boolean isResultLogicLimitMoney() {
        return resultLogicLimitMoney;
    }

    public void startLogic(){
        limitMoney(bankAccount,cashWithdrawal);
    }

    private void limitMoney(BankAccount bankAccount, BigDecimal cashWithdrawal){

        BigDecimal limit;
        BigDecimal money = bankAccount.getBankFamilyAccount().getBalanceMoneyFamily();

        if(bankAccount.getLimitToday()!=null&&
                bankAccount.getLimitToday().signum()>0&&
                !bankAccount.isFamilyLimitLock())
        {
            limit = bankAccount.getLimitToday();
            if(logicLimitMoney(money, limit, cashWithdrawal)){
                bankAccount.getBankFamilyAccount().
                        setBalanceMoneyFamily(money.subtract(cashWithdrawal));
                bankAccount.setLimitToday(limit.subtract(cashWithdrawal));
                componentForService.getBankAccountRepository().save(bankAccount);
            }
        }


        if(bankAccount.getBankFamilyAccount().getLimitToday()!=null&&
                bankAccount.getBankFamilyAccount().getLimitToday().signum()>0&&
                bankAccount.isFamilyLimitLock())
        {
            limit = bankAccount.getBankFamilyAccount().getLimitToday();
            if(logicLimitMoney( money, limit, cashWithdrawal)) {
                bankAccount.getBankFamilyAccount().
                        setBalanceMoneyFamily(money.subtract(cashWithdrawal));
                bankAccount.getBankFamilyAccount().
                        setLimitToday(limit.subtract(cashWithdrawal));
                componentForService.getBankAccountRepository().save(bankAccount);
            }
        }
    }

    private boolean logicLimitMoney( BigDecimal money, BigDecimal limit, BigDecimal cashWithdrawal){

        if(limit!=null&&money!=null&&
                limit.compareTo(cashWithdrawal)>=0&&
                money.compareTo(cashWithdrawal)>=0)
        {
            resultLogicLimitMoney = true;
        }

        return resultLogicLimitMoney;
    }
}