package miyucomics.hexcassettes.data

import miyucomics.hexcassettes.inits.HexcassettesNetworking
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.common.util.INBTSerializable
import java.util.concurrent.ConcurrentHashMap

class CassetteState : INBTSerializable<CompoundTag> {
    var owned = 0
    val hexes: MutableMap<String, QueuedHex> = ConcurrentHashMap()
    private var previouslyActiveSlots: Set<String> = emptySet()

    fun sync(player: ServerPlayer) = HexcassettesNetworking.sendSync(player, this)

    fun tick(player: ServerPlayer) {
        hexes.values.forEach { it.delay -= 1 }
        val ready = hexes.entries.filter { it.value.delay <= 0 }
        ready.forEach { (pattern, hex) ->
            if (hexes.remove(pattern, hex)) hex.cast(player, pattern)
        }
        val activeSlots = hexes.keys.toSet()
        if (previouslyActiveSlots != activeSlots) sync(player)
        previouslyActiveSlots = activeSlots
    }

    override fun serializeNBT(provider: HolderLookup.Provider): CompoundTag = serialize()

    fun serialize(): CompoundTag = CompoundTag().also { compound ->
        compound.putInt("owned", owned)
        val serialized = CompoundTag()
        hexes.forEach { (pattern, hex) -> serialized.put(pattern, hex.serialize()) }
        compound.put("hexes", serialized)
    }

    override fun deserializeNBT(provider: HolderLookup.Provider, nbt: CompoundTag) {
        read(nbt)
    }

    private fun read(compound: CompoundTag) {
        owned = compound.getInt("owned")
        hexes.clear()
        val serializedHexes = compound.getCompound("hexes")
        serializedHexes.allKeys.forEach { key ->
            hexes[key] = QueuedHex.deserialize(serializedHexes.getCompound(key))
        }
        previouslyActiveSlots = hexes.keys.toSet()
    }

    companion object {
        @JvmStatic
        fun deserialize(compound: CompoundTag) = CassetteState().also { it.read(compound) }
    }
}
