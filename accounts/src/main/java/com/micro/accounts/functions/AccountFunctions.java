package com.micro.accounts.functions;

import com.micro.accounts.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;


@Configuration
public class AccountFunctions {

    private static final Logger log = LoggerFactory.getLogger(AccountFunctions.class);


    @Bean
    public Consumer<Long>updateCommunication(AccountService accountService){
        return accountNumber -> {
            log.info("updating communication status for account number:- "+ accountNumber.toString());
            accountService.updateCommunicationStatus(accountNumber);
        };
    }
}
