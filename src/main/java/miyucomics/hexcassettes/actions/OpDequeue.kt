package miyucomics.hexcassettes.actions

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexcassettes.hexcompat.ComponentCompat

class OpDequeue : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        env.requireCassetteState()
        return SpellAction.Result(Spell(ComponentCompat.encode(args.getDisplayIota(0, argc).text)), 0, emptyList())
    }

    private data class Spell(val key: String) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            env.requireCassetteState().hexes.remove(key)
        }
    }
}
