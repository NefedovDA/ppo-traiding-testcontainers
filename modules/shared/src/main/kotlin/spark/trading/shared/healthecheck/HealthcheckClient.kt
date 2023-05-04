package spark.trading.shared.healthecheck

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class HealthcheckClient(private val client: HttpClient) {
    suspend fun check(servers: Map<String, String>): String? = run {
        val errors =
            servers
                .mapValues { (_, host) ->
                    client.get(host + "healthcheck")
                        .run { if (status == HttpStatusCode.OK) null else bodyAsText() }
                }
                .filterValues { it != null }

        if (errors.isEmpty()) return@run null
        "Some of used servers are not available: " +
            errors.toList()
                .joinToString(separator = ", ") { (name, error) -> "$name - $error" }
    }
}
