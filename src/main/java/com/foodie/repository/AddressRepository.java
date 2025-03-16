package com.foodie.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodie.model.Address;
import com.foodie.model.User;

public interface AddressRepository extends JpaRepository<Address, Long> {

	List<Address> findByUser(User user);

}
