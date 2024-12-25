package ru.queuejw.messenger.services

import org.springframework.stereotype.Service
import ru.queuejw.messenger.model.Message
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.time.LocalDateTime
import java.util.*

@Service
class MessageService {
    private val serverHost = "localhost"
    private val serverPort = 5190

    fun sendMessage(senderId: String, recipientId: String, content: String): Message {
        println("[DEBUG] Sending message via OSCAR: senderId=$senderId, recipientId=$recipientId, content=$content")
        val message = Message(
            id = UUID.randomUUID().toString(),
            senderId = senderId,
            recipientId = recipientId,
            content = content,
            timestamp = LocalDateTime.now().toString()
        )

        try {
            Socket(serverHost, serverPort).use { socket ->
                val output = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                output.write("SEND|${message.senderId}|${message.recipientId}|${message.content}|${message.timestamp}\n")
                output.flush()
            }
        } catch (e: Exception) {
            println("[DEBUG] Error sending message via OSCAR: ${e.message}")
            throw RuntimeException("Не удалось отправить сообщение через OSCAR: ${e.message}", e)
        }
        return message
    }

    fun getMessagesForUser(userId: String): List<Message> {
        println("[DEBUG] Fetching messages via OSCAR for userId=$userId")
        val messages = mutableListOf<Message>()
        try {
            Socket(serverHost, serverPort).use { socket ->
                val output = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                output.write("RECEIVE|$userId\n")
                output.flush()

                var response: String?
                while (input.readLine().also { response = it } != null) {
                    println("[DEBUG] OSCAR server response: $response")
                    val parts = response!!.split("|")
                    if (parts.size == 6 && parts[0] == "MESSAGE") {
                        messages.add(
                            Message(
                                id = parts[1],
                                senderId = parts[2],
                                recipientId = parts[3],
                                content = parts[4],
                                timestamp = parts[5]
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            println("[DEBUG] Error fetching messages: ${e.message}")
            throw RuntimeException("Не удалось получить сообщения через OSCAR: ${e.message}", e)
        }
        return messages
    }
}
