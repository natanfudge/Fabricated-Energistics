package fe.blockentity

import fe.block.DriveBayBlock
import fe.item.StorageDisk
import fe.util.Builders
import fe.util.ImplementedInventory
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.DefaultedList


class DriveBayBlockEntity : BlockEntity(Type), ImplementedInventory {
    companion object {
        val Type = Builders.blockEntityType(DriveBayBlock) { DriveBayBlockEntity() }
    }

    override val items: DefaultedList<ItemStack> = DefaultedList.ofSize(10, ItemStack.EMPTY)

    override fun markDirty() {
        super<ImplementedInventory>.markDirty()
    }

    override fun isValidInvStack(slot: Int, stack: ItemStack) = stack.item is StorageDisk

    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)
        Inventories.fromTag(tag, items)
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        Inventories.toTag(tag, items)
        return super.toTag(tag)
    }



}