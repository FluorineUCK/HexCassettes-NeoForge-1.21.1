package miyucomics.hexcassettes.actions

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexcassettes.data.CassetteState

class OpKillAll : SpellAction {
    override val argc = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result =
        SpellAction.Result(Spell(env.requireCassetteState()), 0, emptyList())

    private data class Spell(val state: CassetteState) : RenderedSpell {
        override fun cast(env: CastingEnvironment) = state.hexes.clear()
    }
}
