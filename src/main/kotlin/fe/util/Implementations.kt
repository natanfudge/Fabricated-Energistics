package fe.util

import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.world.BlockView

abstract class BlockWithBlockEntity(settings: Settings, private val blockEntityProducer: () -> BlockEntity) :
    BlockEntityProvider, Block(settings) {
    override fun createBlockEntity(view: BlockView) = blockEntityProducer()
}