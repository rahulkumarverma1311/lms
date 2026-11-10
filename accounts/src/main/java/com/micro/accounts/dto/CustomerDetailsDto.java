package com.micro.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(name = "Accounts",description = "Schema to Hold Customer Account,Card and Loans Information")
public class CustomerDetailsDto {

    @Schema(
            description = "Name Of Customer",
            example = "Rahul"
    )
    @NotEmpty(message = "Name Can Not Be Empty or Null")
    @Size(min = 2 ,max = 35,message = "The Length Of Customer Name Should Be In Range 2 To 35")
    private String name;
    @Schema(
            description = "Email Of Customer",
            example = "user@gmail.com"
    )
    @NotEmpty(message = "Email Can Not Be Empty or Null")
    @Email(message = "Email Address Should Be Valid")
    private String email;
    @Schema(
            description = "Mobile Number Of Customer",
            example = "9211420420"
    )
    @NotEmpty
    @Pattern(regexp = "(^$|[0-9]{10})",message = "Mobile Number Must Have 10 Digit")
    private String mobileNumber;
    @Schema(
            description = "Account Details Of Customer"
    )
    private AccountsDto accountsDto;
    @Schema(
            description = "Cards Details Of Customer"
    )
    private CardsDto cardsDto;
    @Schema(
            description = "Loans Details Of Customer"
    )
    private LoansDto loansDto;
}
