package fe.util

import drawer.ForItemStack
import drawer.NbtFormat
import kotlinx.serialization.list
import net.minecraft.block.ChestBlock
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.recipe.Ingredient
import net.minecraft.util.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import kotlin.math.min

fun ItemStack.copy(count: Int): ItemStack = copy().apply { this.count = count }
fun ItemStack.actuallyEquals(other: ItemStack) = ItemStack.areEqualIgnoreDamage(this, other)
fun ItemStack.equalsIgnoreCount(other: ItemStack) =
    ItemStack.areItemsEqual(this, other) && ItemStack.areTagsEqual(this, other)

val ItemConvertible.itemStack get() = ItemStack(this)
fun ItemConvertible.itemStack(count: Int) = ItemStack(this, count)

inline fun Ingredient.matches(itemStack: ItemStack) = method_8093(itemStack)

private fun Inventory.stackIsNotEmptyAndCanAddMore(toStack: ItemStack, stackToAdd: ItemStack): Boolean {
    return !toStack.isEmpty &&
            areItemsEqual(toStack, stackToAdd)
            && toStack.isStackable
            && toStack.count < toStack.maxCount
            && toStack.count < this.invMaxStackAmount
}


/**
 * Returns the remaining stack
 */
fun Inventory.insert(stack: ItemStack, direction: Direction = Direction.UP): ItemStack {
    val remainingAfterNonEmptySlots = distributeToAvailableSlots(stack, acceptEmptySlots = false, direction = direction)
    return distributeToAvailableSlots(remainingAfterNonEmptySlots, acceptEmptySlots = true, direction = direction)
}

fun World.inventoryExistsIn(pos: BlockPos): Boolean = world.getBlock(pos) is InventoryProvider
        || world.getBlockEntity(pos) is Inventory


fun World.getInventoryIn(pos: BlockPos): Inventory? {
    val blockEntityInventory = world.getBlockEntity(pos)

    // Fuck you notch
    if (blockEntityInventory is ChestBlockEntity) {
        val blockState = world.getBlockState(pos)
        if (blockState.block is ChestBlock) {
            return ChestBlock.getInventory(blockState, this, pos, true)
        }
    }

    if (blockEntityInventory is Inventory) return blockEntityInventory
    val blockState = world.getBlockState(pos)
    return (blockState.block as? InventoryProvider)?.getInventory(blockState, this, pos)
}

fun Inventory.getAllItems(): List<ItemStack> = List(invSize) { getInvStack(it) }

fun itemStackList(size: Int): DefaultedList<ItemStack> = DefaultedList.ofSize(size, ItemStack.EMPTY)


fun List<ItemStack>.toTag(): Tag = NbtFormat().serialize(ForItemStack.list, this.filter { !it.isEmpty })
fun CompoundTag.toItemStackList(): List<ItemStack> = try {
    NbtFormat().deserialize(ForItemStack.list, this)
} catch (e: Exception) {
    listOf()
}

fun MutableList<ItemStack>.fillWithEmptyStacksUntil(index: Int): MutableList<ItemStack> {
    repeat(index - size) {
        add(ItemStack.EMPTY)
    }
    return this
}

private fun areItemsEqual(stack1: ItemStack, stack2: ItemStack): Boolean {
    return stack1.item === stack2.item && ItemStack.areTagsEqual(stack1, stack2)
}

private fun Inventory.availableSlots(direction: Direction): Iterable<Int> {
    return if (this is SidedInventory) getInvAvailableSlots(direction).toList() else (0 until invSize)
}

private fun Inventory.canInsert(slot: Int, stack: ItemStack, direction: Direction): Boolean {
    return if (this is SidedInventory) canInsertInvStack(slot, stack, direction) else isValidInvStack(slot, stack)
}

private fun Inventory.distributeToAvailableSlots(
    stack: ItemStack,
    acceptEmptySlots: Boolean,
    direction: Direction
): ItemStack {
    val maxStackSize = min(invMaxStackAmount,stack.maxCount)
    var stackCountLeftToDistribute = stack.count
    for (slot in availableSlots(direction)) {
        if (!canInsert(slot, stack, direction)) continue

        val stackInSlot = getInvStack(slot)
        if ((acceptEmptySlots && stackInSlot.isEmpty) || stackIsNotEmptyAndCanAddMore(stackInSlot, stack)) {
            val amountThatCanFitInSlot = maxStackSize - stackInSlot.count
            if (amountThatCanFitInSlot >= 0) {
                setInvStack(
                    slot, ItemStack(
                        stack.item,
                        min(maxStackSize, stackInSlot.count + stackCountLeftToDistribute)
                    )
                )
                stackCountLeftToDistribute -= amountThatCanFitInSlot
            }
        }

        if (stackCountLeftToDistribute <= 0) return ItemStack.EMPTY

    }

    return stack.copy(count = stackCountLeftToDistribute)
}

