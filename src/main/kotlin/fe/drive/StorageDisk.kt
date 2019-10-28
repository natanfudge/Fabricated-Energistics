package fe.drive

import fe.FabricatedEnergistics
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.DefaultedList

class StorageDisk(val differentItemsCapacity: Int, val perItemCapacity: Int) :
    Item(Settings().group(FabricatedEnergistics.Group)) {
    companion object{
        private const val PerItemCapacitiy = 63
        val cells = mapOf(
            StorageDisk(PerItemCapacitiy,1000) to "storage_cell_1k",
            StorageDisk(PerItemCapacitiy,4000) to "storage_cell_4k",
            StorageDisk(PerItemCapacitiy,16000) to "storage_cell_16k",
            StorageDisk(PerItemCapacitiy,64000) to "storage_cell_64k"
        )
    }
    private val items = DefaultedList.ofSize(differentItemsCapacity, ItemStack.EMPTY)

}