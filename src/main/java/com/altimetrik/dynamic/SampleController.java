package com.altimetrik.dynamic;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/alti-bank")
@RequiredArgsConstructor
public class SampleController {

    private final UsefulService usefulService;
    private final ValidationService validationService;

    @GetMapping("/clients")
    public String processClient(@RequestHeader String authorization) {
        validationService.validate(authorization);
        return usefulService.doSomethingUseful("client");
    }

    @GetMapping("/accounts")
    public String processAccount(@RequestHeader String authorization) {
        validationService.validate(authorization);
        return usefulService.doSomethingUseful("account");
    }

    @GetMapping("/loans")
    public String processLoan(@RequestHeader String authorization) {
        validationService.validate(authorization);
        return usefulService.doSomethingUseful("loan");
    }

    @GetMapping("/beneficiaries")
    public String processBeneficiaries(@RequestHeader String authorization) {
        validationService.validate(authorization);
        return usefulService.doSomethingUseful("beneficiary");
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurityException(SecurityException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler({DateTimeParseException.class, IllegalArgumentException.class})
    public ResponseEntity<String> handleSecurityException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
