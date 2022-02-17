package com.roman.configuration

import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.params.TestNet3Params
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class BitcoinConfiguration(
) {

    @Bean
    open fun networkParameters(): NetworkParameters = TestNet3Params()
}