package com.cinemaabyss.proxy.web

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/health")
class HealthController {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun health(): Map<String, Boolean> = mapOf("status" to true)
}
