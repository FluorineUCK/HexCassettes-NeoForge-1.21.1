package miyucomics.hexcassettes.advancement

import com.mojang.serialization.Codec
import net.minecraft.advancements.critereon.ContextAwarePredicate
import net.minecraft.advancements.critereon.EntityPredicate
import net.minecraft.advancements.critereon.SimpleCriterionTrigger
import net.minecraft.server.level.ServerPlayer
import java.util.Optional

class HexcassettesCriterion : SimpleCriterionTrigger<HexcassettesCriterion.Condition>() {
    override fun codec(): Codec<Condition> = Condition.CODEC

    fun trigger(player: ServerPlayer) = trigger(player) { true }

    class Condition(private val playerPredicate: Optional<ContextAwarePredicate>) : SimpleInstance {
        override fun player(): Optional<ContextAwarePredicate> = playerPredicate

        companion object {
            @JvmField
            val CODEC: Codec<Condition> = EntityPredicate.ADVANCEMENT_CODEC
                .optionalFieldOf("player")
                .xmap(::Condition, Condition::player)
                .codec()
        }
    }
}
