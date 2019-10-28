package fe.drive

import fe.util.getMinecraftClient
import io.github.cottonmc.cotton.gui.CottonScreenController
import io.github.cottonmc.cotton.gui.client.CottonScreen
import io.github.cottonmc.cotton.gui.widget.*
import net.minecraft.container.BlockContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.recipe.RecipeType
import net.minecraft.text.TranslatableText


class DriveBayScreenController(syncId: Int, playerInventory: PlayerInventory, context: BlockContext?) :
    CottonScreenController(
        RecipeType.SMELTING,
        syncId,
        playerInventory,
        getBlockInventory(context),
        getBlockPropertyDelegate(context)
    ) {
    override fun getCraftingResultSlotIndex(): Int {
        return -1 //There's no real result slot
    }

    override fun canUse(entity: PlayerEntity): Boolean {
        return true
    }

    init {
        val rootPanel = WGridPanel(3)
        this.rootPanel = rootPanel
        rootPanel.add(WLabel(TranslatableText("block.fe.drive_bay_gui"), WLabel.DEFAULT_TEXT_COLOR), 0, 0)
        grid(rootPanel, blockInventory) {
            repeat(5){
                inventorySlot(21,2 + 6 * it)
                inventorySlot(27,2 + 6 * it)
            }

        }

        rootPanel.add(createPlayerInventoryPanel(), 0, 35)
        rootPanel.add(WWidget(),0,38)

//        rootPanel.setLocation(rootPanel.x,rootPanel.y - 30)
//        rootPanel.setSize(rootPanel.width,5 * 18 + 18)
//        rootPanel.height += 27
        rootPanel.validate(this)
    }
}



class GridPainter(
    private val root: WGridPanel,
    private val blockInventory: Inventory
) {
    private var currentRow = 0
    private var currentIndex = 0
    fun inventorySlot(column: Int, row: Int) {
        root.add(
            WItemSlot.of(blockInventory, currentIndex), column, row
        )
        currentIndex++
    }

    fun row(size: Int) {
        repeat(size) { column ->
            root.add(
                WItemSlot.of(blockInventory, currentIndex), column, currentRow
            )
            currentIndex++
        }
        currentRow++
    }
}

class Column

fun grid(rootPanel: WGridPanel, inventory: Inventory, init: GridPainter.() -> Unit) =
    init(GridPainter(rootPanel, inventory))

class DriveBayScreen(container: DriveBayScreenController, player: PlayerEntity) :
    CottonScreen<DriveBayScreenController>(container, player) {
    override fun keyPressed(ch: Int, keyCode: Int, modifiers: Int): Boolean {
        if (getMinecraftClient().options.keyInventory.matchesKey(ch, keyCode)) {
            getMinecraftClient().openScreen(null)
            return true
        }
        return super.keyPressed(ch, keyCode, modifiers)
    }

}