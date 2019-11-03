package fe.client.gui.stolen

import fe.network.NetworkGuiInventory
import fe.util.insert
import fe.util.isServer
import io.github.cottonmc.cotton.gui.GuiDescription
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder
import io.github.cottonmc.cotton.gui.ValidatedSlot
import io.github.cottonmc.cotton.gui.client.BackgroundPainter
import io.github.cottonmc.cotton.gui.client.LibGuiClient
import io.github.cottonmc.cotton.gui.widget.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.container.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeFinder
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*


open class LettuceScreenController(
    syncId: Int,
    private var playerInventory: PlayerInventory,
    protected var blockInventory: NetworkGuiInventory,
    propertyDelegate: PropertyDelegate?
) : CraftingContainer<Inventory?>(null, syncId), GuiDescription {
    protected var world = playerInventory.player.world
    private var _propertyDelegate = propertyDelegate
    protected var _rootPanel  : WPanel= WGridPanel()
    private var _titleColor = WLabel.DEFAULT_TEXT_COLOR
    private var darkTitleColor = WLabel.DEFAULT_DARKMODE_TEXT_COLOR
    private var _focus: WWidget? = null


    init {
        if (propertyDelegate != null && propertyDelegate.size() > 0) addProperties(propertyDelegate)
    }

    override fun getRootPanel(): WPanel = _rootPanel

    override fun getTitleColor(): Int = if (LibGuiClient.config.darkMode) darkTitleColor else _titleColor

    override fun setRootPanel(panel: WPanel): LettuceScreenController {
        _rootPanel = panel
        return this
    }

    override fun setTitleColor(color: Int): LettuceScreenController {
        _titleColor = color
        return this
    }

    @Environment(EnvType.CLIENT)
    override fun addPainters() {
        _rootPanel.backgroundPainter = BackgroundPainter.VANILLA
    }

    override fun addSlotPeer(slot: ValidatedSlot) {
        addSlot(slot)
    }
    //TODO: when shiftclicking back it's inserting too many items on the existing thing.
    //TODO: taking half a stack from the block inventory doesn't work

    override fun onSlotClick(
        slotNumber: Int,
        clickData: Int,
        action: SlotActionType,
        player: PlayerEntity
    ): ItemStack {

        if (slotNumber < 0) return ItemStack.EMPTY
        if (slotNumber >= slotList.size) return ItemStack.EMPTY
        val slot = slotList[slotNumber]
        // we trust vanilla knows what's it doing when interacting with the playerInventory.
        // Other than in quickmove.
        // In quickmove it just does an infinite loop.
        // thanks Mojang.
        if(slot.inventory === playerInventory && action != SlotActionType.QUICK_MOVE) {
            return super.onSlotClick(slotNumber, clickData, action, player)
        }
        if (slot == null || !slot.canTakeItems(player)) return ItemStack.EMPTY

        val result = when (action) {
            SlotActionType.PICKUP -> pickup(slot)
            SlotActionType.QUICK_MOVE -> quickMove(slot)
            SlotActionType.SWAP -> TODO()
            SlotActionType.CLONE -> TODO()
            SlotActionType.THROW -> TODO()
            SlotActionType.QUICK_CRAFT -> false //TODO: crafting terminal?
            SlotActionType.PICKUP_ALL -> false //TODO
        }

        return if(result)   slot.stack.copy() else ItemStack.EMPTY


    }

    /** These return true if something was changed. */

    private fun quickMove(slot: Slot): Boolean {
        if (!slot.hasStack()) return false
        val toTransfer = slot.stack
        if (slot.inventory === blockInventory) {
            val extracted = blockInventory.extract(toTransfer, toTransfer.maxCount)
            val amountRemaining = playerInventory.insert(extracted)
            // Put back what the player can't fit
            if(amountRemaining.count > 0) blockInventory.insert(amountRemaining)
            //Try to transfer the item from the player to the block
        } else {
            val remaining = blockInventory.insert(toTransfer)
            toTransfer.count = remaining.count
        }

        return true
    }

    private fun pickup(slot: Slot): Boolean {
        val toTransfer = slot.stack
        val heldStack = playerInventory.cursorStack
        if (heldStack.isEmpty) {
            if (!slot.hasStack()) return false
            // Pick up
            playerInventory.cursorStack = blockInventory.extract(toTransfer, toTransfer.maxCount)

        } else {
            // Put down
            heldStack.count = blockInventory.insert(heldStack).count
        }

        return true
    }

    fun doMouseUp(x: Int, y: Int, state: Int): WWidget? {
        return _rootPanel.onMouseUp(x, y, state)
    }

    fun doMouseDown(x: Int, y: Int, button: Int): WWidget? {
        return _rootPanel.onMouseDown(x, y, button)
    }

    fun doMouseDrag(x: Int, y: Int, button: Int) {
        _rootPanel.onMouseDrag(x, y, button)
    }


    override fun getPropertyDelegate(): PropertyDelegate? {
        return _propertyDelegate
    }

    override fun setPropertyDelegate(delegate: PropertyDelegate): GuiDescription {
        _propertyDelegate = delegate
        return this
    }

    fun createPlayerInventoryPanel(): WPlayerInvPanel {
        return WPlayerInvPanel(playerInventory)
    }

    override fun populateRecipeFinder(recipeFinder: RecipeFinder) {

    }

    override fun clearCraftingSlots() {
        blockInventory.clear()
    }

    override fun matches(recipe: Recipe<in Inventory?>) = false


    override fun getCraftingResultSlotIndex(): Int {
        return -1
    }

    override fun getCraftingWidth(): Int {
        return 1
    }

    override fun getCraftingHeight(): Int {
        return 1
    }

    @Environment(EnvType.CLIENT)
    override fun getCraftingSlotCount(): Int {
        return 1
    }

    override fun canUse(entity: PlayerEntity): Boolean {
        return blockInventory.canPlayerUseInv(entity)
    }

    override fun isFocused(widget: WWidget): Boolean {
        return _focus === widget
    }

    override fun getFocus(): WWidget? {
        return _focus
    }

    override fun requestFocus(widget: WWidget) { //TODO: Are there circumstances where focus can't be stolen?
        if (_focus === widget) return  //Nothing happens if we're already focused
        if (!widget.canFocus()) return  //This is kind of a gotcha but needs to happen
        if (_focus != null) _focus!!.onFocusLost()
        _focus = widget
        _focus!!.onFocusGained()
    }

    override fun releaseFocus(widget: WWidget) {
        if (_focus === widget) {
            _focus = null
            widget.onFocusLost()
        }
    }

    companion object {
        fun getBlockPropertyDelegate(ctx: BlockContext): PropertyDelegate {
            return ctx.run<PropertyDelegate> { world: World, pos: BlockPos? ->
                val state = world.getBlockState(pos)
                val block = state.block
                if (block is PropertyDelegateHolder) {
                    return@run (block as PropertyDelegateHolder).propertyDelegate
                }
                val be = world.getBlockEntity(pos)
                if (be != null && be is PropertyDelegateHolder) {
                    return@run (be as PropertyDelegateHolder).propertyDelegate
                }
                ArrayPropertyDelegate(0)
            }.orElse(ArrayPropertyDelegate(0))
        }
    }
}