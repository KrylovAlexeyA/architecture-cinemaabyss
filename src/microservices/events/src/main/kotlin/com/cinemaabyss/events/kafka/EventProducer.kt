package com.cinemaabyss.events.kafka

import com.cinemaabyss.events.model.*
import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.concurrent.CompletableFuture

private val logger = KotlinLogging.logger {}

@Service
class EventProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    fun publishMovieEvent(movieEvent: MovieEvent): CompletableFuture<SendResult<String, Any>> {
        val event = Event(
            id = "movie-${movieEvent.movie_id}-${movieEvent.action}",
            type = "movie",
            timestamp = OffsetDateTime.now(),
            payload = movieEvent
        )
        
        logger.info { "Publishing movie event: $event" }
        
        return kafkaTemplate.send("movie-events", event.id, event)
            .whenComplete { result, throwable ->
                if (throwable == null) {
                    logger.info { "Movie event published successfully: ${result.recordMetadata}" }
                } else {
                    logger.error(throwable) { "Failed to publish movie event" }
                }
            }
    }

    fun publishUserEvent(userEvent: UserEvent): CompletableFuture<SendResult<String, Any>> {
        val event = Event(
            id = "user-${userEvent.user_id}-${userEvent.action}",
            type = "user",
            timestamp = userEvent.timestamp,
            payload = userEvent
        )
        
        logger.info { "Publishing user event: $event" }
        
        return kafkaTemplate.send("user-events", event.id, event)
            .whenComplete { result, throwable ->
                if (throwable == null) {
                    logger.info { "User event published successfully: ${result.recordMetadata}" }
                } else {
                    logger.error(throwable) { "Failed to publish user event" }
                }
            }
    }

    fun publishPaymentEvent(paymentEvent: PaymentEvent): CompletableFuture<SendResult<String, Any>> {
        val event = Event(
            id = "payment-${paymentEvent.payment_id}-${paymentEvent.status}",
            type = "payment",
            timestamp = paymentEvent.timestamp,
            payload = paymentEvent
        )
        
        logger.info { "Publishing payment event: $event" }
        
        return kafkaTemplate.send("payment-events", event.id, event)
            .whenComplete { result, throwable ->
                if (throwable == null) {
                    logger.info { "Payment event published successfully: ${result.recordMetadata}" }
                } else {
                    logger.error(throwable) { "Failed to publish payment event" }
                }
            }
    }
}
