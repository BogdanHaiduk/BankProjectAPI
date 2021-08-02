package com.test.bankProject.rest;

import com.test.bankProject.dto.AuthenticationRequestDto;
import com.test.bankProject.dto.AbstractBankAccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.test.bankProject.service.ServiceUser;

import java.util.Map;


@RestController
@RequestMapping(value = "/api/v1/users")
public class UserRestControllerV1 {

    private final ServiceUser serviceUser;

    @Autowired
    public UserRestControllerV1(ServiceUser serviceUser) {
        this.serviceUser = serviceUser;
    }

    @GetMapping("/myAccount")
    public ResponseEntity<AbstractBankAccountDto> myAccount(@RequestHeader(name = "Authorization") String token) {
        return new ResponseEntity<>(serviceUser.myAccount(token), HttpStatus.OK);
    }

//выдача наличных
    @PostMapping("/cashWithdrawal")
    public ResponseEntity<Map<Object,Object>> cashWithdrawal(@RequestHeader(name = "Authorization")String token,
                                              @RequestBody AuthenticationRequestDto authenticationRequestDto)
    {
        return new ResponseEntity<>(serviceUser.cashWithdrawal(token, authenticationRequestDto),HttpStatus.OK);
    }

//пополнение наличных
    @PostMapping("/accountReplenishment")
    public ResponseEntity<Map<Object,Object>> accountReplenishment(@RequestHeader(name = "Authorization") String token,
                                               @RequestBody AuthenticationRequestDto authenticationRequestDto)
    {
        return new ResponseEntity<>(serviceUser.accountReplenishment(token, authenticationRequestDto), HttpStatus.OK);
    }
}