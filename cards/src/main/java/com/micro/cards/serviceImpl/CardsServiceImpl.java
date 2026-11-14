package com.micro.cards.serviceImpl;

import com.micro.cards.constants.CardsConstants;
import com.micro.cards.dtos.CardsDto;
import com.micro.cards.entity.Cards;
import com.micro.cards.exceptions.CardAlreadyExistException;
import com.micro.cards.exceptions.ResourceNotFoundException;
import com.micro.cards.mepper.CardsMapper;
import com.micro.cards.repository.CardsRepository;
import com.micro.cards.service.CardsService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
@Service
@RequiredArgsConstructor
public class CardsServiceImpl implements CardsService {

    private static final Logger log = LoggerFactory.getLogger(CardsServiceImpl.class);
    private final CardsRepository cardsRepository;
    private final MeterRegistry meterRegistry;

    /**
     * @param mobileNumber - Mobile Number of the Customer
     */
    @Override
    public void createCard(String mobileNumber) {
        Optional<Cards> byMobileNumber = this.cardsRepository.findByMobileNumber(mobileNumber);
        if(byMobileNumber.isPresent()){
            throw new CardAlreadyExistException("Card already registered with given mobileNumber "+mobileNumber);
        }
        cardsRepository.save(createNewCard(mobileNumber));
        meterRegistry.counter("cards.operation","operation","created").increment();
    }

    /**
     * @param mobileNumber - Mobile Number of the Customer
     * @return the new card details
     */
    private Cards createNewCard(String mobileNumber) {
        Cards newCard = new Cards();
        long randomCardNumber = 100000000000L + new Random().nextInt(900000000);
        newCard.setCardNumber(Long.toString(randomCardNumber));
        newCard.setMobileNumber(mobileNumber);
        newCard.setCardType(CardsConstants.CREDIT_CARD);
        newCard.setTotalLimit(CardsConstants.NEW_CARD_LIMIT);
        newCard.setAmountUsed(0);
        newCard.setAvailableAmount(CardsConstants.NEW_CARD_LIMIT);
        return newCard;
    }


    /**
     *
     * @param mobileNumber - Input mobile Number
     * @return Card Details based on a given mobileNumber
     */
    @Override
    public CardsDto fetchCard(String mobileNumber) {
        Cards cards = cardsRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
        );
        log.debug("cards details fetched successfully for mobile number:- {}",mobileNumber);
        return CardsMapper.mapToCardsDto(cards, new CardsDto());
    }

    /**
     *
     * @param cardsDto - CardsDto Object
     * @return boolean indicating if the update of card details is successful or not
     */
    @Override
    public boolean updateCard(CardsDto cardsDto) {
        Cards cards = cardsRepository.findByCardNumber(cardsDto.getCardNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Card", "CardNumber", cardsDto.getCardNumber()));
        CardsMapper.mapToCards(cardsDto, cards);
        cardsRepository.save(cards);
        meterRegistry.counter("cards.operation","operation","updated").increment();
        return  true;
    }

    /**
     * @param mobileNumber - Input MobileNumber
     * @return boolean indicating if the delete of card details is successful or not
     */
    @Override
    public boolean deleteCard(String mobileNumber) {
        Cards cards = cardsRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
        );
        cardsRepository.deleteById(cards.getCardId());
        meterRegistry.counter("cards.operation","operation","deleted").increment();
        return true;
    }
}
