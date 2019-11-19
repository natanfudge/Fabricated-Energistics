package fe.network

import net.minecraft.item.ItemStack

class MeNetwork {
    companion object {
        /**
         * Returns null if all of the networks are null
         */
        fun combineNetworks(networks: List<MeNetwork?>): MeNetwork? {
            val existingNetworks = networks.filterNotNull()
            if (existingNetworks.isEmpty()) return null
            val firstNetwork = existingNetworks[0]
            // They are all the same network
            if (existingNetworks.all { it == firstNetwork }) return firstNetwork
            val combinedNetwork = MeNetwork()
            for (network in existingNetworks) {
                combinedNetwork.itemHolders.addAll(network.itemHolders)
            }
            return combinedNetwork
        }
    }

    private val itemHolders = mutableListOf<ItemHolder>()
    val isActive: Boolean = false
    fun allStoredItems(): List<ItemStack> = itemHolders.flatMap { it.listContents() }

    fun contributeItemHolder(itemHolder: ItemHolder) {
        itemHolders.add(itemHolder)
    }

    /**
     * The inserted stack will NOT be modified
     * @return Stacks that the network does not have space for. If there is no space, it will be a (deep) clone of [stack].
     */
    fun insert(stack: ItemStack): ItemStack {
        val usedStack = stack.copy()
        for (itemHolder in itemHolders) {
            itemHolder.insertIntoPartiallyFilledSlots(usedStack)
            if (usedStack.isEmpty) return ItemStack.EMPTY
        }

        for (itemHolder in itemHolders) {
            itemHolder.insertIntoEmptySlots(usedStack)
            if (usedStack.isEmpty) return ItemStack.EMPTY
        }

        return usedStack
    }

    /**
     * [stackExample] Represents _what_ stack will be extracted, [amount] represents _how much_ of the stack will be extracted.
     * [stackExample] will NOT be modified.
     */
    fun extract(stackExample: ItemStack, amount: Int): ItemStack {
        val extracted = stackExample.copy()
        var extractedAmount = 0
        for (itemHolder in itemHolders) {
            val amountLeft = amount - extractedAmount
            extractedAmount += itemHolder.extract(extracted, amountLeft)
            if (amountLeft == 0) break
            assert(amountLeft > 0)
        }
        extracted.count = extractedAmount
        return extracted
    }
}

val InactiveNetwork = MeNetwork()


interface ItemHolder {
    /**
     * Returns all non-empty stacks
     */
    fun listContents(): List<ItemStack>

    /**
     * WILL MODIFY the stack, and from the stack will remain whatever cannot be inserted.
     */
    fun insertIntoPartiallyFilledSlots(stack: ItemStack)

    /**
     * WILL modify the stack, and from the stack will remain whatever cannot be inserted.
     */
    fun insertIntoEmptySlots(stack: ItemStack)

    /**
     * WILL NOT modify the stack.
     * @param amount How much needs to be extracted
     * @return How much was extracted
     */
    fun extract(exampleStack: ItemStack, amount: Int): Int
}

