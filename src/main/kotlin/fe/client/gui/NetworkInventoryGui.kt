package fe.client.gui

import fe.chest.MeChestBlock
import fe.chest.MeChestBlockEntity
import fe.client.gui.stolen.LettuceScreenController
import fe.modId
import fe.network.NetworkGuiInventory
import fe.util.FreeExitableScreen
import fe.util.grid
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WLabel
import net.minecraft.container.BlockContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.recipe.RecipeType
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import spatialcrafting.util.EmptyInventory
import java.util.function.BiFunction



//private fun BlockContext.run(getter : () -> )

private fun BlockContext.getNetworkInventory(): NetworkGuiInventory = run(BiFunction<World,BlockPos,NetworkGuiInventory> { world, pos ->
    (world.getBlockEntity(pos) as MeChestBlockEntity).getNetworkInventory()
}).orElseThrow { RuntimeException("Could not find network") }

class NetworkInventoryScreenController(syncId: Int, playerInventory: PlayerInventory, context: BlockContext) :
    LettuceScreenController(
        syncId,
        playerInventory,
        context.getNetworkInventory(),
        getBlockPropertyDelegate(context)
    ) {

    override fun canUse(entity: PlayerEntity): Boolean {
        return blockInventory.canPlayerUseInv(entity)
    }

    companion object {
        val Id = modId("chest_network")
    }

    //TODO: custom drawing for itemstacks to display numbers nicer
    init {
        val rootPanel = WGridPanel(18)
        this._rootPanel = rootPanel
        rootPanel.add(WLabel(TranslatableText(MeChestBlock.translationKey), WLabel.DEFAULT_TEXT_COLOR), 0, 0)
        grid(rootPanel, blockInventory) {
            inventorySlot(1, 2)
            inventorySlot(2, 2)
//            inventorySlot(3, 2)
//            inventorySlot(4, 2)
        }

        rootPanel.add(createPlayerInventoryPanel(), 0, 5)

        rootPanel.validate(this)
    }
}


class NetworkInventoryScreen(container: NetworkInventoryScreenController, player: PlayerEntity) :
    FreeExitableScreen<NetworkInventoryScreenController>(container, player)