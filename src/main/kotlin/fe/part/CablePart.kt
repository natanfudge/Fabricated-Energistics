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
import fe.client.model.part.TankPartModelKey
import fe.modId
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes

class CablePart(definition: PartDefinition?, holder: MultipartHolder?) : AbstractPart(definition, holder) {
    companion object {
        //        var NET_TANK: ParentNetIdSingle<PartTank> = NET_ID.subType(PartTank::class.java, "simple_pipes:tank")
        val Shape: VoxelShape = VoxelShapes.cuboid(2 / 16.0, 0.0, 2 / 16.0, 14 / 16.0, 12 / 16.0, 14 / 16.0)

        val Definition = PartDefinition(modId("tank"),
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
        return Shape
    }

    override fun getModelKey(): PartModelKey? = TankPartModelKey

    override fun addAllAttributes(list: AttributeList<*>?) {
        super.addAllAttributes(list)
    }

    override fun onAdded(bus: MultipartEventBus) {
        super.onAdded(bus)
        bus.addContextlessListener(this, PartTickEvent::class.java) { onTick() }
    }

    override fun getPickStack(): ItemStack {
        //TODO: generalize for all cables
        return ItemStack(CoveredCableBlock.All.first())
    }

    protected fun onTick() {
        isPlayerInteracting = false
    }

    override fun onActivate(player: PlayerEntity, hand: Hand, hit: BlockHitResult): Boolean {
        if (player.getStackInHand(hand).isEmpty) {
            return true
        }
        if (player.world.isClient) {
            return true
        }
        isPlayerInteracting = true
        return false
    }

    override fun onRemoved() {
        super.onRemoved()
    }
}