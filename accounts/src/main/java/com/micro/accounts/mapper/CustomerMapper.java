package com.micro.accounts.mapper;

import com.micro.accounts.dto.CustomerDetailsDto;
import com.micro.accounts.dto.CustomerDto;
import com.micro.accounts.entity.Customer;

public class CustomerMapper {
    public static CustomerDto mapToCustomerDto(Customer customer,CustomerDto customerDto){
        customerDto.setEmail(customer.getEmail());
        customerDto.setName(customer.getName());
        customerDto.setMobileNumber(customer.getMobileNumber());
        return customerDto;
    }
    public static CustomerDetailsDto mapToCustomerDetailsDto(Customer customer, CustomerDetailsDto customerDetailsDto){
        customerDetailsDto.setEmail(customer.getEmail());
        customerDetailsDto.setName(customer.getName());
        customerDetailsDto.setMobileNumber(customer.getMobileNumber());
        return customerDetailsDto;
    }
    public static Customer mapToCustomer(CustomerDto customerDto,Customer customer){
            customer.setEmail(customerDto.getEmail());
            customer.setName(customerDto.getName());
            customer.setMobileNumber(customerDto.getMobileNumber());
            return customer;
    }
}
