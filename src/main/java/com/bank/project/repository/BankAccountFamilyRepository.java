package com.bank.project.repository;


import com.bank.project.entity.BankFamilyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface BankAccountFamilyRepository extends JpaRepository<BankFamilyAccount, Long> {
    BankFamilyAccount findByBankAdminFamilyAccountId (Long id);
}
