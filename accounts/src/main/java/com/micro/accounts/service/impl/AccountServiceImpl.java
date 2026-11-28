package com.micro.accounts.service.impl;

import com.micro.accounts.constants.AccountsConstants;
import com.micro.accounts.dto.AccountsDto;
import com.micro.accounts.dto.AccountsMessageDto;
import com.micro.accounts.dto.CustomerDto;
import com.micro.accounts.entity.Accounts;
import com.micro.accounts.entity.Customer;
import com.micro.accounts.exception.CustomerAlreadyExistException;
import com.micro.accounts.exception.ResourceNotFoundException;
import com.micro.accounts.mapper.AccountsMapper;
import com.micro.accounts.mapper.CustomerMapper;
import com.micro.accounts.repository.AccountsRepository;
import com.micro.accounts.repository.CustomerRepository;
import com.micro.accounts.service.AccountService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private MeterRegistry meterRegistry;
    private StreamBridge streamBridge;
    @Override
    public void createAccount(CustomerDto customerDto) {
        log.debug("creating account with mobile number , name , email :- {},{},{}",customerDto.getMobileNumber(),customerDto.getName(),customerDto.getEmail());
        Optional<Customer> existingCustomer = this.customerRepository.findByMobileNumber(customerDto.getMobileNumber());
        if (existingCustomer.isPresent())
            throw new CustomerAlreadyExistException("Customer is already register with this mobile number:" + customerDto.getMobileNumber());
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
//        customer.setCreated_at(LocalDateTime.now());
//        customer.setCreated_by("anyone");
        Customer savedCustomer = this.customerRepository.save(customer);
        log.debug("calling createNew Account method for mobile number:-{}",customerDto.getMobileNumber());
        Accounts newAccount = this.accountsRepository.save(createNewAccount(savedCustomer));
        log.debug("account created successfully  with mobile number , name , email :- {},{},{}",customerDto.getMobileNumber(),customerDto.getName(),customerDto.getEmail());
        meterRegistry.counter("accounts.operation","operation","created").increment();
        sendCommunication(newAccount,savedCustomer);
    }

    private Accounts createNewAccount(Customer customer) {
        Accounts newAccount = new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());
        Long accountNumber = 1000000000L + new Random().nextInt(900000000);
        newAccount.setAccountNumber(accountNumber);
        newAccount.setAccountType(AccountsConstants.SAVING);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
//        newAccount.setCreated_at(LocalDateTime.now());
//        newAccount.setCreated_by("anyone");
        log.debug("account created successfully from createNewAccount with mobile number , name , email :- {},{},{}",customer.getMobileNumber(),customer.getName(),customer.getEmail());
        return newAccount;
    }

    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        log.debug("fetching account details for mobile number:-{}",mobileNumber);
        Customer customer = this.customerRepository.findByMobileNumber(mobileNumber).orElseThrow(() -> new ResourceNotFoundException("Customer", "Mobile Number", mobileNumber));
        Accounts accounts = this.accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(() -> new ResourceNotFoundException("Account", "CustomerId", customer.getCustomerId().toString()));
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        AccountsDto accountsDto = AccountsMapper.mapToAccountDto(accounts, new AccountsDto());
        customerDto.setAccountsDto(accountsDto);
        log.debug("account details fetched successfully for mobile number:-{}",mobileNumber);
        return customerDto;
    }

    @Override
    public Boolean updateAccount(CustomerDto customerDto) {
        log.debug("updating account details for mobile number:-{}",customerDto.getMobileNumber());
        Boolean isUpdated = false;
        AccountsDto accountsDto = customerDto.getAccountsDto();
        if (accountsDto != null) {
            Accounts accounts = this.accountsRepository.findById(accountsDto.getAccountNumber()).orElseThrow(() -> new ResourceNotFoundException("Account", "Account Number", accountsDto.getAccountNumber().toString()));
            AccountsMapper.mapToAccounts(accountsDto, accounts);
            accounts = this.accountsRepository.save(accounts);
            Long customerId = accounts.getCustomerId();
            Customer customer = this.customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer", "Customer Id", customerId.toString()));
            CustomerMapper.mapToCustomer(customerDto, customer);
            this.customerRepository.save(customer);
            isUpdated = true;
        }
        meterRegistry.counter("accounts.operation","operation","updated").increment();
        return isUpdated;
    }

    @Override
    public Boolean deleteAccount(@RequestParam String mobileNumber) {
        Customer customer = this.customerRepository.findByMobileNumber(mobileNumber).orElseThrow(() -> new ResourceNotFoundException("Customer", "Mobile Number", mobileNumber));
        this.accountsRepository.deleteByCustomerId(customer.getCustomerId());
        this.customerRepository.deleteById(customer.getCustomerId());
        log.debug("deleting account details for mobile number:-{}",mobileNumber);
        meterRegistry.counter("accounts.operation","operation","deleted").increment();
        return true;
    }

    @Override
    public Boolean updateCommunicationStatus(Long accountNumber) {
        boolean isUpdated = false;
        if(accountNumber != null) {
            Accounts accounts = this.accountsRepository.findById(accountNumber).orElseThrow(() -> new ResourceNotFoundException("Account", "Account number", accountNumber.toString()));
            accounts.setCommunicationSw(true);
            accountsRepository.save(accounts);
            isUpdated = true;
        }
        return isUpdated;
    }


    private void sendCommunication(Accounts accounts,Customer customer){
        AccountsMessageDto accountsMessageDto = new AccountsMessageDto(accounts.getAccountNumber(),customer.getName(),customer.getEmail(),customer.getMobileNumber());
        log.info("sending message details:-{}",accountsMessageDto);
        boolean send = streamBridge.send("sendCommunication-out-0", accountsMessageDto);
        log.info("is message send successfully:-{}",send);
    }
}
