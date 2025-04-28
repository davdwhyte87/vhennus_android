package com.vhennus.general.utils

import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.math.ec.ECPoint
import java.math.BigInteger
import java.security.MessageDigest
import java.security.Security
import java.util.*

import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.params.ParametersWithRandom
import org.bouncycastle.crypto.signers.HMacDSAKCalculator
import org.bouncycastle.math.ec.FixedPointCombMultiplier

import org.bouncycastle.jce.spec.ECParameterSpec
import org.bouncycastle.util.BigIntegers

import java.security.*
import java.security.spec.ECGenParameterSpec
import java.util.*

fun signTransaction(
    sender: String,
    receiver: String,
    amount: String,
    nonce: String,
    privateKeyHex: String
): String {
    Security.addProvider(BouncyCastleProvider())

    // Hash the transaction data
    val txData = "$sender$receiver$amount$nonce"
    val txHash = sha256(txData.toByteArray())

    // Prepare key and curve
    val privateKeyD = BigInteger(privateKeyHex, 16)
    val ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1")
    val domainParams = ECDomainParameters(ecSpec.curve, ecSpec.g, ecSpec.n, ecSpec.h)

    val privKeyParams = ECPrivateKeyParameters(privateKeyD, domainParams)

    // Sign deterministically (RFC 6979 style)
    val signer = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))
    signer.init(true, privKeyParams)
    val signature = signer.generateSignature(txHash)

    val r = signature[0].toByteArray().stripLeadingZeroes()
    val s = signature[1].toByteArray().stripLeadingZeroes()

    // Concatenate r + s as hex
    return (r + s).joinToString("") { "%02x".format(it) }
}


fun signMessage(
    message: String,
    privateKeyHex: String
): String {
    Security.addProvider(BouncyCastleProvider())

    // 1. Hash the message data directly
    val messageHash = sha256(message.toByteArray())

    // 2. Prepare private key using proper EC parameters
    val privateKey = BigInteger(privateKeyHex, 16)
    val ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1")
    val domainParams = ECDomainParameters(ecSpec.curve, ecSpec.g, ecSpec.n, ecSpec.h)
    val privKeyParams = ECPrivateKeyParameters(privateKey, domainParams)

    // 3. Create deterministic signer (RFC 6979)
    val signer = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))
    signer.init(true, privKeyParams)

    // 4. Generate signature components
    val signatureComponents = signer.generateSignature(messageHash)

    // 5. Convert to fixed-length 32-byte arrays
    val r = BigIntegers.asUnsignedByteArray(32, signatureComponents[0])
    val s = BigIntegers.asUnsignedByteArray(32, signatureComponents[1])

    // 6. Concatenate R||S and return as hex
    return (r + s).joinToString("") { "%02x".format(it) }
}

private fun sha256(input: ByteArray): ByteArray {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(input)
}


fun ByteArray.stripLeadingZeroes(): ByteArray {
    return this.dropWhile { it == 0.toByte() }.toByteArray()
}


//fun generateSecp256k1KeyPair(): KeyPair {
//    Security.addProvider(BouncyCastleProvider())
//
//    val keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC")
//    val ecSpec = ECGenParameterSpec("secp256k1")
//    keyPairGenerator.initialize(ecSpec, SecureRandom())
//
//    return keyPairGenerator.generateKeyPair()
//}


object KeyGenerator {

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    fun generateKeysFromSeed(seedPhrase: String): Pair<String, String> {
        val digest = MessageDigest.getInstance("SHA-256")
        val seed = digest.digest(seedPhrase.toByteArray(Charsets.UTF_8))

        // Create BigInteger private key from seed
        val privateKeyInt = BigInteger(1, seed)

        val ecParams = ECNamedCurveTable.getParameterSpec("secp256k1")
        val curve = ecParams.curve
        val G = ecParams.g

        val publicKeyPoint: ECPoint = G.multiply(privateKeyInt).normalize()
        val publicKeyCompressed = publicKeyPoint.getEncoded(true) // compressed

        val privateKeyHex = seed.joinToString("") { "%02x".format(it) }
        val publicKeyHex = publicKeyCompressed.joinToString("") { "%02x".format(it) }

        return Pair(privateKeyHex, publicKeyHex)
    }
}