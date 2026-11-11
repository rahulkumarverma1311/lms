package com.micro.accounts.controller;

import com.micro.accounts.constants.AccountsConstants;
import com.micro.accounts.dto.*;
import com.micro.accounts.service.AccountService;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeoutException;

@Tag(
        name = "CRUD Operation For Accounts Service of Verma Bank",
        description = "CRUD operation for Account Details"
)
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class AccountsController {


    @Autowired
    private  AccountService accountService;


    @Value("${build.version}")
    private  String buildVersion;

    @Autowired
    private AccountsContactInfoDto accountsContactInfoDto;

    private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);
    public AccountsController(AccountService accountService){
        this.accountService = accountService;
    }

    @Operation(
            summary = "Create Account REST API",
            description = "Creating Customer and Account in Verma Bank"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status CREATED"
    )
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody CustomerDto customerDto) {
        this.accountService.createAccount(customerDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(AccountsConstants.STATUS_201, AccountsConstants.MESSAGE_201));
    }

    @Operation(
            summary = "Fetch Account REST API",
            description = "Creating Fetch Customer and Account Details in Verma Bank"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status OK"
    )
    @GetMapping("/fetch")
    public ResponseEntity<CustomerDto> fetchAccountDetails(@RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile Number Must Have 10 Digit") String mobileNumber) {
        CustomerDto customerDto = this.accountService.fetchAccount(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(customerDto);
    }

    @Operation(
            summary = "Update Account REST API",
            description = "Creating Update Customer and Account details in Verma Bank"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Http Status OK"
            ), @ApiResponse(
            responseCode = "417",
            description = "EXPECTATION FAILED"
                ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Http Status INTERNAL SERVER ERROR",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )})
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateAccountDetails(@Valid @RequestBody CustomerDto customerDto) {
        Boolean b = this.accountService.updateAccount(customerDto);
        if (b) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        } else {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_UPDATE));
        }
    }

    @Operation(
            summary = "Create Delete Account REST API",
            description = "Creating Delete Customer and Account in Verma Bank"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Http Status OK"
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "EXPECTATION FAILED"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Http Status INTERNAL SERVER ERROR"
            )})
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteAccountDetails(@RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile Number Must Have 10 Digit") String mobileNumber) {
        boolean b = this.accountService.deleteAccount(mobileNumber);
        if (b) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        } else {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_DELETE));
        }
    }


    @Operation(
            summary = "Get Build information",
            description = "Get Build information that is deployed into accounts microservice"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @Retry(name = "getBuildInfo",fallbackMethod = "getBuildInfoFallBack")
    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildVersion() {
        return ResponseEntity.status(HttpStatus.OK).body(buildVersion);
    }

    public ResponseEntity<String>getBuildInfoFallBack(Throwable throwable){
        logger.debug("getBuildInfoFallBack() method called");

        return ResponseEntity.ok("0.9");
    }

    @GetMapping("/contact-info")
    public ResponseEntity<AccountsContactInfoDto> contactInfo(){
        return ResponseEntity.status(HttpStatus.OK).body(accountsContactInfoDto);
    }
}
