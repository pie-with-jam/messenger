package ru.queuejw.messenger.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import ru.queuejw.messenger.model.Message
import ru.queuejw.messenger.services.MessageService

@RestController
@RequestMapping("/api/messages")
class MessageController @Autowired constructor(private val messageService: MessageService) {

    @PostMapping("/send")
    fun sendMessage(
        @RequestParam senderId: String,
        @RequestParam recipientId: String,
        @RequestParam content: String
    ): ResponseEntity<String> {
        println("[DEBUG] Processing /send with senderId=$senderId, recipientId=$recipientId, content=$content")
        return try {
            messageService.sendMessage(senderId, recipientId, content)
            ResponseEntity.ok("[DEBUG] Message sent successfully")
        } catch (e: Exception) {
            println("[DEBUG] Error in /send: ${e.message}")
            ResponseEntity.status(500).body("[DEBUG] Failed to send message: ${e.message}")
        }
    }

    @GetMapping("/receive")
    fun getMessages(@RequestParam userId: String): ResponseEntity<List<Message>> {
        println("[DEBUG] Processing /receive for userId=$userId")
        return try {
            val messages = messageService.getMessagesForUser(userId)
            println("[DEBUG] Retrieved messages: $messages")
            ResponseEntity.ok(messages)
        } catch (e: Exception) {
            println("[DEBUG] Error in /receive: ${e.message}")
            ResponseEntity.status(500).body(emptyList())
        }
    }
}
