package com.bank.project.repository;

import com.bank.project.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    BankAccount findByUsername(String name);
    Optional<BankAccount> findById(Long id);

}