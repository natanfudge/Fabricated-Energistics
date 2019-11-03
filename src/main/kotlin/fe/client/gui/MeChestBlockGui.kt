package fe.client.gui

import fe.chest.MeChestBlock
import fe.modId
import fe.util.ExitableScreen
import fe.util.grid
import io.github.cottonmc.cotton.gui.CottonScreenController
import io.github.cottonmc.cotton.gui.widget.*
import net.minecraft.container.BlockContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.recipe.RecipeType
import net.minecraft.text.TranslatableText


class MeChestScreenController(syncId: Int, playerInventory: PlayerInventory, context: BlockContext) :
    CottonScreenController(
        RecipeType.SMELTING,
        syncId,
        playerInventory,
        getBlockInventory(context),
        getBlockPropertyDelegate(context)
    ) {

    companion object{
        val Id = modId("chest_drives")
    }

    override fun canUse(entity: PlayerEntity): Boolean {
        return blockInventory.canPlayerUseInv(entity)
    }

    init {
        val rootPanel = WGridPanel(18)
        this.rootPanel = rootPanel
        rootPanel.add(WLabel(TranslatableText(MeChestBlock.translationKey), WLabel.DEFAULT_TEXT_COLOR), 0, 0)
        grid(rootPanel, blockInventory) {
            inventorySlot(4,2)
        }

        rootPanel.add(createPlayerInventoryPanel(), 0, 5)

        rootPanel.validate(this)
    }
}




class MeChestScreen(container: MeChestScreenController, player: PlayerEntity) :
    ExitableScreen<MeChestScreenController>(container, player)