package com.test.bankProject.service;

import com.test.bankProject.dto.AbstractBankAccountDto;
import com.test.bankProject.dto.ImplementDto;
import com.test.bankProject.entity.BankAccount;
import com.test.bankProject.entity.BankFamilyAccount;
import com.test.bankProject.dto.RequestDto;
import com.test.bankProject.entity.Role;
import com.test.bankProject.rest.ThredLimit;
import com.test.bankProject.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ServiceUser {

    private final ComponentForService componentForService;
    private Map<Object,Object> result ;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public ServiceUser(ComponentForService componentForService) {
        this.componentForService = componentForService;
    }

    public void accountRegistration (RequestDto requestDto){
        String password = passwordEncoder.encode(requestDto.getPassword());
        List<Role> roles = new ArrayList<>();
        roles.add(componentForService.getRoleRepository().getById(1L));

        BankAccount bankAccount = new BankAccount(requestDto.getUsername(),password,roles);
        bankAccount.setBankFamilyAccount(null);
        componentForService.saveBankAccount(bankAccount);
    }

    public ResponseEntity<Map<Object,Object>> forLogin(RequestDto requestDto, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        try {
            String username = requestDto.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
            BankAccount user = componentForService.getBankAccountRepository().findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("User with username: " + username + " not found");
            }
            String jwtToken = jwtTokenProvider.createToken(username, user.getRoles());

            Map<Object, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("token", jwtToken);

            if(user.getDateForLimit()!=null) new ThredLimit(user,componentForService).start();
            if(user.getBankFamilyAccount()!=null
                    && user.getBankFamilyAccount().isLimitLockTime()
                    && user.getBankFamilyAccount().getDateForLimit()!=null)
                new ThredLimit(user.getBankFamilyAccount(),componentForService).start();

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }


    public AbstractBankAccountDto myAccount(String token) {
        BankAccount bankAccount = componentForService.searchBankAccountByToken(token);
        BankAccount adminFamily = null;
        if(bankAccount.getBankFamilyAccount()!=null) adminFamily = componentForService.searchBankAccountByID(bankAccount.getBankFamilyAccount().getBankAdminFamilyAccountId());
        return ImplementDto.infoAnyAccount(bankAccount, componentForService);
    }

    public Map<Object,Object> cashWithdrawal(String token, RequestDto requestDto) {
        BankAccount bankAccount = componentForService.searchBankAccountByToken(token);
        BigDecimal cashWithdrawal = requestDto.getWithdrawalOrReplenishment();
        LogicMoney logicMoney = new LogicMoney(bankAccount,cashWithdrawal, componentForService);
        logicMoney.startLogic();
        result = new LinkedHashMap<>();
        result.put("IdFamily", bankAccount.getBankFamilyAccount().getId());
        result.put("CashWithdrawal", requestDto.getWithdrawalOrReplenishment());
        result.put("FamilyAccountBalance", bankAccount.getBankFamilyAccount().getBalanceMoneyFamily());
        result.put("OperationSuccessful", logicMoney.isResultLogicLimitMoney());
        if(bankAccount.isLimitLockTime()&&!bankAccount.isFamilyLimitLock())result.put("LimitOnToday", bankAccount.getLimitToday());
        if(!bankAccount.isLimitLockTime()&&!bankAccount.isFamilyLimitLock())result.put("PersinalLimit", bankAccount.getLimitToday());
        if(bankAccount.isFamilyLimitLock()&&!bankAccount.isLimitLockTime()) result.put("LimitFamily", bankAccount.getBankFamilyAccount().getLimitToday());

        return result;
    }

    public Map<Object, Object> accountReplenishment(String token, RequestDto requestDto) {
        BankFamilyAccount bankFamilyAccount = componentForService.searchBankFamilyAccountByID(requestDto.getIdBankFamilyAccount()) ;
        BankAccount bankAccount = componentForService.searchBankAccountByToken(token);

        BigDecimal balance = bankFamilyAccount.getBalanceMoneyFamily();
        BigDecimal cashReplenishment = requestDto.getWithdrawalOrReplenishment();
        if(cashReplenishment.signum()>0) {
            bankFamilyAccount.setBalanceMoneyFamily(balance.add(cashReplenishment));
            componentForService.getBankAccountFamilyRepository().save(bankFamilyAccount);
        }
        result = new LinkedHashMap<>();
        result.put("accountReplenishment", requestDto.getWithdrawalOrReplenishment());
        result.put("FamilyAccountBalance", bankFamilyAccount.getBalanceMoneyFamily());
        result.put("MoneyReplenisherPerson", bankAccount.getUsername());
        return result;
    }

}