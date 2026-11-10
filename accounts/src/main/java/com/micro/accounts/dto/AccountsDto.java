package com.micro.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(name = "Accounts",description = "Schema to Hold Customer Account Information")
public class AccountsDto {
    @Schema(
            description = "Account Number Of Customer",
            example = "1728463991"
    )
    @NotEmpty(message = "Account Number can not be null or empty")
    @Pattern(regexp = "(^$|[0-9]{10})",message = "Mobile Number Must Have 10 Digit")
    private Long accountNumber;
    @Schema(
            description = "Account Type Of Customer",
            example = "Savings"
    )
    @NotEmpty(message = "account type can not be empty or null")
    private String accountType;
    @Schema(
            description = "Branch Of Bank Account of Customer",
            example = "Jaipur"
    )
    @NotEmpty(message = "account type can not be empty or null")
    private String branchAddress;
}
