package com.microservice.AuthService.utils;

import org.springframework.beans.BeanUtils;
import com.microservice.AuthService.dto.RegisterRequest;
import com.microservice.AuthService.entity.UserCredential;

/**
 * Utility class for the AuthService.
 * Provides helper methods to safely map data between incoming API requests (DTOs) 
 * and database objects (Entities).
 */
public class AppUtils {

    /**
     * Converts a RegisterRequest DTO (from the API) into a UserCredential Entity (for the Database).
     */
    public static UserCredential dtoToEntity(RegisterRequest registerRequest) {
        UserCredential userCredential = new UserCredential();
        BeanUtils.copyProperties(registerRequest, userCredential);
        return userCredential;
    }
}
