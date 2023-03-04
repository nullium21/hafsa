package moe.lina.hafsa.commands

import dev.kord.common.Color
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.TextInputStyle
import dev.kord.core.Kord
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.User
import dev.kord.core.entity.component.SelectMenuComponent
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.core.entity.interaction.OptionValue
import dev.kord.core.event.interaction.ComponentInteractionCreateEvent
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.attachment
import dev.kord.rest.builder.interaction.boolean
import dev.kord.rest.builder.interaction.string
import io.ktor.http.*
import kotlinx.datetime.LocalDate
import moe.lina.hafsa.pluralkit.Member
import moe.lina.hafsa.pluralkit.PluralKitAPI

object EditMember : Command {
    override val name = "member-edit"
    override val description = "Edit a member's info"

    override suspend fun ChatInputCreateBuilder.setup() {
        string("id", "PluralKit Member ID") {
            minLength = 5
            maxLength = 5
            required = true
        }

        string("name", "New name")
        string("display-name", "New display name")
        string("description", "New description")
        string("color", "New color (in RRGGBB format)") {
            minLength = 6
            maxLength = 6
        }
        string("birthday", "New birthday")
        boolean("autoproxy", "Enable/disable autoproxy")
        attachment("avatar", "New profile picture")
    }

    private val currentlyEditing = mutableMapOf<Snowflake, Member>()
    private val interactionAuthors = mutableMapOf<Snowflake, User>()
    private val interactionData = mutableMapOf<Snowflake, Map<String, OptionValue<*>>>()

    override suspend fun tryHandle(kord: Kord, interaction: GuildChatInputCommandInteraction): Boolean {
        val command = interaction.command
        val memberId = command.strings["id"]!!

        val mainResponse = interaction.deferPublicResponse()

        Member.getById(PluralKitAPI.CLIENT, memberId)
            .bimap({
                mainResponse.respond {
                    content = "**Fetching member `${memberId}` failed.** HTTP error code: **`${it}`**"
                }
            }) { member ->
                val newMember = member.copy(
                    name = command.strings["name"] ?: member.name,
                    displayName = command.strings["display-name"] ?: member.displayName,
                    description = command.strings["description"] ?: member.description,
                    color = command.strings["color"]?.let { Color(it.toInt(16)) } ?: member.color,
                    birthday = command.strings["birthday"]?.let { LocalDate.parse(it) } ?: member.birthday,
                    autoproxyEnabled = command.booleans["autoproxy"] ?: member.autoproxyEnabled,
                    avatarUrl = command.attachments["avatar"]?.let { Url(it.url) } ?: member.avatarUrl
                )

                val response = mainResponse.respond {
                    embeds = mutableListOf(MemberInfo.createInfoEmbed(newMember)
                        .also { it.title = "Editing **`${it.title}`**" })
                    components = createEmbedButtons()
                }

                currentlyEditing[response.message.id] = newMember
                interactionAuthors[response.message.id] = interaction.user
                interactionData[response.message.id] = command.options
            }

        return true
    }

    override suspend fun tryHandleComponent(kord: Kord, event: ComponentInteractionCreateEvent): Boolean {
        val interaction = event.interaction
        val messageId = interaction.message.id

        if (interaction.componentId != "edit-save" && interaction.componentId != "edit-cancel")
            return false

        if (!interactionAuthors.contains(messageId)
            || !currentlyEditing.contains(messageId)
            || !interactionData.contains(messageId)) {
            interaction.deferEphemeralResponse().respond {
                content = "**Error: Invalid snowflake. Must be a bug.**"
            }

            interaction.message.delete("Invalid snowflake")
            currentlyEditing.remove(messageId)
            interactionAuthors.remove(messageId)
            interactionData.remove(messageId)
            return true
        }

        when (interaction.componentId) {
            "edit-save" -> {
                val token = PluralKitAPI.SYSTEM_TOKENS[interaction.user.id]

                interaction.message.edit {
                    embeds = mutableListOf(MemberInfo.createInfoEmbed(currentlyEditing[messageId]!!)
                        .also { it.title = "New info: **`${it.title}`**" })

                    components = if (token != null) mutableListOf() else createEmbedButtons()
                }

                if (token == null) {
                    interaction.respondEphemeral {
                        content =
                            "**Unknown [system token](https://pluralkit.me/api/#authentication). Provide one via `/pk-token` and save again.**"
                    }
                } else {
                    println("Token: '$token'")
                    Member.edit(
                        PluralKitAPI.CLIENT, currentlyEditing[messageId]!!.id,
                        Member.Partial(currentlyEditing[messageId]!!),
                        token
                    )

                    currentlyEditing.remove(messageId)
                    interactionAuthors.remove(messageId)
                    interactionData.remove(messageId)
                }
            }

            "edit-cancel" -> {
                interaction.deferEphemeralResponse().respond {
                    content = "**Cancelled.**"
                }

                interaction.message.delete("Interaction cancelled")
                currentlyEditing.remove(messageId)
                interactionAuthors.remove(messageId)
                interactionData.remove(messageId)
            }
        }

        return true
    }

    private fun createEmbedButtons(): MutableList<MessageComponentBuilder> = mutableListOf(ActionRowBuilder().apply {
        interactionButton(ButtonStyle.Danger, "edit-save") { label = "Save" }
        interactionButton(ButtonStyle.Primary, "edit-cancel") { label = "Cancel" }
    })
}
