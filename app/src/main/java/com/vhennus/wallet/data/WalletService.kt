package com.vhennus.wallet.data

import com.vhennus.BuildConfig
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.net.Socket
import java.util.Scanner

class WalletService {

    // send data to network via TCP
    suspend fun sendData(message: String): String {
        val address = BuildConfig.BLOCKCHAIN_URL
        val ipData = address.split(":")
        val host = ipData[0]
        val port = ipData[1].toInt()

        val responseBuffer = ByteArrayOutputStream()

        return try {
            Socket(host, port).use { socket ->
                socket.soTimeout = 5000

                val output = socket.getOutputStream()
                output.write(message.toByteArray())
                output.flush()
                socket.shutdownOutput()

                val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                val response = input.readText().trim()
                response
            }
        } catch (e: Exception) {
            println("Socket error: ${e.message}")
            ""
        }
    }
//
//    suspend fun sendData(message:String ):String{
//        val address = BuildConfig.BLOCKCHAIN_URL
//
//        // split ip string
//        val ipData = address.split(":")
//        val host = ipData.get(0)
//        val port = Integer.parseInt(ipData.get(1))
//        var data = ""
//        try {
//            val connection = Socket(host, port)
//            // get writer
//            val writer = connection.getOutputStream()
//            // send data
//            writer.write(message.toByteArray())
//
//            // receieve response
//            val reader = Scanner(connection.getInputStream())
//
//
//            while (reader.hasNext()){
//                data += reader.nextLine()+"\n"
//            }
//            reader.close()
//            writer.close()
//            connection.close()
//        }catch (e:Exception){
//
//        }
//
//        return data
//    }
}