package miyucomics.hexcassettes.hexcompat

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag

object IotaCodecCompat {
    fun encode(iota: Iota): Tag =
        IotaType.TYPED_CODEC.encodeStart(NbtOps.INSTANCE, iota).getOrThrow()

    fun decode(tag: Tag): Iota? =
        IotaType.TYPED_CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(null)
}
