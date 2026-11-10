package com.micro.cards.service;

import com.micro.cards.dtos.CardsDto;


public interface CardsService {
    void createCard(String mobileNumber);
    CardsDto fetchCard(String mobileNumber);
    boolean updateCard(CardsDto cardsDto);
    boolean deleteCard(String mobileNumber);
}
