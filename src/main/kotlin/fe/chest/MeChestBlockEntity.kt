package fe.chest

import fe.item.StorageDisk
import fe.container.NetworkGuiInventory
import fe.util.Builders
import fe.util.ImplementedInventory
import fe.util.SyncedBlockEntity
import fe.util.itemStackList
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.DefaultedList


class MeChestBlockEntity : SyncedBlockEntity(Type), ImplementedInventory, BlockEntityClientSerializable {
    companion object {
        val Type = Builders.blockEntityType(MeChestBlock) { MeChestBlockEntity() }
    }

    override val items: DefaultedList<ItemStack> = DefaultedList.ofSize(1, ItemStack.EMPTY)

    fun getNetworkInventory(): NetworkGuiInventory =
        NetworkGuiInventory(items)

    override fun markDirty() {
        super<SyncedBlockEntity>.markDirty()
    }

    override fun isValidInvStack(slot: Int, stack: ItemStack) = stack.item is StorageDisk

    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)
        Inventories.fromTag(tag, items)
    }

    override fun fromClientTag(tag: CompoundTag) {
        // For some reason the same tag is being used in the client and in the server so to avoid them sharing
        // the same item we copy the stack here
        val loadedItems = itemStackList(1)
        Inventories.fromTag(tag, loadedItems)

        loadedItems.forEachIndexed { i, stack ->
            items[i] = stack.copy()
        }
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        Inventories.toTag(tag, items)
        return super.toTag(tag)
    }

}


