package fe.network

import fe.item.StorageDisk
import fe.util.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.DefaultedList
import java.lang.Integer.min

private object Keys {
    const val Inventory = "disk_inventory"
}


/**
 * Returns null if the ItemStack item is not a storage disk
 */
fun getStorageDiskContents(disk: ItemStack): MutableList<ItemStack> {
    val item = disk.item as StorageDisk

    val inventoryTag = disk.getSubTag(Keys.Inventory)
    return if (inventoryTag != null) {
        inventoryTag.toItemStackList().toMutableList().fillWithEmptyStacksUntil(StacksShownAtAtime)
    } else {
        val emptyList = itemStackList(size = item.differentItemsCapacity)
        disk.setNbtInventory(emptyList)
        logDebug { "Inserting new inventory NBT" }
        emptyList
    }

}

private fun ItemStack.setNbtInventory(newInventory: DefaultedList<ItemStack>) {
    putSubTag(Keys.Inventory, newInventory.toTag())
}

private const val StacksShownAtAtime = 45

class NetworkGuiInventory(
    private val disks: List<ItemStack>,
    var filter: (ItemStack) -> Boolean = { true },
    var sortBy: (ItemStack, ItemStack) -> Int = { stackA, StackB ->
        stackA.item.name.asFormattedString().compareTo(StackB.item.name.asFormattedString())
    },
    var invertOrder: Boolean = false,
    var slotsSkipped: Int = 0
) :
    Inventory {
    init {
        for (disk in disks) require(disk.isEmpty || disk.item is StorageDisk)
        if (LogDebug) ensureNoHolesExist()
    }

    /** Inserts the stack into the inventory, returns whatever stacks it couldn't fill in. */
    fun insert(stack: ItemStack): ItemStack {
        logSlots { "Before Insert: " }
        val returned = insertStack(stack)
        updateVisibleSlots()
        logSlots { "After Insert: " }
        return returned
    }
//inventory 14794
    // stack 15502
    // tag 15516
    /**
     * Takes out the item matching the [stack], with the specified [amount]. Returns the stack that was taken out.
     */
    fun extract(stack: ItemStack, amount: Int): ItemStack = extractStack(stack, amount).also { updateVisibleSlots() }


    override fun getInvStack(slot: Int): ItemStack {
        return visibleSlots[slot].copy()
    }

    private fun logSlots(text: () -> String) {
//        logVisibleSlots(text)
        logDiskInventory(text)
    }

    private inline fun logVisibleSlots(text: () -> String) {
        logDebug { "visible slots " + text() + visibleSlots.filter { !it.isEmpty }.joinToString(", ") }
    }

    private inline fun logDiskInventory(text: () -> String) {
        logDebug { "combined inventory  " + text() + combineDiskInventories().joinToString(", ") }
    }

    override fun markDirty() {
    }

    override fun clear() {
        logWarning { "Wiping the network is too dangerous to be possible" }
    }
    //TODO: override onSlotCLick

    override fun setInvStack(slot: Int, stack: ItemStack) {
//        visibleSlots[slot] = stack
////        logSlots { "Before setInvStack: " }
//        val oldStack = visibleSlots[slot]
//        val difference = stack.count - oldStack.count
////        visibleSlots[slot] = stack
//        if (difference > 0) {
//            insertStack(stack.copy(count = difference))
//        }
//        if (difference < 0) {
//            extractStack(amount = -difference, stackTakenAway = oldStack)
//        }
//
//        if (difference != 0) visibleSlots = assignVisibleSlots()
////        logSlots { "After setInvStack: " }
    }


    override fun removeInvStack(slot: Int): ItemStack {
        logWarning { "Removing an entire itemstack shouldn't be needed" }
        val x = 2
        return ItemStack.EMPTY
    }

    override fun canPlayerUseInv(player: PlayerEntity): Boolean {
        return true //TODO: restrictions on network access
    }

    override fun getInvSize(): Int {
        return StacksShownAtAtime
    }

    //TODO: override shiftclick behavior to only take one stack
    override fun takeInvStack(slot: Int, amount: Int): ItemStack {
        logWarning { "No one should be trying to take stacks directly" }
//        logSlots { "Before takeInvStack: " }
//        val stackTakenAway = visibleSlots[slot]
//        val taken = extractStack(stackTakenAway, min(amount, stackTakenAway.maxCount))
//
//        visibleSlots = assignVisibleSlots()

//        logSlots { "After takeInvStack: " }
        return ItemStack.EMPTY
    }

//    private fun removeStack(removedStack )

    private fun insertStack(addedStack: ItemStack): ItemStack {
        val usedStack = addedStack.copy()
        insertIntoExistingStacks(usedStack)

        if (!usedStack.isEmpty) insertIntoSpace(usedStack)
        return usedStack
    }


    private fun getDiskMaxCount(disk: ItemStack) = (disk.item as StorageDisk).perItemCapacity

    private fun ensureNoHolesExist() {
        for (disk in disks) {
            val inventory = getStorageDiskContents(disk)
            var reachedSpace = false
            for (stack in inventory) {
                if (stack.isEmpty) reachedSpace = true
                else assert(!reachedSpace) { "No holes exist" }
            }
        }
    }

    private fun combineDiskInventories(): Sequence<ItemStack> {
        val list = mutableListOf<ItemStack>()
        for (disk in disks) {
            val contents = getStorageDiskContents(disk)
            for (stack in contents) {
                // We assume that stacks are not stored with "holes" of empty stacks between them, see ensureNoHolesExist()
                if (stack.isEmpty) break
                list.add(stack)
            }
        }

        return list.asSequence()
    }

    private fun assignVisibleSlots(): MutableList<ItemStack> {
        //TODO: need to combine itemstacks of the same item
        val beforeInversion = combineDiskInventories().filter(filter).sortedWith(Comparator(sortBy)).drop(slotsSkipped)
            .take(StacksShownAtAtime).toMutableList()
        val result = if (invertOrder) beforeInversion.toMutableList() else beforeInversion
        result.fillWithEmptyStacksUntil(StacksShownAtAtime)

        return result
    }

    private fun updateVisibleSlots() {
        visibleSlots = assignVisibleSlots()
    }


    private var visibleSlots = assignVisibleSlots()


    /**
     * Returns if we should try to insert into empty spaces.
     */
    private fun insertIntoExistingStacks(addedStack: ItemStack) {
        diskIter@ for (disk in disks) {
            val inventory = getStorageDiskContents(disk)
            for (diskStack in inventory) {
                // End of disk
                if (diskStack.isEmpty) break

                if (addedStack.equalsIgnoreCount(diskStack)) {
                    val amountAdded = min(getDiskMaxCount(disk) - diskStack.count, addedStack.count)
                    diskStack.count += amountAdded
                    addedStack.count -= amountAdded

                    disk.setNbtInventory(DefaultedList.copyOf(ItemStack.EMPTY, *inventory.toTypedArray()))
                    // done adding.
                    if (addedStack.isEmpty) return
                    // We assume once we have found the item no more instance of it exist in the disk
                    break
                }
            }
        }
    }

    private fun insertIntoSpace(addedStack: ItemStack) {
        for (disk in disks) {
            val inventory = getStorageDiskContents(disk)

            // We don't insert multiple stacks of the same item.
            // This "continue" will go into effect when we've tried to insert into the stack with insertIntoExistingStacks
            // but there wasn't enough space.
            // In this case, we can't just insert the same item into a new stack because every stack represents a DIFFERENT item.
            if (inventory.any { it.equalsIgnoreCount(addedStack) }) continue

            for (i in inventory.indices) {
                val diskStack = inventory[i]
                if (diskStack.isEmpty) {
                    val amountAdded = min(getDiskMaxCount(disk), addedStack.count)
                    inventory[i] = addedStack.copy(count = amountAdded)
                    addedStack.count -= amountAdded

                    disk.setNbtInventory(DefaultedList.copyOf(ItemStack.EMPTY, *inventory.toTypedArray()))

                    // done adding
                    if (addedStack.isEmpty) return
                }

            }
        }
    }

    private fun extractStack(stackTakenAway: ItemStack, amount: Int): ItemStack {
        var amountLeftToBeTaken = amount
        diskIter@ for (disk in disks) {
            val inventory = getStorageDiskContents(disk)
            for (diskStack in inventory) {
                // End of disk
                if (diskStack.isEmpty) break

                if (stackTakenAway.equalsIgnoreCount(diskStack)) {
                    val amountTakenFromStack = min(amountLeftToBeTaken, diskStack.count)
                    diskStack.count -= amountTakenFromStack
                    // Don't allow holes to remain
                    if (diskStack.isEmpty) {
                        inventory.remove(diskStack)
                        // Keep the total amount (including empty stacks) constant
                        inventory.add(ItemStack.EMPTY)
                    }

                    amountLeftToBeTaken -= amountTakenFromStack

                    disk.setNbtInventory(DefaultedList.copyOf(ItemStack.EMPTY, *inventory.toTypedArray()))
                    // done taking
                    if (amountLeftToBeTaken == 0) break@diskIter
                    // We assume once we have found the item no more instance of it exist in the disk
                    break
                }
            }
        }
        return stackTakenAway.copy(count = amount - amountLeftToBeTaken)
    }

    override fun isInvEmpty(): Boolean {
        return visibleSlots.all { it.isEmpty }
    }

}