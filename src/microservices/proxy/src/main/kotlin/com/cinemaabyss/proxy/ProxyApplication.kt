package com.cinemaabyss.proxy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class ProxyApplication

fun main(args: Array<String>) {
    runApplication<ProxyApplication>(*args)
}
