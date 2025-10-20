package com.cinemaabyss.events.web

import com.cinemaabyss.events.kafka.EventProducer
import com.cinemaabyss.events.model.*
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventProducer: EventProducer
) {

    @GetMapping("/health", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun health(): ResponseEntity<Map<String, Boolean>> {
        logger.info { "Health check requested" }
        return ResponseEntity.ok(mapOf("status" to true))
    }

    @PostMapping("/movie", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createMovieEvent(@RequestBody movieEvent: MovieEvent): Mono<ResponseEntity<EventResponse>> {
        logger.info { "Creating movie event: $movieEvent" }
        
        return Mono.fromFuture(eventProducer.publishMovieEvent(movieEvent))
            .map { result ->
                val recordMetadata = result.recordMetadata
                val response = EventResponse(
                    status = "success",
                    partition = recordMetadata.partition(),
                    offset = recordMetadata.offset(),
                    event = Event(
                        id = "movie-${movieEvent.movie_id}-${movieEvent.action}",
                        type = "movie",
                        timestamp = OffsetDateTime.now(),
                        payload = movieEvent
                    )
                )
                ResponseEntity.status(201).body(response)
            }
            .doOnSuccess { response ->
                logger.info { "Movie event created successfully: $response" }
            }
            .doOnError { error ->
                logger.error(error) { "Failed to create movie event" }
            }
    }

    @PostMapping("/user", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createUserEvent(@RequestBody userEvent: UserEvent): Mono<ResponseEntity<EventResponse>> {
        logger.info { "Creating user event: $userEvent" }
        
        return Mono.fromFuture(eventProducer.publishUserEvent(userEvent))
            .map { result ->
                val recordMetadata = result.recordMetadata
                val response = EventResponse(
                    status = "success",
                    partition = recordMetadata.partition(),
                    offset = recordMetadata.offset(),
                    event = Event(
                        id = "user-${userEvent.user_id}-${userEvent.action}",
                        type = "user",
                        timestamp = userEvent.timestamp,
                        payload = userEvent
                    )
                )
                ResponseEntity.status(201).body(response)
            }
            .doOnSuccess { response ->
                logger.info { "User event created successfully: $response" }
            }
            .doOnError { error ->
                logger.error(error) { "Failed to create user event" }
            }
    }

    @PostMapping("/payment", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createPaymentEvent(@RequestBody paymentEvent: PaymentEvent): Mono<ResponseEntity<EventResponse>> {
        logger.info { "Creating payment event: $paymentEvent" }
        
        return Mono.fromFuture(eventProducer.publishPaymentEvent(paymentEvent))
            .map { result ->
                val recordMetadata = result.recordMetadata
                val response = EventResponse(
                    status = "success",
                    partition = recordMetadata.partition(),
                    offset = recordMetadata.offset(),
                    event = Event(
                        id = "payment-${paymentEvent.payment_id}-${paymentEvent.status}",
                        type = "payment",
                        timestamp = paymentEvent.timestamp,
                        payload = paymentEvent
                    )
                )
                ResponseEntity.status(201).body(response)
            }
            .doOnSuccess { response ->
                logger.info { "Payment event created successfully: $response" }
            }
            .doOnError { error ->
                logger.error(error) { "Failed to create payment event" }
            }
    }
}
