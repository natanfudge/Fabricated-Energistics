package fe.network

import fabricktx.api.BlockList
import fabricktx.api.KBlockEntity
import fabricktx.api.StateBlock
import fabricktx.api.adjacentPositions
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

//interface NetworkBlock /*: IBlock*/ {
//    fun getNetworkBlockEntity(world: World, pos: BlockPos): NetworkBlockEntity =
//        getNetworkBlockEntityOrNull(world, pos)
//            ?: error("NetworkBlocks must have a NetworkBlockEntity!")
//
//    fun getNetworkBlockEntityOrNull(world: World, pos: BlockPos): NetworkBlockEntity? =
//        world.getBlockEntity(pos) as? NetworkBlockEntity
//
////    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, placedStack: ItemStack) {
////        getNetworkBlockEntity(world, pos).updateNetwork()
////    }
////
////    override fun onRemoved(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
////        for (nearPos in pos.adjacentPositions()) {
////            getNetworkBlockEntityOrNull(world, nearPos)?.updateNetwork()
////        }
////    }
//
//}

abstract class NetworkBlock<T : BlockEntity>(
    settings: Settings,
    /**
     * The SAME [blockEntityProducer] must be given for multiple blocks of the same class.
     */
    blockEntityProducer: () -> T
) : StateBlock<T>(settings, blockEntityProducer) {
    fun getNetworkBlockEntity(world: World, pos: BlockPos): NetworkBlockEntity =
        getNetworkBlockEntityOrNull(world, pos)
            ?: error("NetworkBlocks must have a NetworkBlockEntity!")

    fun getNetworkBlockEntityOrNull(world: World, pos: BlockPos): NetworkBlockEntity? =
        world.getBlockEntity(pos) as? NetworkBlockEntity

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)
        getNetworkBlockEntity(world, pos).updateNetwork()
    }

    override fun onBlockRemoved(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        super.onBlockRemoved(state, world, pos, newState, moved)
        for (nearPos in pos.adjacentPositions()) {
            getNetworkBlockEntityOrNull(world, nearPos)?.updateNetwork()
        }
    }

}

//abstract class MultipleNetworkBlock<T : BlockEntity>(
//    settings: Settings,
//    /**
//     * The SAME [blockEntityProducer] must be given for multiple blocks of the same class.
//     */
//    blockEntityProducer: () -> T
//) : MultipleStateBlock<T>(settings, blockEntityProducer), NetworkBlock {
//    override fun onPlaced(world: World, pos: BlockPos, state: BlockState?, placer: LivingEntity?, itemStack: ItemStack?) {
//        super.onPlaced(world, pos, state, placer, itemStack)
//        getNetworkBlockEntity(world, pos).updateNetwork()
//    }
//}
//
//abstract class SingularNetworkBlock<T : BlockEntity>(
//    settings: Block.Settings,
//    /**
//     * The SAME [blockEntityProducer] must be given for multiple blocks of the same class.
//     */
//    blockEntityProducer: () -> T
//) : SingularStateBlock<T>(settings, blockEntityProducer), NetworkBlock {
//    override fun onPlaced(world: World, pos: BlockPos, state: BlockState?, placer: LivingEntity?, itemStack: ItemStack?) {
//        super.onPlaced(world, pos, state, placer, itemStack)
//        getNetworkBlockEntity(world, pos).updateNetwork()
//    }
//
//    override fun onBlockRemoved(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
//        super.onBlockRemoved(state, world, pos, newState, moved)
//    }
//}


abstract class NetworkBlockEntity : KBlockEntity {
    constructor(block: StateBlock<*>) : super(block)
    constructor(blocks: BlockList<*>) : super(blocks)

    private lateinit var _network: MeNetwork
    val network: MeNetwork
        get() {
            if (!::_network.isInitialized) updateNetwork()
            return _network
        }

    fun updateNetwork() {
        val network = MeNetwork()
        propagateNetwork(network)
    }

    private fun propagateNetwork(network: MeNetwork) {
        if (!this::_network.isInitialized || this._network != network) {
            this._network = network
            contributeToNetwork()?.let { network.contributeItemHolder(it) }
            for (nearbyNode in pos.adjacentPositions().mapNotNull { world!!.getBlockEntity(it) as? NetworkBlockEntity }) {
                nearbyNode.propagateNetwork(network)
            }
        }
    }

//    override fun cancelRemoval() {
//        updateNetwork()
//    }

    open fun contributeToNetwork(): ItemHolder? = null

}
