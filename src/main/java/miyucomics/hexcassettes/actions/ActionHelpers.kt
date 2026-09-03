package miyucomics.hexcassettes.actions

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import miyucomics.hexcassettes.data.CassetteState
import miyucomics.hexcassettes.inits.getCassetteState
import miyucomics.hexpose.iotas.DisplayIota
import net.minecraft.server.level.ServerPlayer

internal fun CastingEnvironment.requireCassettePlayer(): ServerPlayer =
    (castingEntity as? ServerPlayer) ?: throw MishapBadCaster()

internal fun CastingEnvironment.requireCassetteState(): CassetteState =
    requireCassettePlayer().getCassetteState()

internal fun List<Iota>.getDisplayIota(index: Int, argc: Int): DisplayIota {
    val value = getOrElse(index) { throw MishapNotEnoughArgs(index + 1, size) }
    if (value is DisplayIota) return value
    throw MishapInvalidIota.ofType(value, argc - (index + 1), "display")
}
