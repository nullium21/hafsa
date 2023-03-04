package moe.lina.hafsa.commands

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.EmbedBuilder
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import moe.lina.hafsa.pluralkit.PluralKitAPI
import moe.lina.hafsa.pluralkit.System
import moe.lina.hafsa.pluralkit.withMembers

object SystemInfo : Command {

    override val name = "system"
    override val description = "Get system info"

    override suspend fun ChatInputCreateBuilder.setup() {
        string("id", "PluralKit System ID") {
            minLength = 5
            maxLength = 5
            required = true
        }
    }

    override suspend fun tryHandle(kord: Kord, interaction: GuildChatInputCommandInteraction): Boolean {
        val interactionResponse = interaction.deferPublicResponse()
        val command = interaction.command
        val systemId = command.strings["id"]!!

        System.getById(PluralKitAPI.CLIENT, systemId)
            .withMembers(PluralKitAPI.CLIENT)
            .bimap({
                interactionResponse.respond { content = "**Fetching system `${systemId}` failed.** HTTP error code: **`${it}`**" }
            }) { system -> interactionResponse.respond { embeds = mutableListOf(EmbedBuilder().apply {
                system.color?.let { color = it }
                system.avatarUrl?.let { thumbnail { url = "$it" } }

                title = "${system.name ?: ""} System (`${system.id}`, ${system.members!!.size} members):"

                system.members.forEach { member ->
                    field {
                        name = member.displayName ?: member.name
                        inline = true
                        value = """
                            > **PluralKit ID**: `${member.id}`
                            > **Tags**: ${member.proxyTags.joinToString(", ")}
                        """.trimIndent()
                    }
                }
            }) } }

        return true
    }
}
