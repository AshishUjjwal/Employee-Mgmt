package com.microservice.address.utils;

import org.springframework.beans.BeanUtils;
import com.microservice.address.dto.AddressDto;
import com.microservice.address.entity.Address;

/**
 * Utility class for the Address application.
 * Provides helper methods, such as converting between Address entities and AddressDto objects.
 */
public class AppUtils {

    public static AddressDto entityToDto(Address addressEntity) {
        AddressDto addressDto = new AddressDto();
        BeanUtils.copyProperties(addressEntity, addressDto);
        return addressDto;
    }

    public static Address dtoToEntity(AddressDto addressDto) {
        Address addressEntity = new Address();
        BeanUtils.copyProperties(addressDto, addressEntity);
        return addressEntity;
    }
}
