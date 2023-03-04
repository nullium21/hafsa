package moe.lina.hafsa.util

import dev.kord.common.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ColorToStringSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("PluralKit.color", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Color
        = Color(decoder.decodeString().toInt(16))

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeString("${
            value.red.toString(16).padStart(2, '0')
        }${
            value.green.toString(16).padStart(2, '0')
        }${
            value.blue.toString(16).padStart(2, '0')
        }")
    }
}