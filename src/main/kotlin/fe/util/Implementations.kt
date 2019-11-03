package fe.util

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.BlockView

abstract class BlockWithBlockEntity(settings: Settings, private val blockEntityProducer: () -> BlockEntity) :
    BlockEntityProvider, Block(settings) {
    override fun createBlockEntity(view: BlockView) = blockEntityProducer()
}

abstract class SyncedBlockEntity(type : BlockEntityType<*>) : BlockEntityClientSerializable, BlockEntity(type){
    override fun toClientTag(tag: CompoundTag): CompoundTag = toTag(tag)
    override fun fromClientTag(tag: CompoundTag) = fromTag(tag)
}