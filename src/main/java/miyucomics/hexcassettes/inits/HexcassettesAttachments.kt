package miyucomics.hexcassettes.inits

import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.data.CassetteState
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import java.util.function.Supplier

object HexcassettesAttachments {
    private val TYPES: DeferredRegister<AttachmentType<*>> =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, HexcassettesMain.MOD_ID)

    @JvmField
    val CASSETTE_STATE: Supplier<AttachmentType<CassetteState>> = TYPES.register(
        "cassette_state",
        Supplier { AttachmentType.serializable(::CassetteState).copyOnDeath().build() }
    )

    fun register(modBus: IEventBus) = TYPES.register(modBus)
}

fun Player.getCassetteState(): CassetteState = getData(HexcassettesAttachments.CASSETTE_STATE)
