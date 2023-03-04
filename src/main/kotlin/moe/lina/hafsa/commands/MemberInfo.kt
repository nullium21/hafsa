package moe.lina.hafsa.commands

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.EmbedBuilder
import moe.lina.hafsa.pluralkit.Member
import moe.lina.hafsa.pluralkit.PluralKitAPI

object MemberInfo : Command {

    override val name = "member"
    override val description = "Get member info"

    override suspend fun ChatInputCreateBuilder.setup() {
        string("id", "PluralKit Member ID") {
            minLength = 5
            maxLength = 5
            required = true
        }
    }

    override suspend fun tryHandle(kord: Kord, interaction: GuildChatInputCommandInteraction): Boolean {
        val interactionResponse = interaction.deferPublicResponse()
        val command = interaction.command
        val memberId = command.strings["id"]!!

        Member.getById(PluralKitAPI.CLIENT, memberId)
            .bimap({
                interactionResponse.respond { content = "**Fetching member `${memberId}` failed.** HTTP error code: **`${it}`**" }
            }) { member -> interactionResponse.respond { embeds = mutableListOf(createInfoEmbed(member)) }}

        return true
    }

    fun createInfoEmbed(member: Member) = EmbedBuilder().apply {
        member.color?.let { color = it }
        member.avatarUrl?.let { thumbnail { url = "$it" } }

        title = member.displayName ?: member.name
        member.description?.let { field("Description") { it } }

        field("PluralKit ID", true) { "`${member.id}`" }
        field("Pronouns", true) { member.pronouns ?: "Unknown" }
        field("Birthday", true) { "${member.birthday ?: "Unknown"}" }

        field("Tags") { member.proxyTags.joinToString("\n") { "> $it" } }
    }
}
