package com.microservice.address.service;

import com.microservice.address.dto.AddressDto;
import com.microservice.address.entity.Address;
import com.microservice.address.repository.AddressRepository;
import com.microservice.address.utils.AppUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepository repository;

    public AddressService(AddressRepository repository) {
        this.repository = repository;
    }

    public AddressDto saveAddress(AddressDto addressDto) {
        Address address = AppUtils.dtoToEntity(addressDto);
        Address savedAddress = repository.save(address);
        return AppUtils.entityToDto(savedAddress);
    }

    public List<AddressDto> getAllAddresses() {
        return repository.findAll().stream()
                .map(AppUtils::entityToDto)
                .collect(Collectors.toList());
    }

    public AddressDto getAddressById(Long id) {
        Address address = repository.findById(id).orElse(null);
        if (address != null) {
            return AppUtils.entityToDto(address);
        }
        return null;
    }

    public AddressDto updateAddress(Long id, AddressDto addressDto) {
        Address existingAddress = repository.findById(id).orElse(null);
        if (existingAddress != null) {
            existingAddress.setStreet(addressDto.getStreet());
            existingAddress.setCity(addressDto.getCity());
            existingAddress.setZipCode(addressDto.getZipCode());
            Address updatedAddress = repository.save(existingAddress);
            return AppUtils.entityToDto(updatedAddress);
        }
        return null;
    }

    public void deleteAddress(Long id) {
        repository.deleteById(id);
    }
}
