package com.test.bankProject.security;


import com.test.bankProject.entity.BankAccount;
import com.test.bankProject.security.jwt.JwtUser;
import com.test.bankProject.security.jwt.JwtUserFactory;
import com.test.bankProject.service.ComponentForService;
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
