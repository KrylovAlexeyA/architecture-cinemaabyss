package com.cinemaabyss.proxy.web

import com.cinemaabyss.proxy.config.AppProperties
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api")
class ProxyController(
    private val props: AppProperties,
    private val webClientBuilder: WebClient.Builder
) {

    private fun chooseMoviesTarget(): String {
        if (!props.gradualMigration) return props.monolithUrl
        val percent = props.moviesMigrationPercent.coerceIn(0, 100)
        val roll = Random.nextInt(100)
        return if (roll < percent) props.moviesServiceUrl else props.monolithUrl
    }

    private fun chooseUsersTarget(): String {
        if (!props.gradualMigration) return props.monolithUrl
        val percent = props.usersMigrationPercent.coerceIn(0, 100)
        val roll = Random.nextInt(100)
        return if (roll < percent) props.usersServiceUrl else props.monolithUrl
    }

    private fun choosePaymentsTarget(): String {
        if (!props.gradualMigration) return props.monolithUrl
        val percent = props.paymentsMigrationPercent.coerceIn(0, 100)
        val roll = Random.nextInt(100)
        return if (roll < percent) props.paymentsServiceUrl else props.monolithUrl
    }

    private fun chooseSubscriptionsTarget(): String {
        if (!props.gradualMigration) return props.monolithUrl
        val percent = props.subscriptionsMigrationPercent.coerceIn(0, 100)
        val roll = Random.nextInt(100)
        return if (roll < percent) props.subscriptionsServiceUrl else props.monolithUrl
    }

    private fun buildTarget(baseUrl: String, path: String, query: String?): String {
        val normalized = if (path.startsWith("/")) path else "/$path"
        val q = if (query.isNullOrBlank()) "" else "?$query"
        return baseUrl + normalized + q
    }

    @RequestMapping("/movies/**")
    fun proxyMovies(
        @RequestHeader headers: HttpHeaders,
        @RequestBody(required = false) body: String?,
        request: ServerHttpRequest
    ): Mono<ResponseEntity<ByteArray>> {
        val path = "/api" + request.uri.path.removePrefix("/api")
        val targetBase = chooseMoviesTarget()
        val url = buildTarget(targetBase, path, request.uri.rawQuery)
        logger.info { "Proxy /api$path -> $url" }
        return forward(headers, body, request.method?.name() ?: "GET", url)
    }

    @RequestMapping("/users/**")
    fun proxyUsers(
        @RequestHeader headers: HttpHeaders,
        @RequestBody(required = false) body: String?,
        request: ServerHttpRequest
    ): Mono<ResponseEntity<ByteArray>> {
        val path = "/api" + request.uri.path.removePrefix("/api")
        val targetBase = chooseUsersTarget()
        val url = buildTarget(targetBase, path, request.uri.rawQuery)
        logger.info { "Proxy /api$path -> $url" }
        return forward(headers, body, request.method?.name() ?: "GET", url)
    }

    @RequestMapping("/payments/**")
    fun proxyPayments(
        @RequestHeader headers: HttpHeaders,
        @RequestBody(required = false) body: String?,
        request: ServerHttpRequest
    ): Mono<ResponseEntity<ByteArray>> {
        val path = "/api" + request.uri.path.removePrefix("/api")
        val targetBase = choosePaymentsTarget()
        val url = buildTarget(targetBase, path, request.uri.rawQuery)
        logger.info { "Proxy /api$path -> $url" }
        return forward(headers, body, request.method?.name() ?: "GET", url)
    }

    @RequestMapping("/subscriptions/**")
    fun proxySubscriptions(
        @RequestHeader headers: HttpHeaders,
        @RequestBody(required = false) body: String?,
        request: ServerHttpRequest
    ): Mono<ResponseEntity<ByteArray>> {
        val path = "/api" + request.uri.path.removePrefix("/api")
        val targetBase = chooseSubscriptionsTarget()
        val url = buildTarget(targetBase, path, request.uri.rawQuery)
        logger.info { "Proxy /api$path -> $url" }
        return forward(headers, body, request.method?.name() ?: "GET", url)
    }

    @RequestMapping("/**")
    fun proxyOther(
        @RequestHeader headers: HttpHeaders,
        @RequestBody(required = false) body: String?,
        request: ServerHttpRequest
    ): Mono<ResponseEntity<ByteArray>> {
        val path = "/api" + request.uri.path.removePrefix("/api")
        val url = buildTarget(props.monolithUrl, path, request.uri.rawQuery)
        logger.info { "Proxy /api$path -> $url" }
        return forward(headers, body, request.method?.name() ?: "GET", url)
    }

    private fun forward(
        incomingHeaders: HttpHeaders,
        body: String?,
        method: String,
        url: String
    ): Mono<ResponseEntity<ByteArray>> {
        val client = webClientBuilder.build()
        
        return when (method.uppercase()) {
            "GET" -> {
                client.get()
                    .uri(url)
                    .headers { h -> copyHeaders(incomingHeaders, h) }
                    .retrieve()
                    .toEntity(ByteArray::class.java)
            }
            "POST" -> {
                val spec = client.post().uri(url).headers { h -> copyHeaders(incomingHeaders, h) }
                if (!body.isNullOrEmpty()) {
                    spec.body(BodyInserters.fromValue(body))
                }
                spec.retrieve().toEntity(ByteArray::class.java)
            }
            "PUT" -> {
                val spec = client.put().uri(url).headers { h -> copyHeaders(incomingHeaders, h) }
                if (!body.isNullOrEmpty()) {
                    spec.body(BodyInserters.fromValue(body))
                }
                spec.retrieve().toEntity(ByteArray::class.java)
            }
            "PATCH" -> {
                val spec = client.patch().uri(url).headers { h -> copyHeaders(incomingHeaders, h) }
                if (!body.isNullOrEmpty()) {
                    spec.body(BodyInserters.fromValue(body))
                }
                spec.retrieve().toEntity(ByteArray::class.java)
            }
            "DELETE" -> {
                client.delete()
                    .uri(url)
                    .headers { h -> copyHeaders(incomingHeaders, h) }
                    .retrieve()
                    .toEntity(ByteArray::class.java)
            }
            else -> {
                client.method(org.springframework.http.HttpMethod.valueOf(method.uppercase()))
                    .uri(url)
                    .headers { h -> copyHeaders(incomingHeaders, h) }
                    .retrieve()
                    .toEntity(ByteArray::class.java)
            }
        }
    }

    private fun copyHeaders(source: HttpHeaders, target: HttpHeaders) {
        source.forEach { (key, value) ->
            if (!HttpHeaders.HOST.equals(key, ignoreCase = true)) {
                target[key] = value
            }
        }
    }
}