package miyucomics.hexcassettes

import at.petrak.hexcasting.api.HexAPI
import miyucomics.hexcassettes.advancement.HexcassettesCriterion
import miyucomics.hexcassettes.client.ClientBootstrap
import miyucomics.hexcassettes.inits.HexcassettesActions
import miyucomics.hexcassettes.inits.HexcassettesAttachments
import miyucomics.hexcassettes.inits.getCassetteState
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import miyucomics.hexcassettes.inits.HexcassettesSounds
import net.minecraft.advancements.CriterionTrigger
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

@Mod(HexcassettesMain.MOD_ID)
class HexcassettesMain(modBus: IEventBus, container: ModContainer) {
    init {
        ITEMS.register(modBus)
        TRIGGERS.register(modBus)
        HexcassettesSounds.register(modBus)
        HexcassettesAttachments.register(modBus)
        HexcassettesNetworking.register(modBus)
        HexcassettesActions.register(modBus)
        modBus.addListener(::addCreativeTabContents)
        NeoForge.EVENT_BUS.addListener(::tickPlayer)
        NeoForge.EVENT_BUS.addListener(::onLogin)
        NeoForge.EVENT_BUS.addListener(::onClone)
        container.registerConfig(ModConfig.Type.COMMON, HexcassettesConfiguration.SPEC)

        if (FMLEnvironment.dist == Dist.CLIENT) ClientBootstrap.initialize(modBus)
    }

    private fun addCreativeTabContents(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey == HexAPI.modLoc("hexcasting")) event.accept(CASSETTE.get())
    }

    private fun tickPlayer(event: PlayerTickEvent.Post) {
        val player = event.entity
        if (!player.level().isClientSide && player is ServerPlayer) player.getCassetteState().tick(player)
    }

    private fun onLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        (event.entity as? ServerPlayer)?.let { it.getCassetteState().sync(it) }
    }

    private fun onClone(event: PlayerEvent.Clone) {
        // AttachmentType.copyOnDeath performs the persistent clone. Sync the resulting value.
        (event.entity as? ServerPlayer)?.let { it.getCassetteState().sync(it) }
    }

    companion object {
        const val MOD_ID = "hexcassettes"
        fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)

        private val ITEMS = DeferredRegister.create(Registries.ITEM, MOD_ID)
        @JvmField val CASSETTE: Supplier<CassetteItem> = ITEMS.register("cassette", ::CassetteItem)

        private val TRIGGERS = DeferredRegister.create<CriterionTrigger<*>>(Registries.TRIGGER_TYPE, MOD_ID)
        @JvmField val QUINIO = HexcassettesCriterion()
        @JvmField val TAPE_WORM = HexcassettesCriterion()
        @JvmField val FULL_ARSENAL = HexcassettesCriterion()

        init {
            TRIGGERS.register("quinio", Supplier { QUINIO })
            TRIGGERS.register("tape_worm", Supplier { TAPE_WORM })
            TRIGGERS.register("full_arsenal", Supplier { FULL_ARSENAL })
        }
    }
}
