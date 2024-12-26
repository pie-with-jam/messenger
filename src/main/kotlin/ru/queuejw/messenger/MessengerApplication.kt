package ru.queuejw.messenger


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@RestController
class MessengerApplication

fun main(args: Array<String>) {
	runApplication<MessengerApplication>(*args)
}