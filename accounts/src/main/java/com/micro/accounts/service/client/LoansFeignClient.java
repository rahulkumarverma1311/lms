package com.micro.accounts.service.client;

import com.micro.accounts.dto.LoansDto;


import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/api")
public interface LoansFeignClient {
    @GetExchange(value = "/fetch",accept = "application/json")
    ResponseEntity<LoansDto> fetchLoanDetails(@RequestHeader("deerav-correlation-id") String correlationId,@RequestParam String mobileNumber);
}
