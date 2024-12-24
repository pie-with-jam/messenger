package ru.queuejw.messenger.model

import java.time.LocalDateTime

/**
 * Класс, представляющий пользователя в системе.
 *
 * @property id Уникальный идентификатор пользователя.
 * @property login Логин пользователя, используемый для входа в систему.
 * @property password Пароль пользователя.
 * @property nickname Никнейм пользователя, отображаемый в приложении.
 * @property accountCreationDate Дата и время создания аккаунта.
 * @property isAdmin Флаг, указывающий, является ли пользователь администратором.
 */
data class User(
    val id: String,
    val login: String,
    val password: String,
    val nickname: String,
    val accountCreationDate: LocalDateTime,
    val isAdmin: Boolean
)
