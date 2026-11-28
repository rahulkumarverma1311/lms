package com.micro.message.functions;

import com.micro.message.dto.AccountsMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class MessageFunctions {

    private static final Logger log = LoggerFactory.getLogger(MessageFunctions.class);

    @Bean
    public Function<AccountsMessageDto,AccountsMessageDto>email(){

        return accountsMessageDto -> {
            log.info("sending email with account details:- "+ accountsMessageDto.toString());
            return accountsMessageDto;
        };
    }



    @Bean
    public Function<AccountsMessageDto,Long>sms(){

        return accountsMessageDto -> {
            log.info("sending sms with account details:- "+ accountsMessageDto.toString());
            return accountsMessageDto.accountNumber();
        };
    }
}
