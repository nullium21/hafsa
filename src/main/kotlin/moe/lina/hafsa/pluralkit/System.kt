@file:UseSerializers(UUIDSerializer::class, UrlSerializer::class)

package moe.lina.hafsa.pluralkit

import arrow.core.Either
import arrow.core.flatMap
import dev.kord.common.Color
import io.ktor.client.*
import io.ktor.http.*
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import moe.lina.hafsa.pluralkit.PluralKitAPI.BASE_URL
import moe.lina.hafsa.util.UUIDSerializer
import moe.lina.hafsa.util.UrlSerializer
import moe.lina.hafsa.util.fetch
import java.util.UUID

@Serializable
data class System(
    val id: String,
    val uuid: UUID,
    val name: String?,
    val description: String?,
    val tag: String?,
    val pronouns: String?,
    val avatarUrl: Url?,
    val banner: Url?,
    val color: Color?,
    val created: Instant?,

    val members: List<Member>? = null
) {
    suspend fun getMembers(client: HttpClient) =
        client.fetch<List<Member>>("${BASE_URL}/systems/${id}/members")

    suspend fun withMembers(client: HttpClient) =
        getMembers(client).map { copy(members = it) }

    companion object {
        suspend fun getById(client: HttpClient, id: String) =
            client.fetch<System>("${BASE_URL}/systems/${id}")
    }
}

suspend inline fun Either<HttpStatusCode, System>.withMembers(client: HttpClient) = flatMap { it.withMembers(client) }
