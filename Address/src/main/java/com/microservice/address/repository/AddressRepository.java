package com.microservice.address.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microservice.address.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
