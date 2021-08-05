package com.bank.project.rest;

import com.bank.project.dto.RequestDto;
import com.bank.project.security.jwt.JwtTokenProvider;
import com.bank.project.service.ServiceUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/auth/")
public class AuthenticationRestControllerV1 {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ServiceUser serviceUser;

    @Autowired
    public AuthenticationRestControllerV1(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                                          ServiceUser serviceUser) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.serviceUser = serviceUser;
    }

    @PostMapping("accountRegistration")
    public String accountRegistration(@RequestBody RequestDto requestDto)
    {
        try {
            serviceUser.accountRegistration(requestDto);
            return  "Operation successful";
        }catch (Exception ex){
            return  "Operation not successful";
        }
    }

    @PostMapping("login")
    public ResponseEntity<Map<Object,Object>> login(@RequestBody RequestDto requestDto)  {
        return serviceUser.forLogin(requestDto, authenticationManager, jwtTokenProvider);
    }

}