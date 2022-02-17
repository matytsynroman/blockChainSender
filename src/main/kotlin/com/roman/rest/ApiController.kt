package com.roman.rest

import com.roman.dto.SegwitBitcoinTransactionRequestDTO
import com.roman.dto.SignBitcoinTransactionResponseDTO
import com.roman.service.AddressService
import com.roman.service.SignService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiController(
    private val addressService: AddressService,
    private val signService: SignService
) {

    @GetMapping("/createSegwitAddress")
    fun createSegwitAddress(): String =
        addressService.createSegwitAddress()

    @PostMapping("/signSegwitTransaction")
    fun signSegwitTransaction(@RequestBody request: SegwitBitcoinTransactionRequestDTO): SignBitcoinTransactionResponseDTO =
        signService.signTransaction(request)
}