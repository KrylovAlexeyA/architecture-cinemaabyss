package com.cinemaabyss.proxy.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app")
class AppProperties {
    lateinit var monolithUrl: String
    lateinit var moviesServiceUrl: String
    lateinit var eventsServiceUrl: String
    lateinit var usersServiceUrl: String
    lateinit var paymentsServiceUrl: String
    lateinit var subscriptionsServiceUrl: String
    var gradualMigration: Boolean = true
    var moviesMigrationPercent: Int = 50
    var usersMigrationPercent: Int = 0
    var paymentsMigrationPercent: Int = 0
    var subscriptionsMigrationPercent: Int = 0
}
