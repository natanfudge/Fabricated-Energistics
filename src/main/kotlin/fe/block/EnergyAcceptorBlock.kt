package fe.block

import fe.blockentity.EnergyAcceptorBlockEntity
import fe.network.MeNetwork
import fe.network.NetworkBlock
import fe.network.NetworkBlockEntity
import fe.util.BlockWithBlockEntity
import fe.util.adjacentPositions
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

//TODO: on validate of energy acceptor block, and on placement of networkblock, recurse through all nodes and tell them to contribute their inventory
// And assign them to one Network instance created at te start.

//TODO: probably remove NetworkBlock, and then rename networkblockentity to networknode
object EnergyAcceptorBlock : BlockWithBlockEntity(Settings.of(Material.METAL), ::EnergyAcceptorBlockEntity),
    NetworkBlock {

    override fun onPlaced(
        world: World,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        itemStack: ItemStack
    ) {
        //TODO: only activate when has power

        val nearbyNodes = pos.adjacentPositions() .map { world.getBlockEntity(it) }.filterIsInstance<NetworkBlockEntity>()
        val nearbyNetworks = nearbyNodes.map { it.network }
        val combinedNetwork = MeNetwork.combineNetworks(nearbyNetworks) ?: MeNetwork()
        (world.getBlockEntity(pos) as EnergyAcceptorBlockEntity).network = combinedNetwork

        nearbyNodes.forEach { it.propagate(combinedNetwork) }
    }
}

private fun NetworkBlockEntity.propagate(network: MeNetwork) {
    if (this.network != network) {
        this.network = network
        for (nearbyNode in pos.adjacentPositions().mapNotNull { world!!.getBlockEntity(it) as? NetworkBlockEntity }) {
            nearbyNode.propagate(network)
        }
    }
}