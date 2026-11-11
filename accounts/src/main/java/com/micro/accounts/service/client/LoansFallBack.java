package com.micro.accounts.service.client;

import com.micro.accounts.dto.LoansDto;
import org.springframework.cloud.client.circuitbreaker.httpservice.HttpServiceFallback;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
@HttpServiceFallback(value = LoansFallBack.class, group = "loans")
public class LoansFallBack {


    public ResponseEntity<LoansDto> fetchLoanDetails(String correlationId,String mobileNumber){
        return null;
    }
}
