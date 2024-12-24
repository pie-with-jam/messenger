package ru.queuejw.messenger.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import ru.queuejw.messenger.model.Message
import ru.queuejw.messenger.services.MessageService

/**
 * Контроллер для работы с сообщениями.
 * Обрабатывает запросы на отправку и получение сообщений.
 */
@RestController
@RequestMapping("/api/messages")
class MessageController @Autowired constructor(private val messageService: MessageService) {

    /**
     * Отправляет сообщение.
     *
     * @param senderId ID отправителя.
     * @param recipientId ID получателя.
     * @param content Содержимое сообщения.
     * @return Ответ с успешным результатом или ошибкой.
     */
    @PostMapping("/send")
    fun sendMessage(
        @RequestParam senderId: String,
        @RequestParam recipientId: String,
        @RequestParam content: String
    ): ResponseEntity<String> {
        return try {
            messageService.sendMessage(senderId, recipientId, content)
            ResponseEntity.ok("Message sent successfully")
        } catch (e: Exception) {
            ResponseEntity.status(500).body("Failed to send message")
        }
    }

    /**
     * Получает все сообщения для пользователя.
     *
     * @param userId ID пользователя.
     * @return Список сообщений.
     */
    @GetMapping("/receive")
    fun getMessages(@RequestParam userId: String): ResponseEntity<List<Message>> {
        return try {
            val messages = messageService.getMessagesForUser(userId)
            ResponseEntity.ok(messages)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(emptyList())
        }
    }
}
