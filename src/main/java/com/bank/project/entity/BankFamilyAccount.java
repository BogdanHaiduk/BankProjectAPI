package com.bank.project.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;


@Entity
@Table(name = "bank_family_account")
@Data
public class BankFamilyAccount extends BaseEntity {

    @Column(name = "bank_admin_family_account_id")
    private Long bankAdminFamilyAccountId;

    @Column(name = "balance_money_family")
    private BigDecimal balanceMoneyFamily;

    @OneToMany (mappedBy="bankFamilyAccount", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private List<BankAccount> bankAccounts;

    public BankFamilyAccount(Long bankAdminFamilyAccountId, BigDecimal balanceMoneyFamily) {
        this.bankAdminFamilyAccountId = bankAdminFamilyAccountId;
        this.balanceMoneyFamily = balanceMoneyFamily;
    }

    public BankFamilyAccount (){ }

}