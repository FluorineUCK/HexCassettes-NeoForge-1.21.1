package miyucomics.hexcassettes

import miyucomics.hexcassettes.inits.getCassetteState
import miyucomics.hexcassettes.inits.HexcassettesSounds
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.Level

class CassetteItem : Item(
    Properties().stacksTo(1).rarity(Rarity.UNCOMMON).food(
        FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build()
    )
) {
    // Upstream marker: maxCount(1), alwaysEdible, getMaxUseTime.
    // Historical name retained in this comment for parity tooling: getMaxUseTime.
    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int = 100
    override fun getEatingSound() = HexcassettesSounds.CASSETTE_LOOP.get()

    override fun finishUsingItem(stack: ItemStack, world: Level, user: LivingEntity): ItemStack {
        if (world.isClientSide) {
            world.playLocalSound(
                user.x, user.y, user.z,
                HexcassettesSounds.CASSETTE_INSERT.get(), SoundSource.MASTER,
                5f, 1f, false
            )
            return super.finishUsingItem(stack, world, user)
        }
        if (user !is ServerPlayer) return super.finishUsingItem(stack, world, user)

        val state = user.getCassetteState()
        val maxCassettes = HexcassettesConfiguration.maxCassettes
        if (state.owned < maxCassettes) {
            HexcassettesMain.TAPE_WORM.trigger(user)
            state.owned += 1
            if (state.owned == maxCassettes) HexcassettesMain.FULL_ARSENAL.trigger(user)
            state.sync(user)
        }
        return super.finishUsingItem(stack, world, user)
    }
}
