@file:UseSerializers(UUIDSerializer::class, UrlSerializer::class, ColorToStringSerializer::class)

package moe.lina.hafsa.pluralkit

import kotlinx.serialization.UseSerializers
import moe.lina.hafsa.util.UUIDSerializer
import moe.lina.hafsa.util.UrlSerializer

import dev.kord.common.Color
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import moe.lina.hafsa.pluralkit.PluralKitAPI.BASE_URL
import moe.lina.hafsa.util.ColorToStringSerializer
import moe.lina.hafsa.util.fetch
import java.util.UUID

@Serializable
data class ProxyTag(val prefix: String?, val suffix: String?) {
    override fun toString() =
        "${
            prefix?.let { "`$it`" } ?: ""
        }**`text`**${
            suffix?.let { "`$it`" } ?: ""
        }"
}

@Serializable
data class Member(
    val id: String,
    val uuid: UUID,
    val name: String,
//    val system: String?,
    val displayName: String?,
    val color: Color?,
    val birthday: LocalDate?,
    val pronouns: String?,
    val avatarUrl: Url?,
//    val webhookAvatarUrl: Url?,
    val banner: Url?,
    val description: String?,
    val created: Instant?,
    val proxyTags: List<ProxyTag>,
    val keepProxy: Boolean,
    val autoproxyEnabled: Boolean?
) {
    @Serializable
    data class Partial(
        val name: String?,
        val displayName: String?,
        val color: Color?,
        val birthday: LocalDate?,
        val pronouns: String?,
        val avatarUrl: Url?,
        val banner: Url?,
        val description: String?,
        val proxyTags: List<ProxyTag>?,
        val keepProxy: Boolean?,
        val autoproxyEnabled: Boolean?
    ) {
        constructor(full: Member)
                : this(
            full.name,
            full.displayName,
            full.color,
            full.birthday,
            full.pronouns,
            full.avatarUrl,
            full.banner,
            full.description,
            full.proxyTags,
            full.keepProxy,
            full.autoproxyEnabled
        )
    }

    companion object {
        suspend fun getById(client: HttpClient, id: String) =
            client.fetch<Member>("${BASE_URL}/members/${id}")

        suspend fun edit(client: HttpClient, id: String, data: Partial, auth: String) =
            client.patch("${BASE_URL}/members/$id") {
                userAgent("Hafsa 1.0 by nullium21")
                contentType(ContentType.Application.Json)
                setBody(data)
                header("Authorization", auth)
            }
    }
}
