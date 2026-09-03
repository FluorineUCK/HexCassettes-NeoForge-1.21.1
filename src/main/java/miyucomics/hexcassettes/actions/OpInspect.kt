package miyucomics.hexcassettes.actions

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPlayer
import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexcassettes.hexcompat.ComponentCompat
import miyucomics.hexcassettes.inits.getCassetteState

class OpInspect : ConstMediaAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val player = args.getPlayer(env.world, 0, argc)
        val key = ComponentCompat.encode(args.getDisplayIota(1, argc).text)
        return player.getCassetteState().hexes.containsKey(key).asActionResult
    }
}
