package fe.block

import fe.blockentity.DriveBayBlockEntity
import fe.modId
import fe.network.NetworkNode
import fe.util.BlockWithBlockEntity
import fe.util.Builders
import fe.util.openGui
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object DriveBayBlock : BlockWithBlockEntity(Builders.blockSettings(Material.METAL), ::DriveBayBlockEntity), NetworkNode {
    val Id = modId("drive")
    private fun World.getDriveBayBlockEntity(pos: BlockPos) = world.getBlockEntity(pos) as DriveBayBlockEntity
    override fun activate(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): Boolean {
        if (world.isClient) return true
        player.openGui(Id, pos)
        return true
    }

}

