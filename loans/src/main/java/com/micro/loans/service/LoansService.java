package com.micro.loans.service;

import com.micro.loans.dtos.LoansDto;

public interface LoansService {
    // customer mobile number
    void createLoan(String mobileNumber);
    LoansDto fetchLoan(String mobileNumber);
    boolean updateLoan(LoansDto loansDto);
    boolean deleteLoan(String mobileNumber);
}