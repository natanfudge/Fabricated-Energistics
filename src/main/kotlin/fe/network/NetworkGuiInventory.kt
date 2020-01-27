package fe.network

import fe.logWarning
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack

private object Keys {
    const val Inventory = "disk_inventory"
}


private const val StacksShownAtAtime = 45

class NetworkGuiInventory(
    private val network: MeNetwork,
    var filter: (ItemStack) -> Boolean = { true },
    var sortBy: (ItemStack, ItemStack) -> Int = { stackA, StackB ->
        stackA.item.name.asFormattedString().compareTo(StackB.item.name.asFormattedString())
    },
    var invertOrder: Boolean = false,
    var slotsSkipped: Int = 0
) : Inventory {


    /** Inserts the stack into the inventory, returns whatever stacks it couldn't fill in. */
    fun insertToNetwork(stack: ItemStack): ItemStack = network.insert(stack)
        .also { updateVisibleSlots() }


    /**
     * Takes out the item matching the [stack], with the specified [amount]. Returns the stack that was taken out.
     */
    fun extractFromNetwork(stack: ItemStack, amount: Int): ItemStack = network.extract(stack, amount)
        .also { updateVisibleSlots() }


    override fun getInvStack(slot: Int): ItemStack {
        return visibleSlots[slot].copy()
    }


    override fun markDirty() {
    }

    override fun clear() {
        logWarning { "Wiping the network is too dangerous to be possible" }
    }

    override fun setInvStack(slot: Int, stack: ItemStack) {
        // Minecraft calls this pointlessly
    }


    override fun removeInvStack(slot: Int): ItemStack {
        logWarning { "Removing an entire itemstack shouldn't be needed" }
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
        return ItemStack.EMPTY
    }


    private fun assignVisibleSlots(): MutableList<ItemStack> {
        //TODO: need to combine itemstacks of the same item
        val beforeInversion =
            network.allStoredItems().asSequence().filter(filter).sortedWith(Comparator(sortBy)).drop(slotsSkipped)
                .take(StacksShownAtAtime).toMutableList()
        val result = if (invertOrder) beforeInversion.toMutableList() else beforeInversion
        result.fillWithEmptyStacksUntil(StacksShownAtAtime)

        return result
    }

    private fun MutableList<ItemStack>.fillWithEmptyStacksUntil(index: Int): MutableList<ItemStack> {
        repeat(index - size) {
            add(ItemStack.EMPTY)
        }
        return this
    }


    private fun updateVisibleSlots() {
        visibleSlots = assignVisibleSlots()
    }


    private var visibleSlots = assignVisibleSlots()


    override fun isInvEmpty(): Boolean {
        return visibleSlots.all { it.isEmpty }
    }

}