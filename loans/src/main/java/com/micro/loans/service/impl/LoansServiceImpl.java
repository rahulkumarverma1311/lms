package com.micro.loans.service.impl;

import com.micro.loans.constants.LoansConstants;
import com.micro.loans.dtos.LoansDto;
import com.micro.loans.entity.Loans;
import com.micro.loans.exception.LoanAlreadyExistException;
import com.micro.loans.exception.ResourceNotFoundException;
import com.micro.loans.mappers.LoansMapper;
import com.micro.loans.repository.LoansRepository;
import com.micro.loans.service.LoansService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LoansServiceImpl implements LoansService {

    private final LoansRepository loansRepository;
    @Override
    public void createLoan(String mobileNumber) {
        Optional<Loans> loanAlready = this.loansRepository.findByMobileNumber(mobileNumber);
        if(loanAlready.isPresent()){
            throw new LoanAlreadyExistException("Loan already registered with given mobileNumber :"+mobileNumber);
        }
        loansRepository.save(createNewLoan(mobileNumber));

    }

    private Loans createNewLoan(String mobileNumber) {
        Loans newLoan = new Loans();
        long randomLoanNumber = 100000000000L + new Random().nextInt(900000000);
        newLoan.setLoanNumber(Long.toString(randomLoanNumber));
        newLoan.setMobileNumber(mobileNumber);
        newLoan.setLoanType(LoansConstants.HOME_LOAN);
        newLoan.setTotalLoan(LoansConstants.NEW_LOAN_LIMIT);
        newLoan.setAmountPaid(0);
        newLoan.setOutstandingAmount(LoansConstants.NEW_LOAN_LIMIT);
        return newLoan;
    }

    @Override
    public LoansDto fetchLoan(String mobileNumber) {
        Loans loans = this.loansRepository.findByMobileNumber(mobileNumber).orElseThrow(() -> new ResourceNotFoundException("Loan", "Mobile Number", mobileNumber));
        return LoansMapper.toLoansDto(loans, new LoansDto());
    }

    @Override
    public boolean updateLoan(LoansDto loansDto) {
        Loans loans = this.loansRepository.findByMobileNumber(loansDto.getMobileNumber()).orElseThrow(() -> new ResourceNotFoundException("Loan", "Mobile Number", loansDto.getMobileNumber()));
        LoansMapper.toLoans(loansDto,loans);
        this.loansRepository.save(loans);
        return true;
    }

    @Override
    public boolean deleteLoan(String mobileNumber) {
        Loans loans = loansRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "mobileNumber", mobileNumber)
        );
        loansRepository.deleteById(loans.getLoanId());
        return true;
    }
}
