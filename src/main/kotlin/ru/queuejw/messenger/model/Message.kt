package ru.queuejw.messenger.model

/**
 * Класс, представляющий сообщение.
 *
 * @property id Уникальный идентификатор сообщения.
 * @property senderId ID отправителя.
 * @property recipientId ID получателя.
 * @property content Содержимое сообщения.
 * @property timestamp Время отправки сообщения.
 */
data class Message(
    val id: String,
    val senderId: String,
    val recipientId: String,
    val content: String,
    val timestamp: String // или другой тип в зависимости от формата времени
)
