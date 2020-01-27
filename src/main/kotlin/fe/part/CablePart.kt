package fe.part

import alexiil.mc.lib.attributes.AttributeList
import alexiil.mc.lib.multipart.api.AbstractPart
import alexiil.mc.lib.multipart.api.MultipartEventBus
import alexiil.mc.lib.multipart.api.MultipartHolder
import alexiil.mc.lib.multipart.api.PartDefinition
import alexiil.mc.lib.multipart.api.event.PartTickEvent
import alexiil.mc.lib.multipart.api.render.PartModelKey
import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.IMsgWriteCtx
import alexiil.mc.lib.net.NetByteBuf
import fe.block.CoveredCableBlock
import fe.block.CoveredCableBlocks
import fe.client.model.CableShapes
import fe.modId
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes

object CablePartModelKey : PartModelKey() {
    override fun equals(other: Any?): Boolean = this === other

    override fun hashCode(): Int = System.identityHashCode(this)
}

class CablePart(definition: PartDefinition?, holder: MultipartHolder?) : AbstractPart(definition, holder) {
    companion object {

        val Definition = PartDefinition(modId("cable"),
            PartDefinition.IPartNbtReader { definition, holder, tag ->
                CablePart(definition, holder, tag)
            },
            PartDefinition.IPartNetLoader { definition, holder, buf, ctx ->
                CablePart(definition, holder, buf, ctx)
            }
        )
    }

    private var isPlayerInteracting = false

    constructor(definition: PartDefinition, holder: MultipartHolder, tag: CompoundTag) : this(definition, holder) {}

    override fun toTag(): CompoundTag {
        return super.toTag()
    }

    constructor(definition: PartDefinition, holder: MultipartHolder, buf: NetByteBuf, ctx: IMsgReadCtx) : this(
        definition,
        holder
    ) {
    }

    override fun writeCreationData(buffer: NetByteBuf, ctx: IMsgWriteCtx) {
        super.writeCreationData(buffer, ctx)
    }

    override fun getShape(): VoxelShape {
        //TODO: generalize
        return CableShapes.Covered.Core
    }

    override fun getModelKey(): PartModelKey = CablePartModelKey

    override fun addAllAttributes(list: AttributeList<*>?) {
        super.addAllAttributes(list)
    }

    override fun onAdded(bus: MultipartEventBus) {
        super.onAdded(bus)
        bus.addContextlessListener(this, PartTickEvent::class.java) { onTick() }
    }

    override fun getPickStack(): ItemStack {
        //TODO: generalize for all cables
        return ItemStack(CoveredCableBlocks.first())
    }

    protected fun onTick() {
        isPlayerInteracting = false
    }

    override fun onUse(player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (player.getStackInHand(hand).isEmpty) {
            return ActionResult.SUCCESS
        }
        if (player.world.isClient) {
            return ActionResult.SUCCESS
        }
        isPlayerInteracting = true
        return ActionResult.FAIL
    }

    override fun onRemoved() {
        super.onRemoved()
    }
}