//package fe.blockentity
//
//import fabricktx.api.ImplementedInventory
//import fe.item.StorageDisk
//import fe.network.NetworkBlockEntity
//import net.minecraft.block.entity.BlockEntity
//import net.minecraft.inventory.Inventories
//import net.minecraft.item.ItemStack
//import net.minecraft.nbt.CompoundTag
//import net.minecraft.util.DefaultedList
//
//
//class DriveBayBlockEntity : NetworkBlockEntity(DriveBayBlock), ImplementedInventory {
//
//    override val items: DefaultedList<ItemStack> = DefaultedList.ofSize(10, ItemStack.EMPTY)
//
//    override fun markDirty() {
//        super<ImplementedInventory>.markDirty()
//    }
//
//    override fun isValidInvStack(slot: Int, stack: ItemStack) = stack.item is StorageDisk
//
//    override fun fromTag(tag: CompoundTag) {
//        super.fromTag(tag)
//        Inventories.fromTag(tag, items)
//    }
//
//    override fun toTag(tag: CompoundTag): CompoundTag {
//        Inventories.toTag(tag, items)
//        return super.toTag(tag)
//    }
//
//
//
//}