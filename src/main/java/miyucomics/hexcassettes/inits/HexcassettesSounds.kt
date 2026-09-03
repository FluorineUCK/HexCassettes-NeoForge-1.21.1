package miyucomics.hexcassettes.inits

import miyucomics.hexcassettes.HexcassettesMain
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvent
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object HexcassettesSounds {
    private val SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, HexcassettesMain.MOD_ID)

    @JvmField val CASSETTE_EJECT = register("cassette_eject")
    @JvmField val CASSETTE_FAIL = register("cassette_fail")
    @JvmField val CASSETTE_INSERT = register("cassette_insert")
    @JvmField val CASSETTE_LOOP = register("cassette_loop")

    fun register(bus: IEventBus) = SOUNDS.register(bus)

    private fun register(name: String): Supplier<SoundEvent> =
        SOUNDS.register(name, Supplier { SoundEvent.createVariableRangeEvent(HexcassettesMain.id(name)) })
}
