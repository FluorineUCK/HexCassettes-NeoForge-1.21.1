package miyucomics.hexcassettes.actions

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.EulerPathFinder
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.api.utils.TreeList
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import miyucomics.hexcassettes.CassetteCastEnv
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.data.QueuedHex
import miyucomics.hexcassettes.hexcompat.ComponentCompat
import miyucomics.hexcassettes.hexcompat.IotaCodecCompat
import miyucomics.hexpose.iotas.DisplayIota
import net.minecraft.world.item.DyeColor
import kotlin.math.abs
import kotlin.math.roundToInt

class OpEnqueue : Action {
    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        val state = env.requireCassetteState()
        if (image.stack.size < 2) throw MishapNotEnoughArgs(2, image.stack.size)

        val stack = image.stack.toMutableList()
        var key = if (env is CassetteCastEnv && !state.hexes.containsKey(env.key)) {
            env.key
        } else {
            val pattern = HexPattern.fromAngles(ENQUEUE_SIGNATURE, HexDir.EAST)
            ComponentCompat.encode(PatternIota.display(EulerPathFinder.findAltDrawing(pattern, env.world.gameTime)))
        }
        var labelled = 0
        if (stack.last() is DisplayIota) {
            key = ComponentCompat.encode((stack.removeLast() as DisplayIota).text)
            labelled++
        }

        val potentialDelay = stack.removeLast()
        val rounded = (potentialDelay as? DoubleIota)?.double?.roundToInt()
        if (potentialDelay !is DoubleIota || rounded == null ||
            abs(potentialDelay.double - rounded) > DoubleIota.TOLERANCE || rounded <= 0
        ) throw MishapInvalidIota.of(potentialDelay, labelled, "int.positive")

        val potentialHex = stack.removeLast()
        if (potentialHex !is ListIota)
            throw MishapInvalidIota.ofType(potentialHex, labelled + 1, "list")

        if (!state.hexes.containsKey(key) && state.hexes.size >= state.owned)
            throw NoFreeCassettes()

        val depth = if (env is CassetteCastEnv && env.depth < 10) env.depth + 1 else 0
        val encoded = IotaCodecCompat.encode(potentialHex)
        val compound = encoded as? net.minecraft.nbt.CompoundTag
            ?: throw IllegalStateException("List iota codec did not produce a compound tag")
        state.hexes[key] = QueuedHex(compound, rounded, depth)

        if (labelled == 0) stack.add(DisplayIota(ComponentCompat.decode(key)))
        val newImage = image.copy(
            TreeList.from(stack), image.parenCount, image.parenthesized,
            image.escapeNext, image.simulateNext, image.opsConsumed, image.userData
        )
        return OperationResult(newImage, emptyList(), continuation, HexEvalSounds.SPELL.get())
    }

    companion object {
        const val ENQUEUE_SIGNATURE = "qeqwqwqwqwqeqaweqqqqqwweeweweewqdwwewewwewweweww"
    }
}

class NoFreeCassettes : Mishap() {
    override fun accentColor(env: CastingEnvironment, errorCtx: Context) = dyeColor(DyeColor.RED)
    override fun errorMessage(env: CastingEnvironment, errorCtx: Context) =
        error(HexcassettesMain.MOD_ID + ":no_free_cassettes")

    override fun execute(
        env: CastingEnvironment,
        errorCtx: Context,
        stack: TreeList<Iota>
    ): TreeList<Iota> = stack
}
