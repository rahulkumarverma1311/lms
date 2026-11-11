package com.micro.accounts.service.client;

import com.micro.accounts.dto.CardsDto;
import org.springframework.cloud.client.circuitbreaker.httpservice.HttpServiceFallback;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
@HttpServiceFallback(value = CardsFallBack.class,group = "cards")
public class CardsFallBack {

    public ResponseEntity<CardsDto> fetchCardDetails(String correlationId,String mobileNumber){
        return null;
    }
}
