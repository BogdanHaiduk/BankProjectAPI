package com.test.bankProject.rest;

import com.test.bankProject.entity.BankAccount;
import com.test.bankProject.entity.BankFamilyAccount;
import com.test.bankProject.entity.BaseEntity;
import com.test.bankProject.service.ComponentForService;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class ThredLimit extends Thread {

    private final BaseEntity baseEntity;

    private final ComponentForService componentForService;

    public ThredLimit(BaseEntity bankAccount, ComponentForService componentForService) {
        this.baseEntity = bankAccount;
        this.componentForService = componentForService;
    }

    @Override
    public void run() {
        long sleepThread = 300000L;
        try {
            Date nowDate = new Date();
            long lastLockTime = baseEntity.getDateForLimit().getTime();
            long updateLockTime = lastLockTime + 300000;
            if (baseEntity.isLimitLockTime() && updateLockTime <= nowDate.getTime()) {
                baseEntity.setLimitToday(baseEntity.getLimitStatic());
                baseEntity.setDateForLimit(nowDate);
                save();
            } else {
                sleepThread = updateLockTime - nowDate.getTime();
                log.info("Thread will sleep on " + sleepThread / 3600000 + " hours");
            }
            log.info("demon for check bankAccountLockTime is working now");
            //Проверка каждые 5 минут
            Thread.sleep(sleepThread);
            run();
        }catch (Exception ex){ log.error(ex+""); }
    }

    private void save (){
        if(baseEntity.getClass().equals(BankAccount.class)) componentForService.saveBankAccount((BankAccount) baseEntity);
        if(baseEntity.getClass().equals(BankFamilyAccount.class)) componentForService.saveBankFamilyAccount((BankFamilyAccount) baseEntity);
        log.info("Successful update date for limit");
    }


}