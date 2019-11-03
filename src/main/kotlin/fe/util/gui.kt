package fe.util

import fe.client.gui.NetworkInventoryScreenController
import fe.client.gui.stolen.LettuceScreen
import fe.client.gui.stolen.LettuceScreenController
import io.github.cottonmc.cotton.gui.CottonScreenController
import io.github.cottonmc.cotton.gui.client.CottonScreen
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

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
abstract class FreeExitableScreen<T : LettuceScreenController>(container: T, player: PlayerEntity)
    : LettuceScreen<T>(container, player){
    override fun keyPressed(ch: Int, keyCode: Int, modifiers: Int): Boolean {
        if (getMinecraftClient().options.keyInventory.matchesKey(ch, keyCode)) {
            getMinecraftClient().openScreen(null)
            return true
        }
        return super.keyPressed(ch, keyCode, modifiers)
    }
}

fun PlayerEntity.openGui(id : Identifier,pos : BlockPos) = ContainerProviderRegistry.INSTANCE.openContainer(
    id,this){it.writeBlockPos(pos)}