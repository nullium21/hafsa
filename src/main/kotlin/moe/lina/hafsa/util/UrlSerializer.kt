package moe.lina.hafsa.util

import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object UrlSerializer : KSerializer<Url> {
    override val descriptor = PrimitiveSerialDescriptor("Url", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder)
        = Url(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: Url)
        = encoder.encodeString(value.toString())
}