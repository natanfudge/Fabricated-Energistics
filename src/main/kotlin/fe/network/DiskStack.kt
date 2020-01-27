package fe.network

import drawer.ForDefaultedList
import drawer.ForItemStack
import drawer.getFrom
import drawer.put
import fabricktx.api.*
import fe.item.StorageDisk
import fe.modId
import nerdhub.cardinal.components.api.ComponentRegistry
import nerdhub.cardinal.components.api.ComponentType
import nerdhub.cardinal.components.api.component.Component
import nerdhub.cardinal.components.api.component.extension.CloneableComponent
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.DefaultedList


class DiskStack(private val stackProvider: () -> ItemStack) : ItemHolder {
    companion object {
         val DiskInventory: ComponentType<InventoryComponent> = ComponentRegistry.INSTANCE.registerIfAbsent(
            modId("disk-inventory"),
            InventoryComponent::class.java
        )

    }

    init {
        require(stackProvider().isEmpty || stackProvider().item is StorageDisk)
        inDebug {
            ensureNoHolesExist()
            ensureItemsAreGroupedTogether()
        }

    }

    private val disk = stackProvider().item as? StorageDisk


    private fun getInventory() : DefaultedList<ItemStack>
            = if(stackProvider().isEmpty) itemStackList() else DiskInventory.get(stackProvider()).inventory

    // This is just a sanity check
    private fun ensureNoHolesExist() {
        var reachedSpace = false
        for (stack in getInventory()) {
            if (stack.isEmpty) reachedSpace = true
            else assert(!reachedSpace) { "No holes exist" }
        }
    }

    // Another sanity check
    private fun ensureItemsAreGroupedTogether() {
        val reachedItems = mutableListOf<ItemStack>()
        for (stack in getInventory()) {
            for (previousStack in reachedItems) assert(!stack.equalsIgnoreCount(previousStack)) { "Items are not spread out" }
            reachedItems.add(stack)
        }
    }

    override fun listContents(): List<ItemStack> {
        val nonEmpty = mutableListOf<ItemStack>()
        for (stack in getInventory()) {
            // We assume that stacks are not stored with "holes" of empty stacks between them, see ensureNoHolesExist()
            if (stack.isEmpty) break
            nonEmpty.add(stack)
        }
        return nonEmpty
    }

    override fun insertIntoPartiallyFilledSlots(stack: ItemStack) {
        for (diskStack in getInventory()) {
            // End of disk
            if (diskStack.isEmpty) break

            if (stack.equalsIgnoreCount(diskStack)) {
                val amountAdded = Integer.min(disk!!.perItemCapacity - diskStack.count, stack.count)
                diskStack.count += amountAdded
                stack.count -= amountAdded

                // done adding.
                if (stack.isEmpty) return
                // We assume once we have found the item no more instance of it exist in the disk (see ensureItemsAreGroupedTogether)
                break
            }
        }
    }

    override fun insertIntoEmptySlots(stack: ItemStack) {
        val inventory = getInventory()

        // We don't insert multiple stacks of the same item.
        // This "return" will go into effect when we've tried to insert into the stack with insertIntoExistingStacks
        // but there wasn't enough space.
        // In this case, we can't just insert the same item into a new stack because every stack represents a DIFFERENT item.
        if (inventory.any { it.equalsIgnoreCount(stack) }) return

        for (i in inventory.indices) {
            val diskStack = inventory[i]
            if (diskStack.isEmpty) {
                val amountAdded = Integer.min(disk!!.perItemCapacity, stack.count)
                inventory[i] = stack.copy(count = amountAdded)
                stack.count -= amountAdded
                // done adding
                if (stack.isEmpty) return
            }

        }
    }

    override fun extract(exampleStack: ItemStack, amount: Int): Int {
        val inventory = getInventory()
        inventory.forEachIndexed { i, diskStack ->
            // End of disk
            if (diskStack.isEmpty) return@forEachIndexed

            if (exampleStack.equalsIgnoreCount(diskStack)) {
                val amountTakenFromStack = Integer.min(amount, diskStack.count)
                diskStack.count -= amountTakenFromStack
                // Don't allow holes to remain
                if (diskStack.isEmpty) inventory.removeHole(i)

                // done taking, we assume once we have found the item no more instance of it exist in the disk
                return amountTakenFromStack
            }
        }
        return 0
    }

    private fun DefaultedList<ItemStack>.removeHole(index: Int) {
        for (i in (index + 1 until size)) {
            set(i - 1, get(i))
        }

        set(size - 1, ItemStack.EMPTY)
    }
}


interface InventoryComponent : Component {
    val inventory: DefaultedList<ItemStack>
}

private val serializer = ForDefaultedList(ForItemStack)


data class InventoryComponentImpl(
    private val diskItem: StorageDisk,
    override val inventory: DefaultedList<ItemStack>
) : InventoryComponent, CloneableComponent {

    override fun toTag(tag: CompoundTag): CompoundTag {
        serializer.put(inventory, tag)
        return tag
    }

    override fun newInstance(): CloneableComponent = InventoryComponentImpl(
        diskItem,
        itemStackList(diskItem.differentItemsCapacity).apply {
            inventory.forEachIndexed { index, itemStack ->
                inventory[index] = itemStack.copy()
            }
        }
    )

    override fun fromTag(tag: CompoundTag) {
        inventory.clear()
        serializer.getFrom(tag).forEachIndexed { index, itemStack ->
            inventory[index] = itemStack
        }
    }

}



//class DiskStackProvider(private val diskGetter : () -> DiskStack) : ItemHolder by diskGetter()