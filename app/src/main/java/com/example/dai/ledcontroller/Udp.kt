package com.example.dai.ledcontroller

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import android.util.Log
import java.io.UnsupportedEncodingException


class Udp(private var ip: String, private var port: Int) {

    private lateinit var buff: ByteArray

    fun udpSend(sendText: String) {
        try {
            buff = sendText.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            Log.d("UdpError", e.toString())
        }

        val sendTextThread = Thread(Runnable {
            try {
                val iNetAddress = InetAddress.getByName(ip)
                val socket = DatagramSocket()
                val packet = DatagramPacket(
                    buff,
                    buff.size,
                    iNetAddress,
                    port
                )
                socket.send(packet)
                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("UdpError", e.toString())
            }
        })
        sendTextThread.start()
    }

}