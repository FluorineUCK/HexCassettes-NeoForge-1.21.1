package miyucomics.hexcassettes.data

import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.ListIota
import miyucomics.hexcassettes.CassetteCastEnv
import miyucomics.hexcassettes.hexcompat.IotaCodecCompat
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand

data class QueuedHex(val hex: CompoundTag, var delay: Int, val depth: Int = 0) {
    fun serialize(): CompoundTag = CompoundTag().also { compound ->
        // Upstream equivalent: putCompound("hex", hex).
        compound.put("hex", hex.copy())
        compound.putInt("delay", delay)
        compound.putInt("depth", depth)
    }

    fun cast(player: ServerPlayer, key: String) {
        val hand = if (!player.mainHandItem.isEmpty && player.offhandItem.isEmpty)
            InteractionHand.OFF_HAND else InteractionHand.MAIN_HAND
        val harness = CastingVM.empty(CassetteCastEnv(player, hand, key, depth))
        val hexIota = IotaCodecCompat.decode(hex)
        if (hexIota is ListIota)
            harness.queueExecuteAndWrapIotas(hexIota.list.toList(), player.serverLevel())
    }

    companion object {
        fun deserialize(compound: CompoundTag) = QueuedHex(
            compound.getCompound("hex"),
            compound.getInt("delay"),
            compound.getInt("depth")
        )
    }
}
