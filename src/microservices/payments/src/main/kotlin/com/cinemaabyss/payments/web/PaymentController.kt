package com.cinemaabyss.payments.web

import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

data class Payment(val id: Int? = null, val user_id: Int, val amount: Double, val timestamp: OffsetDateTime? = null)

@RestController
@RequestMapping("/api/payments")
class PaymentController(private val jdbcTemplate: JdbcTemplate) {

    private val mapper = RowMapper<Payment> { rs, _ ->
        Payment(
            id = rs.getInt("id"),
            user_id = rs.getInt("user_id"),
            amount = rs.getDouble("amount"),
            timestamp = rs.getObject("timestamp", OffsetDateTime::class.java)
        )
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun get(
        @RequestParam(required = false) id: Int?,
        @RequestParam(name = "user_id", required = false) userId: Int?
    ): Mono<Any> = Mono.fromCallable {
        when {
            id != null -> jdbcTemplate.queryForObject("SELECT id, user_id, amount, timestamp FROM payments WHERE id = ?", mapper, id)
            userId != null -> jdbcTemplate.query("SELECT id, user_id, amount, timestamp FROM payments WHERE user_id = ?", mapper, userId)
            else -> jdbcTemplate.query("SELECT id, user_id, amount, timestamp FROM payments", mapper)
        }
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody payment: Payment): Mono<Payment> = Mono.fromCallable {
        val newId = jdbcTemplate.queryForObject(
            "INSERT INTO payments (user_id, amount, timestamp) VALUES (?, ?, now()) RETURNING id",
            Int::class.java,
            payment.user_id,
            payment.amount
        )!!
        payment.copy(id = newId, timestamp = OffsetDateTime.now())
    }
}
