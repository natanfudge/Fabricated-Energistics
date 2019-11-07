package fe.container

import fe.chest.MeChestBlock
import fe.chest.MeChestBlockEntity
import fe.modId
import fe.util.copy
import fe.util.grid
import fe.util.insert
import io.github.cottonmc.cotton.gui.CottonScreenController
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WLabel
import net.minecraft.container.BlockContext
import net.minecraft.container.Container
import net.minecraft.container.Slot
import net.minecraft.container.SlotActionType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.BiFunction
import kotlin.math.roundToInt

private const val RightClick = 1

private fun BlockContext.getNetworkInventory(): NetworkGuiInventory =
    run(BiFunction<World, BlockPos, NetworkGuiInventory> { world, pos ->
        (world.getBlockEntity(pos) as MeChestBlockEntity).getNetworkInventory()
    }).orElseThrow { RuntimeException("Could not find network") }
//TODO: test taking only 1

//TODO: allow the spreading items thing because it's annoying

private enum class SpreadStage {
    START,
    GATHER_SLOTS,
    END
}

class NetworkInventoryScreenController(syncId: Int, playerInventory: PlayerInventory, context: BlockContext) :
    CottonScreenController(
        null,
        syncId,
        playerInventory,
        context.getNetworkInventory(),
        getBlockPropertyDelegate(context)
    ) {

    private val networkGuiInventory get() = blockInventory as NetworkGuiInventory
    private var cursorStack: ItemStack
        get() = playerInventory.cursorStack
        set(value) {
            playerInventory.cursorStack = value
        }

    override fun canUse(entity: PlayerEntity): Boolean {
        return blockInventory.canPlayerUseInv(entity)
    }

    companion object {
        val Id = modId("chest_network")
    }

    //TODO: custom drawing for itemstacks to display numbers nicer
    init {
        val rootPanel = WGridPanel(3)
        this.rootPanel = rootPanel
        rootPanel.add(WLabel(TranslatableText(MeChestBlock.translationKey), WLabel.DEFAULT_TEXT_COLOR), 0, 0)
        grid(rootPanel, blockInventory) {
            inventorySlot(0, 4, slotsHigh = 5, slotsWide = 9)
        }

        rootPanel.add(createPlayerInventoryPanel(), 0, 40)

        rootPanel.validate(this)
    }

    private var totalSpreadSlotAmount = 0
    private var networkSpreadSlotAmount = 0

    //TODO: handle swap and clone
    override fun onSlotClick(
        slotNumber: Int,
        clickData: Int,
        action: SlotActionType,
        player: PlayerEntity
    ): ItemStack {

        if (action == SlotActionType.QUICK_CRAFT) {
            handleQuickCraft(clickData, slotNumber, action, player)
        }

        if (slotNumber < 0) {
            return if (action == SlotActionType.QUICK_MOVE) ItemStack.EMPTY
            else super.onSlotClick(slotNumber, clickData, action, player)
        }
        if (slotNumber >= slotList.size) return ItemStack.EMPTY
        val slot = slotList[slotNumber]
        // we trust vanilla knows what's it doing when interacting with the playerInventory.
        // Other than in quickmove.
        // In quickmove it just does an infinite loop.
        // thanks Mojang.
        if (slot.inventory === playerInventory && action != SlotActionType.QUICK_MOVE) {
            return super.onSlotClick(slotNumber, clickData, action, player)
        }
        if (slot == null || !slot.canTakeItems(player)) return ItemStack.EMPTY

        val result = when (action) {
            SlotActionType.PICKUP -> pickup(slot, leftClick = clickData == RightClick)
            SlotActionType.QUICK_MOVE -> quickMove(slot)
            SlotActionType.SWAP -> swap(slot,clickData)
            SlotActionType.CLONE -> super.onSlotClick(slotNumber, clickData, action, player) != ItemStack.EMPTY
            SlotActionType.THROW -> player.drop(slot, ctrlClick = clickData == 1)
            SlotActionType.QUICK_CRAFT -> spread()
            SlotActionType.PICKUP_ALL -> false
        }

        return if (result) slot.stack.copy() else ItemStack.EMPTY

    }

    private fun handleQuickCraft(
        clickData: Int,
        slotNumber: Int,
        action: SlotActionType,
        player: PlayerEntity
    ) {
        when (unpackSpreadStage(clickData)) {
            SpreadStage.START -> {
                totalSpreadSlotAmount = 0
                networkSpreadSlotAmount = 0
            }
            SpreadStage.GATHER_SLOTS -> {
                totalSpreadSlotAmount++
            }
            SpreadStage.END -> {
                val amountOfEach = if(shouldSpreadOneOfEach(clickData)) 1 else cursorStack.count / totalSpreadSlotAmount
                val amountToInsert = amountOfEach * networkSpreadSlotAmount
                moveFromPlayerHandToNetwork(cursorStack.copy(count = amountToInsert))

                totalSpreadSlotAmount = 0
                networkSpreadSlotAmount = 0
                super.onSlotClick(slotNumber, clickData, action, player)
            }
        }
    }

    private fun unpackSpreadStage(clickData: Int) = when (Container.unpackButtonId(clickData)) {
        0 -> SpreadStage.START
        1 -> SpreadStage.GATHER_SLOTS
        2 -> SpreadStage.END
        else -> error("Impossible")
    }

    private fun shouldSpreadOneOfEach(clickData: Int): Boolean = when (Container.unpackQuickCraftStage(clickData)) {
        0 -> false
        1 -> true
        2 -> error("Did not expect the a stage of 2 here")
        else -> error("Impossible")
    }

    /** These return true if something was changed. */

    private fun quickMove(slot: Slot): Boolean {
        if (!slot.hasStack()) return false
        val toTransfer = slot.stack
        if (slot.inventory === blockInventory) {
            val extracted = networkGuiInventory.extract(toTransfer, toTransfer.maxCount)
            val amountRemaining = playerInventory.insert(extracted)
            // Put back what the player can't fit
            if (amountRemaining.count > 0) networkGuiInventory.insertToNetwork(amountRemaining)
            //Try to transfer the item from the player to the block
        } else {
            val remaining = networkGuiInventory.insertToNetwork(toTransfer)
            toTransfer.count = remaining.count
        }

        return true
    }

    private fun pickup(slot: Slot, leftClick: Boolean): Boolean {
        if (cursorStack.isEmpty) {
            val toTransfer = slot.stack
            if (!slot.hasStack()) return false
            // Pick up
            val amountToTake =
                if (leftClick) (toTransfer.maxTransferAmount / 2.0).roundToInt() else toTransfer.maxTransferAmount
            cursorStack = networkGuiInventory.extract(toTransfer, amountToTake)

        } else {
            moveFromPlayerHandToNetwork(if (leftClick) cursorStack.copy(count = 1) else cursorStack)
        }


        return true
    }

    private fun moveFromPlayerHandToNetwork(stackInPlayerHandToInsert: ItemStack) {
        // Put down
        val amountLeft = networkGuiInventory.insertToNetwork(stackInPlayerHandToInsert).count
        val amountReduced = stackInPlayerHandToInsert.count - amountLeft

        cursorStack.count -= amountReduced
    }

    private fun PlayerEntity.drop(slot: Slot, ctrlClick: Boolean): Boolean {
        val toDrop = slot.stack
        val extracted = networkGuiInventory.extract(toDrop, amount = if (ctrlClick) toDrop.maxTransferAmount else 1)
        return if (extracted.count > 0) {
            dropItem(extracted, true)
            true
        } else false

    }

    private fun spread(): Boolean {
        networkSpreadSlotAmount++
        return false
    }

    private fun swap(slot : Slot, toolbarSlot : Int) : Boolean{
        if(toolbarSlot < 0 || toolbarSlot > 8) return false
        val swappedStack = playerInventory.getInvStack(toolbarSlot)

        if(slot.inventory == blockInventory){
            val extracted = networkGuiInventory.extract(slot.stack,slot.stack.maxTransferAmount)
            val amountLeftAfterInsertion = networkGuiInventory.insertToNetwork(swappedStack).count
            playerInventory.setInvStack(toolbarSlot,extracted)
            if(amountLeftAfterInsertion > 0) playerInventory.insert(swappedStack.copy(amountLeftAfterInsertion))
        }else{
            return false
//            val amountLeftAfterInsertion = networkGuiInventory.insertToNetwork(swappedStack)
//            val extracted =
        }

//        val amountLeft = networkGuiInventory.insertToNetwork(swappedStack).count
//        swappedStack.count = 0
//
//        val amountReduced = swappedStack.count - amountLeft
//
//        swappedStack.count -= amountReduced
        return true
    }


    private val ItemStack.maxTransferAmount get() = Integer.min(maxCount, count)
}