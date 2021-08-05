package com.bank.project.service;


import com.bank.project.dto.RequestDto;
import com.bank.project.entity.BankFamilyAccount;
import com.bank.project.entity.BaseEntity;
import com.bank.project.repository.BankAccountFamilyRepository;
import com.bank.project.repository.BankAccountRepository;
import com.bank.project.repository.RoleRepository;
import com.bank.project.security.jwt.JwtTokenProvider;
import com.bank.project.entity.BankAccount;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Component
public class ComponentForService {

    private final BankAccountRepository bankAccountRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BankAccountFamilyRepository bankAccountFamilyRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public ComponentForService(BankAccountRepository bankAccountRepository, JwtTokenProvider jwtTokenProvider,
                               BankAccountFamilyRepository bankAccountFamilyRepository, RoleRepository roleRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.bankAccountFamilyRepository = bankAccountFamilyRepository;
        this.roleRepository = roleRepository;
    }

    public boolean checkRoleGlobalAdmin(BankAccount adminGlobal) {
        return (adminGlobal.getRoles().toString().equals("[ROLE_GLOBALADMIN]"));
    }

    @Transactional
    public void saveBankAccount(BankAccount bankAccount){
        bankAccountRepository.save(bankAccount);
    }

    @Transactional
    public void saveBankFamilyAccount(BankFamilyAccount bankFamilyAccount){
        bankAccountFamilyRepository.save(bankFamilyAccount);
    }

    public BankAccount searchBankAccountByToken (String token){
        return bankAccountRepository.findByUsername((jwtTokenProvider.getUsername(token.replace("Bearer_", ""))));
    }

    public BankFamilyAccount searchBankFamilyAccountByID(Long idBankFamilyAccount){
        return bankAccountFamilyRepository.getById(idBankFamilyAccount);
    }

    public BankAccount searchBankAccountByID(Long id){
        return bankAccountRepository.getById(id);
    }

    public BaseEntity logicStaticOrDayLimit(BaseEntity baseEntity, RequestDto requestDto, boolean onDay){
        if ((!baseEntity.isLockAccount())) {
            baseEntity.setLimitStatic(requestDto.getLimitMoney());
            if (!onDay) {
                baseEntity.setLimitLockTime(false);
                baseEntity.setDateForLimit(null);
            } else {
                baseEntity.setLimitLockTime(true);
                baseEntity.setDateForLimit(new Date());
            }
            return baseEntity;
        }
        return baseEntity;
    }

    public BaseEntity logicForLiftingOfRestrictions(BaseEntity baseEntity){
        if ((!baseEntity.isLockAccount())) {
            baseEntity.setLimitStatic(new BigDecimal(0));
            baseEntity.setLimitLockTime(false);
            baseEntity.setDateForLimit(null);
        }
        return baseEntity;
    }

}