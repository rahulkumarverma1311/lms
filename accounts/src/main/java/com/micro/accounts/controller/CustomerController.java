package com.micro.accounts.controller;

import com.micro.accounts.dto.CustomerDetailsDto;
import com.micro.accounts.service.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "CRUD Api For Customer of Verma Bank",
        description = "CRUD operation for Customer Details"
)
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated

public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService=customerService;
    }

    @GetMapping("/fetchCustomerDetails")
    public ResponseEntity<CustomerDetailsDto> fetchCustomerDetails(
            @RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile Number Must Have 10 Digit") String mobileNumber){
        logger.info("fetching customer details for customer mobile number :- {}",mobileNumber);
        CustomerDetailsDto customerDetailsDto = customerService.fetchCustomerDetails(mobileNumber);
        logger.info("customer details fetched successfully for mobile number :- {}",mobileNumber);
        return ResponseEntity.ok(customerDetailsDto);

    }

    @GetMapping("/email")
    public ResponseEntity<String> getCustomerEmail(@PathVariable String mobileNumber){
        return ResponseEntity.ok(customerService.getCustomerEmailByMobileNumber(mobileNumber));
    }
}
