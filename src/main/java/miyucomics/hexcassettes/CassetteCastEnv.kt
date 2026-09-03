package miyucomics.hexcassettes

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand

class CassetteCastEnv(
    caster: ServerPlayer,
    castingHand: InteractionHand,
    val key: String,
    val depth: Int = 0
) : PlayerBasedCastEnv(caster, castingHand) {
    init {
        if (depth >= 9) HexcassettesMain.QUINIO.trigger(caster)
    }

    override fun getCastingHand(): InteractionHand = castingHand
    override fun produceParticles(particles: ParticleSpray, pigment: FrozenPigment) = Unit
    override fun getPigment(): FrozenPigment = IXplatAbstractions.INSTANCE.getPigment(caster)

    public override fun extractMediaEnvironment(costLeft: Long, simulate: Boolean): Long {
        if (caster.isCreative) return 0
        return extractMediaFromInventory(costLeft, true, simulate)
    }
}
