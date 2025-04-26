package com.rstep1.post_service.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rstep1.common_lib.dto.ErrorResponse;
import com.rstep1.common_lib.exception.ServiceException;
import com.rstep1.post_service.exception.UnauthorizedException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;

public class UserServiceErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SneakyThrows(IOException.class)
    public Exception decode(String methodKey, Response response) {
        ErrorResponse errorResponse = parseErrorResponse(response);
        
        return switch (response.status()) {
            case 401 -> new UnauthorizedException(errorResponse.message());
            case 403 -> new UnauthorizedException(errorResponse.message());
            default -> new ServiceException(errorResponse.message());
        };
    }

    private ErrorResponse parseErrorResponse(Response response) throws IOException {
        if (response.body() == null) {
            return new ErrorResponse("Error", response.reason());
        }

        try (InputStream body = response.body().asInputStream()) {
            return objectMapper.readValue(body, ErrorResponse.class);
        } catch (Exception e) {
            return new ErrorResponse("Error", "Failed to parse error response: " + e.getMessage());
        }
    }
}