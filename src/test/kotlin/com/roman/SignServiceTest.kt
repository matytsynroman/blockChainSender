package com.roman

import com.roman.dto.SegwitBitcoinTransactionRequestDTO
import com.roman.dto.UTXO
import org.bitcoinj.core.*
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.script.ScriptBuilder
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigInteger

@SpringBootTest
class SignServiceTest {

    private val networkParameters: NetworkParameters = TestNet3Params()

    @Test
    fun testBuildingSimpleP2WPKH() {

        val toAddr = Address.fromString(networkParameters, "tb1q4f62c0f8dapwc89j6c6tt490vhy6gwv6064tr7")

        val request = SegwitBitcoinTransactionRequestDTO(
            amount = 10000,
            fee = 10000,
            sourceAddress = "tb1qmr9xu79de045hkhxvjchuakrttmvaky8fze5yn",
            destinationAddress = "tb1q4f62c0f8dapwc89j6c6tt490vhy6gwv6064tr7",
            utxo = listOf(
                UTXO(
                    outputIndex = 0,
                    prevHash = "d3da32fad32ebbc7c25932867b5881bbc02d61702d81628aed70998bd4f45068",
                    outputValue = 75000
                )
            )
        )
        val fromAddress = Address.fromString(networkParameters, "tb1qmr9xu79de045hkhxvjchuakrttmvaky8fze5yn")

        val transaction = Transaction(networkParameters)

        transaction.addOutput(
            Coin.valueOf(calculateChange(request)),
            fromAddress
        )

        transaction.addOutput(
            Coin.valueOf(request.amount),
            toAddr
        )


        val key = getKey()

        request.utxo.forEach {
            val outPoint = TransactionOutPoint(networkParameters, it.outputIndex, Sha256Hash.wrap(it.prevHash))
            transaction.addSignedInput(
                outPoint,
                ScriptBuilder.createOutputScript(fromAddress),
                Coin.valueOf(it.outputValue),
                key
            );
        }
        transaction.verify()
    }

    private fun calculateChange(request: SegwitBitcoinTransactionRequestDTO): Long {
        val totalAmount = request.utxo.sumOf { it.outputValue }
        return totalAmount - request.amount - request.fee
    }

    private fun getKey(): ECKey {
        return ECKey.fromPrivate(BigInteger("7694823339399292367982256306916755928232917506189457417132430989192851140061"))
    }
}