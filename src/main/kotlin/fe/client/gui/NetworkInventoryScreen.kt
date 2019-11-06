package fe.client.gui

import fe.container.NetworkInventoryScreenController
import fe.util.ExitableScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity


class NetworkInventoryScreen(container: NetworkInventoryScreenController, player: PlayerEntity) :
    ExitableScreen<NetworkInventoryScreenController>(container, player){
    override fun init(minecraftClient_1: MinecraftClient?, screenWidth: Int, screenHeight: Int) {
        super.init(minecraftClient_1, screenWidth, screenHeight)
        container.addPainters()
        reposition()
    }

    override fun reposition() {
        val basePanel = container.rootPanel
        containerWidth = basePanel.width
        containerHeight = basePanel.height
        //DEBUG
        if (containerWidth < 16) containerWidth = 300
        if (containerHeight < 16) containerHeight = 300
        left = width / 2 - containerWidth / 2
        top = height / 2 - containerHeight / 2
    }

}