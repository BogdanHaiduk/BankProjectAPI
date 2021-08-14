package com.bank.project;

import com.bank.project.dto.RequestDto;
import com.bank.project.entity.BankAccount;
import com.bank.project.entity.BankFamilyAccount;
import com.bank.project.security.jwt.JwtTokenProvider;
import com.bank.project.service.ComponentForService;
import com.bank.project.service.LogicMoney;
import com.bank.project.service.ServiceUser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest
  public class LogicMoneyUnitTest {

    @MockBean
    private ComponentForService componentForService;
    @Autowired
    private ServiceUser serviceUser;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public BankFamilyAccount initBankFamilyAccount(){
        BankFamilyAccount bankFamilyAccount = new BankFamilyAccount();

        bankFamilyAccount.setBalanceMoneyFamily(new BigDecimal("3000.00000001"));
        bankFamilyAccount.setLimitStatic(new BigDecimal(2384));
        bankFamilyAccount.setLimitLockTime(false);
        bankFamilyAccount.setLockAccount(false);

        return bankFamilyAccount;
    }

    @Test
    public void cashWithdrawal(){

        BankAccount bankAccount = new BankAccount();
        var bankFamilyAccount = initBankFamilyAccount();

        bankAccount.setFamilyLimitLock(true);
        bankAccount.setBankFamilyAccount(bankFamilyAccount);

        LogicMoney logicMoney = new LogicMoney(bankAccount, new BigDecimal(300), componentForService);
        logicMoney.startLogic();

        Assert.assertEquals(new BigDecimal("2700.00000001"),bankAccount.getBankFamilyAccount().getBalanceMoneyFamily());
        Mockito.verify(componentForService, Mockito.times(1)).saveBankAccount(bankAccount);

    }

    @Test
    public void accountReplenishment(){
        String jwtToken = jwtTokenProvider.createToken("TestUser", new ArrayList<>());
        RequestDto requestDto = new RequestDto();
        requestDto.setWithdrawalOrReplenishment(new BigDecimal("5400.00000001"));

        var bankFamilyAccount = initBankFamilyAccount();

        Mockito.when(componentForService.searchBankFamilyAccountByID(any()))
                .thenReturn(bankFamilyAccount);
        Mockito.when(componentForService.searchBankAccountByToken(any()))
                .thenReturn(new BankAccount());

        serviceUser.accountReplenishment(jwtToken,requestDto);

        Mockito.verify(componentForService, Mockito.times(1)).saveBankFamilyAccount(bankFamilyAccount);
        Assert.assertEquals(new BigDecimal("8400.00000002"), bankFamilyAccount.getBalanceMoneyFamily());
    }

}