package com.vhennus.general.utils

import org.bouncycastle.asn1.sec.SECNamedCurves
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
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

import java.security.*
import java.security.spec.ECGenParameterSpec
import java.util.*

fun signTransaction(
    sender: String,
    receiver: String,
    amount: String,
    timestamp: Long,
    id: String,
    privateKeyHex: String
): String {
    // 0) Add BC if not already
    Security.addProvider(BouncyCastleProvider())

    // 1) Build exactly the same transaction string you verify in Rust/Python
    val txData = "$sender$receiver$amount$timestamp$id"
    println("🔏 tx_data to sign: $txData")
    val raw = txData.toByteArray(StandardCharsets.UTF_8)
    println("raw hex: ${raw.joinToString("") { "%02x".format(it) }}")

    // 1) SHA-256 the raw bytes
    val md = MessageDigest.getInstance("SHA-256")
    val rawHash = md.digest(raw)


    // 2) Load secp256k1 domain parameters
    val curve = SECNamedCurves.getByName("secp256k1")
    val domain = ECDomainParameters(curve.curve, curve.g, curve.n, curve.h)

    // 3) Parse private key (32-byte hex)
    val d = BigInteger(privateKeyHex, 16)
    val privParams = ECPrivateKeyParameters(d, domain)

    // 4) Create deterministic ECDSA signer (RFC 6979)
    val signer = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))
    signer.init(true, privParams)

    // 5) Sign the raw bytes
    val components = signer.generateSignature(rawHash)
    var r = components[0]
    var s = components[1]

    // 6) Enforce low-S: if s > n/2, replace s with n - s
    val halfN = domain.n.shiftRight(1)
    if (s > halfN) {
        s = domain.n.subtract(s)
    }

    // 7) Convert BigIntegers to 32-byte big-endian arrays
    fun BigInteger.to32Bytes(): ByteArray {
        val b = toByteArray().let {
            // strip leading zero if it produced a 33-byte array
            if (it.size == 33 && it[0].toInt() == 0) it.copyOfRange(1, 33) else it
        }
        return if (b.size < 32) ByteArray(32 - b.size) + b else b
    }

    val rBytes = r.to32Bytes()
    val sBytes = s.to32Bytes()

    // 8) Return r||s as hex
    return (rBytes + sBytes).joinToString("") { "%02x".format(it) }
}

fun getTxId(sender: String, receiver: String, amount: String, ts: Long): String {
    val digest = MessageDigest.getInstance("SHA-256")

    // Convert the timestamp to 8 bytes (big-endian, signed)
    val tsBytes = ByteBuffer.allocate(8).putLong(ts).array()

    // Update the hash with all the parts
    digest.update(tsBytes)
    digest.update(sender.toByteArray(StandardCharsets.UTF_8))
    digest.update(receiver.toByteArray(StandardCharsets.UTF_8))
    digest.update(amount.toByteArray(StandardCharsets.UTF_8))

    // Convert the hash to hex
    return digest.digest().joinToString("") { "%02x".format(it) }
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

fun signMessage2(
    message: String,
    privateKeyHex: String
): String {
    Security.addProvider(BouncyCastleProvider())

    // 1) Build exactly the same transaction string you verify in Rust/Python
    val txData = "$message"
    println("🔏 tx_data to sign: $txData")
    val raw = txData.toByteArray(StandardCharsets.UTF_8)
    println("raw hex: ${raw.joinToString("") { "%02x".format(it) }}")

    // 1) SHA-256 the raw bytes
    val md = MessageDigest.getInstance("SHA-256")
    val rawHash = md.digest(raw)


    // 2) Load secp256k1 domain parameters
    val curve = SECNamedCurves.getByName("secp256k1")
    val domain = ECDomainParameters(curve.curve, curve.g, curve.n, curve.h)

    // 3) Parse private key (32-byte hex)
    val d = BigInteger(privateKeyHex, 16)
    val privParams = ECPrivateKeyParameters(d, domain)

    // 4) Create deterministic ECDSA signer (RFC 6979)
    val signer = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))
    signer.init(true, privParams)

    // 5) Sign the raw bytes
    val components = signer.generateSignature(rawHash)
    var r = components[0]
    var s = components[1]

    // 6) Enforce low-S: if s > n/2, replace s with n - s
    val halfN = domain.n.shiftRight(1)
    if (s > halfN) {
        s = domain.n.subtract(s)
    }

    // 7) Convert BigIntegers to 32-byte big-endian arrays
    fun BigInteger.to32Bytes(): ByteArray {
        val b = toByteArray().let {
            // strip leading zero if it produced a 33-byte array
            if (it.size == 33 && it[0].toInt() == 0) it.copyOfRange(1, 33) else it
        }
        return if (b.size < 32) ByteArray(32 - b.size) + b else b
    }

    val rBytes = r.to32Bytes()
    val sBytes = s.to32Bytes()

    // 8) Return r||s as hex
    return (rBytes + sBytes).joinToString("") { "%02x".format(it) }
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