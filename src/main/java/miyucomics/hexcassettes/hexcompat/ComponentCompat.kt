package miyucomics.hexcassettes.hexcompat

import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization

object ComponentCompat {
    fun encode(component: Component): String =
        ComponentSerialization.CODEC
            .encodeStart(NbtOps.INSTANCE, component)
            .getOrThrow()
            .toString()

    fun decode(serialized: String): Component =
        ComponentSerialization.CODEC
            .parse(NbtOps.INSTANCE, net.minecraft.nbt.TagParser.parseTag(serialized))
            .result()
            .orElseGet { Component.literal(serialized.take(MAX_KEY_CHARS)) }

    const val MAX_KEY_CHARS = 2048
}
