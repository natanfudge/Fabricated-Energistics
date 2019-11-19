package fe.client.gui

//import fe.block.DriveBayBlock
import fe.block.DriveBayBlock
import fe.util.ExitableScreen
import fe.util.grid
import io.github.cottonmc.cotton.gui.CottonScreenController
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WLabel
import net.minecraft.container.BlockContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.recipe.RecipeType
import net.minecraft.text.TranslatableText


class DriveBayScreenController(syncId: Int, playerInventory: PlayerInventory, context: BlockContext) :
    CottonScreenController(
        RecipeType.SMELTING,
        syncId,
        playerInventory,
        getBlockInventory(context),
        getBlockPropertyDelegate(context)
    ) {

    override fun canUse(entity: PlayerEntity): Boolean {
        return blockInventory.canPlayerUseInv(entity)
    }


    init {
        val rootPanel = WGridPanel(3)
        this.rootPanel = rootPanel
        rootPanel.add(WLabel(TranslatableText(DriveBayBlock.translationKey), WLabel.DEFAULT_TEXT_COLOR), 0, 0)
        grid(rootPanel, blockInventory) {
            inventorySlot(21, 2, slotsWide = 2, slotsHigh = 5)
        }

        rootPanel.add(createPlayerInventoryPanel(), 0, 35)

        rootPanel.validate(this)
    }
}


class DriveBayScreen(container: DriveBayScreenController, player: PlayerEntity) :
    ExitableScreen<DriveBayScreenController>(container, player)