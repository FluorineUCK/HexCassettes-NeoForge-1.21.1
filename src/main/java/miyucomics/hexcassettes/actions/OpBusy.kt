package miyucomics.hexcassettes.actions

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexcassettes.hexcompat.ComponentCompat
import miyucomics.hexpose.iotas.DisplayIota

class OpBusy : ConstMediaAction {
    override val argc = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> =
        env.requireCassetteState().hexes.keys
            .map { DisplayIota(ComponentCompat.decode(it)) }
            .asActionResult
}
