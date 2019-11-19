package fe.network

import fe.util.BlockWithBlockEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface NetworkBlock {
    fun getNetworkBlockEntity(world: World, pos: BlockPos): NetworkBlockEntity =
        (world.getBlockEntity(pos) as? NetworkBlockEntity)
            ?: error("NetworkBlocks must have a NetworkBlockEntity!")
}

abstract class NetworkBlockEntity(block : BlockWithBlockEntity) : BlockEntity(block.entityType) {
    var network: MeNetwork? = null
}
