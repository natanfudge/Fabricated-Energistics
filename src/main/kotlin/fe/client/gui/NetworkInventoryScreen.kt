package fe.client.gui

import fe.container.NetworkInventoryScreenController
import net.minecraft.entity.player.PlayerEntity


class NetworkInventoryScreen(container: NetworkInventoryScreenController, player: PlayerEntity) :
    ExitableScreen<NetworkInventoryScreenController>(container, player) {

    //TODO: commenting this out might cause issues with transferring items
    override fun reposition() {
        val basePanel = description.rootPanel
        if (basePanel != null) {
            containerWidth = basePanel.width
            containerHeight = basePanel.height
            if (containerWidth < 16) {
                containerWidth = 300
            }
            if (containerHeight < 16) {
                containerHeight = 300
            }
        }

        x = width / 2 - containerWidth / 2
        y = height / 2 - containerHeight / 2
    }

}