package com.bank.project.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "limit_static")
    private BigDecimal limitStatic;

    @Column(name = "limit_today")
    private BigDecimal limitToday;

    @Column(name = "date_for_limit")
    private Date dateForLimit;

    @Column(name = "limit_lock_time")
    private boolean limitLockTime;

    @Column(name = "lock_account")
    private boolean lockAccount;

    public void setLimitStatic(BigDecimal limitStatic) {
        this.limitStatic = limitStatic;
        this.limitToday = limitStatic;
    }

}