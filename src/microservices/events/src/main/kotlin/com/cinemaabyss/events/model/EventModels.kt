package com.cinemaabyss.events.model

import java.time.OffsetDateTime

data class MovieEvent(
    val movie_id: Int,
    val title: String,
    val action: String,
    val user_id: Int? = null,
    val rating: Double? = null,
    val genres: List<String>? = null,
    val description: String? = null
)

data class UserEvent(
    val user_id: Int,
    val action: String,
    val timestamp: OffsetDateTime,
    val username: String? = null,
    val email: String? = null
)

data class PaymentEvent(
    val payment_id: Int,
    val user_id: Int,
    val amount: Double,
    val status: String,
    val timestamp: OffsetDateTime,
    val method_type: String? = null
)

data class Event(
    val id: String,
    val type: String,
    val timestamp: OffsetDateTime,
    val payload: Any
)

data class EventResponse(
    val status: String,
    val partition: Int,
    val offset: Long,
    val event: Event
)
