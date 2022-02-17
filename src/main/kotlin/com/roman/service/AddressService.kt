package com.roman.service

import com.roman.persistance.entity.Address
import com.roman.persistance.repository.AddressRepository
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.core.SegwitAddress
import org.springframework.stereotype.Service

@Service
class AddressService(
    private val networkParameters: NetworkParameters,
    private val addressRepository: AddressRepository
) {

    fun createSegwitAddress(): String {
        val ecKey = ECKey()
        val segwitAddress = SegwitAddress.fromKey(networkParameters, ECKey())
        val address = Address(
            segwitAddress.toBech32(),
            ecKey.privKeyBytes
        )
        addressRepository.save(address)

        return segwitAddress.toBech32()
    }
}