package moe.lina.hafsa.commands

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.string
import moe.lina.hafsa.pluralkit.PluralKitAPI

object SaveToken : Command {

    override val name = "pk-token"
    override val description = "Save the PluralKit system token for later use"

    override suspend fun ChatInputCreateBuilder.setup() {
        string("token", "PluralKit system token retrieved from `pk;token`") {
            minLength = 64
            maxLength = 64
            required = true
        }
    }

    override suspend fun tryHandle(kord: Kord, interaction: GuildChatInputCommandInteraction): Boolean {
        val command = interaction.command
        val token = command.strings["token"]!!
        val userId = interaction.user.id

        val response = interaction.deferEphemeralResponse()

        PluralKitAPI.SYSTEM_TOKENS[userId] = token

        response.respond { content = "Token saved!" }

        return true
    }
}
