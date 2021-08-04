package com.test.bankProject.rest;

import com.test.bankProject.dto.RequestDto;
import com.test.bankProject.dto.AbstractBankAccountDto;
import com.test.bankProject.service.ServiceGlobalAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/v1/globalAdmin/")
public class GlobalAdminRestController {

    private final ServiceGlobalAdmin serviceGlobalAdmin;

    @Autowired
    public GlobalAdminRestController(ServiceGlobalAdmin serviceGlobalAdmin) {
        this.serviceGlobalAdmin = serviceGlobalAdmin;
    }

    @GetMapping("allUserInBankSystem")
    public ResponseEntity<List<AbstractBankAccountDto>> allUserInBankSystem(@RequestHeader (name = "Authorization") String token){
        return new ResponseEntity<>(serviceGlobalAdmin.allUserInBankSystem(token), HttpStatus.OK);
    }

    @PostMapping("static/limitForAll")
    public ResponseEntity<List<AbstractBankAccountDto>>  staticLimitForAll(@RequestHeader(name = "Authorization") String token,
                                                                           @RequestBody RequestDto requestDto) {
        serviceGlobalAdmin.limitForAll(token, requestDto, false);
        return new ResponseEntity<>(serviceGlobalAdmin.allUserInBankSystem(token), HttpStatus.OK);
    }

    @PostMapping("onDay/limitForAll")
    public ResponseEntity<List<AbstractBankAccountDto>>  onDayLimitForAll(@RequestHeader(name = "Authorization") String token,
                                                                          @RequestBody RequestDto requestDto) {
        serviceGlobalAdmin.limitForAll(token, requestDto, true);
        return new ResponseEntity<>(serviceGlobalAdmin.allUserInBankSystem(token), HttpStatus.OK);
    }

    @PostMapping("static/limitForSelectedFamily")
    public ResponseEntity<AbstractBankAccountDto> staticLimitForSelectedFamily (@RequestHeader (name = "Authorization") String token,
                                                                                @RequestBody RequestDto requestDto)
    {
        return new ResponseEntity<>(serviceGlobalAdmin.limitForSelectedFamily(token, requestDto, false),HttpStatus.OK);
    }

    @PostMapping("onDay/limitForSelectedFamily")
    public ResponseEntity<AbstractBankAccountDto> onDayLimitForSelectedFamily (@RequestHeader (name = "Authorization") String token,
                                                                               @RequestBody RequestDto requestDto)
    {
        return new ResponseEntity<>(serviceGlobalAdmin.limitForSelectedFamily(token, requestDto, true),HttpStatus.OK);
    }

    @PostMapping("static/limitForSelectedPerson")
    public ResponseEntity<AbstractBankAccountDto> staticLimitForSelectedPerson (@RequestHeader (name = "Authorization") String token,
                                                                                @RequestBody RequestDto requestDto)
    {
        return new ResponseEntity<>(serviceGlobalAdmin.limitForSelectedPerson(token, requestDto, false),HttpStatus.OK);
    }

    @PostMapping("onDay/limitForSelectedPerson")
    public ResponseEntity<AbstractBankAccountDto> onDayLimitForSelectedPerson (@RequestHeader (name = "Authorization") String token,
                                                                               @RequestBody RequestDto requestDto)
    {
        return new ResponseEntity<>(serviceGlobalAdmin.limitForSelectedPerson(token, requestDto, true), HttpStatus.OK);
    }

    @PostMapping(value = "liftingOfPersonalRestrictions/ForOneFamily")
    public void liftingOfPersonalRestrictions(@RequestHeader(name = "Authorization") String token,
                                                        @RequestBody RequestDto requestDto){
        serviceGlobalAdmin.liftingOfPersonalRestrictionsForFamily(token, requestDto);
    }

    @PostMapping(value = "liftingOfPersonalRestrictions/ForOnePerson")
    public void liftingOfPersonalRestrictionsForPerson(@RequestHeader(name = "Authorization") String token,
                                                       @RequestBody RequestDto requestDto){
        serviceGlobalAdmin.liftingOfPersonalRestrictionsForPerson(token, requestDto);
    }

    @PostMapping(value = "liftingOfPersonalRestrictions/ForAll")
    public void liftingOfPersonalRestrictionsForAll(@RequestHeader(name = "Authorization") String token){
        serviceGlobalAdmin.liftingOfPersonalRestrictionsForAll(token);
    }

}