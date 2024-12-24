package ru.queuejw.messenger.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ru.queuejw.messenger.util.AESUtils
import org.springframework.http.ResponseEntity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ru.queuejw.messenger.services.UserService

/**
 * Контроллер для аутентификации пользователей.
 * Обрабатывает запросы на регистрацию и логин.
 *
 * Методы:
 * - login: Обрабатывает запрос на вход пользователя.
 * - register: Обрабатывает запрос на регистрацию нового пользователя.
 *
 * @param userService Сервис для работы с пользователями.
 * @param aesConfig Конфигурация для шифрования/дешифрования данных (не используется в текущем контексте).
 */
@RestController
@RequestMapping("/api")
class AuthController @Autowired constructor(private val userService: UserService, private val aesConfig: AESUtils) {

    private val objectMapper = jacksonObjectMapper()

    /**
     * Обрабатывает запрос на логин пользователя.
     * Проверяет введенные данные и осуществляет аутентификацию.
     *
     * @param login Логин пользователя.
     * @param password Пароль пользователя.
     * @return ResponseEntity с результатом запроса (успех или ошибка).
     */
    @PostMapping("/login")
    fun login(@RequestParam login: String, @RequestParam password: String): ResponseEntity<String> {
        println("Login attempt with login: $login")
        val user = userService.getUserByLogin(login)
        if (user != null && userService.validatePassword(user, password)) {
            println("Login successful")
            return ResponseEntity.ok("Login successful")
        }
        println("Invalid login or password")
        return ResponseEntity.status(401).body("Invalid login or password")
    }

    /**
     * Обрабатывает запрос на регистрацию нового пользователя.
     * Проверяет соответствие паролей, уникальность логина и длину введенных данных.
     *
     * @param login Логин нового пользователя.
     * @param password Пароль нового пользователя.
     * @param confirmPassword Подтверждение пароля.
     * @return ResponseEntity с результатом запроса (успех или ошибка).
     */
    @PostMapping("/register")
    fun register(@RequestParam login: String, @RequestParam password: String, @RequestParam confirmPassword: String): ResponseEntity<String> {
        println("Registering user with login: $login")

        // Валидация длины логина
        if (login.length < 4 || login.length > 16) {
            println("Login must be between 4 and 16 characters")
            return ResponseEntity.badRequest().body("Login must be between 4 and 16 characters")
        }

        // Валидация длины пароля
        if (password.length < 4 || password.length > 32) {
            println("Password must be between 4 and 32 characters")
            return ResponseEntity.badRequest().body("Password must be between 4 and 32 characters")
        }

        if (password != confirmPassword) {
            println("Passwords do not match")
            return ResponseEntity.badRequest().body("Passwords do not match")
        }

        if (userService.isLoginTaken(login)) {
            println("Login already taken")
            return ResponseEntity.badRequest().body("Login already taken")
        }

        val user = userService.registerUser(login, password)
        println("User registered with ID: ${user.id}")
        return ResponseEntity.ok("User registered successfully")
    }
}