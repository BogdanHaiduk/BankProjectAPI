package com.bank.project.security;


import com.bank.project.security.jwt.JwtUser;
import com.bank.project.security.jwt.JwtUserFactory;
import com.bank.project.entity.BankAccount;
import com.bank.project.service.ComponentForService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    private final ComponentForService componentForService;

    @Autowired
    public JwtUserDetailsService(ComponentForService componentForService) {
        this.componentForService = componentForService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BankAccount user = componentForService.getBankAccountRepository().findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username: " + username + " not found");
        }

        JwtUser jwtUser = JwtUserFactory.create(user);
        return jwtUser;
    }
}
