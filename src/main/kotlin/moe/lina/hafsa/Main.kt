package moe.lina.hafsa

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.on
import io.github.cdimascio.dotenv.dotenv
import moe.lina.hafsa.commands.EditMember
import moe.lina.hafsa.commands.MemberInfo
import moe.lina.hafsa.commands.SaveToken
import moe.lina.hafsa.commands.SystemInfo

suspend fun main(args: Array<String>) {
    val env = dotenv()
    val kord = Kord(env["TOKEN"])

    val commands = listOf(
        SystemInfo, MemberInfo, EditMember,
        SaveToken
    )

    commands.forEach {
        kord.createGuildChatInputCommand(
            Snowflake(1072869095990644746),
            it.name, it.description
        ) { with(it) { setup() } }
    }

    kord.on<GuildChatInputCommandInteractionCreateEvent> {
        commands.firstOrNull {
            if (it.name == interaction.command.rootName) it.tryHandle(kord, interaction)
            else false
        }
    }

    kord.on<SelectMenuInteractionCreateEvent> {
        commands.firstOrNull { it.tryHandleComponent(kord, this) }
    }

    kord.on<ButtonInteractionCreateEvent> {
        commands.firstOrNull { it.tryHandleComponent(kord, this) }
    }

    kord.login {
//        intents = Intents.all
    }
}
