package moe.lina.hafsa.commands

import dev.kord.core.Kord
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.core.event.interaction.ComponentInteractionCreateEvent
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder

interface Command {

    val name: String
    val description: String

    suspend fun ChatInputCreateBuilder.setup()
    suspend fun tryHandle(kord: Kord, interaction: GuildChatInputCommandInteraction): Boolean
    suspend fun tryHandleComponent(kord: Kord, event: ComponentInteractionCreateEvent): Boolean = false
}
