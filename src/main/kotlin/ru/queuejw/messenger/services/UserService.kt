package ru.queuejw.messenger.services

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service
import ru.queuejw.messenger.model.User
import ru.queuejw.messenger.util.AESUtils
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*

/**
 * Сервис для управления пользователями в системе.
 *
 * Этот сервис предоставляет функциональность для регистрации пользователей, валидации их паролей,
 * проверки занятости логина, а также шифрования и дешифрования паролей.
 */
@Service
class UserService(private val aesConfig: AESUtils) {

    private val objectMapper = jacksonObjectMapper().apply {
        // Регистрируем модуль для обработки Java 8 Date/Time типов
        registerModule(JavaTimeModule())
    }

    init {
        // Создаем директорию users, если она не существует, при старте приложения
        val usersDirectory = Paths.get("users")
        if (!Files.exists(usersDirectory)) {
            try {
                Files.createDirectories(usersDirectory)
            } catch (e: Exception) {
                throw RuntimeException("Не удалось создать директорию users", e)
            }
        }
    }

    /**
     * Регистрирует нового пользователя.
     *
     * @param login Логин пользователя.
     * @param password Пароль пользователя.
     * @return Зарегистрированный пользователь.
     * @throws IllegalArgumentException Если логин уже занят.
     * @throws RuntimeException Если произошла ошибка при записи данных пользователя.
     */
    fun registerUser(login: String, password: String): User {
        if (isLoginTaken(login)) {
            throw IllegalArgumentException("Логин уже занят")
        }

        val id = generateUniqueId()
        val encryptedPassword = encryptPassword(password)

        val user = User(
            id = id,
            login = login,
            password = encryptedPassword,
            nickname = login,
            accountCreationDate = LocalDateTime.now(),
            isAdmin = false
        )

        val userFilePath = Paths.get("users", "$id", "data.json")
        try {
            // Создаем директорию для пользователя, если она не существует
            Files.createDirectories(userFilePath.parent)
            // Записываем данные пользователя в файл
            Files.write(userFilePath, objectMapper.writeValueAsBytes(user))
        } catch (e: Exception) {
            throw RuntimeException("Не удалось зарегистрировать пользователя", e)
        }

        return user
    }

    /**
     * Получает пользователя по его ID.
     *
     * @param userId ID пользователя.
     * @return Пользователь или null, если не найден.
     */
    fun getUserById(userId: String): User? {
        val userFilePath = Paths.get("users", userId, "data.json")
        return if (Files.exists(userFilePath)) {
            objectMapper.readValue(userFilePath.toFile(), User::class.java)
        } else {
            null
        }
    }


    /**
     * Получает пользователя по логину.
     *
     * @param login Логин пользователя.
     * @return Пользователь или null, если пользователь не найден.
     */
    fun getUserByLogin(login: String): User? {
        val userFiles = Files.walk(Paths.get("users"))
            .filter { it.toString().endsWith("data.json") }
            .toList()  // Преобразуем Stream в List

        return userFiles
            .map { path -> objectMapper.readValue<User>(path.toFile()) }
            .firstOrNull { user -> user.login == login }
    }

    /**
     * Проверяет правильность пароля пользователя.
     *
     * @param user Пользователь.
     * @param password Пароль для проверки.
     * @return true, если пароль совпадает, иначе false.
     */
    fun validatePassword(user: User, password: String): Boolean {
        val decryptedPassword = decryptPassword(user.password)
        return decryptedPassword == password
    }

    /**
     * Проверяет, занят ли логин.
     *
     * @param login Логин для проверки.
     * @return true, если логин занят, иначе false.
     */
    fun isLoginTaken(login: String): Boolean {
        return getUserByLogin(login) != null
    }

    /**
     * Генерирует уникальный ID для нового пользователя.
     *
     * @return Уникальный ID.
     */
    private fun generateUniqueId(): String {
        var id: String
        do {
            id = UUID.randomUUID().toString().take(8) // Генерация уникального ID
        } while (Files.exists(Paths.get("users", id, "data.json")))
        return id
    }

    /**
     * Шифрует пароль с использованием AES.
     *
     * @param password Пароль для шифрования.
     * @return Зашифрованный пароль.
     */
    private fun encryptPassword(password: String): String {
        return aesConfig.encrypt(password)
    }

    /**
     * Дешифрует пароль с использованием AES.
     *
     * @param encryptedPassword Зашифрованный пароль.
     * @return Дешифрованный пароль.
     */
    private fun decryptPassword(encryptedPassword: String): String {
        return aesConfig.decrypt(encryptedPassword)
    }
}