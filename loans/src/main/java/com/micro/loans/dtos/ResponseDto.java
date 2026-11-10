package com.micro.loans.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(name = "Response",description = "Schema to Hold Success Response")
public class ResponseDto {
    @Schema(
            name = "Http Status In Response"
    )
    private String statusCode;
    @Schema(
            name = "Response Message"
    )
    private String statusMessage;
}
