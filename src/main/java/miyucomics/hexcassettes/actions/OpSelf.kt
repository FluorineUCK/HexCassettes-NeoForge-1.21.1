package miyucomics.hexcassettes.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import miyucomics.hexcassettes.CassetteCastEnv
import miyucomics.hexcassettes.hexcompat.ComponentCompat
import miyucomics.hexpose.iotas.DisplayIota

class OpSelf : ConstMediaAction {
    override val argc = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> =
        if (env is CassetteCastEnv) listOf(DisplayIota(ComponentCompat.decode(env.key)))
        else listOf(NullIota())
}
