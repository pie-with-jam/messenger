package ru.queuejw.messenger


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MessengerApplication

fun main(args: Array<String>) {
	runApplication<MessengerApplication>(*args)
}