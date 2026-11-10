package com.micro.accounts.service;

import com.micro.accounts.dto.CustomerDto;

public interface AccountService {
    void createAccount(CustomerDto customerDto);
    CustomerDto fetchAccount(String mobileNumber);
    Boolean updateAccount(CustomerDto customerDto);
    Boolean deleteAccount(String mobileNumber);

}
