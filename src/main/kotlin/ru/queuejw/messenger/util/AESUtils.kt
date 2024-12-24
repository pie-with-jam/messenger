package ru.queuejw.messenger.util

import org.springframework.stereotype.Component
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

/**
 * Утилитный объект для работы с шифрованием и дешифрованием данных с использованием алгоритма AES.
 * Содержит методы для шифрования и расшифровки строк.
 */
@Component
object AESUtils {

    // Алгоритм для шифрования
    private const val ALGORITHM = "AES"
    // Ключ для шифрования (16 байт)
    private val key: Key = SecretKeySpec("1234567890123456".toByteArray(), ALGORITHM)

    /**
     * Шифрует переданные данные с использованием алгоритма AES.
     *
     * @param data строка, которую необходимо зашифровать.
     * @return зашифрованная строка в формате Base64.
     */
    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encrypted = cipher.doFinal(data.toByteArray())
        return Base64.getEncoder().encodeToString(encrypted)
    }

    /**
     * Расшифровывает данные, зашифрованные с использованием алгоритма AES.
     *
     * @param encryptedData строка, зашифрованная в формате Base64.
     * @return расшифрованная строка.
     */
    fun decrypt(encryptedData: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decoded = Base64.getDecoder().decode(encryptedData)
        val decrypted = cipher.doFinal(decoded)
        return String(decrypted)
    }
}