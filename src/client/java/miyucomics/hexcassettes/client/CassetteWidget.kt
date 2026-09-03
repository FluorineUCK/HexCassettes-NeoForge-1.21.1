package miyucomics.hexcassettes.client

import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.hexcompat.ComponentCompat
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import miyucomics.hexcassettes.inits.HexcassettesSounds
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class CassetteWidget(val index: Int, x: Int, y: Int) : Button(
    x, y, 32, 16, Component.empty(), { }, DEFAULT_NARRATION
) {
    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        graphics.blit(texture, x, y, 0f, 0f, width, height, width, height)
        val textToDraw = if (hasCassette()) ClientStorage.activeCassettes[index] else freeText
        val renderer = Minecraft.getInstance().font
        graphics.drawString(renderer, textToDraw, x + width, y + renderer.lineHeight / 2, -1, true)
    }

    override fun playDownSound(soundManager: SoundManager) {
        val sound = if (hasCassette()) HexcassettesSounds.CASSETTE_EJECT.get()
            else HexcassettesSounds.CASSETTE_FAIL.get()
        soundManager.play(SimpleSoundInstance.forUI(sound, 1f, 3f))
    }

    override fun onPress() {
        if (hasCassette()) {
            HexcassettesNetworking.sendRemove(
                ComponentCompat.encode(ClientStorage.activeCassettes[index])
            )
        }
    }

    private fun hasCassette() = index < ClientStorage.activeCassettes.size

    companion object {
        val texture: ResourceLocation = HexcassettesMain.id("textures/cassette.png")
        val freeText: Component = Component.translatable("hexcassettes.free")
            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
    }
}
