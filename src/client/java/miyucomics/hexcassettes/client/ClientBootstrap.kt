package miyucomics.hexcassettes.client

import com.mojang.blaze3d.platform.InputConstants
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.common.NeoForge
import org.lwjgl.glfw.GLFW

object ClientBootstrap {
    private val CASSETTE_KEYBIND = KeyMapping(
        "key.hexcassettes.ponder_cassette",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_G,
        "key.categories.hexcassettes"
    )

    fun initialize(modBus: IEventBus) {
        modBus.addListener(::registerKeyMappings)
        NeoForge.EVENT_BUS.addListener(::onClientTick)
        NeoForge.EVENT_BUS.addListener(::onLogin)
        NeoForge.EVENT_BUS.addListener(::onMovementInput)
    }

    private fun registerKeyMappings(event: RegisterKeyMappingsEvent) = event.register(CASSETTE_KEYBIND)

    private fun onClientTick(event: ClientTickEvent.Post) {
        val client = Minecraft.getInstance()
        if (CASSETTE_KEYBIND.consumeClick() && client.screen == null)
            client.setScreen(CassetteScreen())
    }

    private fun onLogin(event: ClientPlayerNetworkEvent.LoggingIn) = HexcassettesNetworking.requestSync()

    private fun onMovementInput(event: MovementInputUpdateEvent) {
        if (Minecraft.getInstance().screen !is CassetteScreen) return
        val options = Minecraft.getInstance().options
        val input = event.input
        input.up = options.keyUp.isDown
        input.down = options.keyDown.isDown
        input.left = options.keyLeft.isDown
        input.right = options.keyRight.isDown
        input.jumping = options.keyJump.isDown
        input.shiftKeyDown = options.keyShift.isDown
        input.forwardImpulse = (if (input.up) 1f else 0f) - (if (input.down) 1f else 0f)
        input.leftImpulse = (if (input.left) 1f else 0f) - (if (input.right) 1f else 0f)
    }
}
