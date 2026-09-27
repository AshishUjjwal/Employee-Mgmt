package com.microservice.address.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import com.microservice.address.entity.Address;
import com.microservice.address.repository.AddressRepository;

@RestController
@RequestMapping("/v1/address")
public class AddressController {

    private final AddressRepository repository;

    public AddressController(AddressRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @CacheEvict(value = "addresses", allEntries = true)
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
        Address savedAddress = repository.save(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAddress);
    }

    @GetMapping
    @Cacheable(value = "addresses")
    public ResponseEntity<List<Address>> getAllAddresses() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    @Cacheable(value = "addresses", key = "#id")
    public ResponseEntity<Address> getAddressById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "addresses", allEntries = true)
    public ResponseEntity<Address> updateAddress(@PathVariable Long id, @RequestBody Address address) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setStreet(address.getStreet());
                    existing.setCity(address.getCity());
                    existing.setZipCode(address.getZipCode());
                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = "addresses", allEntries = true)
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
