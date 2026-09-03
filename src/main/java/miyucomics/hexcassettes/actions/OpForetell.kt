package miyucomics.hexcassettes.actions

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexcassettes.hexcompat.ComponentCompat

class OpForetell : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val key = ComponentCompat.encode(args.getDisplayIota(0, argc).text)
        return env.requireCassetteState().hexes[key]?.delay?.asActionResult ?: null.asActionResult
    }
}
