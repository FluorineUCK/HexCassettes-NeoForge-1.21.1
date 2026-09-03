package miyucomics.hexcassettes

import net.neoforged.neoforge.common.ModConfigSpec

object HexcassettesConfiguration {
    private val builder = ModConfigSpec.Builder()

    @JvmField
    val MAX_CASSETTES: ModConfigSpec.IntValue = builder
        .comment("Maximum number of cassette slots a player can own.")
        .defineInRange("max_cassettes", 6, 1, 64)

    @JvmField
    val SPEC: ModConfigSpec = builder.build()

    val maxCassettes: Int
        get() = MAX_CASSETTES.get()
}
