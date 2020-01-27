package fe.client.gui

import fabricktx.api.getMinecraftClient
import io.github.cottonmc.cotton.gui.CottonCraftingController
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory

abstract class ExitableScreen<T : CottonCraftingController>(container: T, player: PlayerEntity) :
    CottonInventoryScreen<T>(container, player) {
    override fun keyPressed(ch: Int, keyCode: Int, modifiers: Int): Boolean {
        if (getMinecraftClient().options.keyInventory.matchesKey(ch, keyCode)) {
            getMinecraftClient().openScreen(null)
            return true
        }
        return super.keyPressed(ch, keyCode, modifiers)
    }
}

class GridPainter(
    private val root: WGridPanel,
    private val blockInventory: Inventory
) {
    private var currentRow = 0
    private var currentIndex = 0
    fun inventorySlot(column: Int, row: Int, slotsWide: Int = 1, slotsHigh: Int = 1) {
        root.add(
            WItemSlot.of(blockInventory, currentIndex, slotsWide, slotsHigh), column, row
        )
        currentIndex++
    }

}


fun grid(rootPanel: WGridPanel, inventory: Inventory, init: GridPainter.() -> Unit) =
    init(GridPainter(rootPanel, inventory))
