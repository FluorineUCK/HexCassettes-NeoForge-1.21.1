package miyucomics.hexcassettes.inits

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.HexRegistries
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.actions.OpBusy
import miyucomics.hexcassettes.actions.OpDequeue
import miyucomics.hexcassettes.actions.OpEnqueue
import miyucomics.hexcassettes.actions.OpForetell
import miyucomics.hexcassettes.actions.OpInspect
import miyucomics.hexcassettes.actions.OpKillAll
import miyucomics.hexcassettes.actions.OpSelf
import miyucomics.hexcassettes.actions.OpSpecs
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object HexcassettesActions {
    private val ACTIONS: DeferredRegister<ActionRegistryEntry> =
        DeferredRegister.create(HexRegistries.ACTION, HexcassettesMain.MOD_ID)

    init {
        registerAll { id, entry -> ACTIONS.register(id.path, Supplier { entry }) }
    }

    fun register(bus: IEventBus) = ACTIONS.register(bus)

    fun registerAll(registrar: (ResourceLocation, ActionRegistryEntry) -> Unit) {
        register("enqueue", "qeqwqwqwqwqeqaweqqqqqwweeweweewqdwwewewwewweweww", HexDir.EAST, OpEnqueue(), registrar)
        register("dequeue", "eqeweweweweqedwqeeeeewwqqwqwqqweawwqwqwwqwwqwqww", HexDir.WEST, OpDequeue(), registrar)
        register("killall", "eqeweweweweqedwqeeeeewwqqwqwqqw", HexDir.WEST, OpKillAll(), registrar)
        register("specs", "qeqwqwqwqwqeqaweqqqqq", HexDir.EAST, OpSpecs(), registrar)
        register("busy", "qeqwqwqwqwqeqaweqqqqqaww", HexDir.EAST, OpBusy(), registrar)
        register("inspect", "eqeweweweweqedwqeeeee", HexDir.WEST, OpInspect(), registrar)
        register("foretell", "eqeweweweweqedwqeeeeedww", HexDir.WEST, OpForetell(), registrar)
        register("self", "qeqwqwqwqwqeqaweqqqqqwweeweweew", HexDir.EAST, OpSelf(), registrar)
    }

    private fun register(
        name: String,
        signature: String,
        startDir: HexDir,
        action: Action,
        registrar: (ResourceLocation, ActionRegistryEntry) -> Unit
    ) = registrar(
        HexcassettesMain.id(name),
        ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action)
    )
}
