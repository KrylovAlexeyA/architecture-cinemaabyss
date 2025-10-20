package com.cinemaabyss.events.kafka

import com.cinemaabyss.events.model.Event
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class EventConsumer {

    @KafkaListener(topics = ["movie-events"], groupId = "events-service-group")
    fun consumeMovieEvent(
        @Payload event: Event,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        acknowledgment: Acknowledgment
    ) {
        logger.info { "Consumed movie event from partition $partition at offset $offset: $event" }
        acknowledgment.acknowledge()
    }

    @KafkaListener(topics = ["user-events"], groupId = "events-service-group")
    fun consumeUserEvent(
        @Payload event: Event,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        acknowledgment: Acknowledgment
    ) {
        logger.info { "Consumed user event from partition $partition at offset $offset: $event" }
        acknowledgment.acknowledge()
    }

    @KafkaListener(topics = ["payment-events"], groupId = "events-service-group")
    fun consumePaymentEvent(
        @Payload event: Event,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        acknowledgment: Acknowledgment
    ) {
        logger.info { "Consumed payment event from partition $partition at offset $offset: $event" }
        
        // Здесь можно добавить дополнительную обработку события
        
        acknowledgment.acknowledge()
    }
}
