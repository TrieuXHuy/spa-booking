package com.group8.spabooking.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponse {

    private String message;
    private UserResponse user;
}
