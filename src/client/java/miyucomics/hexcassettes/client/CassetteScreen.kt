package miyucomics.hexcassettes.client

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class CassetteScreen : Screen(Component.literal("Cassettes")) {
    override fun init() {
        for (index in 0 until ClientStorage.ownedCassettes)
            addRenderableWidget(CassetteWidget(index, 16, index * 25 + 16))
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        graphics.fillGradient(0, 0, width, height, -1072689136, -804253680)
        super.render(graphics, mouseX, mouseY, delta)
    }

    override fun isPauseScreen() = false
}
