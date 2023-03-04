package moe.lina.hafsa.pluralkit

import dev.kord.common.entity.Snowflake
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

object PluralKitAPI {
    const val BASE_URL = "https://api.pluralkit.me/v2"

    val SYSTEM_TOKENS = mutableMapOf<Snowflake, String>()

    @OptIn(ExperimentalSerializationApi::class)
    val CLIENT = HttpClient {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                namingStrategy = JsonNamingStrategy.SnakeCase
                ignoreUnknownKeys = true
                explicitNulls = false

            })
        }
    }
}
