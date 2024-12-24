package ru.queuejw.messenger.services

import org.springframework.stereotype.Service
import ru.queuejw.messenger.model.Message
import java.nio.file.Files
import java.nio.file.Paths
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.time.LocalDateTime
import java.util.*

/**
 * Сервис для управления сообщениями.
 */
@Service
class MessageService {

    private val objectMapper = jacksonObjectMapper()

    init {
        // Создаем директорию messages, если она не существует
        val messagesDirectory = Paths.get("messages")
        if (!Files.exists(messagesDirectory)) {
            try {
                Files.createDirectories(messagesDirectory)
            } catch (e: Exception) {
                throw RuntimeException("Не удалось создать директорию messages", e)
            }
        }
    }

    /**
     * Отправляет сообщение пользователю.
     *
     * @param senderId ID отправителя.
     * @param recipientId ID получателя.
     * @param content Содержимое сообщения.
     * @return Отправленное сообщение.
     */
    fun sendMessage(senderId: String, recipientId: String, content: String): Message {
        val message = Message(
            id = UUID.randomUUID().toString(),
            senderId = senderId,
            recipientId = recipientId,
            content = content,
            timestamp = LocalDateTime.now().toString()
        )

        val messageFilePath = Paths.get("messages", message.id)
        try {
            // Сохраняем сообщение в файл
            Files.write(messageFilePath, objectMapper.writeValueAsBytes(message))
        } catch (e: Exception) {
            throw RuntimeException("Не удалось отправить сообщение", e)
        }

        return message
    }

    /**
     * Получает все сообщения, отправленные пользователю.
     *
     * @param userId ID пользователя.
     * @return Список сообщений, полученных пользователем.
     */
    fun getMessagesForUser(userId: String): List<Message> {
        val messageFiles = Files.walk(Paths.get("messages"))
            .filter { Files.isRegularFile(it) }
            .toList()

        return messageFiles.map { path ->
            // Исправляем десериализацию
            objectMapper.readValue<Message>(path.toFile())
        }.filter { it.recipientId == userId } // Фильтрация по получателю
    }


}
