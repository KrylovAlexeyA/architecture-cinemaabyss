package com.cinemaabyss.subscriptions.web

import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

data class Subscription(
    val id: Int? = null,
    val user_id: Int,
    val plan_type: String,
    val start_date: OffsetDateTime,
    val end_date: OffsetDateTime
)

@RestController
@RequestMapping("/api/subscriptions")
class SubscriptionController(private val jdbcTemplate: JdbcTemplate) {

    private val mapper = RowMapper<Subscription> { rs, _ ->
        Subscription(
            id = rs.getInt("id"),
            user_id = rs.getInt("user_id"),
            plan_type = rs.getString("plan_type"),
            start_date = rs.getObject("start_date", OffsetDateTime::class.java),
            end_date = rs.getObject("end_date", OffsetDateTime::class.java)
        )
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun get(
        @RequestParam(required = false) id: Int?,
        @RequestParam(name = "user_id", required = false) userId: Int?
    ): Mono<Any> = Mono.fromCallable {
        when {
            id != null -> jdbcTemplate.queryForObject(
                "SELECT id, user_id, plan_type, start_date, end_date FROM subscriptions WHERE id = ?",
                mapper, id
            )
            userId != null -> jdbcTemplate.query(
                "SELECT id, user_id, plan_type, start_date, end_date FROM subscriptions WHERE user_id = ?",
                mapper, userId
            )
            else -> jdbcTemplate.query(
                "SELECT id, user_id, plan_type, start_date, end_date FROM subscriptions",
                mapper
            )
        }
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody s: Subscription): Mono<Subscription> = Mono.fromCallable {
        val newId = jdbcTemplate.queryForObject(
            "INSERT INTO subscriptions (user_id, plan_type, start_date, end_date) VALUES (?, ?, ?, ?) RETURNING id",
            Int::class.java,
            s.user_id,
            s.plan_type,
            s.start_date,
            s.end_date
        )!!
        s.copy(id = newId)
    }
}
