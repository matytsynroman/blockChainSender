package com.roman.service

import com.roman.dto.SegwitBitcoinTransactionRequestDTO
import com.roman.dto.SignBitcoinTransactionResponseDTO
import com.roman.persistance.repository.AddressRepository
import org.bitcoinj.core.*
import org.bitcoinj.crypto.TransactionSignature
import org.bitcoinj.script.Script
import org.bitcoinj.script.ScriptBuilder
import org.bouncycastle.util.encoders.Hex
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class SignService(
    private val networkParameters: NetworkParameters,
    private val addressRepository: AddressRepository
) {

    private val anyoneCanPay = false


    fun signTransaction(request: SegwitBitcoinTransactionRequestDTO): SignBitcoinTransactionResponseDTO {

        val transaction = Transaction(networkParameters)

        transaction.addOutput(
            Coin.valueOf(calculateChange(request)),
            Address.fromString(networkParameters, request.sourceAddress)
        )

        transaction.addOutput(
            Coin.valueOf(request.amount),
            Address.fromString(networkParameters, request.destinationAddress)
        )

        //Add inputs
        val address = Address.fromString(
            networkParameters, request.sourceAddress
        )

        val key = getKey(request.sourceAddress)

        request.utxo.forEach {
            val outPoint = TransactionOutPoint(networkParameters, it.outputIndex, Sha256Hash.wrap(it.prevHash))
            val input = TransactionInput(
                networkParameters,
                transaction,
                ByteArray(0),
                outPoint,
                Coin.valueOf(it.outputValue)
            )
            transaction.addInput(input)
        }


        for (input in transaction.inputs) {
            val index = input.index
            val scriptPubKey = ScriptBuilder.createOutputScript(address)

            val signature =
                getSegwitTransactionKey(
                    transaction,
                    index,
                    scriptPubKey,
                    key,
                    request.utxo[index].outputValue.toBigDecimal()
                )
            input.witness = TransactionWitness.redeemP2WPKH(signature, key)
            input.scriptSig = ScriptBuilder.createEmpty()

        }

        transaction.verify()

        return SignBitcoinTransactionResponseDTO(Hex.toHexString(transaction.bitcoinSerialize()))
    }

    private fun calculateChange(request: SegwitBitcoinTransactionRequestDTO): Long {
        val totalAmount = request.utxo.sumOf { it.outputValue }
        return totalAmount - request.amount - request.fee
    }

    private fun getSegwitTransactionKey(
        transaction: Transaction,
        index: Int,
        script: Script,
        key: ECKey,
        amount: BigDecimal
    ): TransactionSignature {

        return transaction.calculateWitnessSignature(
            index,
            key,
            script,
            Coin.valueOf(amount.toLong()),
            Transaction.SigHash.ALL,
            anyoneCanPay
        )
    }

    private fun getKey(address: String): ECKey {
        val foundBitcoinAddress = addressRepository.findByAddress(address)
        return ECKey.fromPrivate(foundBitcoinAddress!!.privateKey)
    }


}