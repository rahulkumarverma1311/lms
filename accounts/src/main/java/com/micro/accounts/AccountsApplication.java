package com.micro.accounts;

import com.micro.accounts.dto.AccountsContactInfoDto;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
@EnableFeignClients
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@EnableConfigurationProperties(value = {AccountsContactInfoDto.class})
@OpenAPIDefinition(info = @Info(
        title = "Accounts Service REST API Documentation",
        description = "Verma Bank Accounts Microservice REST API Documentation",
        version = "v1",
        contact = @Contact(
                name = "Rahul Kumar Verma",
                email = "rahul.verma@gmail.com",
                url = "https://deerav.com"
        ), license = @License(
        name = "Apache 2.O",
        url = "https://deerav.com"
)

),externalDocs = @ExternalDocumentation(
		description = "Verma Bank Accounts Microservice REST API Documentation"
))
public class AccountsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountsApplication.class, args);
    }

}
