package com.test.bankProject.rest;

import com.test.bankProject.entity.BankAccount;
import com.test.bankProject.dto.AuthenticationRequestDto;
import com.test.bankProject.security.jwt.JwtTokenProvider;
import com.test.bankProject.service.ServiceUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.test.bankProject.service.ComponentForService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/auth/")
public class AuthenticationRestControllerV1 {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ComponentForService componentForService;
    private final ServiceUser serviceUser;

    @Autowired
    public AuthenticationRestControllerV1(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                                          ComponentForService componentForService, ServiceUser serviceUser) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.componentForService = componentForService;
        this.serviceUser = serviceUser;
    }

    @PostMapping("accountRegistration")
    public String accountRegistration(@RequestBody AuthenticationRequestDto authenticationRequestDto)
    {
        try {
            serviceUser.accountRegistration(authenticationRequestDto);
            return  "Operation successful";
        }catch (Exception ex){
            return  "Operation not successful";
        }
    }

    @PostMapping("login")
    public ResponseEntity login(@RequestBody AuthenticationRequestDto requestDto)  {
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

            if(user.getDateForLimit()!=null||(user.getBankFamilyAccount()!=null&&
                    user.getBankFamilyAccount().isLimitLockTime()&&
                    user.getBankFamilyAccount().getDateForLimit()!=null))
                new ThredLimit(user,componentForService).start();

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}