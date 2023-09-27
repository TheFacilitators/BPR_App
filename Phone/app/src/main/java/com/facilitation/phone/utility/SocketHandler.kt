package com.facilitation.phone.utility

import android.bluetooth.BluetoothSocket
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class SocketHandler(private val socket: BluetoothSocket) {

     fun sendMP3File(mp3File : File) {
        try {
            val outputStream = socket.outputStream
            val fileInputStream = FileInputStream(mp3File)
            val buffer = ByteArray(4096)
            var bytesRead: Int

            while (true) {
                bytesRead = fileInputStream.read(buffer)
                if (bytesRead == -1) break
                outputStream.write(buffer, 0, bytesRead)
                outputStream.flush()
            }
            fileInputStream.close()
            outputStream.close()
            closeSocket()
            return

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun closeSocket() {
        // Close the server socket when done
        try {
            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}