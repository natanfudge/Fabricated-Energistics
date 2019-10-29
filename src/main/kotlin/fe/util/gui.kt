package fe.util

import fe.drive.DriveBayScreenController
import io.github.cottonmc.cotton.gui.CottonScreenController
import io.github.cottonmc.cotton.gui.client.CottonScreen
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory

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

}


fun grid(rootPanel: WGridPanel, inventory: Inventory, init: GridPainter.() -> Unit) =
    init(GridPainter(rootPanel, inventory))

abstract class ExitableScreen<T : CottonScreenController>(container: T, player: PlayerEntity)
    : CottonScreen<T>(container, player){
    override fun keyPressed(ch: Int, keyCode: Int, modifiers: Int): Boolean {
        if (getMinecraftClient().options.keyInventory.matchesKey(ch, keyCode)) {
            getMinecraftClient().openScreen(null)
            return true
        }
        return super.keyPressed(ch, keyCode, modifiers)
    }
}