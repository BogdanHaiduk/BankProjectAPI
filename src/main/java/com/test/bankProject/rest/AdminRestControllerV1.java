package com.test.bankProject.rest;

import com.test.bankProject.dto.AuthenticationRequestDto;
import com.test.bankProject.dto.AbstractBankAccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.test.bankProject.service.ServiceAdmin;

import java.util.List;


@RestController
@RequestMapping(value = "/api/v1/admin/")
public class AdminRestControllerV1 {

    private ServiceAdmin serviceAdmin;

    @Autowired
    public AdminRestControllerV1(ServiceAdmin serviceAdmin) {
        this.serviceAdmin = serviceAdmin;
    }

    @GetMapping (value = "allFamily")
    public ResponseEntity<List<AbstractBankAccountDto>> allFamily(@RequestHeader(name = "Authorization") String token){

        return new ResponseEntity<>(serviceAdmin.allFamily(token), HttpStatus.OK);

    }

    @GetMapping (value = "familyAccount")
    public ResponseEntity<AbstractBankAccountDto> familyAccount(@RequestHeader(name = "Authorization")String token){

        return new ResponseEntity<>(serviceAdmin.familyAccount(token), HttpStatus.OK);

    }

    @PostMapping(value = "createNewFamilyAccount")
    public void createNewFamilyAccount (@RequestHeader(name = "Authorization") String token){
        serviceAdmin.createNewFamilyAccount(token);
    }

    @PostMapping(value = "addNewPersonFamily")
    public String addNewPersonFamily(@RequestHeader(name = "Authorization") String token,
                                             @RequestBody AuthenticationRequestDto authenticationRequestDto){
        return serviceAdmin.addNewPersonFamily(token,authenticationRequestDto);
    }

//Лимиты:
    @PostMapping(value = "static/limitMoneyForAllFamily")
    public void limitMoneyForAllFamily (@RequestHeader (name = "Authorization") String token,
                                                  @RequestBody AuthenticationRequestDto authenticationRequestDto){
        serviceAdmin.limitMoneyForAllFamily(token,authenticationRequestDto, false);
    }

    @PostMapping(value = "limitOnDayForAllFamily")
    public void limitOnDayForAllFamily (@RequestHeader (name = "Authorization") String token,
                                        @RequestBody AuthenticationRequestDto authenticationRequestDto){
        serviceAdmin.limitMoneyForAllFamily(token,authenticationRequestDto, true);
    }

//Лимит без ограничений на время и его без автоматического обновления
    @PostMapping(value = "static/limitMoneyForOneMemberFamily")
    public ResponseEntity<AbstractBankAccountDto> limitMoneyForOneMemberFamily (@RequestHeader (name = "Authorization") String token,
                                                                                @RequestBody AuthenticationRequestDto authenticationRequestDto)
    {
        return new ResponseEntity<>(serviceAdmin.limitMoneyForOneMemberFamily(token, authenticationRequestDto, false),HttpStatus.OK);
    }

//Лимит с ограничением на время и с автоматическим обновлением
    @PostMapping(value = "limitOnDayForOneMemberFamily")
    public ResponseEntity<AbstractBankAccountDto> limitOnDayForOneMemberFamily(@RequestHeader(name = "Authorization") String token,
                                                                               @RequestBody AuthenticationRequestDto authenticationRequestDto)
    {
        return new ResponseEntity<>(serviceAdmin.limitMoneyForOneMemberFamily(token, authenticationRequestDto, true),HttpStatus.OK);
    }

//Снятие личных ограничений с пользователя и открыт доступ к общему лимиту денег для снятия всей семьи
    @PostMapping(value = "liftingOfPersonalRestrictions/ForOneMemberFamily")
    public ResponseEntity<AbstractBankAccountDto> liftingOfPersonalRestrictions(@RequestHeader(name = "Authorization") String token,
                                                                                @RequestBody AuthenticationRequestDto authenticationRequestDto){
        return new ResponseEntity<>(serviceAdmin.liftingOfPersonalRestrictions(token,authenticationRequestDto),
                HttpStatus.OK);
    }

}