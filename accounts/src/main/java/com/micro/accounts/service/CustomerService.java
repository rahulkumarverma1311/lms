package com.micro.accounts.service;

import com.micro.accounts.dto.CustomerDetailsDto;
import com.micro.accounts.dto.CustomerDto;

public interface CustomerService {
    CustomerDetailsDto fetchCustomerDetails(String mobileNumber,String correlationId);
}
