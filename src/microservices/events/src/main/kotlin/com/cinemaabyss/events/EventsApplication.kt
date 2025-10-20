package com.cinemaabyss.events

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class EventsApplication

fun main(args: Array<String>) {
    runApplication<EventsApplication>(*args)
}
