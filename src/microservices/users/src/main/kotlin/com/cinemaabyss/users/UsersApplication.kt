package com.cinemaabyss.users

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class UsersApplication

fun main(args: Array<String>) {
    runApplication<UsersApplication>(*args)
}
