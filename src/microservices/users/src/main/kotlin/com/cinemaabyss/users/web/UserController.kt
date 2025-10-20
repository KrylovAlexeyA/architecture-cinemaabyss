package com.cinemaabyss.users.web

import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

data class User(val id: Int? = null, val username: String, val email: String)

@RestController
@RequestMapping("/api/users")
class UserController(private val jdbcTemplate: JdbcTemplate) {

    private val mapper = RowMapper<User> { rs, _ ->
        User(
            id = rs.getInt("id"),
            username = rs.getString("username"),
            email = rs.getString("email")
        )
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun get(@RequestParam(required = false) id: Int?): Mono<Any> = Mono.fromCallable {
        if (id != null) {
            jdbcTemplate.queryForObject("SELECT id, username, email FROM users WHERE id = ?", mapper, id)
        } else {
            jdbcTemplate.query("SELECT id, username, email FROM users", mapper)
        }
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody user: User): Mono<User> = Mono.fromCallable {
        val newId = jdbcTemplate.queryForObject(
            "INSERT INTO users (username, email) VALUES (?, ?) RETURNING id",
            Int::class.java,
            user.username,
            user.email
        )!!
        user.copy(id = newId)
    }
}
