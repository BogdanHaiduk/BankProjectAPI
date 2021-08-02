package com.test.bankProject.rest;

import com.test.bankProject.entity.BankAccount;
import com.test.bankProject.service.ComponentForService;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class ThredLimit extends Thread {

    private final BankAccount bankAccount;

    private final ComponentForService componentForService;

    public ThredLimit(BankAccount bankAccount, ComponentForService componentForService) {
        this.bankAccount = bankAccount;
        this.componentForService = componentForService;
    }

    @Override
    public void run() {
        try{
            boolean bankAccountLockTime = false;
            boolean bankFamilyAccountLockTime = false;
            long sleepThread = 300000L;
            if (bankAccount.isLimitLockTime()) bankAccountLockTime = true;
            if (bankAccount.getBankFamilyAccount().isLimitLockTime()) bankFamilyAccountLockTime = true;
            while (bankAccountLockTime) {
                Date nowDate = new Date();
                long lastLockTime = bankAccount.getDateForLimit().getTime();
                long updateLockTime = lastLockTime + 86400000;
                if (bankAccount.isLimitLockTime() &&
                        updateLockTime <= nowDate.getTime()) {
                    bankAccount.setLimitToday(bankAccount.getLimitStatic());
                    bankAccount.setDateForLimit(nowDate);
                    componentForService.getBankAccountRepository().save(bankAccount);
                    log.info("Successful update date for limit");
                } else {
                    sleepThread = updateLockTime - nowDate.getTime();
                    log.info("Thread will sleep on " + sleepThread / 3600000 + " hours");
                }
                log.info("demon for check bankAccountLockTime is working now");
                //Проверка каждые 5 минут
                Thread.sleep(sleepThread);
            }
            while (bankFamilyAccountLockTime) {
                Date nowDate = new Date();
                long lastLockTime = bankAccount.getBankFamilyAccount().getDateForLimit().getTime();
                long updateLockTime = lastLockTime + 86400000;
                if (bankAccount.getBankFamilyAccount().isLimitLockTime()&&
                        updateLockTime <= nowDate.getTime())
                    {
//                        bankAccount.getBankFamilyAccount().setLimitMoneyFamily(bankAccount.getBankFamilyAccount().getStaticLimit());
                        bankAccount.getBankFamilyAccount().setDateForLimit(nowDate);
                        componentForService.saveBankFamilyAccount(bankAccount.getBankFamilyAccount());
                        log.info("Successful update date for limit");
                    }else {
                        sleepThread = updateLockTime - nowDate.getTime();
                        log.info("Thread will sleep on " + sleepThread/3600000 + " hours" );
                }
                log.info("demon for check bankFamilyAccountLockTime is working now");
                //Проверка каждые 5 минут
                Thread.sleep(sleepThread);
            }
        }
        catch(Exception exception){
            log.error("Thread has been interrupted");
        }
    }
}