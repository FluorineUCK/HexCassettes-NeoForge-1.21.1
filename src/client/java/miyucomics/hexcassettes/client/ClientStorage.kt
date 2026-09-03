package miyucomics.hexcassettes.client

import net.minecraft.network.chat.Component

object ClientStorage {
    var ownedCassettes: Int = 0
    var activeCassettes: MutableList<Component> = mutableListOf()
}
