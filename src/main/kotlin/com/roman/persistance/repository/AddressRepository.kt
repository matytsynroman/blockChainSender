package com.roman.persistance.repository

import com.roman.persistance.entity.Address
import org.springframework.data.jpa.repository.JpaRepository

interface AddressRepository : JpaRepository<Address, String> {

    fun findByAddress(address: String): Address?
}