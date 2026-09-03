package miyucomics.hexcassettes.inits

import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.client.ClientStorage
import miyucomics.hexcassettes.data.CassetteState
import miyucomics.hexcassettes.hexcompat.ComponentCompat
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadContext

data class CassetteRemovePayload(val key: String) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    companion object {
        val TYPE = CustomPacketPayload.Type<CassetteRemovePayload>(HexcassettesMain.id("cassette_remove"))
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, CassetteRemovePayload> =
            StreamCodec.of(
                { buffer, payload -> buffer.writeUtf(payload.key, ComponentCompat.MAX_KEY_CHARS) },
                { buffer -> CassetteRemovePayload(buffer.readUtf(ComponentCompat.MAX_KEY_CHARS)) }
            )
    }
}

data class SyncCassettesPayload(val owned: Int, val keys: List<String>) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    companion object {
        const val MAX_CASSETTES_ON_WIRE = 64
        val TYPE = CustomPacketPayload.Type<SyncCassettesPayload>(HexcassettesMain.id("sync_cassettes"))
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SyncCassettesPayload> =
            StreamCodec.of(
                { buffer, payload ->
                    val keys = payload.keys.take(MAX_CASSETTES_ON_WIRE)
                    buffer.writeVarInt(payload.owned.coerceIn(0, MAX_CASSETTES_ON_WIRE))
                    buffer.writeVarInt(keys.size)
                    keys.forEach { buffer.writeUtf(it, ComponentCompat.MAX_KEY_CHARS) }
                },
                { buffer ->
                    val owned = buffer.readVarInt().coerceIn(0, MAX_CASSETTES_ON_WIRE)
                    val count = buffer.readVarInt().coerceIn(0, MAX_CASSETTES_ON_WIRE)
                    SyncCassettesPayload(owned, List(count) { buffer.readUtf(ComponentCompat.MAX_KEY_CHARS) })
                }
            )
    }
}

object HexcassettesNetworking {
    fun register(modBus: IEventBus) = modBus.addListener(::registerPayloadHandlers)

    private fun registerPayloadHandlers(event: RegisterPayloadHandlersEvent) {
        event.registrar("1")
            .playToServer(
                CassetteRemovePayload.TYPE,
                CassetteRemovePayload.STREAM_CODEC,
                ::handleCassetteRemove
            )
            .playToClient(
                SyncCassettesPayload.TYPE,
                SyncCassettesPayload.STREAM_CODEC,
                ::handleSyncCassettes
            )
    }

    fun sendRemove(key: String) =
        PacketDistributor.sendToServer(CassetteRemovePayload(key.take(ComponentCompat.MAX_KEY_CHARS)))

    fun requestSync() = PacketDistributor.sendToServer(CassetteRemovePayload(""))

    fun sendSync(player: ServerPlayer, state: CassetteState) =
        PacketDistributor.sendToPlayer(
            player,
            SyncCassettesPayload(state.owned, state.hexes.keys.take(SyncCassettesPayload.MAX_CASSETTES_ON_WIRE))
        )

    private fun handleCassetteRemove(payload: CassetteRemovePayload, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork
            val state = player.getCassetteState()
            if (payload.key.isNotEmpty()) state.hexes.remove(payload.key)
            state.sync(player)
        }
    }

    private fun handleSyncCassettes(payload: SyncCassettesPayload, context: IPayloadContext) {
        context.enqueueWork {
            ClientStorage.ownedCassettes = payload.owned
            ClientStorage.activeCassettes = payload.keys
                .map(ComponentCompat::decode)
                .toMutableList()
        }
    }
}
