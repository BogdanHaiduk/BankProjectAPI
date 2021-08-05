package com.bank.project.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "bank_account")
@Data
public class BankAccount extends BaseEntity {

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "family_limit_lock")
    private boolean familyLimitLock;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "bank_account_roles",
            joinColumns = {@JoinColumn(name = "bank_account_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<Role> roles;

    @ManyToOne (optional=true, cascade=CascadeType.ALL)
    @JoinColumn (name="id_family_bank_account", nullable = true)
    private BankFamilyAccount bankFamilyAccount;


    public BankAccount(String username, String password, List<Role> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public BankAccount() {
    }


}