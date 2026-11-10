package com.micro.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(name = "ErrorResponse",description = "Schema to Hold Error Response Information")
public class ErrorResponseDto {
    @Schema(description = "API Path that Invoked By Client")
    private String apiPath;
    @Schema(description = "Error Code Represented The Error Happened")
    private HttpStatus errorCode;
    @Schema(description = "Error Message")
    private String errorMessage;
    @Schema(description = "Error Time")
    private LocalDateTime errorTime;

}
